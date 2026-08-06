package com.lvdriver.tconstruct_nirvana.material;

/**
 * 材料属性注册便捷方法。
 *
 * <p>对应旧版 {@code TinkerRegistry.addMaterialStats} 的常用重载：
 * 一次性挂载头部/手柄/附加部件三类近战属性。</p>
 */
final class TConStats {

    private TConStats() {
    }

    /** 为材料同时挂载头部、手柄、附加部件属性。 */
    static void add(Material material, HeadMaterialStats head, HandleMaterialStats handle, ExtraMaterialStats extra) {
        material.addStats(head).addStats(handle).addStats(extra);
    }

    /** 为材料挂载弓身属性。 */
    static void add(Material material, BowMaterialStats bow) {
        material.addStats(bow);
    }

    /** 为材料挂载弓弦属性。 */
    static void add(Material material, BowStringMaterialStats bowstring) {
        material.addStats(bowstring);
    }

    /** 为材料挂载箭杆属性。 */
    static void add(Material material, ArrowShaftMaterialStats shaft) {
        material.addStats(shaft);
    }

    /** 为材料挂载箭羽属性。 */
    static void add(Material material, FletchingMaterialStats fletching) {
        material.addStats(fletching);
    }
}
