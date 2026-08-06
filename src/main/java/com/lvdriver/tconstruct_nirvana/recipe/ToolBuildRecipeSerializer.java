package com.lvdriver.tconstruct_nirvana.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * {@link ToolBuildRecipe} 序列化器（1.21.1 RecipeSerializer 的 MapCodec + StreamCodec 接口）。
 */
public class ToolBuildRecipeSerializer implements RecipeSerializer<ToolBuildRecipe> {

    public static final ToolBuildRecipeSerializer INSTANCE = new ToolBuildRecipeSerializer();

    private static final MapCodec<ToolBuildRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(ToolBuildRecipe::category)
    ).apply(instance, ToolBuildRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, ToolBuildRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    CraftingBookCategory.STREAM_CODEC, ToolBuildRecipe::category,
                    ToolBuildRecipe::new);

    @Override
    public MapCodec<ToolBuildRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, ToolBuildRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
