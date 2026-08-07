package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 多方块主机方块实体基类（1:1 移植自 Tinkers' Antique {@code TileMultiblock}）。
 *
 * <p>持有多方块检测器（{@link MultiblockDetection} 子类），在 tick 或收到
 * servant 通知时重新检测结构；{@link #checkMultiblockStructure()} 成功后更新
 * {@code minPos/maxPos}（内部坐标）并通知附属方块绑定主机。</p>
 */
public abstract class TileMultiblock extends BlockEntity implements IMasterLogic {

    public static final String TAG_ACTIVE = "active";
    public static final String TAG_MINPOS = "minPos";
    public static final String TAG_MAXPOS = "maxPos";

    /** 最大内部尺寸（每轴，1:1 旧版 MAX_SIZE=9）。 */
    protected static final int MAX_SIZE = 9;

    /** 结构当前是否成型。 */
    protected boolean active;
    /** 上次检测到的结构信息。 */
    protected MultiblockDetection.MultiblockStructure info;
    /** 多方块检测器。 */
    protected MultiblockDetection multiblock;
    /** 结构内部最小坐标。 */
    protected BlockPos minPos;
    /** 结构内部最大坐标。 */
    protected BlockPos maxPos;

    public TileMultiblock(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** 构造时设置多方块检测器（1:1 旧版 setMultiblock）。 */
    protected void setMultiblock(MultiblockDetection multiblock) {
        this.multiblock = multiblock;
    }

    public BlockPos getMinPos() {
        return minPos;
    }

    public BlockPos getMaxPos() {
        return maxPos;
    }

    /** 附属方块变化时重新检测（1:1 旧版 notifyChange）。 */
    @Override
    public void notifyChange(IServantLogic servant, BlockPos pos) {
        checkMultiblockStructure();
    }

    /** 检查多方块结构并更新状态。 */
    public void checkMultiblockStructure() {
        if (level == null || level.isClientSide) {
            return;
        }
        boolean wasActive = active;

        BlockState state = level.getBlockState(worldPosition);
        if (!(state.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.BlockMultiblockController)) {
            active = false;
        } else {
            var facing = state.getValue(com.lvdriver.tconstruct_nirvana.block.BlockMultiblockController.FACING).getOpposite();

            // 区块已加载才重检（1:1 旧版 checkIfMultiblockCanBeRechecked）
            if (info == null || multiblock.checkIfMultiblockCanBeRechecked(level, info)) {
                MultiblockDetection.MultiblockStructure structure = multiblock.detectMultiblock(level, worldPosition.relative(facing), MAX_SIZE);
                if (structure == null) {
                    active = false;
                    updateStructureInfoInternal(null);
                } else {
                    active = true;
                    MultiblockDetection.assignMultiBlock(level, worldPosition, structure.blocks);
                    updateStructureInfoInternal(structure);
                    if (wasActive) {
                        level.sendBlockUpdated(worldPosition, state, state, 3);
                    }
                }
            }
        }

        // 状态变化 → 更新渲染
        if (wasActive != active) {
            level.sendBlockUpdated(worldPosition, state, state, 3);
            setChanged();
        }
    }

    protected final void updateStructureInfoInternal(MultiblockDetection.MultiblockStructure structure) {
        info = structure;

        if (structure == null) {
            structure = new MultiblockDetection.MultiblockStructure(0, 0, 0, java.util.List.of(worldPosition));
        }

        if (info != null) {
            minPos = info.minPos.offset(1, 1, 1); // 去墙和地板
            maxPos = info.maxPos.offset(-1, hasCeiling() ? -1 : 0, -1); // 去墙，无天花板
        } else {
            minPos = maxPos = worldPosition;
        }
        updateStructureInfo(structure);
    }

    /** 结构是否有天花板（影响内部 maxPos 计算）。 */
    protected boolean hasCeiling() {
        return true;
    }

    /** 结构检测成功后的回调（子类更新容量等）。 */
    protected abstract void updateStructureInfo(MultiblockDetection.MultiblockStructure structure);

    public boolean isActive() {
        return active && (level == null || level.isClientSide || info != null);
    }

    /** 结构失效（1:1 旧版 setInvalid，如炉内被堵）。 */
    public void setInvalid() {
        this.active = false;
        updateStructureInfoInternal(null);
    }

    public boolean isClientWorld() {
        return level != null && level.isClientSide;
    }

    public boolean isServerWorld() {
        return level != null && !level.isClientSide;
    }

    /* NBT / 网络同步 */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putBoolean(TAG_ACTIVE, active);
        if (minPos != null) {
            tag.putLong(TAG_MINPOS, minPos.asLong());
        }
        if (maxPos != null) {
            tag.putLong(TAG_MAXPOS, maxPos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        active = tag.getBoolean(TAG_ACTIVE);
        minPos = tag.contains(TAG_MINPOS) ? BlockPos.of(tag.getLong(TAG_MINPOS)) : null;
        maxPos = tag.contains(TAG_MAXPOS) ? BlockPos.of(tag.getLong(TAG_MAXPOS)) : null;
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = super.getUpdateTag(registries);
        saveAdditional(tag, registries);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider registries) {
        boolean wasActive = active;
        loadAdditional(tag, registries);
        // active 状态变化 → 刷新渲染
        if (active != wasActive && level != null) {
            BlockState state = level.getBlockState(worldPosition);
            level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    /** 由 {@link Level} ticker 驱动（服务端每 tick）。 */
    public abstract void tickServer();
}
