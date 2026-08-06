package com.lvdriver.tconstruct_nirvana.material;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 材料定义（1:1 移植自 Tinkers' Antique {@code Material}）。
 *
 * <p>材料是匠魂一切内容的基石：工具部件的属性来源于材料，冶炼炉熔炼、浇铸
 * 与工具部件制作都围绕材料展开。本类承载材料标识、颜色、属性数据与特质关联；
 * 物品价值常量（Ingot=144 等）供液体换算与部件制作使用。</p>
 *
 * <p>1.21.1 迁移说明：材料数据为纯静态注册（{@link ModMaterials}），不依赖 NBT；
 * 特质以字符串标识占位（{@code Trait} 类在修饰符会话落地），物品关联
 * （RecipeMatch→ItemTag 匹配）在后续会话补全。</p>
 */
public class Material {

    /** 未知材料：任何材料查询缺失属性时的兜底值。 */
    public static final Material UNKNOWN = new Material("unknown", 0xffffff);

    // 物品价值常量（用于液体换算与部件制作）
    public static final int VALUE_Ingot = 144;
    public static final int VALUE_Nugget = VALUE_Ingot / 9;
    public static final int VALUE_Fragment = VALUE_Ingot / 4;
    public static final int VALUE_Shard = VALUE_Ingot / 2;
    public static final int VALUE_Gem = 666; // 可被 3 整除
    public static final int VALUE_Block = VALUE_Ingot * 9;
    public static final int VALUE_SearedBlock = VALUE_Ingot * 2;
    public static final int VALUE_SearedMaterial = VALUE_Ingot / 2;
    public static final int VALUE_Glass = 1000;
    public static final int VALUE_BrickBlock = VALUE_Ingot * 4;
    public static final int VALUE_SlimeBall = 250;

    static {
        UNKNOWN.addStats(new HeadMaterialStats(1, 1, 1, 0));
        UNKNOWN.addStats(new HandleMaterialStats(1f, 0));
        UNKNOWN.addStats(new ExtraMaterialStats(0));
        UNKNOWN.addStats(new BowMaterialStats(1f, 1f, 0f));
        UNKNOWN.addStats(new BowStringMaterialStats(1f));
        UNKNOWN.addStats(new ArrowShaftMaterialStats(1f, 0));
        UNKNOWN.addStats(new FletchingMaterialStats(1f, 1f));
        UNKNOWN.addStats(new ProjectileMaterialStats());
    }

    /** 唯一标识材料的字符串（全小写，无空白）。 */
    public final String identifier;

    /** 关联的流体 ID（可为 null）。冶炼炉会话填充熔融金属流体。 */
    protected String fluidId;

    /** 是否可在部件加工台制作部件。 */
    protected boolean craftable;

    /** 是否可通过冶炼炉浇铸部件（须先关联流体）。 */
    protected boolean castable;

    /** 材料文字颜色（ARGB），用于 tooltip 等显示。 */
    public final int materialTextColor;

    /** 是否对玩家隐藏（特殊/内部材料）。 */
    private boolean hidden;

    /** 属性类型 -> 属性数据（LinkedHashMap 保证迭代顺序）。 */
    protected final Map<String, IMaterialStats> stats = new LinkedHashMap<>();

    /** 属性类型 -> 特质标识列表（trait 实现于修饰符会话落地）。 */
    protected final Map<String, List<String>> traits = new LinkedHashMap<>();

    public Material(String identifier, int color) {
        this(identifier, color, false);
    }

    public Material(String identifier, int color, boolean hidden) {
        this.identifier = identifier;
        this.hidden = hidden;
        // 若未显式设置 alpha，则补为不透明
        if (((color >> 24) & 0xFF) == 0) {
            color |= 0xFF << 24;
        }
        this.materialTextColor = color;
    }

    /** 若为 true 则材料不会展示给玩家，用于特殊/内部材料。 */
    public boolean isHidden() {
        return hidden;
    }

    /** 声明材料可见（由集成逻辑调用，使之前隐藏的材料显示）。 */
    public void setVisible() {
        hidden = false;
    }

    /** 关联流体（熔融金属），用于熔炼/浇铸。 */
    public Material setFluid(String fluidId) {
        this.fluidId = fluidId;
        return this;
    }

    public String getFluidId() {
        return fluidId;
    }

    public boolean hasFluid() {
        return fluidId != null;
    }

    /** 设为 true 允许在部件加工台制作该材料的部件。 */
    public Material setCraftable(boolean craftable) {
        this.craftable = craftable;
        return this;
    }

    public boolean isCraftable() {
        return craftable || castable;
    }

    /** 设为 true 允许冶炼炉浇铸该材料的部件（须先关联流体）。 */
    public Material setCastable(boolean castable) {
        this.castable = castable;
        return this;
    }

    public boolean isCastable() {
        return hasFluid() && castable;
    }

    /* ---------- 属性 ---------- */

    /** 挂载一类属性数据（同类型属性后挂载者覆盖前者）。 */
    public Material addStats(IMaterialStats materialStats) {
        stats.put(materialStats.getIdentifier(), materialStats);
        return this;
    }

    /** 获取指定类型的属性数据，缺失返回 null。 */
    @SuppressWarnings("unchecked")
    public <T extends IMaterialStats> T getStats(String identifier) {
        return (T) stats.get(identifier);
    }

    /** 获取指定类型的属性数据，缺失时回退到 {@link #UNKNOWN} 的对应属性。 */
    @SuppressWarnings("unchecked")
    public <T extends IMaterialStats> T getStatsOrUnknown(String identifier) {
        T found = (T) stats.get(identifier);
        if (found == null && this != UNKNOWN) {
            return UNKNOWN.getStats(identifier);
        }
        return found;
    }

    public Collection<IMaterialStats> getAllStats() {
        return stats.values();
    }

    public boolean hasStats(String identifier) {
        return getStats(identifier) != null;
    }

    /* ---------- 特质 ---------- */

    /** 添加默认特质（任意部件生效）。 */
    public Material addTrait(String traitId) {
        return addTrait(traitId, null);
    }

    /** 添加仅在指定部件类型（见 {@link MaterialTypes}）上生效的特质。 */
    public Material addTrait(String traitId, String dependency) {
        getStatTraits(dependency).add(traitId);
        return this;
    }

    private List<String> getStatTraits(String id) {
        return traits.computeIfAbsent(id, k -> new LinkedList<>());
    }

    public List<String> getDefaultTraits() {
        return List.copyOf(getStatTraits(null));
    }

    /** 获取指定部件类型的特质列表；无该类型时回退到默认特质。 */
    public List<String> getAllTraitsForStats(String statsId) {
        if (traits.containsKey(statsId)) {
            return List.copyOf(traits.get(statsId));
        } else if (traits.containsKey(null)) {
            return List.copyOf(traits.get(null));
        }
        return List.of();
    }

    /* ---------- 标识与本地化 ---------- */

    public String getIdentifier() {
        return identifier;
    }

    /** 本地化名称 key：{@code material.<identifier>.name}。 */
    public String getLocalizedNameKey() {
        return String.format("material.%s.name", identifier);
    }

    /** 本地化前缀 key：{@code material.<identifier>.prefix}（用于"钴 镐头"式命名）。 */
    public String getLocalizedPrefixKey() {
        return String.format("material.%s.prefix", identifier);
    }

    @Override
    public String toString() {
        return "Material{" + identifier + '}';
    }
}
