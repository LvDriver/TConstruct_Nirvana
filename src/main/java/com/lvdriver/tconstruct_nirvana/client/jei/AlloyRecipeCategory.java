package com.lvdriver.tconstruct_nirvana.client.jei;

import com.lvdriver.tconstruct_nirvana.recipe.AlloyRecipe;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 合金配方 JEI 分类：多输入流体 → 输出流体。
 */
public class AlloyRecipeCategory implements IRecipeCategory<RecipeHolder<AlloyRecipe>> {

    private final IDrawable icon;

    public AlloyRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(
                new net.minecraft.world.item.ItemStack(com.lvdriver.tconstruct_nirvana.fluid.ModFluids.MOLTEN_MANYULLYN.bucket().get()));
    }

    @Override
    public RecipeType<RecipeHolder<AlloyRecipe>> getRecipeType() {
        return TConJeiPlugin.ALLOY;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.tconstruct_nirvana.alloy");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<AlloyRecipe> holder, IFocusGroup focuses) {
        AlloyRecipe recipe = holder.value();
        // 输入流体（每槽一个输入条目的匹配流体，量标注）
        int x = 1;
        for (var ingredient : recipe.getInputs()) {
            for (FluidStack fluid : ingredient.getFluids()) {
                builder.addSlot(RecipeIngredientRole.INPUT, x, 18)
                        .addFluidStack(fluid.getFluid(), ingredient.amount())
                        .addTooltipCallback((view, tooltip) ->
                                tooltip.add(Component.literal(ingredient.amount() + " mb")));
            }
            x += 34;
        }
        // 输出流体
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18)
                .addFluidStack(recipe.getResult().getFluid(), recipe.getResult().getAmount());
    }

    @Override
    public int getWidth() {
        return 144;
    }

    @Override
    public int getHeight() {
        return 54;
    }
}
