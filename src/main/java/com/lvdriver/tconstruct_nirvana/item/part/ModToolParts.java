package com.lvdriver.tconstruct_nirvana.item.part;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 工具部件注册中枢（全部注册在 {@link ModItems#ITEMS} 统一物品注册表）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code TinkerTools#registerToolParts}：
 * 部件注册顺序、cost（材料价值）与属性类型完全一致。cost 见
 * {@link Material#VALUE_Ingot}（144=1 锭，72=1 碎块）。</p>
 *
 * <p>形状标识 = 部件物品注册名（{@link ResourceLocation}），模具系统
 * （{@link com.lvdriver.tconstruct_nirvana.item.pattern.PatternItem}）用它关联部件。</p>
 */
public final class ModToolParts {

    /** 形状标识（部件注册名）→ 部件注册句柄，注册顺序即 stencil table GUI 顺序。 */
    private static final Map<ResourceLocation, DeferredItem<? extends ToolPart>> PARTS = new LinkedHashMap<>();

    // 头部部件（cost = 2 锭 = 288，重工具 8 锭 = 1152）
    public static final DeferredItem<ToolPart> PICK_HEAD = part("pick_head", 288, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> SHOVEL_HEAD = part("shovel_head", 288, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> AXE_HEAD = part("axe_head", 288, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> BROAD_AXE_HEAD = part("broad_axe_head", 1152, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> SWORD_BLADE = part("sword_blade", 288, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> LARGE_SWORD_BLADE = part("large_sword_blade", 1152, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> HAMMER_HEAD = part("hammer_head", 1152, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> EXCAVATOR_HEAD = part("excavator_head", 1152, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> KAMA_HEAD = part("kama_head", 288, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> SCYTHE_HEAD = part("scythe_head", 1152, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> PAN_HEAD = part("pan_head", 432, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> SIGN_HEAD = part("sign_head", 432, MaterialTypes.HEAD);

    // 手柄 / 绑定 / 护手 / 板
    public static final DeferredItem<ToolPart> TOOL_ROD = part("tool_rod", 144, MaterialTypes.HANDLE);
    public static final DeferredItem<ToolPart> TOUGH_TOOL_ROD = part("tough_tool_rod", 432, MaterialTypes.HANDLE);
    public static final DeferredItem<ToolPart> BINDING = part("binding", 144, MaterialTypes.EXTRA);
    public static final DeferredItem<ToolPart> TOUGH_BINDING = part("tough_binding", 432, MaterialTypes.EXTRA);
    public static final DeferredItem<ToolPart> WIDE_GUARD = part("wide_guard", 144, MaterialTypes.EXTRA);
    public static final DeferredItem<ToolPart> HAND_GUARD = part("hand_guard", 144, MaterialTypes.EXTRA);
    public static final DeferredItem<ToolPart> CROSS_GUARD = part("cross_guard", 144, MaterialTypes.EXTRA);
    public static final DeferredItem<ToolPart> LARGE_PLATE = part("large_plate", 1152, MaterialTypes.EXTRA);

    // 匕首 / 远程
    public static final DeferredItem<ToolPart> KNIFE_BLADE = part("knife_blade", 144, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> BOW_LIMB = part("bow_limb", 432, MaterialTypes.BOW, MaterialTypes.HEAD);
    public static final DeferredItem<ToolPart> BOW_STRING = part("bow_string", 144, MaterialTypes.BOWSTRING);
    public static final DeferredItem<ToolPart> ARROW_HEAD = part("arrow_head", 288, MaterialTypes.HEAD, MaterialTypes.PROJECTILE);
    public static final DeferredItem<ToolPart> ARROW_SHAFT = part("arrow_shaft", 288, MaterialTypes.SHAFT);
    public static final DeferredItem<ToolPart> FLETCHING = part("fletching", 288, MaterialTypes.FLETCHING);

    /** 弩芯（1:1 旧版 BoltCore，cost=2 锭；双材料简化版见 {@link BoltCore}）。 */
    public static final DeferredItem<BoltCore> BOLT_CORE = ModItems.ITEMS.register("bolt_core",
            () -> new BoltCore(new Item.Properties(), 288));

    /** 磨刀石（1:1 旧版 SharpeningKit，cost=4 碎块=288，修复材料载体，见 {@link SharpeningKit}）。 */
    public static final DeferredItem<SharpeningKit> SHARPENING_KIT = ModItems.ITEMS.register("sharpening_kit",
            () -> new SharpeningKit(new Item.Properties()));

    /** 碎块（1:1 旧版 Shard，cost=72=1 碎块，特殊匹配逻辑见 {@link Shard}）。 */
    public static final DeferredItem<Shard> SHARD = ModItems.ITEMS.register("shard",
            () -> new Shard(new Item.Properties()));

    static {
        // shard / bolt_core / sharpening_kit 同样有模具形状（旧版 registerStencilTableCrafting）
        PARTS.put(ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "shard"), SHARD);
        PARTS.put(ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "bolt_core"), BOLT_CORE);
        PARTS.put(ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "sharpening_kit"), SHARPENING_KIT);
    }

    private ModToolParts() {
    }

    /** 注册部件并登记到形状注册表。 */
    private static DeferredItem<ToolPart> part(String name, int cost, String... statTypes) {
        DeferredItem<ToolPart> deferred = ModItems.ITEMS.register(name,
                () -> new ToolPart(new Item.Properties(), cost, statTypes));
        PARTS.put(ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, name), deferred);
        return deferred;
    }

    /** 按形状标识（部件注册名）查询部件，未找到返回空。 */
    public static Optional<ToolPart> getPart(ResourceLocation shapeId) {
        DeferredItem<? extends ToolPart> deferred = PARTS.get(shapeId);
        if (deferred == null) {
            return Optional.empty();
        }
        // 注册完成后可安全 get()；注册期（创造标签页构建）亦可，DeferredItem 会即时解析已注册条目
        return Optional.ofNullable(deferred.get());
    }

    /** 全部部件（含 shard），按注册顺序。 */
    public static List<DeferredItem<? extends ToolPart>> getAllParts() {
        // shard 已在静态块登记进 PARTS，此处直接返回
        return Collections.unmodifiableList(new java.util.ArrayList<>(PARTS.values()));
    }

    /* ---------- 附属扩展入口（经 TConstructNirvanaAPI.toolParts() 调用） ---------- */

    /**
     * 用本 mod 物品注册表注册新部件（附属扩展，形状标识 = 部件注册名）。
     * 须在物品注册事件（RegisterEvent）之前调用，如附属 Mod 构造器。
     */
    public static DeferredItem<ToolPart> registerPart(String name, int cost, String... statTypes) {
        return part(name, cost, statTypes);
    }

    /**
     * 登记附属自行注册的部件物品（附属扩展，可随时调用）。
     * 建议 shapeId 与物品注册名一致；模具形状即部件注册名，登记后自动可用。
     */
    public static void registerPart(ResourceLocation shapeId, DeferredItem<? extends ToolPart> part) {
        PARTS.put(shapeId, part);
    }
}
