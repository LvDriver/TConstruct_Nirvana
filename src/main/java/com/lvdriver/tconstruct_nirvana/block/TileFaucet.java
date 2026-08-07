package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

/**
 * 龙头方块实体（1:1 移植自 Tinkers' Antique {@code TileFaucet}）。
 *
 * <p>从朝向面容器抽取一锭量（144mb，1:1 {@code TRANSACTION_AMOUNT}）并缓冲，
 * 每 tick 向下浇注最多 {@link #LIQUID_TRANSFER}（6mb，1:1 {@code Config.liquidTransferRate}）；
 * 缓冲排空后若未停止则自动续抽。红石上升沿延迟 2 tick 触发（1:1 旧版
 * scheduleUpdate）。液体缓冲在 NBT 中持久化（存档期间保持浇注状态）。</p>
 */
public class TileFaucet extends BlockEntity {

    /** 每 tick 最大传输量（1:1 旧版 Config.liquidTransferRate = 6）。 */
    public static final int LIQUID_TRANSFER = 6;
    /** 单次交易量：一锭（1:1 旧版 Material.VALUE_Ingot = 144）。 */
    public static final int TRANSACTION_AMOUNT = Material.VALUE_Ingot;

    /** 抽液方向（浇注开始时缓存，1:1 旧版）。 */
    public Direction direction;
    public boolean isPouring;
    public boolean stopPouring;
    /** 已抽取待浇注的液体缓冲。 */
    public FluidStack drained = FluidStack.EMPTY;
    public boolean lastRedstoneState;

    public TileFaucet(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        reset();
    }

    /** 右键触发：开始浇注 / 再点停止（1:1 旧版 activate）。 */
    public boolean activate() {
        BlockState state = getBlockState();
        if (!state.hasProperty(BlockFaucet.FACING)) {
            return false;
        }
        // 正在浇注 → 请求停止（当前缓冲排完即停）
        if (isPouring) {
            stopPouring = true;
            return true;
        }

        direction = state.getValue(BlockFaucet.FACING);
        doTransfer();

        if (!drained.isEmpty()) {
            level.playSound(null, worldPosition, SoundEvents.LEVER_CLICK, SoundSource.BLOCKS,
                    1.0F, 0.8F + 0.4F * level.random.nextFloat());
        }
        return isPouring;
    }

    public void handleRedstone(boolean hasSignal) {
        if (hasSignal != lastRedstoneState) {
            lastRedstoneState = hasSignal;
            if (hasSignal) {
                level.scheduleTick(worldPosition, getBlockState().getBlock(), 2);
            }
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, TileFaucet faucet) {
        if (level.isClientSide) {
            return;
        }
        if (!faucet.isPouring) {
            return;
        }

        if (!faucet.drained.isEmpty()) {
            // 缓冲排空 → 未停止则续抽，否则停止
            if (faucet.drained.getAmount() <= 0) {
                faucet.drained = FluidStack.EMPTY;
                if (!faucet.stopPouring) {
                    faucet.doTransfer();
                } else {
                    faucet.reset();
                }
                if (!faucet.isPouring) {
                    level.playSound(null, pos, SoundEvents.LAVA_POP, SoundSource.BLOCKS,
                            0.2F, 0.8F + 0.4F * level.random.nextFloat());
                }
            } else {
                // 浇注一步
                faucet.pour();
            }
        }
    }

    /** 从上方/侧方容器抽取一锭量（1:1 旧版 doTransfer）。 */
    protected void doTransfer() {
        if (!drained.isEmpty()) {
            return;
        }
        IFluidHandler toDrain = getFluidHandler(worldPosition.relative(direction), direction.getOpposite());
        IFluidHandler toFill = getFluidHandler(worldPosition.below(), Direction.UP);
        if (toDrain == null) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Faucet] doTransfer: no fluid source at {} (dir={})", worldPosition.relative(direction), direction);
        }
        if (toFill == null) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Faucet] doTransfer: no fluid target below at {}", worldPosition.below());
        }
        if (toDrain != null && toFill != null) {
            // 能否抽取（非气体）
            FluidStack drained = toDrain.drain(TRANSACTION_AMOUNT, IFluidHandler.FluidAction.SIMULATE);
            if (drained.isEmpty()) {
                com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                        "[Faucet] doTransfer: source has no drainable fluid ({}mb total)",
                        toDrain.getFluidInTank(0).getAmount());
            }
            // 气体过滤（1:1 旧版 Config.drainGaseousFluids=false 默认；1.21.1 无 isGaseous，按负密度判定）
            if (!drained.isEmpty() && drained.getFluid().getFluidType().getDensity() >= 0) {
                // 能否注入
                int filled = toFill.fill(drained, IFluidHandler.FluidAction.SIMULATE);
                if (filled == 0) {
                    com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                            "[Faucet] doTransfer: target refused fluid {} (simulate fill 0)", drained.getFluid());
                }
                if (filled > 0) {
                    // 抽取并缓冲，开始浇注
                    this.drained = toDrain.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    this.isPouring = true;
                    pour();
                    sync();
                    return;
                }
            }
        }
        // 抽液失败
        reset();
    }

    /** 缓冲液体向下浇注一步（最多 6mb，1:1 旧版 pour）。 */
    protected void pour() {
        if (drained.isEmpty()) {
            return;
        }
        IFluidHandler toFill = getFluidHandler(worldPosition.below(), Direction.UP);
        if (toFill != null) {
            FluidStack fillStack = drained.copyWithAmount(Math.min(drained.getAmount(), LIQUID_TRANSFER));
            int filled = toFill.fill(fillStack, IFluidHandler.FluidAction.SIMULATE);
            if (filled == 0) {
                com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                        "[Faucet] pour: target refused {} (simulate fill 0)", fillStack.getFluid());
            }
            if (filled > 0) {
                this.drained = drained.copyWithAmount(drained.getAmount() - filled);
                toFill.fill(fillStack.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            }
        } else {
            // 下方容器丢失：重置，缓冲液体丢失（1:1 旧版）
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Faucet] pour: target below lost at {}", worldPosition.below());
            reset();
        }
    }

    protected void reset() {
        isPouring = false;
        stopPouring = false;
        drained = FluidStack.EMPTY;
        direction = Direction.DOWN;
        lastRedstoneState = false;
        sync();
    }

    private void sync() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
        }
    }

    protected IFluidHandler getFluidHandler(BlockPos pos, Direction side) {
        return level.getCapability(Capabilities.FluidHandler.BLOCK, pos, side);
    }

    /* ---------- 保存/同步 ---------- */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!drained.isEmpty()) {
            tag.put("drained", drained.save(registries));
            tag.putInt("direction", direction.get3DDataValue());
            tag.putBoolean("stop", stopPouring);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        drained = tag.contains("drained")
                ? FluidStack.parseOptional(registries, tag.getCompound("drained"))
                : FluidStack.EMPTY;
        if (!drained.isEmpty()) {
            isPouring = true;
            // 范围校验防损坏 NBT 越界（security_review）
            int dir = tag.getInt("direction");
            direction = dir >= 0 && dir < 6 ? Direction.from3DDataValue(dir) : Direction.DOWN;
            stopPouring = tag.getBoolean("stop");
        } else {
            reset();
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }
}
