package com.lvdriver.tconstruct_nirvana.recipe;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.List;

/**
 * 合金配方 DataGen builder（输出 {@code recipe/alloy/*.json}）。
 */
public class AlloyRecipeBuilder {

    private final List<SizedFluidIngredient> inputs = new ArrayList<>();
    private final FluidStack result;

    private AlloyRecipeBuilder(FluidStack result) {
        this.result = result;
    }

    public static AlloyRecipeBuilder alloy(FluidStack result) {
        return new AlloyRecipeBuilder(result);
    }

    public AlloyRecipeBuilder input(SizedFluidIngredient input) {
        inputs.add(input);
        return this;
    }

    public void save(RecipeOutput output, ResourceLocation id) {
        output.accept(id, new AlloyRecipe(inputs, result), (AdvancementHolder) null);
    }
}
