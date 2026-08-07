package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * 匠魂多方块检测基类（1:1 移植自 Tinkers' Antique {@code MultiblockTinker}，
 * 去 Mantle 依赖）。
 *
 * <p>持有主机方块实体；{@link #isValidSlave} 校验附属方块是否属于本主机
 * （尚未归属或已归属本主机的附属方块才有效，防止两个冶炼炉抢方块）。</p>
 */
public abstract class MultiblockTinker extends MultiblockCuboid {

    /** 主机方块实体（控制器）。 */
    public final TileMultiblock tile;

    public MultiblockTinker(TileMultiblock tile, boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
        super(hasFloor, hasFrame, hasCeiling);
        this.tile = tile;
    }

    /**
     * 校验附属方块：已归属其他主机的方块无效（1:1 旧版 isValidSlave）。
     * 例外：原主机已不存在（被拆除/失效）时放行，防止换位重建被残留绑定卡死。
     */
    protected boolean isValidSlave(Level world, BlockPos pos) {
        if (!world.isLoaded(pos)) {
            return false;
        }
        if (world.getBlockEntity(pos) instanceof IServantLogic slave) {
            if (slave.hasValidMaster() && !tile.getBlockPos().equals(slave.getMasterPosition())) {
                BlockPos masterPos = slave.getMasterPosition();
                // 原主机不存在 → 残留绑定作废，放行
                if (masterPos == null || !world.isLoaded(masterPos)
                        || !(world.getBlockEntity(masterPos) instanceof TileMultiblock)) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }
}
