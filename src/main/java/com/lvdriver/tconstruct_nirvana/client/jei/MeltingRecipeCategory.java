package com.lvdriver.tconstruct_nirvana.client.jei;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.recipe.MeltingRecipe;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * 熔炼配方 JEI 分类：物品（tag）→ 熔融流体（显示熔点 tooltip）。
 */
public class MeltingRecipeCategory implements IRecipeCategory<RecipeHolder<MeltingRecipe>> {

    private final IDrawable icon;

    public MeltingRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(ModItems.COBALT_INGOT.get()));
    }

    @Override
    public RecipeType<RecipeHolder<MeltingRecipe>> getRecipeType() {
        return TConJeiPlugin.MELTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.tconstruct_nirvana.melting");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<MeltingRecipe> holder, IFocusGroup focuses) {
        MeltingRecipe recipe = holder.value();
        // 输入：c: tag（tag 内任意物品）
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 18)
                .addIngredients(Ingredient.of(recipe.getInput().tag()))
                .addTooltipCallback((view, tooltip) -> tooltip.add(Component.literal(recipe.getInput().amount() + " mb")));
        // 输出：熔融流体 + 熔点
        builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18)
                .addFluidStack(recipe.getResult().getFluid(), recipe.getResult().getAmount())
                .addTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("jei.tconstruct_nirvana.melting.temperature", recipe.getTemperature())));
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
