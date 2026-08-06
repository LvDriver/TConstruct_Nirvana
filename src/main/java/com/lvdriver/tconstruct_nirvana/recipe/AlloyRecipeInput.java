package com.lvdriver.tconstruct_nirvana.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;

/**
 * 合金配方输入：熔炼炉流体槽的只读快照（1.21.1 {@link RecipeInput} 是物品接口，
 * 流体配方以空物品栈占位，流体内容走 {@link #fluids()}）。
 */
public record AlloyRecipeInput(List<FluidStack> fluids) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return fluids.size();
    }

    @Override
    public boolean isEmpty() {
        return fluids.isEmpty();
    }
}
