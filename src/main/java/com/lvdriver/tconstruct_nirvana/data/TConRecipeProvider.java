package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.recipe.AlloyRecipeBuilder;
import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.concurrent.CompletableFuture;

/**
 * 配方生成（DataGen）。
 *
 * <p>工具组装配方：工作台按序摆入部件 → 输出工具（特殊配方，逻辑见
 * {@link com.lvdriver.tconstruct_nirvana.recipe.ToolBuildRecipe}）。
 * 合金配方：输入流体 → 输出流体，10 条数据 1:1 自旧版
 * {@code TinkerSmeltery.registerAlloys}（旧版集成条件默认满足，全部注册；
 * obsidianAlloy 配置开关旧版默认 true，本版无条件注册）。</p>
 */
public class TConRecipeProvider extends RecipeProvider {

    public TConRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        SpecialRecipeBuilder.special(category -> new com.lvdriver.tconstruct_nirvana.recipe.ToolBuildRecipe(category))
                .save(output, com.lvdriver.tconstruct_nirvana.TConstructNirvana.MODID + ":tool_build");
        // 工具修复：工具 + 磨刀石（1:1 旧版 RepairRecipe）
        SpecialRecipeBuilder.special(category -> new com.lvdriver.tconstruct_nirvana.recipe.RepairRecipe(category))
                .save(output, com.lvdriver.tconstruct_nirvana.TConstructNirvana.MODID + ":repair");

        buildAlloyRecipes(output);
    }

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

    private static ResourceLocation alloy(String path) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "alloy/" + path);
    }
}
