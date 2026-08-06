package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.SpecialRecipeBuilder;

import java.util.concurrent.CompletableFuture;

/**
 * 配方生成（DataGen）。
 *
 * <p>工具组装配方：工作台按序摆入部件 → 输出工具（特殊配方，逻辑见
 * {@link com.lvdriver.tconstruct_nirvana.recipe.ToolBuildRecipe}）。</p>
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
    }
}
