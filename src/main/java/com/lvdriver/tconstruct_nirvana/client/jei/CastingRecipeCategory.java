package com.lvdriver.tconstruct_nirvana.client.jei;

import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import com.lvdriver.tconstruct_nirvana.item.pattern.PatternItem;
import com.lvdriver.tconstruct_nirvana.recipe.CastingRecipe;
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
 * 浇铸配方 JEI 分类：模具（或空）+ 熔融流体 → 物品（或 c: tag 首选物品）。
 */
public class CastingRecipeCategory implements IRecipeCategory<RecipeHolder<CastingRecipe>> {

    private final IDrawable icon;

    public CastingRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(ModPatterns.CAST.get()));
    }

    @Override
    public RecipeType<RecipeHolder<CastingRecipe>> getRecipeType() {
        return TConJeiPlugin.CASTING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.tconstruct_nirvana.casting");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CastingRecipe> holder, IFocusGroup focuses) {
        CastingRecipe recipe = holder.value();
        // 模具槽：无模具 = 空；形状模具 = 带形状的浇铸模具；tag 模具 = tag 内物品
        if (recipe.getCastShape() != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 18)
                    .addItemStack(PatternItem.setShape(new ItemStack(ModPatterns.CAST.get()), recipe.getCastShape()));
        } else if (recipe.getCastTag() != null) {
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 18)
                    .addIngredients(Ingredient.of(recipe.getCastTag()))
                    .addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("jei.tconstruct_nirvana.casting.consumed")));
        } else {
            builder.addSlot(RecipeIngredientRole.INPUT, 1, 18)
                    .addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("jei.tconstruct_nirvana.casting.basin")));
        }
        // 流体槽（消耗量）
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 18)
                .addFluidStack(recipe.getFluid().getFluid(), recipe.getFluidAmount());
        // 输出槽（动态 tag 输出显示首选物品）
        ItemStack result = recipe.getResult();
        if (!result.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18).addItemStack(result);
        } else if (recipe.getOutput().right().isPresent()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18)
                    .addIngredients(Ingredient.of(recipe.getOutput().right().orElseThrow()))
                    .addTooltipCallback((view, tooltip) -> tooltip.add(Component.translatable("jei.tconstruct_nirvana.casting.preference")));
        } else {
            builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18);
        }
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
