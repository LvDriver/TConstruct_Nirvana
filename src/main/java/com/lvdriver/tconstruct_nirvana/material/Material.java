package com.lvdriver.tconstruct_nirvana.material;

import com.lvdriver.tconstruct_nirvana.util.ItemTagMatch;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 材料定义（1:1 移植自 Tinkers' Antique {@code Material}）。
 *
 * <p>材料是匠魂一切内容的基石：工具部件的属性来源于材料，冶炼炉熔炼、浇铸
 * 与工具部件制作都围绕材料展开。本类承载材料标识、颜色、属性数据与特质关联；
 * 物品价值常量（Ingot=144 等）供液体换算与部件制作使用。</p>
 *
 * <p>1.21.1 迁移说明：材料数据为纯静态注册（{@link ModMaterials}），不依赖 NBT；
 * 特质以字符串标识占位（{@code Trait} 类在修饰符会话落地）；物品关联以
 * {@link com.lvdriver.tconstruct_nirvana.util.ItemTagMatch}（TagKey 匹配）承载，
 * 替代旧版 Mantle RecipeMatch。</p>
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

    /** 物品关联列表（旧版 RecipeMatchRegistry.items）：TagKey 匹配 + 价值。 */
    private final List<ItemTagMatch> itemMatches = new ArrayList<>();

    /** 代表物品（显示用，如创造标签页/JEI）；{@link ItemStack#EMPTY} 表示未设置。 */
    private ItemStack representativeItem = ItemStack.EMPTY;

    /** 代表物品的 tag 形式（旧版 representativeOre）：运行时从 tag 内容取首个物品。 */
    private TagKey<Item> representativeOre;

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

    /* ---------- 物品关联（旧版 RecipeMatch→1.21.1 TagKey） ----------
     * 注册侧已由 ModMaterials.registerItemAssociations 接入；
     * 查询/展示侧（matches / getMatchValue / getRepresentativeItem 等）
     * 当前无调用方，待创造标签页 / JEI 信息 / 部件加工台与冶炼炉会话接入。 */

    /**
     * 关联一类物品到本材料：该 tag 下每个物品代表 {@code value} 价值（mb）。
     * 旧版 {@code addItem(String oredict, int needed, int amount)} 的 1.21.1 版
     * （needed 恒为 1，已省略）；矿物词典名映射为 {@code c:} 前缀 tag。
     */
    public Material addItem(TagKey<Item> tag, int value) {
        itemMatches.add(new ItemTagMatch(tag, value));
        return this;
    }

    /** 关联价值为 {@link #VALUE_Ingot} 的一类物品（旧版 {@code addItemIngot}）。 */
    public Material addItemIngot(TagKey<Item> tag) {
        return addItem(tag, VALUE_Ingot);
    }

    /**
     * 金属材料快捷关联：锭/粒/块三件套（旧版 {@code addCommonItems("Cobalt")}）。
     *
     * @param metalPath 小写金属名（如 {@code "cobalt"}、{@code "pig_iron"}），
     *                  映射为 {@code c:ingots/<path>} / {@code c:nuggets/<path>} /
     *                  {@code c:storage_blocks/<path>}
     */
    public Material addCommonItems(String metalPath) {
        addItem(cTag("ingots/" + metalPath), VALUE_Ingot);
        addItem(cTag("nuggets/" + metalPath), VALUE_Nugget);
        addItem(cTag("storage_blocks/" + metalPath), VALUE_Block);
        return this;
    }

    private static TagKey<Item> cTag(String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }

    /** 返回首个命中该物品的匹配器（按添加顺序），未关联返回 empty。 */
    public Optional<ItemTagMatch> matches(ItemStack stack) {
        for (ItemTagMatch match : itemMatches) {
            if (match.matches(stack)) {
                return Optional.of(match);
            }
        }
        return Optional.empty();
    }

    /** 该物品代表的本材料价值（mb），未关联返回 0。 */
    public int getMatchValue(ItemStack stack) {
        return matches(stack).map(ItemTagMatch::amount).orElse(0);
    }

    /** 是否已关联任何物品（旧版 {@code hasItems}）。 */
    public boolean hasItems() {
        return !itemMatches.isEmpty();
    }

    /** 全部物品关联（只读视图）。 */
    public List<ItemTagMatch> getItemMatches() {
        return List.copyOf(itemMatches);
    }

    /* ---------- 代表物品 ---------- */

    /**
     * 指定代表物品的 tag：{@link #getRepresentativeItem()} 时从该 tag 运行期内容
     * 取首个物品（旧版 {@code setRepresentativeItem(String representativeOre)}）。
     */
    public Material setRepresentativeItem(TagKey<Item> representativeOre) {
        this.representativeOre = representativeOre;
        return this;
    }

    /** 指定代表物品（旧版 {@code setRepresentativeItem(Item)}）。 */
    public Material setRepresentativeItem(Item representativeItem) {
        return setRepresentativeItem(new ItemStack(representativeItem));
    }

    /**
     * 指定代表物品。
     *
     * <p>迁移说明：旧版要求该物品已通过 {@code addItem} 关联（否则 warn 拒绝）；
     * 1.21.1 中 tag 内容在数据包加载后才可用，而材料绑定发生在 Mod 构造器
     * （早于加载），无法校验，故改为直接接受，由调用方保证合理性。</p>
     */
    public Material setRepresentativeItem(ItemStack representativeItem) {
        if (representativeItem == null || representativeItem.isEmpty()) {
            this.representativeItem = ItemStack.EMPTY;
        } else {
            this.representativeItem = representativeItem.copy();
        }
        return this;
    }

    /**
     * 获取代表物品（用于创造标签页/JEI 显示）。
     * tag 形式优先（运行时解析 tag 内容首个物品），其次为直接设置的代表物品；
     * 均无返回 {@link ItemStack#EMPTY}。
     */
    public ItemStack getRepresentativeItem() {
        if (representativeOre != null) {
            HolderSet.Named<Item> holders = BuiltInRegistries.ITEM.getTag(representativeOre).orElse(null);
            if (holders != null && holders.size() > 0) {
                return new ItemStack(holders.get(0).value());
            }
        }
        return representativeItem;
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

    /** 按标识查找材料（内部委托 ModMaterials 静态注册表），未找到返回 {@link #UNKNOWN}。 */
    public static Material getByIdentifier(String identifier) {
        return ModMaterials.getMaterial(identifier) != null
                ? ModMaterials.getMaterial(identifier)
                : UNKNOWN;
    }

    /**
     * 组合物品名（1:1 旧版 Material.getCombinedItemName）：材料前缀 + 工具名，
     * 如 "钴 镐"（前缀取各材料 {@link #getLocalizedPrefixKey}，未知材料跳过）。
     */
    public static net.minecraft.network.chat.Component getCombinedItemName(
            net.minecraft.network.chat.Component itemName, Collection<Material> materials) {
        StringBuilder sb = new StringBuilder();
        for (Material material : materials) {
            if (material != UNKNOWN) {
                sb.append(net.minecraft.network.chat.Component
                        .translatable(material.getLocalizedPrefixKey()).getString()).append(' ');
            }
        }
        return net.minecraft.network.chat.Component.literal(sb + itemName.getString());
    }
}
