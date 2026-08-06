package com.lvdriver.tconstruct_nirvana.item.pattern;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * 模具注册中枢（空白模具 + 浇铸模具，注册在 {@link ModItems#ITEMS} 统一物品注册表）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code TinkerTools.pattern} 与
 * {@code TinkerSmeltery.cast}：模具为单物品多形状（形状存 DataComponent），
 * 形状 = 部件注册名，见 {@link PatternItem}。</p>
 */
public final class ModPatterns {

    /** 空白模具（部件加工台制作部件用，可拓印任意部件形状）。 */
    public static final DeferredItem<PatternItem> PATTERN = ModItems.ITEMS.register("pattern",
            () -> new PatternItem(new Item.Properties()));

    /** 空白浇铸模具（冶炼炉浇铸部件用）。 */
    public static final DeferredItem<CastItem> CAST = ModItems.ITEMS.register("cast",
            () -> new CastItem(new Item.Properties()));

    private ModPatterns() {
    }
}
