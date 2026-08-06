package com.lvdriver.tconstruct_nirvana.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * {@link PartRecipe} 序列化器。
 *
 * <p>JSON 格式：{@code {"type":"tconstruct_nirvana:part",
 * "pattern":"tconstruct_nirvana:pick_head","cost":288}}。</p>
 */
public class PartRecipeSerializer implements RecipeSerializer<PartRecipe> {

    public static final PartRecipeSerializer INSTANCE = new PartRecipeSerializer();

    private static final MapCodec<PartRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("pattern").forGetter(PartRecipe::getPattern),
            net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("cost").forGetter(PartRecipe::getCost)
    ).apply(instance, PartRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, PartRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC, PartRecipe::getPattern,
                    ByteBufCodecs.VAR_INT, PartRecipe::getCost,
                    PartRecipe::new);

    @Override
    public MapCodec<PartRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, PartRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
