package com.lvdriver.tconstruct_nirvana.util;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

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

    /** 需要钴级工具才能挖掘（旧版采掘等级 4 = COBALT，无对应原版 tag，自定义）。
     * 采掘判定走 HarvestCheck 事件（{@code ToolHelper.requiredHarvestLevel} 映射），无物品侧 tag。 */
    public static final TagKey<Block> NEEDS_COBALT_TOOL = BlockTags.create(
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

    /** 血骨材料来源（旧版 oredict "boneBloodied"：坏死骨；凋灵骷髅掉落）。 */
    public static final TagKey<Item> BONE_BLOODIED = itemTag(TConstructNirvana.MODID, "bone_bloodied");
    /** 凋灵之骨（1:1 旧版 oredict "boneWithered" → NeoForge 标准位 c:bones/wither）。 */
    public static final TagKey<Item> BONES_WITHER = itemTag("c", "bones/wither");

    /** 全部模具（1:1 旧版 oredict "pattern"）。 */
    public static final TagKey<Item> PATTERNS = itemTag(TConstructNirvana.MODID, "patterns");
    /** 全部浇铸模具（1:1 旧版 oredict "cast"）。 */
    public static final TagKey<Item> CASTS = itemTag(TConstructNirvana.MODID, "casts");
    /** 工作站（1:1 旧版 oredict "workbench"：工具站/锻造厂）。 */
    public static final TagKey<Item> WORKBENCHES = itemTag(TConstructNirvana.MODID, "workbenches");

    /** 焦黑石系列（1:1 旧版 oredict "blockSeared"，12 主变体，不含楼梯/台阶）。 */
    public static final TagKey<Block> SEARED_BLOCKS = blockTag(TConstructNirvana.MODID, "seared_blocks");
    public static final TagKey<Item> SEARED_BLOCKS_ITEMS = itemTag(TConstructNirvana.MODID, "seared_blocks");

    // 史莱姆方块集合（1:1 旧版 oredict blockSlimeDirt / blockSlimeGrass / blockSlimeCongealed）
    public static final TagKey<Block> SLIME_BLOCKS_DIRT = blockTag(TConstructNirvana.MODID, "slime_blocks/dirt");
    public static final TagKey<Item> SLIME_BLOCKS_DIRT_ITEMS = itemTag(TConstructNirvana.MODID, "slime_blocks/dirt");
    public static final TagKey<Block> SLIME_BLOCKS_GRASS = blockTag(TConstructNirvana.MODID, "slime_blocks/grass");
    public static final TagKey<Item> SLIME_BLOCKS_GRASS_ITEMS = itemTag(TConstructNirvana.MODID, "slime_blocks/grass");
    public static final TagKey<Block> SLIME_BLOCKS_CONGEALED = blockTag(TConstructNirvana.MODID, "slime_blocks/congealed");
    public static final TagKey<Item> SLIME_BLOCKS_CONGEALED_ITEMS = itemTag(TConstructNirvana.MODID, "slime_blocks/congealed");

    /**
     * 流体 common tag（500mod 兼容，NeoForge 1.21.1 标准：与 {@code Tags.Fluids}
     * 同风格，id 为 {@code c:<name>} 如 {@code c:molten_iron}，文件自动落
     * {@code data/c/tags/fluid/}，供配方/附属 mod 引用）。
     */
    public static TagKey<Fluid> fluidTag(String path) {
        return FluidTags.create(ResourceLocation.fromNamespaceAndPath("c", path));
    }

    // 材料-物品关联自定义 tag（旧版 oredict 名无 c: 对应项时的 mod 命名空间方案，
    // DataGen 将原版物品加入；其他 mod 可自行加入同 tag 物品扩展匹配）
    /** 旧版具体物品 Items.FLINT → tconstruct_nirvana:flint。 */
    public static final TagKey<Item> FLINT = itemTag(TConstructNirvana.MODID, "flint");
    /** 旧版 blockCactus → tconstruct_nirvana:cactus。 */
    public static final TagKey<Item> CACTUS = itemTag(TConstructNirvana.MODID, "cactus");
    /** 旧版 blockPrismarine → tconstruct_nirvana:storage_blocks/prismarine。 */
    public static final TagKey<Item> STORAGE_BLOCKS_PRISMARINE = itemTag(TConstructNirvana.MODID, "storage_blocks/prismarine");
    /** 旧版 blockPrismarineBrick → tconstruct_nirvana:storage_blocks/prismarine_bricks。 */
    public static final TagKey<Item> STORAGE_BLOCKS_PRISMARINE_BRICKS = itemTag(TConstructNirvana.MODID, "storage_blocks/prismarine_bricks");
    /** 旧版 blockPrismarineDark → tconstruct_nirvana:storage_blocks/dark_prismarine。 */
    public static final TagKey<Item> STORAGE_BLOCKS_DARK_PRISMARINE = itemTag(TConstructNirvana.MODID, "storage_blocks/dark_prismarine");
    /** 旧版 Items.DYE(白) 骨粉 → tconstruct_nirvana:bonemeal。 */
    public static final TagKey<Item> BONE_MEAL = itemTag(TConstructNirvana.MODID, "bonemeal");
    /** 旧版 paper → tconstruct_nirvana:paper。 */
    public static final TagKey<Item> PAPER = itemTag(TConstructNirvana.MODID, "paper");
    /** 旧版 Blocks.SPONGE → tconstruct_nirvana:sponges。 */
    public static final TagKey<Item> SPONGES = itemTag(TConstructNirvana.MODID, "sponges");
    /** 旧版 vine → tconstruct_nirvana:vines。 */
    public static final TagKey<Item> VINES = itemTag(TConstructNirvana.MODID, "vines");
    /** 旧版 Blocks.PACKED_ICE → tconstruct_nirvana:packed_ice。 */
    public static final TagKey<Item> PACKED_ICE = itemTag(TConstructNirvana.MODID, "packed_ice");
    /** 旧版 Blocks.END_ROD → tconstruct_nirvana:end_rods。 */
    public static final TagKey<Item> END_RODS = itemTag(TConstructNirvana.MODID, "end_rods");

    // 熔炼配方输入（旧版具体物品/无 c: 对应 oredict → mod 命名空间 tag，DataGen 加入原版物品；
    // 其他 mod 可自行打同 tag 扩展"万物皆可熔"）
    /** 旧版 Blocks.ICE → tconstruct_nirvana:ice。 */
    public static final TagKey<Item> ICE = itemTag(TConstructNirvana.MODID, "ice");
    /** 旧版 Blocks.SNOW（雪层）→ tconstruct_nirvana:snow。 */
    public static final TagKey<Item> SNOW = itemTag(TConstructNirvana.MODID, "snow");
    /** 旧版 Items.SNOWBALL → tconstruct_nirvana:snowballs。 */
    public static final TagKey<Item> SNOWBALLS = itemTag(TConstructNirvana.MODID, "snowballs");
    /** 旧版 Items.ROTTEN_FLESH → tconstruct_nirvana:rotten_flesh。 */
    public static final TagKey<Item> ROTTEN_FLESH = itemTag(TConstructNirvana.MODID, "rotten_flesh");
    /** 旧版 Items.SPIDER_EYE → tconstruct_nirvana:spider_eyes。 */
    public static final TagKey<Item> SPIDER_EYES = itemTag(TConstructNirvana.MODID, "spider_eyes");
    /** 旧版 Items.FISH(3) 河豚 → tconstruct_nirvana:pufferfish。 */
    public static final TagKey<Item> PUFFERFISH = itemTag(TConstructNirvana.MODID, "pufferfish");
    /** 旧版 Blocks.RAIL → tconstruct_nirvana:rails。 */
    public static final TagKey<Item> RAILS = itemTag(TConstructNirvana.MODID, "rails");
    /** 旧版 Blocks.ACTIVATOR_RAIL → tconstruct_nirvana:activator_rails。 */
    public static final TagKey<Item> ACTIVATOR_RAILS = itemTag(TConstructNirvana.MODID, "activator_rails");
    /** 旧版 Blocks.DETECTOR_RAIL → tconstruct_nirvana:detector_rails。 */
    public static final TagKey<Item> DETECTOR_RAILS = itemTag(TConstructNirvana.MODID, "detector_rails");
    /** 旧版 Blocks.GOLDEN_RAIL → tconstruct_nirvana:golden_rails。 */
    public static final TagKey<Item> GOLDEN_RAILS = itemTag(TConstructNirvana.MODID, "golden_rails");
    /** 旧版 Items.IRON_HORSE_ARMOR → tconstruct_nirvana:iron_horse_armors。 */
    public static final TagKey<Item> IRON_HORSE_ARMORS = itemTag(TConstructNirvana.MODID, "iron_horse_armors");
    /** 旧版 Items.GOLDEN_HORSE_ARMOR → tconstruct_nirvana:golden_horse_armors。 */
    public static final TagKey<Item> GOLDEN_HORSE_ARMORS = itemTag(TConstructNirvana.MODID, "golden_horse_armors");
    /** 旧版 oredict "clay"（粘土物品）→ tconstruct_nirvana:clay。 */
    public static final TagKey<Item> CLAY = itemTag(TConstructNirvana.MODID, "clay");
    /** 旧版 oredict "blockClay" → tconstruct_nirvana:clay_blocks。 */
    public static final TagKey<Item> CLAY_BLOCKS = itemTag(TConstructNirvana.MODID, "clay_blocks");
    /** 旧版 Blocks.BONE_BLOCK → tconstruct_nirvana:bone_blocks（无 c: 对应项）。 */
    public static final TagKey<Item> BONE_BLOCKS = itemTag(TConstructNirvana.MODID, "bone_blocks");
    /** 旧版 Blocks.STAINED_HARDENED_CLAY（染色陶瓦，浇铸成硬化粘土）→ tconstruct_nirvana:stained_terracotta。 */
    public static final TagKey<Item> STAINED_TERRACOTTA = itemTag(TConstructNirvana.MODID, "stained_terracotta");
    /** 普通沙（浇铸红沙用，旧版 Blocks.SAND meta 0；排除红沙防配方自循环）。 */
    public static final TagKey<Item> SAND = itemTag(TConstructNirvana.MODID, "sand");

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
