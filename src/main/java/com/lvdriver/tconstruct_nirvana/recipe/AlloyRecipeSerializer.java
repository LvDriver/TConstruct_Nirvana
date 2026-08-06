package com.lvdriver.tconstruct_nirvana.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * {@link AlloyRecipe} 序列化器（1.21.1 RecipeSerializer 的 MapCodec + StreamCodec 接口）。
 *
 * <p>JSON 格式：{@code {"type":"tconstruct_nirvana:alloy","inputs":[{"fluid":..,"amount":..},..],
 * "result":{"fluid":..,"amount":..}}}（SizedFluidIngredient.FLAT_CODEC 与 FluidStack.CODEC）。</p>
 */
public class AlloyRecipeSerializer implements RecipeSerializer<AlloyRecipe> {

    public static final AlloyRecipeSerializer INSTANCE = new AlloyRecipeSerializer();

    private static final MapCodec<AlloyRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            SizedFluidIngredient.FLAT_CODEC.listOf().fieldOf("inputs").forGetter(AlloyRecipe::getInputs),
            FluidStack.CODEC.fieldOf("result").forGetter(AlloyRecipe::getResult)
    ).apply(instance, AlloyRecipe::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> STREAM_CODEC =
            StreamCodec.composite(
                    SizedFluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.list()), AlloyRecipe::getInputs,
                    FluidStack.STREAM_CODEC, AlloyRecipe::getResult,
                    AlloyRecipe::new);

    @Override
    public MapCodec<AlloyRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AlloyRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
