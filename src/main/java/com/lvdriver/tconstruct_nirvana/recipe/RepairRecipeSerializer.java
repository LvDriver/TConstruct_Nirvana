package com.lvdriver.tconstruct_nirvana.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * {@link RepairRecipe} 序列化器（1.21.1 RecipeSerializer 的 MapCodec + StreamCodec 接口）。
 */
public class RepairRecipeSerializer implements RecipeSerializer<RepairRecipe> {

    public static final RepairRecipeSerializer INSTANCE = new RepairRecipeSerializer();

    private static final MapCodec<RepairRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(RepairRecipe::category)
    ).apply(instance, RepairRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    CraftingBookCategory.STREAM_CODEC, RepairRecipe::category,
                    RepairRecipe::new);

    @Override
    public MapCodec<RepairRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, RepairRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
