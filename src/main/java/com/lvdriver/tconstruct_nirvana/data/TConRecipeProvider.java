package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.recipe.AlloyRecipeBuilder;
import com.lvdriver.tconstruct_nirvana.recipe.BucketCastingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.CastingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.MeltingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import com.lvdriver.tconstruct_nirvana.recipe.PartRecipe;
import com.lvdriver.tconstruct_nirvana.util.ItemTagMatch;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 配方生成（DataGen）。
 *
 * <p>覆盖全部自定义配方类型：
 * <ul>
 *   <li>工具组装（crafting_special，会话4）与工具修复（会话4.5b）；</li>
 *   <li>合金 10 条（会话5）；</li>
 *   <li>熔炼（物品 → 流体 + 熔点，输入一律 {@code c:} Tag）、浇铸（模具 + 流体 →
 *   物品，输出支持静态物品 / 动态 {@code c:} Tag 首选）、桶浇铸、部件制作
 *   （本会话新增，数据 1:1 自旧版 {@code TinkerSmeltery.registerMeltingCasting /
 *   registerOredictMeltingCasting} 与 {@code ToolBuilder.tryBuildToolPart}）。</li>
 * </ul></p>
 */
public class TConRecipeProvider extends RecipeProvider {

    /** 矿石价值（1:1 旧版 {@code Material.VALUE_Ore()} = 锭 × oreToIngotRatio(2) = 288）。 */
    private static final int VALUE_ORE = Material.VALUE_Ingot * 2;

    /** 金属：流体注册名 → c: tag 路径（1:1 旧版 registerOredictMeltingCasting 的 ore 后缀，pig_iron 等见 Material 关联）。 */
    private static final Map<String, String> METALS = new LinkedHashMap<>();

    static {
        METALS.put("molten_iron", "iron");
        METALS.put("molten_gold", "gold");
        METALS.put("molten_pigiron", "pig_iron");
        METALS.put("molten_cobalt", "cobalt");
        METALS.put("molten_ardite", "ardite");
        METALS.put("molten_manyullyn", "manyullyn");
        METALS.put("molten_knightslime", "knightslime");
        METALS.put("molten_alubrass", "alubrass");
        METALS.put("molten_alumite", "alumite");
        METALS.put("molten_brass", "brass");
        METALS.put("molten_copper", "copper");
        METALS.put("molten_tin", "tin");
        METALS.put("molten_bronze", "bronze");
        METALS.put("molten_zinc", "zinc");
        METALS.put("molten_lead", "lead");
        METALS.put("molten_nickel", "nickel");
        METALS.put("molten_silver", "silver");
        METALS.put("molten_electrum", "electrum");
        METALS.put("molten_steel", "steel");
        METALS.put("molten_aluminum", "aluminum");
    }

    public TConRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        SpecialRecipeBuilder.special(category -> new com.lvdriver.tconstruct_nirvana.recipe.ToolBuildRecipe(category))
                .save(output, TConstructNirvana.MODID + ":tool_build");
        // 工具修复：工具 + 磨刀石（1:1 旧版 RepairRecipe）
        SpecialRecipeBuilder.special(category -> new com.lvdriver.tconstruct_nirvana.recipe.RepairRecipe(category))
                .save(output, TConstructNirvana.MODID + ":repair");

        buildAlloyRecipes(output);
        buildBucketCasting(output);
        buildMeltingRecipes(output);
        buildCastingRecipes(output);
        buildPartRecipes(output);
    }

    // ==================== 熔炼（物品 → 流体 + 熔点） ====================

    /**
     * 熔炼配方（1:1 自旧版 {@code TinkerSmeltery.registerMeltingCasting} 与
     * {@code registerOredictMeltingCasting}；输入统一转 {@code c:} / mod 命名空间 Tag）。
     */
    private void buildMeltingRecipes(RecipeOutput output) {
        int bucket = 1000;

        // 水（旧版：冰/浮冰/雪/雪球，显式温度 305/310/301）
        melting(output, "water_ice", itemTag(TConTags.ICE, bucket), water(bucket), 305);
        melting(output, "water_packed_ice", itemTag(TConTags.PACKED_ICE, bucket * 2), water(bucket * 2), 310);
        melting(output, "water_snow", itemTag(TConTags.SNOW, bucket), water(bucket), 305);
        melting(output, "water_snowball", itemTag(TConTags.SNOWBALLS, bucket / 8), water(bucket / 8), 301);

        // 血 / 毒液（旧版量 40mb，温度自动）
        melting(output, "blood_rotten_flesh", itemTag(TConTags.ROTTEN_FLESH, 40), fluid(ModFluids.BLOOD, 40));
        melting(output, "venom_spider_eye", itemTag(TConTags.SPIDER_EYES, 40), fluid(ModFluids.VENOM, 40));
        melting(output, "venom_pufferfish", itemTag(TConTags.PUFFERFISH, 40), fluid(ModFluids.VENOM, 40));

        // 史莱姆（旧版史莱姆球 250mb）
        melting(output, "greenslime_slime_ball", itemTag(Tags.Items.SLIME_BALLS, Material.VALUE_SlimeBall),
                fluid(ModFluids.GREEN_SLIME, Material.VALUE_SlimeBall));

        // 钙（旧版：骨粉 48 / 骨 144 / 骨块 432，1:1 温度自动）
        melting(output, "calcium_bone_meal", itemTag(TConTags.BONE_MEAL, Material.VALUE_Ingot / 3),
                fluid(ModFluids.MOLTEN_CALCIUM, Material.VALUE_Ingot / 3));
        melting(output, "calcium_bone", itemTag(Tags.Items.BONES, Material.VALUE_Ingot),
                fluid(ModFluids.MOLTEN_CALCIUM, Material.VALUE_Ingot));
        melting(output, "calcium_bone_block", itemTag(TConTags.BONE_BLOCKS, Material.VALUE_Ingot * 3),
                fluid(ModFluids.MOLTEN_CALCIUM, Material.VALUE_Ingot * 3));

        // 石头类（旧版 forAmount：价值 72/288，按整块价值 288 计算温度）
        melting(output, "stone_cobblestone", itemTag(Tags.Items.COBBLESTONES, Material.VALUE_SearedMaterial),
                fluid(ModFluids.MOLTEN_STONE, Material.VALUE_SearedMaterial),
                MeltingRecipe.calcTemperature(ModFluids.MOLTEN_STONE.still().get().getFluidType().getTemperature(), VALUE_ORE));
        melting(output, "stone_stone", itemTag(Tags.Items.STONES, Material.VALUE_SearedMaterial),
                fluid(ModFluids.MOLTEN_STONE, Material.VALUE_SearedMaterial),
                MeltingRecipe.calcTemperature(ModFluids.MOLTEN_STONE.still().get().getFluidType().getTemperature(), VALUE_ORE));
        melting(output, "obsidian", itemTag(Tags.Items.OBSIDIANS, VALUE_ORE),
                fluid(ModFluids.MOLTEN_OBSIDIAN, VALUE_ORE),
                MeltingRecipe.calcTemperature(ModFluids.MOLTEN_OBSIDIAN.still().get().getFluidType().getTemperature(), VALUE_ORE));

        // 铁轨 / 马铠（旧版 1:1 量：普通铁轨 6/16 锭 = 54，其余 1 锭；马铠 4 锭）
        melting(output, "iron_rail", itemTag(TConTags.RAILS, Material.VALUE_Ingot * 6 / 16),
                fluid(ModFluids.MOLTEN_IRON, Material.VALUE_Ingot * 6 / 16));
        melting(output, "iron_activator_rail", itemTag(TConTags.ACTIVATOR_RAILS, Material.VALUE_Ingot),
                fluid(ModFluids.MOLTEN_IRON, Material.VALUE_Ingot));
        melting(output, "iron_detector_rail", itemTag(TConTags.DETECTOR_RAILS, Material.VALUE_Ingot),
                fluid(ModFluids.MOLTEN_IRON, Material.VALUE_Ingot));
        melting(output, "gold_golden_rail", itemTag(TConTags.GOLDEN_RAILS, Material.VALUE_Ingot),
                fluid(ModFluids.MOLTEN_GOLD, Material.VALUE_Ingot));
        melting(output, "iron_horse_armor", itemTag(TConTags.IRON_HORSE_ARMORS, Material.VALUE_Ingot * 4),
                fluid(ModFluids.MOLTEN_IRON, Material.VALUE_Ingot * 4));
        melting(output, "gold_horse_armor", itemTag(TConTags.GOLDEN_HORSE_ARMORS, Material.VALUE_Ingot * 4),
                fluid(ModFluids.MOLTEN_GOLD, Material.VALUE_Ingot * 4));

        // 粘土 family（旧版 addKnownOreFluid：clay 144 / blockClay 576）
        melting(output, "clay_clay", itemTag(TConTags.CLAY, Material.VALUE_Ingot),
                fluid(ModFluids.MOLTEN_CLAY, Material.VALUE_Ingot));
        melting(output, "clay_clay_block", itemTag(TConTags.CLAY_BLOCKS, Material.VALUE_BrickBlock),
                fluid(ModFluids.MOLTEN_CLAY, Material.VALUE_BrickBlock));

        // 绿宝石 / 钻石（旧版：矿石 = 2 锭 × oreToIngotRatio=2 = 1332，宝石 666，块 5994）
        melting(output, "emerald_ore", itemTag(Tags.Items.ORES_EMERALD, (int) (Material.VALUE_Gem * 2)),
                fluid(ModFluids.MOLTEN_EMERALD, (int) (Material.VALUE_Gem * 2)));
        melting(output, "emerald_gem", itemTag(Tags.Items.GEMS_EMERALD, Material.VALUE_Gem),
                fluid(ModFluids.MOLTEN_EMERALD, Material.VALUE_Gem));
        melting(output, "emerald_block", itemTag(Tags.Items.STORAGE_BLOCKS_EMERALD, Material.VALUE_Gem * 9),
                fluid(ModFluids.MOLTEN_EMERALD, Material.VALUE_Gem * 9));
        melting(output, "diamond_ore", itemTag(Tags.Items.ORES_DIAMOND, (int) (Material.VALUE_Gem * 2)),
                fluid(ModFluids.MOLTEN_DIAMOND, (int) (Material.VALUE_Gem * 2)));
        melting(output, "diamond_gem", itemTag(Tags.Items.GEMS_DIAMOND, Material.VALUE_Gem),
                fluid(ModFluids.MOLTEN_DIAMOND, Material.VALUE_Gem));
        melting(output, "diamond_block", itemTag(Tags.Items.STORAGE_BLOCKS_DIAMOND, Material.VALUE_Gem * 9),
                fluid(ModFluids.MOLTEN_DIAMOND, Material.VALUE_Gem * 9));

        // 玻璃（旧版：沙 1000 / 玻璃块 1000 / 玻璃板 6/16 块 = 375）
        melting(output, "glass_sand", itemTag(Tags.Items.SANDS, Material.VALUE_Glass),
                fluid(ModFluids.MOLTEN_GLASS, Material.VALUE_Glass));
        melting(output, "glass_block", itemTag(Tags.Items.GLASS_BLOCKS, Material.VALUE_Glass),
                fluid(ModFluids.MOLTEN_GLASS, Material.VALUE_Glass));
        melting(output, "glass_pane", itemTag(Tags.Items.GLASS_PANES, Material.VALUE_Glass * 6 / 16),
                fluid(ModFluids.MOLTEN_GLASS, Material.VALUE_Glass * 6 / 16));

        // 金属 family（1:1 旧版 registerOredictMeltingCasting 的 nugget/ingot/block/ore/plate/gear/dust；
        // oreNether/oreDense/orePoor/oreNugget 无 c: 对应 tag 跳过，见 devlog）
        for (Map.Entry<String, String> metal : METALS.entrySet()) {
            String path = metal.getValue();
            ModFluids.FluidEntry entry = ModFluids.FLUIDS_ALL.stream()
                    .filter(f -> f.id().getPath().equals(metal.getKey()))
                    .findFirst().orElseThrow();
            Fluid fluid = entry.still().get();
            melting(output, "metal_" + path + "_nugget", itemTag(metalTag("nuggets", path), Material.VALUE_Nugget),
                    fluid(entry, Material.VALUE_Nugget));
            melting(output, "metal_" + path + "_ingot", itemTag(metalTag("ingots", path), Material.VALUE_Ingot),
                    fluid(entry, Material.VALUE_Ingot));
            melting(output, "metal_" + path + "_block", itemTag(metalTag("storage_blocks", path), Material.VALUE_Block),
                    fluid(entry, Material.VALUE_Block));
            melting(output, "metal_" + path + "_ore", itemTag(metalTag("ores", path), VALUE_ORE),
                    fluid(entry, VALUE_ORE));
            melting(output, "metal_" + path + "_plate", itemTag(metalTag("plates", path), Material.VALUE_Ingot),
                    fluid(entry, Material.VALUE_Ingot));
            melting(output, "metal_" + path + "_gear", itemTag(metalTag("gears", path), Material.VALUE_Ingot * 4),
                    fluid(entry, Material.VALUE_Ingot * 4));
            melting(output, "metal_" + path + "_dust", itemTag(metalTag("dusts", path), Material.VALUE_Ingot),
                    fluid(entry, Material.VALUE_Ingot));
        }
    }

    // ==================== 浇铸（模具 + 流体 → 物品） ====================

    /** 浇铸配方（1:1 自旧版 registerTableCasting / registerBasinCasting + registerOredictMeltingCasting）。 */
    private void buildCastingRecipes(RecipeOutput output) {
        // 具体浇铸（旧版显式配方，输出静态物品；时间 1:1 旧版显式值）
        castingStatic(output, "bone", null, fluid(ModFluids.MOLTEN_CALCIUM, Material.VALUE_Ingot),
                new ItemStack(Items.BONE), 50, false, false);
        castingStatic(output, "bone_block", null, fluid(ModFluids.MOLTEN_CALCIUM, Material.VALUE_Ingot * 3),
                new ItemStack(Items.BONE_BLOCK), 100, false, false);
        castingStatic(output, "emerald", ModPatternsLoc.SHAPE_GEM, fluid(ModFluids.MOLTEN_EMERALD, Material.VALUE_Gem),
                new ItemStack(Items.EMERALD), CastingRecipe.calcCooldownTime(fluid(ModFluids.MOLTEN_EMERALD, Material.VALUE_Gem)), false, false);
        castingStatic(output, "emerald_block", null, fluid(ModFluids.MOLTEN_EMERALD, Material.VALUE_Gem * 9),
                new ItemStack(Items.EMERALD_BLOCK), CastingRecipe.calcCooldownTime(fluid(ModFluids.MOLTEN_EMERALD, Material.VALUE_Gem * 9)), false, false);
        castingStatic(output, "diamond", ModPatternsLoc.SHAPE_GEM, fluid(ModFluids.MOLTEN_DIAMOND, Material.VALUE_Gem),
                new ItemStack(Items.DIAMOND), CastingRecipe.calcCooldownTime(fluid(ModFluids.MOLTEN_DIAMOND, Material.VALUE_Gem)), false, false);
        castingStatic(output, "diamond_block", null, fluid(ModFluids.MOLTEN_DIAMOND, Material.VALUE_Gem * 9),
                new ItemStack(Items.DIAMOND_BLOCK), CastingRecipe.calcCooldownTime(fluid(ModFluids.MOLTEN_DIAMOND, Material.VALUE_Gem * 9)), false, false);
        castingStatic(output, "glass_pane", null, fluid(ModFluids.MOLTEN_GLASS, Material.VALUE_Glass * 6 / 16),
                new ItemStack(Items.GLASS_PANE), 50, false, false);
        castingStatic(output, "obsidian_block", null, fluid(ModFluids.MOLTEN_OBSIDIAN, VALUE_ORE),
                new ItemStack(Items.OBSIDIAN), CastingRecipe.calcCooldownTime(fluid(ModFluids.MOLTEN_OBSIDIAN, VALUE_ORE)), false, false);
        // 硬化粘土（旧版：无模具浇 4 锭；染色陶瓦 + 水还原为普通陶瓦，消耗模具）
        castingStatic(output, "hardened_clay", null, fluid(ModFluids.MOLTEN_CLAY, Material.VALUE_BrickBlock),
                new ItemStack(Items.TERRACOTTA), CastingRecipe.calcCooldownTime(fluid(ModFluids.MOLTEN_CLAY, Material.VALUE_BrickBlock)), false, false);
        castingTagCast(output, "hardened_clay_from_stained", TConTags.STAINED_TERRACOTTA,
                new FluidStack(Fluids.WATER, 250), new ItemStack(Items.TERRACOTTA), 150);
        // 红沙（旧版：普通沙 meta 0 + 血 10mb，消耗沙；专用 tag 排除红沙防自循环）
        castingTagCast(output, "red_sand", TConTags.SAND,
                fluid(ModFluids.BLOOD, 10), new ItemStack(Items.RED_SAND),
                CastingRecipe.calcCooldownTime(fluid(ModFluids.BLOOD, 10)));

        // 金属 family 浇铸（1:1 旧版 PreferenceCastingRecipe：输出动态取 c: tag 首选物品，任何 mod 物品可铸）
        for (Map.Entry<String, String> metal : METALS.entrySet()) {
            String path = metal.getValue();
            ModFluids.FluidEntry entry = ModFluids.FLUIDS_ALL.stream()
                    .filter(f -> f.id().getPath().equals(metal.getKey()))
                    .findFirst().orElseThrow();
            castingDynamic(output, "metal_" + path + "_ingot", ModPatternsLoc.SHAPE_INGOT,
                    fluid(entry, Material.VALUE_Ingot), metalTag("ingots", path), false, false);
            castingDynamic(output, "metal_" + path + "_nugget", ModPatternsLoc.SHAPE_NUGGET,
                    fluid(entry, Material.VALUE_Nugget), metalTag("nuggets", path), false, false);
            castingDynamic(output, "metal_" + path + "_block", null,
                    fluid(entry, Material.VALUE_Block), metalTag("storage_blocks", path), false, false);
            castingDynamic(output, "metal_" + path + "_plate", ModPatternsLoc.SHAPE_PLATE,
                    fluid(entry, Material.VALUE_Ingot), metalTag("plates", path), false, false);
            castingDynamic(output, "metal_" + path + "_gear", ModPatternsLoc.SHAPE_GEAR,
                    fluid(entry, Material.VALUE_Ingot * 4), metalTag("gears", path), false, false);
        }

        // 铸模具创建（1:1 旧版 castCreationFluids：金 2 锭 / 黄铜 1 锭 / 铝黄铜 1 锭浇铸模具，
        // switchOutputs=true：锭/粒/板/齿轮 → 对应形状模具）
        for (Map.Entry<String, String> metal : METALS.entrySet()) {
            String path = metal.getValue();
            for (ModFluids.FluidEntry castFluid : new ModFluids.FluidEntry[]{
                    ModFluids.MOLTEN_GOLD, ModFluids.MOLTEN_BRASS, ModFluids.MOLTEN_ALUBRASS}) {
                int amount = castFluid == ModFluids.MOLTEN_GOLD ? Material.VALUE_Ingot * 2 : Material.VALUE_Ingot;
                String fluidName = castFluid.id().getPath().replace("molten_", "");
                castingDynamic(output, "cast_ingot_" + fluidName + "_" + path, ModPatternsLoc.SHAPE_INGOT,
                        fluid(castFluid, amount), metalTag("ingots", path), true, true);
                castingDynamic(output, "cast_nugget_" + fluidName + "_" + path, ModPatternsLoc.SHAPE_NUGGET,
                        fluid(castFluid, amount), metalTag("nuggets", path), true, true);
                castingDynamic(output, "cast_plate_" + fluidName + "_" + path, ModPatternsLoc.SHAPE_PLATE,
                        fluid(castFluid, amount), metalTag("plates", path), true, true);
                castingDynamic(output, "cast_gear_" + fluidName + "_" + path, ModPatternsLoc.SHAPE_GEAR,
                        fluid(castFluid, amount), metalTag("gears", path), true, true);
            }
        }
    }

    // ==================== 桶浇铸 / 部件制作 ====================

    /** 桶浇铸（通用规则：空桶 + 任意流体 → 满桶，1:1 旧版 BucketCastingRecipe）。 */
    private void buildBucketCasting(RecipeOutput output) {
        save(output, new BucketCastingRecipe(), modLoc("bucket_casting"));
    }

    /** 部件制作配方（1:1 旧版 ToolBuilder.tryBuildToolPart 的槽位合成，每部件一条）。 */
    private void buildPartRecipes(RecipeOutput output) {
        for (var part : ModToolParts.getAllParts()) {
            ResourceLocation shape = ResourceLocation.fromNamespaceAndPath(
                    TConstructNirvana.MODID, part.getId().getPath());
            save(output, new PartRecipe(shape, part.get().getCost()), modLoc("part/" + part.getId().getPath()));
        }
    }

    // ==================== 合金（会话5，10 条 1:1 旧版 registerAlloys） ====================

    /** 合金配方（1:1 自旧版 TinkerSmeltery.registerAlloys，10 条）。 */
    private void buildAlloyRecipes(RecipeOutput output) {
        // 1 桶水 + 1 桶岩浆 = 2 锭黑曜石（1000+1000=288，比例 125+125=36）
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_OBSIDIAN.still().get(), 36))
                .input(SizedFluidIngredient.of(Fluids.WATER, 125))
                .input(SizedFluidIngredient.of(Fluids.LAVA, 125))
                .save(output, alloy("obsidian"));

        // 1 桶水 + 4 灼热锭 + 4 泥砖 = 1 块硬化粘土（250 水 + 72 灼热石 + 144 泥土 = 144 粘土）
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_CLAY.still().get(), 144))
                .input(SizedFluidIngredient.of(Fluids.WATER, 250))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_STONE.still().get(), 72))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_DIRT.still().get(), 144))
                .save(output, alloy("clay"));

        // 1 铁锭 + 1 紫色史莱姆球 + 灼热石 = 1 骑士史莱姆锭（72 铁 + 125 紫史莱姆 + 144 灼热石 = 72 骑士）
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_KNIGHTSLIME.still().get(), 72))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_IRON.still().get(), 72))
                .input(SizedFluidIngredient.of(ModFluids.PURPLESLIME.still().get(), 125))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_STONE.still().get(), 144))
                .save(output, alloy("knightslime"));

        // 1 铁锭 + 血 + 1/3 宝石 = 1 生铁锭（144 铁 + 40 血 + 72 粘土 = 144 生铁）
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_PIGIRON.still().get(), 144))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_IRON.still().get(), 144))
                .input(SizedFluidIngredient.of(ModFluids.BLOOD.still().get(), 40))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_CLAY.still().get(), 72))
                .save(output, alloy("pigiron"));

        // 2 份钴 + 2 份阿迪特 = 2 份玛玉灵（比例单位，非锭数）
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_MANYULLYN.still().get(), 2))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_COBALT.still().get(), 2))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_ARDITE.still().get(), 2))
                .save(output, alloy("manyullyn"));

        // 3 锭铜 + 1 锭锡 = 4 锭青铜
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_BRONZE.still().get(), 4))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_COPPER.still().get(), 3))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_TIN.still().get(), 1))
                .save(output, alloy("bronze"));

        // 1 锭金 + 1 锭银 = 2 锭琥珀金
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_ELECTRUM.still().get(), 2))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_GOLD.still().get(), 1))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_SILVER.still().get(), 1))
                .save(output, alloy("electrum"));

        // 1 锭铜 + 3 锭铝 = 4 锭铝黄铜
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_ALUBRASS.still().get(), 4))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_COPPER.still().get(), 1))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_ALUMINUM.still().get(), 3))
                .save(output, alloy("alubrass"));

        // 2 锭铜 + 1 锭锌 = 3 锭黄铜
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_BRASS.still().get(), 3))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_COPPER.still().get(), 2))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_ZINC.still().get(), 1))
                .save(output, alloy("brass"));

        // 1 锭铝 + 1 锭铁 + 1 黑曜石 = 1 锭铝化钢（144 铝 + 144 铁 + 288 黑曜石 = 144 铝化钢）
        AlloyRecipeBuilder.alloy(new FluidStack(ModFluids.MOLTEN_ALUMITE.still().get(), 144))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_ALUMINUM.still().get(), 144))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_IRON.still().get(), 144))
                .input(SizedFluidIngredient.of(ModFluids.MOLTEN_OBSIDIAN.still().get(), 288))
                .save(output, alloy("alumite"));
    }

    // ==================== 工具方法 ====================

    /** 铸造形状（避免与 recipe 包耦合，本地常量；1:1 旧版 cast_custom meta）。 */
    private static final class ModPatternsLoc {
        static final ResourceLocation SHAPE_INGOT = modLoc("ingot");
        static final ResourceLocation SHAPE_NUGGET = modLoc("nugget");
        static final ResourceLocation SHAPE_GEM = modLoc("gem");
        static final ResourceLocation SHAPE_PLATE = modLoc("plate");
        static final ResourceLocation SHAPE_GEAR = modLoc("gear");
    }

    private static ItemTagMatch itemTag(TagKey<Item> tag, int amount) {
        return new ItemTagMatch(tag, amount);
    }

    private static TagKey<Item> metalTag(String category, String path) {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", category + "/" + path));
    }

    private static FluidStack water(int amount) {
        return new FluidStack(Fluids.WATER, amount);
    }

    private static FluidStack fluid(ModFluids.FluidEntry entry, int amount) {
        return new FluidStack(entry.still().get(), amount);
    }

    /** 熔炼（温度自动推算，1:1 旧版默认构造）。 */
    private void melting(RecipeOutput output, String name, ItemTagMatch input, FluidStack result) {
        save(output, new MeltingRecipe(input, result), modLoc("melting/" + name));
    }

    /** 熔炼（显式温度，1:1 旧版显式 temp 构造）。 */
    private void melting(RecipeOutput output, String name, ItemTagMatch input, FluidStack result, int temperature) {
        save(output, new MeltingRecipe(input, result, temperature), modLoc("melting/" + name));
    }

    /** 浇铸：静态输出 + 形状模具（castShape 为 null = 无模具/铸造盆 → 1:1 旧版 registerBasinCasting）。 */
    private void castingStatic(RecipeOutput output, String name, ResourceLocation castShape,
                               FluidStack fluid, ItemStack result, int time,
                               boolean consumesCast, boolean switchOutputs) {
        save(output, CastingRecipe.ofShape(castShape, fluid, result, consumesCast, switchOutputs)
                .withTime(time).withBasin(castShape == null), modLoc("casting/" + name));
    }

    /** 浇铸：tag 模具（消耗输入物品，如染色陶瓦/沙；1:1 旧版 RecipeMatch.of(物品) 作模具，均走盆）。 */
    private void castingTagCast(RecipeOutput output, String name, TagKey<Item> castTag,
                                FluidStack fluid, ItemStack result, int time) {
        save(output, CastingRecipe.ofTag(castTag, fluid, result, time, true, false)
                .withBasin(true), modLoc("casting/" + name));
    }

    /** 浇铸：动态输出（输出 = c: tag 首选物品，1:1 旧版 PreferenceCastingRecipe；castShape 为 null = 铸块走盆）。 */
    private void castingDynamic(RecipeOutput output, String name, ResourceLocation castShape,
                                FluidStack fluid, TagKey<Item> resultTag,
                                boolean consumesCast, boolean switchOutputs) {
        save(output, CastingRecipe.ofTagOutput(castShape, fluid, resultTag, consumesCast, switchOutputs)
                .withBasin(castShape == null), modLoc("casting/" + name));
    }

    private static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, path);
    }

    private static ResourceLocation alloy(String path) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "alloy/" + path);
    }

    /** 写入配方 JSON（1.21.1 RecipeOutput 无 save 便捷方法，走 accept）。 */
    private static void save(RecipeOutput output, Recipe<?> recipe, ResourceLocation id) {
        output.accept(id, recipe, (net.minecraft.advancements.AdvancementHolder) null);
    }
}
