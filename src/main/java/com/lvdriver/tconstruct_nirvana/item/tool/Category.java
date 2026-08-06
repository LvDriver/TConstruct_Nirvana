package com.lvdriver.tconstruct_nirvana.item.tool;

import java.util.EnumSet;
import java.util.Set;

/**
 * 工具分类（1:1 移植自 Tinkers' Antique {@code Category}）。
 *
 * <p>决定工具行为与显示：TOOL 标记为工具（可采矿）、HARVEST 显示采掘信息、
 * WEAPON 标记为武器（击打耐久损耗减半）、LAUNCHER 为弓弩（显示拉弓/射程）、
 * AOE 为范围挖掘工具。旧版以集合挂在 TinkersItem 上。</p>
 */
public enum Category {

    TOOL("tool"),
    HARVEST("harvest"),
    WEAPON("weapon"),
    LAUNCHER("launcher"),
    PROJECTILE("projectile"),
    AOE("aoe");

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** 便捷构造：旧版 {@code addCategory(...)}。 */
    public static Set<Category> of(Category... categories) {
        Set<Category> set = EnumSet.noneOf(Category.class);
        java.util.Collections.addAll(set, categories);
        return set;
    }
}
