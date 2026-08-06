package com.lvdriver.tconstruct_nirvana.client.jei;

import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import com.lvdriver.tconstruct_nirvana.item.pattern.PatternItem;
import com.lvdriver.tconstruct_nirvana.recipe.PartRecipe;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/**
 * 部件制作配方 JEI 分类：模具 + 材料（c: tag，动态）→ 部件。
 */
public class PartRecipeCategory implements IRecipeCategory<RecipeHolder<PartRecipe>> {

    private final IDrawable icon;

    public PartRecipeCategory(IGuiHelper helper) {
        this.icon = helper.createDrawableItemStack(new ItemStack(ModPatterns.PATTERN.get()));
    }

    @Override
    public RecipeType<RecipeHolder<PartRecipe>> getRecipeType() {
        return TConJeiPlugin.PART;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.tconstruct_nirvana.part");
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<PartRecipe> holder, IFocusGroup focuses) {
        PartRecipe recipe = holder.value();
        // 模具（带形状）
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 18)
                .addItemStack(PatternItem.setShape(new ItemStack(ModPatterns.PATTERN.get()), recipe.getPattern()));
        // 材料：craftable 材料关联的 c: tag 内物品（tool_parts 之外的材料维度由材料系统动态展开，
        // JEI 槽显示全部材料的代表物品列表过重，此处置空并注明）
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 18)
                .addTooltipCallback((view, tooltip) ->
                        tooltip.add(Component.translatable("jei.tconstruct_nirvana.part.material", recipe.getCost())));
        // 输出：该形状部件（材料动态，显示无材料部件占位）
        recipe.getPart().ifPresent(part -> builder.addSlot(RecipeIngredientRole.OUTPUT, 120, 18)
                .addItemStack(new ItemStack(part)));
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
