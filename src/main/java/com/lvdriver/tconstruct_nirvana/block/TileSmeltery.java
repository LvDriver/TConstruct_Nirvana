package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.api.event.MeltingEvent;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import com.lvdriver.tconstruct_nirvana.multiblock.MultiblockDetection;
import com.lvdriver.tconstruct_nirvana.multiblock.MultiblockSmeltery;
import com.lvdriver.tconstruct_nirvana.recipe.AlloyRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.MeltingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.MeltingRecipeInput;
import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import com.lvdriver.tconstruct_nirvana.smeltery.SmelteryTank;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.List;

/**
 * 冶炼炉控制器方块实体（1:1 移植自 Tinkers' Antique {@code TileSmeltery}）。
 *
 * <p>核心逻辑：每秒检测多方块结构（无天花板、需至少一个 seared_tank）；
 * 成型后每 4 tick 加热物品（熔炼配方 → 流体）、合成合金、熔炼炉内生物
 * （默认产出血液）；无燃料时从结构内储罐抽取岩浆。物品栏大小 = 结构内部
 * 宽×高×深（最多 9×9×9），流体容量 = 槽数 × 8 锭。</p>
 */
public class TileSmeltery extends TileHeatingStructureFuelTank
        implements SmelteryTank.TankChangeListener, net.minecraft.world.MenuProvider {

    /** 每槽流体容量（1:1 旧版 CAPACITY_PER_BLOCK = 锭价值 × 8）。 */
    protected static final int CAPACITY_PER_BLOCK = Material.VALUE_Ingot * 8;

    /** 每 tick 最多合成的合金量（mb，1:1 旧版 ALLOYING_PER_TICK=10）。 */
    protected static final int ALLOYING_PER_TICK = 10;

    /** 加热 tick 间隔（1:1 旧版 Config.heatItemsTickrateSmeltery=4）。 */
    protected static final int HEAT_TICKRATE = 4;

    // NBT tags
    public static final String TAG_INSIDEPOS = "insidePos";

    /** 炉内液体。 */
    protected SmelteryTank liquids;

    protected int tick;

    private BlockPos insideCheck; // 上次检查的炉内位置
    private int fullCheckCounter = 0;

    public TileSmeltery(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SMELTERY.get(), pos, state, 0);
        setMultiblock(new MultiblockSmeltery(this));
        liquids = new SmelteryTank(this);
    }

    /* 服务端 tick（1:1 旧版 update） */

    public void tickServer() {
        if (isClientWorld()) {
            return;
        }

        if (!isActive()) {
            // 未成型：每秒检查一次结构
            if (tick == 0) {
                checkMultiblockStructure();
            }
            isHeating = false;
        } else {
            // 已成型：加热/合金/燃料
            if (tick == 0) {
                interactWithEntitiesInside();
            }
            if (tick % HEAT_TICKRATE == 0) {
                heatItems();
                alloyAlloys();
            }

            if (needsFuel) {
                consumeFuel();
            }

            // 逐渐检查炉内是否被堵塞（性能考虑，每 15s 全检）
            if (tick == 0) {
                if (++fullCheckCounter >= 15) {
                    fullCheckCounter = 0;
                    checkMultiblockStructure();
                } else {
                    updateInsideCheck();
                    if (!level.isEmptyBlock(insideCheck)) {
                        // 炉内被堵 → 结构失效
                        setInvalid();
                        insideCheck = null;
                        BlockState state = level.getBlockState(worldPosition);
                        level.sendBlockUpdated(worldPosition, state, state, 3);
                    } else {
                        progressInsideCheck();
                    }
                }
            }
        }

        tick = (tick + 1) % 20;
    }

    private void updateInsideCheck() {
        if (insideCheck == null
                || insideCheck.getX() < minPos.getX()
                || insideCheck.getY() < minPos.getY()
                || insideCheck.getZ() < minPos.getZ()
                || insideCheck.getX() > maxPos.getX()
                || insideCheck.getY() > maxPos.getY()
                || insideCheck.getZ() > maxPos.getZ()) {
            insideCheck = minPos;
        }
    }

    private void progressInsideCheck() {
        insideCheck = insideCheck.offset(1, 0, 0);
        if (insideCheck.getX() > maxPos.getX()) {
            insideCheck = new BlockPos(minPos.getX(), insideCheck.getY(), insideCheck.getZ() + 1);
            if (insideCheck.getZ() > maxPos.getZ()) {
                insideCheck = new BlockPos(minPos.getX(), insideCheck.getY() + 1, minPos.getZ());
            }
        }
    }

    /* 熔炼 */

    @Override
    protected void updateHeatRequired(int index) {
        ItemStack stack = getStackInSlot(index);
        if (!stack.isEmpty()) {
            MeltingRecipe melting = findMeltingRecipe(stack);
            if (melting != null) {
                setHeatRequiredForSlot(index, Math.max(5, melting.getUsableTemperature()));

                // 立即尝试补燃料（1:1 旧版）
                if (!hasFuel()) {
                    consumeFuel();
                }
                return;
            }
        }
        setHeatRequiredForSlot(index, 0);
    }

    /** 查询熔炼配方（物品 tag 匹配 + 部件按材料组件匹配）。 */
    private MeltingRecipe findMeltingRecipe(ItemStack stack) {
        // 部件：按材料组件熔回对应金属（1:1 旧版仅 stone 部件注册，本版扩展为
        // 全部带流体关联的材料，devlog 记录差异）
        if (stack.is(TConTags.TOOL_PARTS) && stack.getItem() instanceof ToolPart part) {
            Material material = part.getMaterial(stack);
            if (material != null && material.hasFluid()) {
                Fluid fluid = ModFluids.findFluid(material.getFluidId());
                if (fluid != null) {
                    // 熔出量 = 部件成本（mb，1:1 旧版 cost 语义），温度按价值推算
                    int cost = part.getCost();
                    return new MeltingRecipe(
                            new com.lvdriver.tconstruct_nirvana.util.ItemTagMatch(TConTags.TOOL_PARTS, cost),
                            new FluidStack(fluid, cost),
                            MeltingRecipe.calcTemperature(fluid.getFluidType().getTemperature(), cost));
                }
            }
            return null;
        }

        // 普通物品：RecipeManager 遍历熔炼配方
        if (level != null) {
            for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.MELTING_TYPE.get())) {
                MeltingRecipe recipe = holder.value();
                if (recipe.matches(new MeltingRecipeInput(stack), level)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    @Override
    protected boolean onItemFinishedHeating(ItemStack stack, int slot) {
        // 满液 → 报错（UI 显示满状态）并返回 false
        if (liquids.getFluidAmount() >= liquids.getCapacity()) {
            itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
            return false;
        }
        MeltingRecipe recipe = findMeltingRecipe(stack);
        if (recipe == null) {
            return false;
        }

        // 事件：允许附属修改产物/温度/量或取消
        MeltingEvent event = new MeltingEvent(level, worldPosition, stack,
                recipe.getResult(), recipe.getTemperature(), recipe.getResult().getAmount());
        NeoForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return false;
        }

        FluidStack fluidStack = event.getFluid();
        if (fluidStack.isEmpty()) {
            return false;
        }
        int amount = Math.max(0, event.getAmount());
        FluidStack toFill = fluidStack.copyWithAmount(amount);
        int filled = liquids.fill(toFill, IFluidHandler.FluidAction.SIMULATE);

        if (filled == toFill.getAmount()) {
            liquids.fill(toFill, IFluidHandler.FluidAction.EXECUTE);
            setInventorySlotContents(slot, ItemStack.EMPTY);
            return true;
        } else {
            // 放不下 → 报错状态
            itemTemperatures[slot] = itemTempRequired[slot] * 2 + 1;
        }
        return false;
    }

    /* 实体熔炼（1:1 旧版 interactWithEntitiesInside） */

    protected void interactWithEntitiesInside() {
        if (info == null) {
            return;
        }
        AABB bb = info.getBoundingBox().contract(-2, -1, -2).move(-1, 0, -1);

        List<Entity> entities = level.getEntitiesOfClass(Entity.class, bb);
        for (Entity entity : entities) {
            // 物品实体：可熔则拾取进槽
            if (entity instanceof ItemEntity itemEntity) {
                if (findMeltingRecipe(itemEntity.getItem()) != null) {
                    ItemStack stack = itemEntity.getItem();
                    for (int i = 0; i < this.getSizeInventory(); i++) {
                        if (!isStackInSlot(i)) {
                            ItemStack invStack = stack.copy();
                            stack.shrink(1);
                            invStack.setCount(1);
                            this.setInventorySlotContents(i, invStack);
                        }
                        if (stack.isEmpty()) {
                            entity.discard();
                            break;
                        }
                    }
                }
            }
            // 活体生物：有燃料才熔（默认产血，1:1 旧版 blood 20mb）
            else if (entity instanceof LivingEntity living && living.isAlive()) {
                if (hasFuel()) {
                    Fluid blood = ModFluids.findFluid("blood");
                    if (blood != null) {
                        if (living.hurt(level.damageSources().lava(), 2f)) {
                            liquids.fill(new FluidStack(blood, 20), IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                } else {
                    needsFuel = true;
                }
            }
        }
    }

    /* 合金（1:1 旧版 alloyAlloys） */

    protected void alloyAlloys() {
        if (liquids.getFluidAmount() > liquids.getCapacity()) {
            return;
        }
        if (level == null) {
            return;
        }
        for (var holder : level.getRecipeManager().getAllRecipesFor(ModRecipeTypes.ALLOY_TYPE.get())) {
            AlloyRecipe recipe = holder.value();
            // 可应用次数（本 tick 上限 ALLOYING_PER_TICK）
            int matched = recipe.matchesAmount(new com.lvdriver.tconstruct_nirvana.recipe.AlloyRecipeInput(liquids.getFluids()));
            if (matched > ALLOYING_PER_TICK) {
                matched = ALLOYING_PER_TICK;
            }
            while (matched > 0) {
                // 先模拟验证：所有输入可抽、输出可填（防止扣了输入却填不满输出）
                boolean canApply = true;
                for (net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient need : recipe.getInputs()) {
                    FluidStack simulated = drainMatching(need, need.amount(), IFluidHandler.FluidAction.SIMULATE);
                    if (simulated.isEmpty() || simulated.getAmount() != need.amount()) {
                        canApply = false;
                        break;
                    }
                }
                if (!canApply) {
                    break;
                }
                // 输出空间检查（simulate）
                FluidStack out = recipe.getResult();
                if (liquids.fill(out.copy(), IFluidHandler.FluidAction.SIMULATE) != out.getAmount()) {
                    break;
                }
                // 实际执行：扣输入 → 加输出（EXECUTE 校验实际扣量，不足则回滚已扣）
                java.util.List<FluidStack> drainedBack = new java.util.ArrayList<>();
                boolean executed = true;
                for (net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient need : recipe.getInputs()) {
                    FluidStack drained = drainMatching(need, need.amount(), IFluidHandler.FluidAction.EXECUTE);
                    if (drained.isEmpty() || drained.getAmount() != need.amount()) {
                        executed = false;
                        break;
                    }
                    drainedBack.add(drained);
                }
                if (!executed) {
                    // 回滚已扣输入（放回炉内）
                    for (FluidStack rollback : drainedBack) {
                        liquids.fill(rollback, IFluidHandler.FluidAction.EXECUTE);
                    }
                    break;
                }
                liquids.fill(out.copy(), IFluidHandler.FluidAction.EXECUTE);
                matched--;
            }
        }
    }

    /** 从炉内抽取与输入匹配的流体（1:1 旧版按输入依次 drain）。 */
    private FluidStack drainMatching(net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient need, int amount, IFluidHandler.FluidAction action) {
        for (FluidStack liquid : liquids.getFluids()) {
            if (need.test(liquid)) {
                return liquids.drain(liquid.copyWithAmount(amount), action);
            }
        }
        return FluidStack.EMPTY;
    }

    /* 多块结构 */

    @Override
    protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
        super.updateStructureInfo(structure);
        this.liquids.setCapacity(getSizeInventory() * CAPACITY_PER_BLOCK);
    }

    @Override
    protected boolean hasCeiling() {
        return false;
    }

    @Override
    protected int getUpdatedInventorySize(int width, int height, int depth) {
        return width * height * depth;
    }

    /* 液体访问 */

    public SmelteryTank getTank() {
        return isActive() ? liquids : null;
    }

    @Override
    public void onTankChanged(List<FluidStack> fluids, FluidStack changed) {
        // 服务端：标记保存 + 通知客户端（GUI 通过 Menu 数据同步）
        if (isServerWorld()) {
            setChanged();
        }
    }

    /* GUI（1:1 旧版 ContainerSmeltery.createContainer） */

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("gui.smeltery.name");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory playerInventory, net.minecraft.world.entity.player.Player player) {
        return new com.lvdriver.tconstruct_nirvana.gui.ContainerSmeltery(id, playerInventory, this);
    }

    /**
     * 点击液体装桶（GUI 调用）：抽指定层 1000mb 液体，消耗玩家背包一个空桶
     * 并放入满桶（1:1 旧版 GuiSmeltery.handleTankClick）。
     *
     * <p>防复制（security_review）：要求该层液体 ≥ 1000mb 才允许装桶——
     * {@code FluidUtil.getFilledBucket} 对任意非零量都返回满桶，不足 1 桶时
     * 装桶会凭空增值（999mb → 1 满桶 → 倒回 → 再装）。</p>
     */
    public boolean fillBucketFromTank(net.minecraft.world.entity.player.Player player, int layerIndex) {
        if (player == null || player.level().isClientSide) {
            return false;
        }
        if (layerIndex < 0 || layerIndex >= liquids.getFluids().size()) {
            return false;
        }
        FluidStack liquid = liquids.getFluids().get(layerIndex);
        // 不足 1 桶拒绝装桶（防 999mb 换满桶复制）
        if (liquid.getAmount() < 1000) {
            return false;
        }
        int amount = 1000;

        // 找玩家背包里的空桶
        net.minecraft.world.entity.player.Inventory inv = player.getInventory();
        int slot = -1;
        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.is(net.minecraft.world.item.Items.BUCKET)) {
                slot = i;
                break;
            }
        }
        if (slot < 0) {
            return false;
        }

        // 抽液 → 装桶（按点击层抽取，非底层）
        FluidStack toDrain = liquid.copyWithAmount(amount);
        FluidStack drained = liquids.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
        if (drained.isEmpty()) {
            return false;
        }
        ItemStack bucket = net.neoforged.neoforge.fluids.FluidUtil.getFilledBucket(drained);
        if (bucket.isEmpty()) {
            liquids.fill(drained, IFluidHandler.FluidAction.EXECUTE); // 回滚
            return false;
        }
        inv.setItem(slot, bucket);
        setChanged();
        return true;
    }

    /* NBT */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        liquids.writeToNBT(tag, registries);
        if (insideCheck != null) {
            tag.putLong(TAG_INSIDEPOS, insideCheck.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        liquids.readFromNBT(tag, registries);
        insideCheck = tag.contains(TAG_INSIDEPOS) ? BlockPos.of(tag.getLong(TAG_INSIDEPOS)) : null;
    }
}
