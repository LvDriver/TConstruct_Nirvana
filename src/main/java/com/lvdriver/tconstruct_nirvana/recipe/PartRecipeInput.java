package com.lvdriver.tconstruct_nirvana.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

/**
 * 部件制作配方输入：模具槽 + 材料槽快照（1:1 旧版 PartBuilder 的
 * pattern 槽 + 1-2 个材料槽）。
 */
public record PartRecipeInput(ItemStack pattern, List<ItemStack> materials) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        if (index == 0) {
            return pattern;
        }
        int materialIndex = index - 1;
        return materialIndex >= 0 && materialIndex < materials.size()
                ? materials.get(materialIndex) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return materials.size() + 1;
    }

    @Override
    public boolean isEmpty() {
        return pattern.isEmpty() && materials.stream().allMatch(ItemStack::isEmpty);
    }
}
