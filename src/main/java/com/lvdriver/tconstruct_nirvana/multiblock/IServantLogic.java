package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * 多方块附属方块逻辑（1:1 移植自 Mantle {@code MultiServantLogic} 简化版）。
 *
 * <p>冶炼炉结构中的附属方块实体（如储罐 {@code TileTank}）实现本接口，
 * 记录所属主机（master）位置；主机销毁/失效时附属同步失效。</p>
 */
public interface IServantLogic {

    /** 是否已指定有效主机。 */
    boolean hasValidMaster();

    /** 当前主机位置（可能为 null）。 */
    BlockPos getMasterPosition();

    /** 覆盖设置主机位置（由主机在结构检测时调用）。 */
    void overrideMaster(BlockPos pos);

    /** 清除主机位置（由主机在结构失效时调用）。 */
    void removeMaster();

    /** 通知主机：本附属方块的结构相关状态发生变化。 */
    void notifyMasterOfChange();

    /** 向附属方块实体写入主机（1.21.1 无 Mantle 的 overrideMaster 静态工具，简化内联）。 */
    static void setMaster(BlockEntity servant, BlockPos master) {
        if (servant instanceof IServantLogic logic) {
            logic.overrideMaster(master);
        }
    }
}
