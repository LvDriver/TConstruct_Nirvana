package com.lvdriver.tconstruct_nirvana.material;

import com.lvdriver.tconstruct_nirvana.util.HarvestLevels;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 全部工具材料的静态注册（1:1 移植自 Tinkers' Antique {@code TinkerMaterials}）。
 *
 * <p>所有材料默认隐藏，由集成逻辑（创造标签页等）调用 {@code setVisible} 激活。
 * 属性数值与特质关联严格对照旧版 {@code TinkerMaterials}，未做任何调整。</p>
 *
 * <p>迁移说明：旧版 {@code addItem* / setRepresentativeItem / safeAdd}（矿物词典物品关联）
 * 已由 {@link #registerItemAssociations()} 以 1.21.1 TagKey 体系补全
 * （{@link com.lvdriver.tconstruct_nirvana.util.ItemTagMatch}）；
 * 特质以字符串标识记录（如 "momentum"），Trait 实现见修饰符会话。</p>
 */
public final class ModMaterials {

    /** 全部已注册材料（顺序即注册顺序）。 */
    public static final List<Material> materials = new ArrayList<>();

    // 自然资源/方块
    public static final Material wood = mat("wood", 0x8e661b);
    public static final Material stone = mat("stone", 0x999999);
    public static final Material flint = mat("flint", 0x696969);
    public static final Material cactus = mat("cactus", 0x00a10f);
    public static final Material bone = mat("bone", 0xede6bf);
    public static final Material obsidian = mat("obsidian", 0x601cc4);
    public static final Material prismarine = mat("prismarine", 0x7edebc);
    public static final Material endstone = mat("endstone", 0xe0d890);
    public static final Material paper = mat("paper", 0xffffff);
    public static final Material sponge = mat("sponge", 0xcacc4e);
    public static final Material firewood = mat("firewood", 0xcc5300);

    // 史莱姆
    public static final Material knightslime = mat("knightslime", 0xf18ff0);
    public static final Material slime = mat("slime", 0x82c873);
    public static final Material blueslime = mat("blueslime", 0x74c8c7);
    public static final Material magmaslime = mat("magmaslime", 0xff960d);

    // 金属
    public static final Material iron = mat("iron", 0xcacaca);
    public static final Material pigiron = mat("pigiron", 0xef9e9b);

    // 下界材料
    public static final Material netherrack = mat("netherrack", 0xb84f4f);
    public static final Material ardite = mat("ardite", 0xd14210);
    public static final Material cobalt = mat("cobalt", 0x2882d4);
    public static final Material manyullyn = mat("manyullyn", 0xa15cf8);

    // 特殊骨材料
    public static final Material bloodbone = mat("bloodbone", 0xc70000);

    // 通用金属与合金
    public static final Material copper = mat("copper", 0xed9f07);
    public static final Material bronze = mat("bronze", 0xe3bd68);
    public static final Material lead = mat("lead", 0x4d4968);
    public static final Material silver = mat("silver", 0xd1ecf6);
    public static final Material electrum = mat("electrum", 0xe8db49);
    public static final Material steel = mat("steel", 0xa7a7a7);
    public static final Material alubrass = mat("alubrass", 0xf0d467);
    public static final Material alumite = mat("alumite", 0xffa7e9);

    // 弓弦材料
    public static final Material string = mat("string", 0xeeeeee);
    public static final Material vine = mat("vine", 0x40a10f);
    public static final Material slimevine_blue = mat("slimevine_blue", 0x74c8c7);
    public static final Material slimevine_purple = mat("slimevine_purple", 0xc873c8);

    // 额外箭杆材料
    public static final Material blaze = mat("blaze", 0xffc100);
    public static final Material reed = mat("reed", 0xaadb74);
    public static final Material ice = mat("ice", 0x97d7e0);
    public static final Material endrod = mat("endrod", 0xe8ffd6);

    // 箭羽材料
    public static final Material feather = mat("feather", 0xeeeeee);
    public static final Material leaf = mat("leaf", 0x1d730c);
    public static final Material slimeleaf_blue = mat("slimeleaf_blue", 0x74c8c7);
    public static final Material slimeleaf_orange = mat("slimeleaf_orange", 0xff960d);
    public static final Material slimeleaf_purple = mat("slimeleaf_purple", 0xc873c8);

    /** 不稳定材料（特殊用途，未注册属性数据）。 */
    public static final Material unstable = new Material("unstable", 0xffffff);

    private ModMaterials() {
    }

    private static Material mat(String name, int color) {
        // 默认隐藏，集成逻辑按需激活
        Material material = new Material(name, color, true);
        materials.add(material);
        return material;
    }

    /** 材料全部数据注册（属性 1:1 + 特质关联 1:1）。由 Mod 入口调用一次。 */
    public static void init() {
        registerCraftableFlags();
        registerToolMaterialStats();
        registerBowMaterialStats();
        registerProjectileMaterialStats();
        registerTraits();
        registerItemAssociations();
        registerFluidAssociations();
    }

    /** 材料 ↔ 流体关联（1:1 自旧版 MaterialIntegration.integrateFluid：金属材料关联熔融流体，
     * 关联后材料可浇铸、流体可熔炼对应物品，冶炼炉会话接入）。 */
    private static void registerFluidAssociations() {
        associateFluid(iron, "molten_iron");
        associateFluid(pigiron, "molten_pigiron");
        associateFluid(cobalt, "molten_cobalt");
        associateFluid(ardite, "molten_ardite");
        associateFluid(manyullyn, "molten_manyullyn");
        associateFluid(knightslime, "molten_knightslime");
        associateFluid(alubrass, "molten_alubrass");
        associateFluid(alumite, "molten_alumite");
        associateFluid(copper, "molten_copper");
        associateFluid(bronze, "molten_bronze");
        associateFluid(lead, "molten_lead");
        associateFluid(silver, "molten_silver");
        associateFluid(electrum, "molten_electrum");
        associateFluid(steel, "molten_steel");
    }

    private static void associateFluid(Material material, String fluidId) {
        material.setFluid(fluidId);
    }

    /** 可制作/可浇铸标记（1:1 自旧版 setupMaterials）。 */
    private static void registerCraftableFlags() {
        wood.setCraftable(true);
        stone.setCraftable(true);
        flint.setCraftable(true);
        cactus.setCraftable(true);
        bone.setCraftable(true);
        obsidian.setCraftable(true).setCastable(true);
        prismarine.setCraftable(true);
        netherrack.setCraftable(true);
        endstone.setCraftable(true);
        paper.setCraftable(true);
        sponge.setCraftable(true);
        firewood.setCraftable(true);
        slime.setCraftable(true);
        blueslime.setCraftable(true);
        knightslime.setCraftable(true);
        magmaslime.setCraftable(true);
        bloodbone.setCraftable(true);
    }

    /** 近战属性（头部/手柄/附加部件），数值 1:1 自旧版 registerToolMaterialStats。 */
    private static void registerToolMaterialStats() {
        // 天然资源/方块 —— (耐久, 速度, 攻击, 采掘等级) / (手柄系数, 手柄耐久) / (附加耐久)
        TConStats.add(wood, new HeadMaterialStats(35, 2.00f, 2.00f, HarvestLevels.STONE),
                new HandleMaterialStats(1.00f, 25), new ExtraMaterialStats(15));
        TConStats.add(stone, new HeadMaterialStats(120, 4.00f, 3.00f, HarvestLevels.IRON),
                new HandleMaterialStats(0.50f, -50), new ExtraMaterialStats(20));
        TConStats.add(flint, new HeadMaterialStats(150, 5.00f, 2.90f, HarvestLevels.IRON),
                new HandleMaterialStats(0.60f, -60), new ExtraMaterialStats(40));
        TConStats.add(cactus, new HeadMaterialStats(210, 4.00f, 3.40f, HarvestLevels.IRON),
                new HandleMaterialStats(0.85f, 20), new ExtraMaterialStats(50));
        TConStats.add(bone, new HeadMaterialStats(200, 5.09f, 2.50f, HarvestLevels.IRON),
                new HandleMaterialStats(1.10f, 50), new ExtraMaterialStats(65));
        TConStats.add(obsidian, new HeadMaterialStats(139, 7.07f, 4.20f, HarvestLevels.COBALT),
                new HandleMaterialStats(0.90f, -100), new ExtraMaterialStats(90));
        TConStats.add(prismarine, new HeadMaterialStats(430, 5.50f, 6.20f, HarvestLevels.IRON),
                new HandleMaterialStats(0.60f, -150), new ExtraMaterialStats(100));
        TConStats.add(endstone, new HeadMaterialStats(420, 3.23f, 3.23f, HarvestLevels.OBSIDIAN),
                new HandleMaterialStats(0.85f, 0), new ExtraMaterialStats(42));
        TConStats.add(paper, new HeadMaterialStats(12, 0.51f, 0.05f, HarvestLevels.STONE),
                new HandleMaterialStats(0.10f, 5), new ExtraMaterialStats(15));
        TConStats.add(sponge, new HeadMaterialStats(1050, 3.02f, 0.00f, HarvestLevels.STONE),
                new HandleMaterialStats(1.20f, 250), new ExtraMaterialStats(250));

        // 史莱姆
        TConStats.add(slime, new HeadMaterialStats(1000, 4.24f, 1.80f, HarvestLevels.STONE),
                new HandleMaterialStats(0.70f, 0), new ExtraMaterialStats(350));
        TConStats.add(blueslime, new HeadMaterialStats(780, 4.03f, 1.80f, HarvestLevels.STONE),
                new HandleMaterialStats(1.30f, -50), new ExtraMaterialStats(200));
        TConStats.add(knightslime, new HeadMaterialStats(850, 5.8f, 5.10f, HarvestLevels.OBSIDIAN),
                new HandleMaterialStats(0.50f, 500), new ExtraMaterialStats(125));
        TConStats.add(magmaslime, new HeadMaterialStats(600, 2.1f, 7.00f, HarvestLevels.STONE),
                new HandleMaterialStats(0.85f, -200), new ExtraMaterialStats(150));

        // 下界
        TConStats.add(netherrack, new HeadMaterialStats(270, 4.50f, 3.00f, HarvestLevels.IRON),
                new HandleMaterialStats(0.85f, -150), new ExtraMaterialStats(75));
        TConStats.add(cobalt, new HeadMaterialStats(780, 12.00f, 4.10f, HarvestLevels.COBALT),
                new HandleMaterialStats(0.90f, 100), new ExtraMaterialStats(300));
        TConStats.add(ardite, new HeadMaterialStats(990, 3.50f, 3.60f, HarvestLevels.COBALT),
                new HandleMaterialStats(1.40f, -200), new ExtraMaterialStats(450));
        TConStats.add(manyullyn, new HeadMaterialStats(820, 7.02f, 8.72f, HarvestLevels.COBALT),
                new HandleMaterialStats(0.50f, 250), new ExtraMaterialStats(50));
        TConStats.add(firewood, new HeadMaterialStats(550, 6.00f, 5.50f, HarvestLevels.STONE),
                new HandleMaterialStats(1.0f, -200), new ExtraMaterialStats(150));

        // 特殊骨材料
        TConStats.add(bloodbone, new HeadMaterialStats(200, 5.09f, 2.50f, HarvestLevels.IRON),
                new HandleMaterialStats(1.10f, 50), new ExtraMaterialStats(65));

        // 金属
        TConStats.add(iron, new HeadMaterialStats(204, 6.00f, 4.00f, HarvestLevels.DIAMOND),
                new HandleMaterialStats(0.85f, 60), new ExtraMaterialStats(50));
        TConStats.add(pigiron, new HeadMaterialStats(380, 6.20f, 4.50f, HarvestLevels.DIAMOND),
                new HandleMaterialStats(1.20f, 0), new ExtraMaterialStats(170));

        // 通用金属
        TConStats.add(copper, new HeadMaterialStats(210, 5.30f, 3.00f, HarvestLevels.IRON),
                new HandleMaterialStats(1.05f, 30), new ExtraMaterialStats(100));
        TConStats.add(bronze, new HeadMaterialStats(430, 6.80f, 3.50f, HarvestLevels.DIAMOND),
                new HandleMaterialStats(1.10f, 70), new ExtraMaterialStats(80));
        TConStats.add(lead, new HeadMaterialStats(434, 5.25f, 3.50f, HarvestLevels.IRON),
                new HandleMaterialStats(0.70f, -50), new ExtraMaterialStats(100));
        TConStats.add(silver, new HeadMaterialStats(250, 5.00f, 5.00f, HarvestLevels.IRON),
                new HandleMaterialStats(0.95f, 50), new ExtraMaterialStats(150));
        TConStats.add(electrum, new HeadMaterialStats(50, 12.00f, 3.00f, HarvestLevels.IRON),
                new HandleMaterialStats(1.10f, -25), new ExtraMaterialStats(250));
        TConStats.add(steel, new HeadMaterialStats(540, 7.00f, 6.00f, HarvestLevels.OBSIDIAN),
                new HandleMaterialStats(0.9f, 150), new ExtraMaterialStats(25));
        TConStats.add(alubrass, new HeadMaterialStats(450, 7.00f, 3.50f, HarvestLevels.DIAMOND),
                new HandleMaterialStats(1.10f, 80), new ExtraMaterialStats(90));
        TConStats.add(alumite, new HeadMaterialStats(700, 8.00f, 5.50f, HarvestLevels.COBALT),
                new HandleMaterialStats(1.25f, 150), new ExtraMaterialStats(100));
    }

    /** 弓身/弓弦属性，数值 1:1 自旧版 registerBowMaterialStats。 */
    private static void registerBowMaterialStats() {
        BowMaterialStats terrible = new BowMaterialStats(0.2f, 0.4f, -1f);

        TConStats.add(wood, new BowMaterialStats(1f, 1f, 0));
        TConStats.add(stone, terrible);
        TConStats.add(flint, terrible);
        TConStats.add(cactus, new BowMaterialStats(1.05f, 0.9f, 0));
        TConStats.add(bone, new BowMaterialStats(0.95f, 1.15f, 0));
        TConStats.add(obsidian, terrible);
        TConStats.add(prismarine, terrible);
        TConStats.add(endstone, terrible);
        TConStats.add(paper, new BowMaterialStats(1.5f, 0.4f, -2f));
        TConStats.add(sponge, new BowMaterialStats(1.15f, 0.75f, 0));

        // 史莱姆
        TConStats.add(slime, new BowMaterialStats(0.85f, 1.3f, 0));
        TConStats.add(blueslime, new BowMaterialStats(1.05f, 1f, 0));
        TConStats.add(knightslime, new BowMaterialStats(0.4f, 2f, 2f));
        TConStats.add(magmaslime, new BowMaterialStats(1.1f, 1.05f, 1f));

        // 下界
        TConStats.add(netherrack, terrible);
        TConStats.add(cobalt, new BowMaterialStats(0.75f, 1.3f, 3f));
        TConStats.add(ardite, new BowMaterialStats(0.45f, 0.8f, 1f));
        TConStats.add(manyullyn, new BowMaterialStats(0.65f, 1.2f, 4f));
        TConStats.add(firewood, new BowMaterialStats(1f, 1f, 0f));

        // 金属
        TConStats.add(iron, new BowMaterialStats(0.5f, 1.5f, 7f));
        TConStats.add(pigiron, new BowMaterialStats(0.6f, 1.4f, 7f));

        // 特殊骨材料
        TConStats.add(bloodbone, new BowMaterialStats(0.95f, 1.15f, 0));

        // 通用金属
        TConStats.add(copper, new BowMaterialStats(0.6f, 1.45f, 5f));
        TConStats.add(bronze, new BowMaterialStats(0.55f, 1.5f, 6f));
        TConStats.add(lead, new BowMaterialStats(0.4f, 1.3f, 3f));
        TConStats.add(silver, new BowMaterialStats(1.2f, 0.8f, 2f));
        TConStats.add(electrum, new BowMaterialStats(1.5f, 1f, 4f));
        TConStats.add(steel, new BowMaterialStats(0.4f, 2f, 9f));
        TConStats.add(alubrass, new BowMaterialStats(0.45f, 1.5f, 6f));
        TConStats.add(alumite, new BowMaterialStats(0.4f, 1.5f, 8f));

        // 弓弦
        BowStringMaterialStats bowstring = new BowStringMaterialStats(1f);
        TConStats.add(string, bowstring);
        TConStats.add(vine, bowstring);
        TConStats.add(slimevine_blue, bowstring);
        TConStats.add(slimevine_purple, bowstring);
    }

    /** 弹射物属性（箭杆/箭羽），数值 1:1 自旧版 registerProjectileMaterialStats。 */
    private static void registerProjectileMaterialStats() {
        // 箭杆
        TConStats.add(wood, new ArrowShaftMaterialStats(1f, 0));
        TConStats.add(bone, new ArrowShaftMaterialStats(0.9f, 5));
        TConStats.add(blaze, new ArrowShaftMaterialStats(0.8f, 3));
        TConStats.add(reed, new ArrowShaftMaterialStats(1.5f, 20));
        TConStats.add(ice, new ArrowShaftMaterialStats(0.95f, 0));
        TConStats.add(endrod, new ArrowShaftMaterialStats(0.7f, 1));

        // 箭羽
        TConStats.add(feather, new FletchingMaterialStats(1.0f, 1f));
        TConStats.add(leaf, new FletchingMaterialStats(0.5f, 1.5f));
        FletchingMaterialStats slimeLeafStats = new FletchingMaterialStats(0.8f, 1.25f);
        TConStats.add(slimeleaf_purple, slimeLeafStats);
        TConStats.add(slimeleaf_blue, slimeLeafStats);
        TConStats.add(slimeleaf_orange, slimeLeafStats);
    }

    /**
     * 特质关联（1:1 自旧版 setupMaterials）。
     *
     * <p>特质以字符串标识记录：旧版 {@code TraitX} 的标识为 {@code "x"}（小写类名去前缀），
     * 分级特质为 {@code "x"} + 等级数字（如 TraitMagnetic(1)→"magnetic1"、TraitMagnetic(2)→"magnetic2"），
     * 特殊命名见 TraitSlimey("green")→"slimey_green"。Trait 实现在修饰符会话落地。</p>
     */
    private static void registerTraits() {
        // 天然资源/方块
        wood.addTrait("ecological");
        stone.addTrait("cheapskate", MaterialTypes.HEAD).addTrait("cheap");
        flint.addTrait("crude2", MaterialTypes.HEAD).addTrait("crude1");
        cactus.addTrait("prickly", MaterialTypes.HEAD).addTrait("spiky");
        obsidian.addTrait("duritos");
        prismarine.addTrait("jagged", MaterialTypes.HEAD)
                .addTrait("aquadynamic", MaterialTypes.HEAD)
                .addTrait("aquadynamic");
        netherrack.addTrait("aridiculous", MaterialTypes.HEAD)
                .addTrait("hellish", MaterialTypes.HEAD)
                .addTrait("hellish");
        endstone.addTrait("alien", MaterialTypes.HEAD)
                .addTrait("enderference")
                .addTrait("enderference", MaterialTypes.PROJECTILE);
        bone.addTrait("splintering", MaterialTypes.HEAD)
                .addTrait("splitting", MaterialTypes.SHAFT)
                .addTrait("fractured");
        paper.addTrait("writable2", MaterialTypes.HEAD).addTrait("writable1");
        sponge.addTrait("squeaky");
        firewood.addTrait("autosmelt");

        // 史莱姆
        slime.addTrait("slimey_green");
        blueslime.addTrait("slimey_blue");
        knightslime.addTrait("crumbling", MaterialTypes.HEAD).addTrait("unnatural");
        magmaslime.addTrait("superheat", MaterialTypes.HEAD).addTrait("flammable");

        // 金属
        iron.addTrait("magnetic2", MaterialTypes.HEAD).addTrait("magnetic1");
        pigiron.addTrait("baconlicious", MaterialTypes.HEAD)
                .addTrait("tasty", MaterialTypes.HEAD)
                .addTrait("tasty");

        // 下界
        cobalt.addTrait("momentum", MaterialTypes.HEAD).addTrait("lightweight");
        ardite.addTrait("stonebound", MaterialTypes.HEAD).addTrait("petramor");
        manyullyn.addTrait("insatiable", MaterialTypes.HEAD).addTrait("coldblooded");

        // 特殊骨材料
        bloodbone.addTrait("raging2", MaterialTypes.HEAD)
                .addTrait("splintering", MaterialTypes.HEAD)
                .addTrait("raging1")
                .addTrait("fractured");

        // 通用金属
        copper.addTrait("established");
        bronze.addTrait("dense");
        lead.addTrait("poisonous").addTrait("heavy");
        silver.addTrait("holy");
        electrum.addTrait("shocking");
        steel.addTrait("sharp", MaterialTypes.HEAD).addTrait("stiff");
        alubrass.addTrait("depthdigger");
        alumite.addTrait("duritos");

        // 额外箭杆
        blaze.addTrait("hovering");
        reed.addTrait("breakable");
        ice.addTrait("freezing");
        endrod.addTrait("endspeed");
    }

    /**
     * 材料↔物品关联（1:1 自旧版 setupMaterials 的 addItem/addCommonItems/
     * setRepresentativeItem，矿物词典名映射为 1.21.1 Tag）。
     *
     * <p>旧版 oredict 名映射规则：有 c: 约定的直接对应（{@code ingotCobalt}→
     * {@code c:ingots/cobalt}、{@code stickWood}→{@code c:rods/wooden} 等）；
     * 无 c: 对应项的原版物品用 mod 命名空间 tag（{@link TConTags}，DataGen 加入
     * 原版物品）；金属/合金锭一律走 {@code c:} 约定（锭/粒/块三件套），其他 mod
     * 的对应物品天然互通。</p>
     *
     * <p>迁移说明：旧版仅关联本 mod 物品的关联（firewood、slimecrystal*、
     * boneBloodied、slimevine*、slimeleaf*）因对应物品尚未注册，留待物品注册会话；
     * 合金金属（pigiron 等）c: tag 现为空，附属/物品注册后自动生效。</p>
     */
    private static void registerItemAssociations() {
        // 天然资源/方块（旧版 oredict → 1.21.1 Tag）
        wood.addItem(Tags.Items.RODS_WOODEN, Material.VALUE_Shard)          // stickWood
                .addItem(ItemTags.PLANKS, Material.VALUE_Ingot)             // plankWood
                .addItem(ItemTags.LOGS, Material.VALUE_Ingot * 4)           // logWood
                .setRepresentativeItem(ItemTags.LOGS);
        stone.addItemIngot(Tags.Items.COBBLESTONES)                         // cobblestone
                .addItemIngot(Tags.Items.STONES)                            // stone
                .setRepresentativeItem(Items.COBBLESTONE);
        flint.addItem(TConTags.FLINT, Material.VALUE_Ingot)                 // Items.FLINT
                .setRepresentativeItem(Items.FLINT);
        cactus.addItemIngot(TConTags.CACTUS)                                // blockCactus
                .setRepresentativeItem(Items.CACTUS);
        bone.addItemIngot(Tags.Items.BONES)                                 // bone
                .addItem(TConTags.BONE_MEAL, Material.VALUE_Fragment)       // bonemeal（旧版 DYE 白）
                .setRepresentativeItem(Items.BONE);
        obsidian.addItemIngot(Tags.Items.OBSIDIANS)                         // obsidian
                .setRepresentativeItem(Items.OBSIDIAN);
        prismarine.addItem(Tags.Items.GEMS_PRISMARINE, Material.VALUE_Fragment)                    // gemPrismarine
                .addItem(TConTags.STORAGE_BLOCKS_PRISMARINE, Material.VALUE_Ingot)                 // blockPrismarine
                .addItem(TConTags.STORAGE_BLOCKS_PRISMARINE_BRICKS, Material.VALUE_Fragment * 9)   // blockPrismarineBrick
                .addItem(TConTags.STORAGE_BLOCKS_DARK_PRISMARINE, Material.VALUE_Ingot * 2)        // blockPrismarineDark
                .setRepresentativeItem(Items.PRISMARINE);
        endstone.addItemIngot(Tags.Items.END_STONES)                        // endstone
                .setRepresentativeItem(Items.END_STONE);
        paper.addItem(TConTags.PAPER, Material.VALUE_Fragment)              // paper
                .setRepresentativeItem(Items.PAPER);
        sponge.addItem(TConTags.SPONGES, Material.VALUE_Ingot)              // Blocks.SPONGE
                .setRepresentativeItem(Items.SPONGE);
        // firewood：旧版关联本 mod firewood 物品，物品未注册，留待物品注册会话

        // 史莱姆
        knightslime.addCommonItems("knightslime");                          // ingot/nugget/block Knightslime
        // slime/blueslime/magmaslime：旧版关联 slimecrystal*，物品未注册，留待物品注册会话

        // 下界
        netherrack.addItemIngot(Tags.Items.NETHERRACKS)                     // netherrack
                .setRepresentativeItem(Items.NETHERRACK);
        cobalt.addCommonItems("cobalt");                                    // ingot/nugget/block Cobalt
        ardite.addCommonItems("ardite");                                    // ingot/nugget/block Ardite
        manyullyn.addCommonItems("manyullyn");                              // ingot/nugget/block Manyullyn

        // 特殊骨材料（boneBloodied 物品未注册，留待物品注册会话）

        // 金属与合金（addCommonItems 全量，c: tag 空时待附属/物品注册填充）
        iron.addCommonItems("iron").setRepresentativeItem(Items.IRON_INGOT);
        pigiron.addCommonItems("pig_iron");
        copper.addCommonItems("copper").setRepresentativeItem(Items.COPPER_INGOT);
        bronze.addCommonItems("bronze");
        lead.addCommonItems("lead");
        silver.addCommonItems("silver");
        electrum.addCommonItems("electrum");
        steel.addCommonItems("steel");
        alubrass.addCommonItems("alubrass");
        alumite.addCommonItems("alumite");

        // 弓弦
        string.addItemIngot(Tags.Items.STRINGS)                             // string
                .setRepresentativeItem(Items.STRING);
        vine.addItemIngot(TConTags.VINES)                                   // vine
                .setRepresentativeItem(Items.VINE);
        // slimevine_*：旧版关联本 mod slimeVine 物品，物品未注册，留待物品注册会话

        // 额外箭杆
        blaze.addItem(Tags.Items.RODS_BLAZE, Material.VALUE_Ingot)          // Items.BLAZE_ROD
                .setRepresentativeItem(Items.BLAZE_ROD);
        reed.addItem(Tags.Items.CROPS_SUGAR_CANE, Material.VALUE_Ingot)     // Items.REEDS
                .setRepresentativeItem(Items.SUGAR_CANE);
        ice.addItem(TConTags.PACKED_ICE, Material.VALUE_Ingot)              // Blocks.PACKED_ICE
                .setRepresentativeItem(Items.PACKED_ICE);
        endrod.addItem(TConTags.END_RODS, Material.VALUE_Ingot)             // Blocks.END_ROD
                .setRepresentativeItem(Items.END_ROD);

        // 箭羽
        feather.addItemIngot(Tags.Items.FEATHERS)                           // feather
                .setRepresentativeItem(Items.FEATHER);
        leaf.addItem(ItemTags.LEAVES, Material.VALUE_Shard)                 // treeLeaves
                .setRepresentativeItem(Items.OAK_LEAVES);
        // slimeleaf_*：旧版关联本 mod slimeLeaves，物品未注册，留待物品注册会话
    }

    /** 全部材料（含隐藏），只读视图。 */
    public static List<Material> getAllMaterials() {
        return Collections.unmodifiableList(materials);
    }

    /** 按标识查找材料，未找到返回 null。 */
    public static Material getMaterial(String identifier) {
        for (Material material : materials) {
            if (material.identifier.equals(identifier)) {
                return material;
            }
        }
        return null;
    }

    /**
     * 附属扩展入口：登记新材料（经 {@code TConstructNirvanaAPI.materials()} 调用）。
     *
     * <p>identifier 已存在时替换原条目（保持注册顺序），与旧版 {@code safeAdd}
     * 语义一致；未存在则追加到末尾。属性/特质挂载在登记后随时可调。</p>
     */
    public static Material registerMaterial(Material material) {
        int index = -1;
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).identifier.equals(material.identifier)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            materials.set(index, material);
        } else {
            materials.add(material);
        }
        return material;
    }
}
