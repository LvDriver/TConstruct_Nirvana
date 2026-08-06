package com.lvdriver.tconstruct_nirvana.util;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * TCon 物品/方块 Tag 定义。
 *
 * <p>1.21.1 中矿物词典由 Tag 承担（旧版 {@code OreDictionary} 的 oredict 名
 * 映射为常见前缀 tag）：{@code ingotCobalt}→{@code c:ingots/cobalt}、
 * {@code oreCobalt}→{@code c:ores/cobalt}、{@code blockCobalt}→{@code c:storage_blocks/cobalt}
 * 等，以保证 500mod 整合包中与 JEI、其他 mod 的矿物词典兼容。</p>
 */
public final class TConTags {

    /** 需要钴级工具才能挖掘（旧版采掘等级 4 = COBALT，无对应原版 tag，自定义）。 */
    public static final TagKey<Block> NEEDS_COBALT_TOOL = BlockTags.create(
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "needs_cobalt_tool"));

    /** 携带该 tag 的工具可采掘 {@link #NEEDS_COBALT_TOOL} 方块（物品侧，工具会话接线）。 */
    public static final TagKey<Item> NEEDS_COBALT_TOOL_ITEMS = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "needs_cobalt_tool"));

    // 矿石（common tag，对应旧版 oredict：oreCobalt / oreArdite）
    public static final TagKey<Block> ORES_COBALT = blockTag("c", "ores/cobalt");
    public static final TagKey<Block> ORES_ARDITE = blockTag("c", "ores/ardite");
    public static final TagKey<Item> ORES_COBALT_ITEMS = itemTag("c", "ores/cobalt");
    public static final TagKey<Item> ORES_ARDITE_ITEMS = itemTag("c", "ores/ardite");

    // 金属块（对应旧版 oredict：blockCobalt / blockArdite）
    public static final TagKey<Block> STORAGE_BLOCKS_COBALT = blockTag("c", "storage_blocks/cobalt");
    public static final TagKey<Block> STORAGE_BLOCKS_ARDITE = blockTag("c", "storage_blocks/ardite");
    public static final TagKey<Item> STORAGE_BLOCKS_COBALT_ITEMS = itemTag("c", "storage_blocks/cobalt");
    public static final TagKey<Item> STORAGE_BLOCKS_ARDITE_ITEMS = itemTag("c", "storage_blocks/ardite");

    // 锭（对应旧版 oredict：ingotCobalt / ingotArdite）
    public static final TagKey<Item> INGOTS_COBALT = itemTag("c", "ingots/cobalt");
    public static final TagKey<Item> INGOTS_ARDITE = itemTag("c", "ingots/ardite");

    // 粒（对应旧版 oredict：nuggetCobalt / nuggetArdite）
    public static final TagKey<Item> NUGGETS_COBALT = itemTag("c", "nuggets/cobalt");
    public static final TagKey<Item> NUGGETS_ARDITE = itemTag("c", "nuggets/ardite");

    /** 全部工具部件（部件-模具关联的部件侧集合，供配方/附属 mod 使用）。 */
    public static final TagKey<Item> TOOL_PARTS = itemTag(TConstructNirvana.MODID, "tool_parts");

    /** 采集类工具可用的 mineable 系列（工具物品 tag 与方块 tag 同名，DataGen 生成）。 */
    public static final List<TagKey<Block>> MINEABLE_TAGS = List.of(
            BlockTags.MINEABLE_WITH_PICKAXE,
            BlockTags.MINEABLE_WITH_AXE,
            BlockTags.MINEABLE_WITH_SHOVEL,
            BlockTags.MINEABLE_WITH_HOE);

    /** 与方块 mineable tag 同名的物品 tag（物品侧标记工具可采掘类别）。 */
    public static TagKey<Item> itemTagFor(TagKey<Block> blockTag) {
        return ItemTags.create(blockTag.location());
    }

    /** 本 mod 物品侧 mineable tag：pickaxe（其余按需在 DataGen 打同名 tag）。 */
    public static final TagKey<Item> MINEABLE_PICKAXE_ITEMS = itemTagFor(BlockTags.MINEABLE_WITH_PICKAXE);
    public static final TagKey<Item> MINEABLE_AXE_ITEMS = itemTagFor(BlockTags.MINEABLE_WITH_AXE);
    public static final TagKey<Item> MINEABLE_SHOVEL_ITEMS = itemTagFor(BlockTags.MINEABLE_WITH_SHOVEL);
    public static final TagKey<Item> MINEABLE_HOE_ITEMS = itemTagFor(BlockTags.MINEABLE_WITH_HOE);

    private static TagKey<Block> blockTag(String namespace, String path) {
        return BlockTags.create(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private static TagKey<Item> itemTag(String namespace, String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }

    private TConTags() {
    }
}
