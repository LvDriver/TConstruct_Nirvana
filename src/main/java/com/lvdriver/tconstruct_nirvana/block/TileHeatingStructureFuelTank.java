package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.multiblock.MultiblockDetection;
import com.lvdriver.tconstruct_nirvana.smeltery.SmelteryFuels;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * 加热结构 + 燃料罐（1:1 移植自 Tinkers' Antique
 * {@code TileHeatingStructureFuelTank}）。
 *
 * <p>冶炼炉结构内的 seared_tank 即燃料罐：无燃料时从任一燃料罐抽取
 * 50mb 燃料（岩浆/烈焰血，见 {@link SmelteryFuels}），换算成燃料 tick
 * 数与炉温。结构成型时按内部尺寸调整物品栏大小。</p>
 */
public abstract class TileHeatingStructureFuelTank extends TileHeatingStructure {

    // NBT Tags
    public static final String TAG_TANKS = "tanks";
    public static final String TAG_FUEL_QUALITY = "fuelQuality";
    public static final String TAG_CURRENT_FUEL = "currentFuel";
    public static final String TAG_CURRENT_TANK = "currentTank";

    /** 单次燃料消耗量（mb，1:1 旧版 50）。 */
    public static final int FUEL_PER_CONSUMPTION = 50;

    /** 一次消耗得到的燃料 tick 数（GUI 燃料百分比基准）。 */
    public int fuelQuality;

    /** 结构内所有燃料罐位置。 */
    public List<BlockPos> tanks = new ArrayList<>();
    /** 当前使用的燃料罐位置。 */
    public BlockPos currentTank;
    /** 最近消耗的燃料（GUI 显示）。 */
    public FluidStack currentFuel;

    public TileHeatingStructureFuelTank(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize) {
        super(type, pos, state, inventorySize);
    }

    @Override
    protected void consumeFuel() {
        // 还有燃料就不用消耗
        if (hasFuel()) {
            return;
        }

        // 找有燃料的罐
        searchForFuel();

        if (currentTank != null) {
            if (level.getBlockEntity(currentTank) instanceof TileTank tankTe) {
                IFluidHandler tank = tankTe.getInternalTank();
                FluidStack liquid = tank.getFluidInTank(0);
                if (!liquid.isEmpty()) {
                    FluidStack in = liquid.copy();
                    int bonusFuel = SmelteryFuels.consumeSmelteryFuel(in);
                    int amount = liquid.getAmount() - in.getAmount();
                    FluidStack drained = tank.drain(amount, IFluidHandler.FluidAction.SIMULATE);

                    // 可以抽取 → 实际抽取并加燃料
                    if (!drained.isEmpty() && drained.getAmount() == amount) {
                        tank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
                        tankTe.onTankContentsChanged();
                        currentFuel = drained.copy();
                        fuelQuality = bonusFuel;
                        addFuel(bonusFuel, drained.getFluid().getFluidType().getTemperature() - 300);

                        // 通知客户端燃料/温度变化（GUI 同步走 Menu，无需发包）
                        if (isServerWorld()) {
                            setChanged();
                        }
                        return;
                    }
                }
                fuelQuality = 0;
            }
        }
    }

    /** 定位一个含燃料的罐（1:1 旧版 searchForFuel）。 */
    private void searchForFuel() {
        // 当前罐还有燃料吗？
        if (currentTank != null && hasTankWithFuel(currentTank, currentFuel)) {
            return;
        }

        // 找同种燃料的其他罐
        for (BlockPos pos : tanks) {
            if (hasTankWithFuel(pos, currentFuel)) {
                currentTank = pos;
                return;
            }
        }

        // 换新燃料再找
        currentFuel = null;
        for (BlockPos pos : tanks) {
            if (hasTankWithFuel(pos, null)) {
                currentTank = pos;
                return;
            }
        }

        currentTank = null;
    }

    private boolean hasTankWithFuel(BlockPos pos, FluidStack preference) {
        IFluidHandler tank = getTankAt(pos);
        if (tank != null) {
            FluidStack fluid = tank.getFluidInTank(0);
            if (!fluid.isEmpty() && fluid.getAmount() > 0 && SmelteryFuels.isSmelteryFuel(fluid)) {
                if (preference == null || fluid.is(preference.getFluid())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 结构成型：记录燃料罐 + 调整物品栏（1:1 旧版 updateStructureInfo）。 */
    @Override
    protected void updateStructureInfo(MultiblockDetection.MultiblockStructure structure) {
        // 记录所有燃料罐
        tanks.clear();
        for (BlockPos pos : structure.blocks) {
            if (level.getBlockState(pos).getBlock() == ModBlocks.SEARED_TANK.get()) {
                tanks.add(pos);
            }
        }

        int inventorySize = getUpdatedInventorySize(structure.xd, structure.yd, structure.zd);

        // 新结构更小 → 弹出放不下的物品
        if (!level.isClientSide && this.getSizeInventory() > inventorySize) {
            for (int i = inventorySize; i < getSizeInventory(); i++) {
                if (!getStackInSlot(i).isEmpty()) {
                    dropItem(getStackInSlot(i));
                }
            }
        }

        resizeInventory(inventorySize);
    }

    /** 按结构尺寸计算物品栏大小（子类定义公式，冶炼炉 = 宽×高×深）。 */
    protected abstract int getUpdatedInventorySize(int width, int height, int depth);

    protected void dropItem(ItemStack stack) {
        BlockPos pos = this.worldPosition.relative(level.getBlockState(worldPosition).getValue(
                com.lvdriver.tconstruct_nirvana.block.BlockMultiblockController.FACING));
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
        level.addFreshEntity(entity);
    }

    private IFluidHandler getTankAt(BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TileTank tank) {
            return tank.getInternalTank();
        }
        return null;
    }

    /* GUI 辅助 */

    /** 槽位加热进度（-1 = 炉温不足，1:1 旧版 getHeatingProgress）。 */
    public float getHeatingProgress(int index) {
        if (index < 0 || index > getSizeInventory() - 1) {
            return -1f;
        }
        if (!canHeat(index)) {
            return -1f;
        }
        return getProgress(index);
    }

    /* NBT */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_FUEL_QUALITY, fuelQuality);
        if (currentTank != null) {
            tag.putLong(TAG_CURRENT_TANK, currentTank.asLong());
        }
        tag.putLongArray(TAG_TANKS, tanks.stream().mapToLong(BlockPos::asLong).toArray());
        if (currentFuel != null) {
            tag.put(TAG_CURRENT_FUEL, currentFuel.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuelQuality = tag.getInt(TAG_FUEL_QUALITY);
        currentTank = tag.contains(TAG_CURRENT_TANK) ? BlockPos.of(tag.getLong(TAG_CURRENT_TANK)) : null;
        tanks.clear();
        for (long l : tag.getLongArray(TAG_TANKS)) {
            tanks.add(BlockPos.of(l));
        }
        currentFuel = tag.contains(TAG_CURRENT_FUEL) ? FluidStack.parseOptional(registries, tag.getCompound(TAG_CURRENT_FUEL)) : null;
    }
}
