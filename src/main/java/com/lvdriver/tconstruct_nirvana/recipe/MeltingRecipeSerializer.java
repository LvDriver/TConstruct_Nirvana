package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.util.ItemTagMatch;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * {@link MeltingRecipe} 序列化器（1.21.1 RecipeSerializer 的 MapCodec + StreamCodec 接口）。
 *
 * <p>JSON 格式：{@code {"type":"tconstruct_nirvana:melting",
 * "input":{"tag":"c:ingots/iron","amount":144},
 * "result":{"id":"tconstruct_nirvana:molten_iron","amount":144},
 * "temperature":769}}。temperature 为配方熔点（DataGen 按旧版公式预计算，
 * 自动推算逻辑见 {@link MeltingRecipe#calcTemperature}）。</p>
 */
public class MeltingRecipeSerializer implements RecipeSerializer<MeltingRecipe> {

    public static final MeltingRecipeSerializer INSTANCE = new MeltingRecipeSerializer();

    private static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemTagMatch.CODEC.fieldOf("input").forGetter(MeltingRecipe::getInput),
            FluidStack.CODEC.fieldOf("result").forGetter(MeltingRecipe::getResult),
            net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("temperature").forGetter(MeltingRecipe::getTemperature)
    ).apply(instance, MeltingRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    ItemTagMatch.STREAM_CODEC, MeltingRecipe::getInput,
                    FluidStack.STREAM_CODEC, MeltingRecipe::getResult,
                    net.minecraft.network.codec.ByteBufCodecs.VAR_INT, MeltingRecipe::getTemperature,
                    MeltingRecipe::new);

    @Override
    public MapCodec<MeltingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
