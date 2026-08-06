package com.lvdriver.tconstruct_nirvana.recipe;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * {@link BucketCastingRecipe} 序列化器（无参数规则配方，
 * JSON 仅 {@code {"type":"tconstruct_nirvana:bucket_casting"}}）。
 */
public class BucketCastingRecipeSerializer implements RecipeSerializer<BucketCastingRecipe> {

    public static final BucketCastingRecipeSerializer INSTANCE = new BucketCastingRecipeSerializer();

    private static final MapCodec<BucketCastingRecipe> CODEC = MapCodec.unit(BucketCastingRecipe::new);

    private static final StreamCodec<RegistryFriendlyByteBuf, BucketCastingRecipe> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public BucketCastingRecipe decode(RegistryFriendlyByteBuf buffer) {
                    return new BucketCastingRecipe();
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, BucketCastingRecipe value) {
                    // 无参数配方，无需写入任何数据
                }
            };

    @Override
    public MapCodec<BucketCastingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, BucketCastingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
