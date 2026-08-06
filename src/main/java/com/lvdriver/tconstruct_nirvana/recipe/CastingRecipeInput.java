package com.lvdriver.tconstruct_nirvana.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 浇铸配方输入：铸造盆/台槽位快照（模具物品 + 待浇铸流体）。
 *
 * <p>{@code cast} 为空栈表示铸造盆（无模具，浇铸块类输出）；模具物品
 * （{@code CastItem}）携带 {@code PATTERN_SHAPE} 组件标识形状。</p>
 */
public record CastingRecipeInput(ItemStack cast, FluidStack fluid) implements RecipeInput {

    @Override
    public ItemStack getItem(int index) {
        return index == 0 ? cast : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return cast.isEmpty() && fluid.isEmpty();
    }
}
