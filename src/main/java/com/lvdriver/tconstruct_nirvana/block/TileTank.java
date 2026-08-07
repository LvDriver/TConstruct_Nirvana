package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

/**
 * seared 储罐方块实体（1:1 移植自 Tinkers' Antique {@code TileTank}）。
 *
 * <p>单格流体容器（4000mb），同时是冶炼炉的燃料罐/液体储罐：
 * 冶炼炉通过 {@link #getInternalTank()} 直接读写；外部管道经
 * {@code IFluidHandler} capability 交互。1.21.1 适配：
 * capability 用 {@code RegisterCapabilitiesEvent} 注册（非旧版 getCapability 覆写）。</p>
 */
public class TileTank extends TileSmelteryComponent {

    /** 容量 4 桶（1:1 旧版 Fluid.BUCKET_VOLUME*4 = 4000mb）。 */
    public static final int CAPACITY = 4000;

    protected FluidTank tank;

    /** 红石比较器信号（变化时才通知邻居，1:1 旧版）。 */
    private int lastStrength = -1;

    public TileTank(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TANK.get(), pos, state);
        this.tank = new FluidTank(CAPACITY);
    }

    /** 供冶炼炉直接读写（不走 capability 路径）。 */
    public IFluidHandler getInternalTank() {
        return tank;
    }

    public boolean containsFluid() {
        return !tank.getFluid().isEmpty();
    }

    public int getBrightness() {
        if (containsFluid()) {
            return tank.getFluid().getFluid().getFluidType().getLightLevel();
        }
        return 0;
    }

    public int comparatorStrength() {
        return 15 * tank.getFluidAmount() / tank.getCapacity();
    }

    /** capability 注册（1.21.1 范式：BE 不再覆写 getCapability）。 */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.FluidHandler.BLOCK,
                ModBlockEntities.TANK.get(),
                (be, direction) -> be.tank);
    }

    /** 液体变化 → 更新红石比较器（1:1 旧版 onTankContentsChanged）。 */
    public void onTankContentsChanged() {
        int newStrength = this.comparatorStrength();
        if (newStrength != lastStrength) {
            if (level != null) {
                level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            }
            this.lastStrength = newStrength;
        }
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tank.writeToNBT(registries, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        tank.readFromNBT(registries, tag);
    }
}
