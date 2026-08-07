package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.api.event.CastingEvent;
import com.lvdriver.tconstruct_nirvana.recipe.CastingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.CastingRecipeInput;
import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * 浇铸台/浇铸盆方块实体（1:1 移植自 Tinkers' Antique {@code TileCasting} +
 * {@code TileCastingTable / TileCastingBasin} 差异）。
 *
 * <p>双槽位（0=模具输入，1=输出）+ 动态容量流体罐（容量由当前配方决定，
 * 1:1 旧版 {@code FluidHandlerCasting}：空罐注入时先查配方设定容量再填充）。
 * 液体注满后按配方冷却时间计时，完成后经 {@link CastingEvent} 产出物品。</p>
 *
 * <p>台/盆差异仅配方来源不同（旧版 getTableCasting / getBasinCasting，
 * 1.21.1 用 {@link CastingRecipe#isBasin()} 过滤）；本类以 {@link #basin}
 * 布尔区分，两个 BlockEntityType 共用。</p>
 */
public class CastingBlockEntity extends BlockEntity {

    /** 模具/输入槽。 */
    public static final int SLOT_CAST = 0;
    /** 输出槽。 */
    public static final int SLOT_OUTPUT = 1;

    /** 浇铸台冷却计时用的液体渲染高度（client 渲染用，1:1 旧版 getFlowDepth）。 */
    public static final float FLOW_DEPTH_TABLE = 0.125F;
    public static final float FLOW_DEPTH_BASIN = 0.725F;

    private final boolean basin;
    /** 双槽物品栏（0=模具输入，1=输出）。 */
    private final net.minecraft.world.SimpleContainer inventory = new net.minecraft.world.SimpleContainer(2);
    /** 浇铸流体罐（容量由配方决定，1:1 旧版 FluidTankAnimated(0) 起步）。 */
    protected final FluidTank tank = new FluidTank(0);
    /** 冷却计时（tick）。 */
    protected int timer;
    /** 当前配方。 */
    protected CastingRecipe recipe;

    public CastingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean basin) {
        super(type, pos, state);
        this.basin = basin;
    }

    public boolean isBasin() {
        return basin;
    }

    public ItemStack getItem(int slot) {
        return inventory.getItem(slot);
    }

    public void setItem(int slot, ItemStack stack) {
        inventory.setItem(slot, stack);
        setChanged();
        syncToClient();
    }

    /** 服务端 BE 数据变化 → 全量同步到客户端（冷却产出/物品进出/液体变化）。
     * 缺失时客户端显示陈旧数据：冷却完成不渲染输出物品、交互后槽位显示错乱。 */
    private void syncToClient() {
        if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket packet =
                    net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
            serverLevel.getChunkSource().chunkMap
                    .getPlayers(new net.minecraft.world.level.ChunkPos(worldPosition.getX() >> 4, worldPosition.getZ() >> 4), false)
                    .forEach(p -> p.connection.send(packet));
        }
    }

    /** 当前冷却进度（0~1，GUI/渲染用，1:1 旧版 getProgress）。 */
    public float getProgress() {
        if (recipe == null || tank.getFluidAmount() == 0) {
            return 0F;
        }
        return Math.min(1F, (float) timer / (float) recipe.getTime());
    }

    /** 浇铸流体罐（客户端渲染用）。 */
    public FluidTank getTank() {
        return tank;
    }

    /** 红石比较器：输出槽有物品 = 15（1:1 旧版 comparatorStrength）。 */
    public int comparatorStrength() {
        return getItem(SLOT_OUTPUT).isEmpty() ? 0 : 15;
    }

    /* ---------- 配方查找 ---------- */

    /** 按模具 + 流体查配方（台/盆各自过滤，1:1 旧版 getTableCasting / getBasinCasting）。 */
    protected CastingRecipe findRecipe(ItemStack cast, net.minecraft.world.level.material.Fluid fluid) {
        if (level == null) {
            return null;
        }
        Optional<CastingRecipe> optional = level.getRecipeManager().getRecipeFor(
                ModRecipeTypes.CASTING_TYPE.get(),
                new CastingRecipeInput(cast, new FluidStack(fluid, 1)),
                level)
                .map(net.minecraft.world.item.crafting.RecipeHolder::value)
                .map(r -> (CastingRecipe) r);
        return optional.filter(r -> r.isBasin() == basin).orElse(null);
    }

    /**
     * 空罐注入新流体时调用：查配方并触发 {@link CastingEvent}（onCastingStart，可取消）。
     * 事件仅在真正注入（EXECUTE）时触发——simulate 探测不产生副作用（security_review）。
     *
     * @return 配方所需液体量；无配方/事件取消返回 0
     */
    public int initNewCasting(net.minecraft.world.level.material.Fluid fluid, boolean setNewRecipe) {
        CastingRecipe recipe = findRecipe(getItem(SLOT_CAST), fluid);
        if (recipe != null) {
            if (setNewRecipe) {
                // 事件：附属可取消浇铸（流体留在上游槽内）或修改产出
                CastingEvent event = new CastingEvent(level, worldPosition, getItem(SLOT_CAST),
                        new FluidStack(fluid, recipe.getFluidAmount()), recipe.getResult());
                NeoForge.EVENT_BUS.post(event);
                if (event.isCanceled()) {
                    return 0;
                }
                this.recipe = recipe;
            }
            return recipe.getFluidAmount();
        }
        return 0;
    }

    /* ---------- 流体交互（1:1 旧版 FluidHandlerCasting） ---------- */

    /** 注入流体：空罐先查配方定容量；输出槽有物或输出槽满则拒绝。 */
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || !getItem(SLOT_OUTPUT).isEmpty()) {
            return 0;
        }
        if (tank.getFluidAmount() == 0) {
            int capacity = initNewCasting(resource.getFluid(), action.execute());
            if (capacity > 0) {
                if (action.execute()) {
                    // 设定容量后正常填充（1:1 旧版 FluidHandlerCasting）
                    tank.setCapacity(capacity);
                    int filled = tank.fill(resource, action);
                    if (filled > 0) {
                        syncToClient();
                    }
                    return filled;
                }
                // 空罐模拟：从 0 起可容纳 min(capacity, amount)
                return Math.min(capacity, resource.getAmount());
            }
        }
        return tank.fill(resource, action);
    }

    /** 抽取流体：抽空后重置浇铸状态（1:1 旧版 FluidHandlerCasting.drain）。 */
    public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        if (resource.isEmpty() || tank.getFluidAmount() == 0) {
            return FluidStack.EMPTY;
        }
        FluidStack fluid = tank.getFluid();
        if (!fluid.is(resource.getFluid())) {
            return FluidStack.EMPTY;
        }
        return drain(resource.getAmount(), action);
    }

    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        FluidStack drained = tank.drain(maxDrain, action);
        if (!drained.isEmpty() && action.execute()) {
            syncToClient();
            if (tank.getFluidAmount() == 0) {
                reset();
            }
        }
        return drained;
    }

    /* ---------- 玩家交互（1:1 旧版 interact） ---------- */

    public void interact(Player player) {
        // 有液体时不可交互
        if (tank.getFluidAmount() > 0) {
            return;
        }
        // 全空 → 放入玩家手持的当前物品（1 个，1:1 旧版 stackSizeLimit=1）
        if (getItem(SLOT_CAST).isEmpty() && getItem(SLOT_OUTPUT).isEmpty()) {
            ItemStack stack = player.getInventory().removeItem(player.getInventory().selected, 1);
            setItem(SLOT_CAST, stack);
        }
        // 否则取出：优先输出槽
        else {
            int slot = !getItem(SLOT_OUTPUT).isEmpty() ? SLOT_OUTPUT : SLOT_CAST;
            ItemStack stack = getItem(slot);
            player.getInventory().placeItemBackInInventory(stack);
            setItem(slot, ItemStack.EMPTY);
            if (slot == SLOT_OUTPUT) {
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            }
        }
    }

    /* ---------- 冷却逻辑（1:1 旧版 update） ---------- */

    public static void tick(Level level, BlockPos pos, BlockState state, CastingBlockEntity be) {
        if (be.recipe == null) {
            return;
        }
        // 满液才开始计时
        if (be.tank.getFluidAmount() == be.tank.getCapacity() && be.tank.getCapacity() > 0) {
            be.timer++;
            if (!level.isClientSide) {
                if (be.timer >= be.recipe.getTime()) {
                    be.finishCasting();
                }
            } else if (level.random.nextFloat() > 0.9F) {
                // 客户端冒烟粒子（1:1 旧版 SMOKE_NORMAL）
                level.addParticle(ParticleTypes.SMOKE,
                        pos.getX() + level.random.nextDouble(), pos.getY() + 1.1D,
                        pos.getZ() + level.random.nextDouble(), 0, 0, 0);
            }
        }
    }

    /** 冷却完成：触发 CastingEvent（onCastingFinish，可改产出）并落输出（1:1 旧版 OnCasted）。 */
    private void finishCasting() {
        CastingEvent event = new CastingEvent(level, worldPosition, getItem(SLOT_CAST),
                tank.getFluid(), recipe.getResult());
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            // 取消产出：液体与模具保留、冷却重来（与事件文档"取消后流体留在槽内"一致，
            // security_review：不得 reset/消耗模具造成流体凭空消失）
            timer = 0;
            return;
        }
        ItemStack output = event.getOutput();

        // 消耗模具（1:1 旧版 consumeCast）
        if (recipe.consumesCast()) {
            setItem(SLOT_CAST, ItemStack.EMPTY);
        }
        // 互换输出（1:1 旧版 switchOutputs：模具保留在 0 槽，产物入 1 槽）
        if (recipe.switchOutputs()) {
            setItem(SLOT_OUTPUT, getItem(SLOT_CAST));
            setItem(SLOT_CAST, output);
        } else {
            setItem(SLOT_OUTPUT, output);
        }
        // 滋滋声（1:1 旧版 Sounds.sizzle，用熔岩声替代）+ 火焰粒子
        level.playSound(null, worldPosition, SoundEvents.LAVA_AMBIENT, SoundSource.AMBIENT, 0.5F, 4.0F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME,
                    worldPosition.getX() + 0.5D, worldPosition.getY() + 1.1D, worldPosition.getZ() + 0.5D,
                    5, 0.25D, 0.0125D, 0.25D, 0.005D);
        }

        reset();
        level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        syncToClient();
    }

    /** 完全重置浇铸状态（1:1 旧版 reset）。 */
    public void reset() {
        timer = 0;
        recipe = null;
        tank.setCapacity(0);
        tank.setFluid(FluidStack.EMPTY);
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
            syncToClient();
        }
    }

    /* ---------- 保存/同步 ---------- */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        ContainerHelper.saveAllItems(tag, inventory.getItems(), registries);
        tank.writeToNBT(registries, tag);
        tag.putInt("timer", timer);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        ContainerHelper.loadAllItems(tag, inventory.getItems(), registries);
        tank.readFromNBT(registries, tag);
        // 恢复配方（1:1 旧版 updateFluidTo：按罐内流体重查）
        if (!tank.getFluid().isEmpty()) {
            recipe = findRecipe(getItem(SLOT_CAST), tank.getFluid().getFluid());
        }
        // 范围校验防篡改 NBT：timer 钳到 [0, 配方时间-1]，避免加载后立即完成（security_review）
        int savedTimer = tag.getInt("timer");
        timer = recipe == null ? 0 : Math.max(0, Math.min(savedTimer, recipe.getTime() - 1));
    }

    /** 全量同步（1.21.1 无自定义 packet 场景的标准做法：update tag 携带全部 NBT）。 */
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }

    /* ---------- capability（1:1 旧版 FluidHandlerCasting：注入触发配方初始化） ---------- */

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CASTING_TABLE.get(),
                (be, direction) -> new CastingFluidHandler(be));
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CASTING_BASIN.get(),
                (be, direction) -> new CastingFluidHandler(be));
    }

    /** 浇铸流体 handler（1:1 旧版 FluidHandlerCasting 语义，委托本 BE）。 */
    private static class CastingFluidHandler implements IFluidHandler {

        private final CastingBlockEntity be;

        CastingFluidHandler(CastingBlockEntity be) {
            this.be = be;
        }

        @Override
        public int getTanks() {
            return be.tank.getTanks();
        }

        @Override
        public int getTankCapacity(int tank) {
            return be.tank.getTankCapacity(tank);
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return be.tank.getFluidInTank(tank);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return be.fill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return be.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return be.drain(maxDrain, action);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return be.tank.isFluidValid(tank, stack);
        }
    }
}
