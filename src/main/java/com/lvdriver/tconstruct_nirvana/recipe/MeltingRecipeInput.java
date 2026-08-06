package com.lvdriver.tconstruct_nirvana.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

/**
 * 熔炼配方输入：冶炼炉燃料槽的单物品快照（1.21.1 {@link RecipeInput} 接口）。
 */
public record MeltingRecipeInput(ItemStack stack) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? stack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return stack.isEmpty();
    }
}
