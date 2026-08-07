package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;

/**
 * 多方块主机逻辑（1:1 移植自 Mantle {@code IMasterLogic} 简化版）。
 *
 * <p>多方块结构的"主机"（如冶炼炉控制器方块实体）实现本接口；
 * 附属方块（servant）结构变化时调用 {@link #notifyChange} 通知主机重新检测。</p>
 */
public interface IMasterLogic {

    /**
     * 附属方块（servant）结构变化时回调，主机应重新检测多方块结构。
     *
     * @param servant 发生变化的附属方块位置
     * @param pos     变化方块的位置
     */
    void notifyChange(IServantLogic servant, BlockPos pos);
}
