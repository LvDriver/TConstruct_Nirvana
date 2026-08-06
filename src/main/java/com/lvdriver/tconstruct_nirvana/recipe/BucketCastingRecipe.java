package com.lvdriver.tconstruct_nirvana.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;

/**
 * 桶浇铸配方（空桶 + 流体 → 满桶），1:1 自旧版 {@code BucketCastingRecipe}。
 *
 * <p>通用规则配方（无参数，DataGen 生成一条）：任何携带 {@code c:buckets/empty}
 * tag 的空桶作为"模具"，任意流体浇入后输出该流体的桶物品（
 * {@link FluidUtil#getFilledBucket}，支持本 mod 与附属/原版流体）。
 * 冷却 5 tick，消耗 1 桶（1000mb）流体并消耗空桶（1:1 旧版）。</p>
 */
public class BucketCastingRecipe implements Recipe<CastingRecipeInput> {

    public static final int BUCKET_VOLUME = 1000;

    @Override
    public boolean matches(CastingRecipeInput recipeInput, Level level) {
        ItemStack cast = recipeInput.cast();
        FluidStack fluid = recipeInput.fluid();
        return !cast.isEmpty() && cast.is(Tags.Items.BUCKETS_EMPTY)
                && !fluid.isEmpty() && !FluidUtil.getFilledBucket(fluid).isEmpty();
    }

    @Override
    public ItemStack assemble(CastingRecipeInput recipeInput, HolderLookup.Provider registries) {
        return FluidUtil.getFilledBucket(recipeInput.fluid());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.BUCKET_CASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.BUCKET_CASTING_TYPE.get();
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String toString() {
        return "bucket_casting(empty bucket + fluid -> filled bucket)";
    }
}
