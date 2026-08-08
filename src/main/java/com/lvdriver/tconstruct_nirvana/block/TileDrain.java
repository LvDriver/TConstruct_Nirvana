package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.smeltery.SmelteryTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 排液口方块实体（1:1 移植自 Tinkers' Antique {@code TileDrain}）。
 *
 * <p>冶炼炉附属方块：流体 capability 代理冶炼炉主罐（{@link TileSmeltery#getTank()}）。
 * 1:1 旧版行为：外部管道（side == null）经只读包装（可抽不可注，防止管道把
 * 液体灌进冶炼炉底部层）；桶交互（side != null）走完整主罐。</p>
 */
public class TileDrain extends TileSmelteryComponent {

    public TileDrain(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        // 调试：输出排液口实际位置（重测时与龙头 [Faucet] no fluid source 的源位置对照）
        if (pos != null) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Drain] created at {} (state={})", pos, state);
        }
    }

    /** 所属冶炼炉主机（无主机/失效返回 null）。 */
    @Nullable
    public TileSmeltery getSmeltery() {
        if (level == null || !hasValidMaster() || !level.isLoaded(getMasterPosition())) {
            if (level != null && !level.isClientSide) {
                com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                        "[Drain] getSmeltery null at {} (master={}, loaded={})",
                        worldPosition, getMasterPosition(), level.isLoaded(getMasterPosition()));
            }
            return null;
        }
        return level.getBlockEntity(getMasterPosition()) instanceof TileSmeltery smeltery ? smeltery : null;
    }

    /** capability 注册（1.21.1 范式：BE 不再覆写 getCapability）。 */
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.DRAIN.get(),
                (be, side) -> {
                    TileSmeltery smeltery = be.getSmeltery();
                    if (smeltery == null) {
                        return null;
                    }
                    SmelteryTank tank = smeltery.getTank();
                    // 炉子未成型（getTank null）→ 返回 null，避免 ExtractOnlyWrapper(null) 在
                    // 龙头/管道 drain 时 NPE；调用方（龙头 doTransfer）会按取不到液处理
                    if (tank == null) {
                        return null;
                    }
                    // 外部管道：只读包装（1:1 旧版 FluidHandlerExtractOnlyWrapper）
                    if (side == null) {
                        return new ExtractOnlyWrapper(tank);
                    }
                    return tank;
                });
    }

    /** 只读流体包装（可抽不可注，1:1 旧版 FluidHandlerExtractOnlyWrapper）。 */
    private static class ExtractOnlyWrapper implements IFluidHandler {

        private final IFluidHandler parent;

        ExtractOnlyWrapper(IFluidHandler parent) {
            this.parent = parent;
        }

        @Override
        public int getTanks() {
            return parent.getTanks();
        }

        @Override
        public int getTankCapacity(int tank) {
            return parent.getTankCapacity(tank);
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return parent.getFluidInTank(tank);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            return 0;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return parent.drain(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return parent.drain(maxDrain, action);
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return false;
        }
    }
}
