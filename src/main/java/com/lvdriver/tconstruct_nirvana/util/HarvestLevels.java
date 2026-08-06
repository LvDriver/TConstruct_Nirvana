package com.lvdriver.tconstruct_nirvana.util;

/**
 * 采掘等级常量（1:1 移植自 Tinkers' Antique {@code HarvestLevels}）。
 *
 * <p>旧版等级体系：0=石 1=铁 2=钻石 3=黑曜石 4=钴。
 * 1.21.1 中"能否挖掘"由工具 tag（{@code needs_*_tool}）决定，
 * 本常量保留在材料属性数据中，供属性显示与后续工具会话换算。</p>
 */
public final class HarvestLevels {

    public static final int STONE = 0;
    public static final int IRON = 1;
    public static final int DIAMOND = 2;
    public static final int OBSIDIAN = 3;
    public static final int COBALT = 4;

    private HarvestLevels() {
    }
}
