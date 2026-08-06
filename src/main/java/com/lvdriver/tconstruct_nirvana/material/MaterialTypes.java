package com.lvdriver.tconstruct_nirvana.material;

/**
 * 材料属性类型标识（1:1 移植自 Tinkers' Antique {@code MaterialTypes}）。
 *
 * <p>每种工具部件槽对应一类属性：头部/手柄/附加部件决定近战工具，
 * 弓/弓弦/弹射物/箭杆/箭羽决定远程与弹射工具。</p>
 */
public final class MaterialTypes {

    /** 近战工具头部（耐久、采掘等级、攻击力、采掘速度）。 */
    public static final String HEAD = "head";
    /** 手柄（系数、耐久修正）。 */
    public static final String HANDLE = "handle";
    /** 附加部件（额外耐久）。 */
    public static final String EXTRA = "extra";

    /** 弓身（拉弓速度、射程、附加伤害）。 */
    public static final String BOW = "bow";
    /** 弓弦（系数）。 */
    public static final String BOWSTRING = "bowstring";

    /** 弹射物部件（占位类型，区分弹射工具部件）。 */
    public static final String PROJECTILE = "projectile";
    /** 箭杆（系数、额外弹药）。 */
    public static final String SHAFT = "shaft";
    /** 箭羽（精准度、系数）。 */
    public static final String FLETCHING = "fletching";

    private MaterialTypes() {
    }
}
