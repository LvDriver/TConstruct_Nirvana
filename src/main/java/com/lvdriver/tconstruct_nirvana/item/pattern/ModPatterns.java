package com.lvdriver.tconstruct_nirvana.item.pattern;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 模具注册中枢（空白模具 + 浇铸模具，注册在 {@link ModItems#ITEMS} 统一物品注册表）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code TinkerTools.pattern} 与
 * {@code TinkerSmeltery.cast / cast_custom}：模具为单物品多形状（形状存
 * DataComponent），形状 = 部件注册名 或 铸造形状（{@code ingot/nugget/gem/plate/gear}，
 * 1:1 旧版 CastCustom 的 5 个 meta，用于金属锭/粒/宝石/板/齿轮浇铸）。</p>
 */
public final class ModPatterns {

    /** 空白模具（部件加工台制作部件用，可拓印任意部件形状）。 */
    public static final DeferredItem<PatternItem> PATTERN = ModItems.ITEMS.register("pattern",
            () -> new PatternItem(new Item.Properties()));

    /** 空白浇铸模具（冶炼炉浇铸部件用）。 */
    public static final DeferredItem<CastItem> CAST = ModItems.ITEMS.register("cast",
            () -> new CastItem(new Item.Properties()));

    // ============ 铸造形状（1:1 旧版 CastCustom 的 5 个 meta，非部件形状） ============

    /** 铸锭模具形状。 */
    public static final ResourceLocation SHAPE_INGOT = castShape("ingot");
    /** 铸粒模具形状。 */
    public static final ResourceLocation SHAPE_NUGGET = castShape("nugget");
    /** 铸宝石模具形状。 */
    public static final ResourceLocation SHAPE_GEM = castShape("gem");
    /** 铸板模具形状。 */
    public static final ResourceLocation SHAPE_PLATE = castShape("plate");
    /** 铸齿轮模具形状。 */
    public static final ResourceLocation SHAPE_GEAR = castShape("gear");

    /** 全部铸造形状（注册顺序即创造页/DataGen 遍历顺序，LinkedHashSet 保序）。 */
    public static final Set<ResourceLocation> CAST_SHAPES = Collections.unmodifiableSet(
            new java.util.LinkedHashSet<>(java.util.List.of(
                    SHAPE_INGOT, SHAPE_NUGGET, SHAPE_GEM, SHAPE_PLATE, SHAPE_GEAR)));

    /** 铸造形状 → 浇铸材料价值（mb，1:1 旧版 CastCustom.addMeta 的 cost 参数）。 */
    public static final Map<ResourceLocation, Integer> CAST_SHAPE_COSTS = createShapeCosts();

    private static Map<ResourceLocation, Integer> createShapeCosts() {
        Map<ResourceLocation, Integer> map = new LinkedHashMap<>();
        map.put(SHAPE_INGOT, Material.VALUE_Ingot);
        map.put(SHAPE_NUGGET, Material.VALUE_Nugget);
        map.put(SHAPE_GEM, Material.VALUE_Gem);
        map.put(SHAPE_PLATE, Material.VALUE_Ingot);
        map.put(SHAPE_GEAR, Material.VALUE_Ingot * 4);
        return Collections.unmodifiableMap(map);
    }

    /** 形状是否已知（部件形状 或 铸造形状）。 */
    public static boolean isKnownShape(ResourceLocation shapeId) {
        return CAST_SHAPES.contains(shapeId) || ModToolParts.getPart(shapeId).isPresent();
    }

    /** 铸造形状的价值（mb）；非铸造形状返回 null。 */
    public static Integer getCastShapeCost(ResourceLocation shapeId) {
        return CAST_SHAPE_COSTS.get(shapeId);
    }

    private static ResourceLocation castShape(String path) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, path);
    }

    private ModPatterns() {
    }
}
