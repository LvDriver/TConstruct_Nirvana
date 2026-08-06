package com.lvdriver.tconstruct_nirvana.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.Event;

/**
 * 冶炼事件基类（{@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}）。
 *
 * <p>冶炼炉系统的对外事件根类型。具体事件：{@link MeltingEvent}（熔化）、
 * {@link CastingEvent}（浇铸）。</p>
 *
 * <p><b>注意</b>：冶炼炉多方块系统为后期子系统，本事件当前尚未有触发点；
 * 事件类先行发布以锁定 API 形状，冶炼炉会话落地后接入触发。
 * 附属可现在就按本 API 编写监听器，触发接入后无需改动。</p>
 *
 * <p><b>稳定 API 承诺</b>：字段与 getter 一经发布不变；新增数据只以新 getter 追加。</p>
 */
public abstract class SmelteryEvent extends Event {

    /** 冶炼炉所在维度。 */
    private final Level level;

    /** 冶炼炉主方块（控制器）坐标。 */
    private final BlockPos pos;

    protected SmelteryEvent(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    /** 冶炼炉所在维度。 */
    public Level getLevel() {
        return level;
    }

    /** 冶炼炉主方块（控制器）坐标。 */
    public BlockPos getPos() {
        return pos;
    }
}
