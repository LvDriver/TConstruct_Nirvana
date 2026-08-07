package com.lvdriver.tconstruct_nirvana.recipe;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

/**
 * {@link CastingRecipe} 序列化器。
 *
 * <p>JSON 格式：{@code {"type":"tconstruct_nirvana:casting",
 * "cast":"tconstruct_nirvana:ingot"（缺省 = 无模具/铸造盆）,
 * "fluid":{"id":..,"amount":..},
 * "result":{"item":..} 或 {"tag":"c:ingots/iron"}（动态首选）,
 * "time":66, "consumes_cast":false, "switch_outputs":false}}。</p>
 */
public class CastingRecipeSerializer implements RecipeSerializer<CastingRecipe> {

    public static final CastingRecipeSerializer INSTANCE = new CastingRecipeSerializer();

    /** 输出：静态物品 或 动态 tag（1:1 旧版 CastingRecipe / PreferenceCastingRecipe）。 */
    private static final com.mojang.serialization.Codec<Either<ItemStack, TagKey<Item>>> OUTPUT_CODEC =
            com.mojang.serialization.Codec.either(ItemStack.OPTIONAL_CODEC, TagKey.codec(Registries.ITEM));

    /** 模具：形状 ID（字符串）或 物品 tag（{"tag":..}）；缺省 = 无模具（铸造盆）。 */
    private static final com.mojang.serialization.Codec<Either<ResourceLocation, TagKey<Item>>> CAST_CODEC =
            com.mojang.serialization.Codec.either(ResourceLocation.CODEC, TagKey.codec(Registries.ITEM));

    private static final MapCodec<CastingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            CAST_CODEC.optionalFieldOf("cast").forGetter(
                    (CastingRecipe r) -> Optional.ofNullable(r.getCast())),
            FluidStack.CODEC.fieldOf("fluid").forGetter(CastingRecipe::getFluid),
            OUTPUT_CODEC.fieldOf("result").forGetter(CastingRecipe::getOutput),
            net.minecraft.util.ExtraCodecs.POSITIVE_INT.fieldOf("time").forGetter(CastingRecipe::getTime),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("consumes_cast", false).forGetter(CastingRecipe::consumesCast),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("switch_outputs", false).forGetter(CastingRecipe::switchOutputs),
            com.mojang.serialization.Codec.BOOL.optionalFieldOf("basin", false).forGetter(CastingRecipe::isBasin)
    ).apply(instance, (cast, fluid, output, time, consumesCast, switchOutputs, basin) ->
            new CastingRecipe(cast.orElse(null), fluid, output, time, consumesCast, switchOutputs, basin)));

    /** 模具流编解码（可选形状/tag 模具，1.21.1 泛型约束：须为 RegistryFriendlyByteBuf 版）。 */
    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<Either<ResourceLocation, TagKey<Item>>>> CAST_STREAM =
            new StreamCodec<>() {
                @Override
                public Optional<Either<ResourceLocation, TagKey<Item>>> decode(RegistryFriendlyByteBuf buffer) {
                    if (!buffer.readBoolean()) {
                        return Optional.empty();
                    }
                    return Optional.of(buffer.readBoolean()
                            ? Either.left(ResourceLocation.STREAM_CODEC.decode(buffer))
                            : Either.right(TAG_STREAM_CODEC.decode(buffer)));
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, Optional<Either<ResourceLocation, TagKey<Item>>> value) {
                    buffer.writeBoolean(value.isPresent());
                    value.ifPresent(cast -> cast.ifLeft(loc -> {
                        buffer.writeBoolean(true);
                        ResourceLocation.STREAM_CODEC.encode(buffer, loc);
                    }).ifRight(tag -> {
                        buffer.writeBoolean(false);
                        TAG_STREAM_CODEC.encode(buffer, tag);
                    }));
                }
            };

    /** 动态 tag 流编解码（网络同步，1.21.1 无 TagKey.streamCodec）。 */
    private static final StreamCodec<RegistryFriendlyByteBuf, TagKey<Item>> TAG_STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public TagKey<Item> decode(RegistryFriendlyByteBuf buffer) {
                    return TagKey.create(Registries.ITEM, ResourceLocation.STREAM_CODEC.decode(buffer));
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, TagKey<Item> value) {
                    ResourceLocation.STREAM_CODEC.encode(buffer, value.location());
                }
            };

    /** 输出流编解码：静态物品 或 动态 tag（布尔标记 + 内容，1.21.1 无 StreamCodec.either）。 */
    private static final StreamCodec<RegistryFriendlyByteBuf, Either<ItemStack, TagKey<Item>>> OUTPUT_STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public Either<ItemStack, TagKey<Item>> decode(RegistryFriendlyByteBuf buffer) {
                    return buffer.readBoolean()
                            ? Either.left(ItemStack.STREAM_CODEC.decode(buffer))
                            : Either.right(TAG_STREAM_CODEC.decode(buffer));
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, Either<ItemStack, TagKey<Item>> value) {
                    value.ifLeft(left -> {
                        buffer.writeBoolean(true);
                        ItemStack.STREAM_CODEC.encode(buffer, left);
                    }).ifRight(right -> {
                        buffer.writeBoolean(false);
                        TAG_STREAM_CODEC.encode(buffer, right);
                    });
                }
            };

    private static final StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> STREAM_CODEC =
            new StreamCodec<>() {
                @Override
                public CastingRecipe decode(RegistryFriendlyByteBuf buffer) {
                    Optional<Either<ResourceLocation, TagKey<Item>>> cast = CAST_STREAM.decode(buffer);
                    FluidStack fluid = FluidStack.STREAM_CODEC.decode(buffer);
                    Either<ItemStack, TagKey<Item>> output = OUTPUT_STREAM_CODEC.decode(buffer);
                    // 正数校验防损坏/篡改数据包产生负冷却（security_review）
                    int time = Math.max(0, buffer.readVarInt());
                    boolean consumesCast = buffer.readBoolean();
                    boolean switchOutputs = buffer.readBoolean();
                    boolean basin = buffer.readBoolean();
                    return new CastingRecipe(cast.orElse(null), fluid, output, time, consumesCast, switchOutputs, basin);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buffer, CastingRecipe recipe) {
                    CAST_STREAM.encode(buffer, Optional.ofNullable(recipe.getCast()));
                    FluidStack.STREAM_CODEC.encode(buffer, recipe.getFluid());
                    OUTPUT_STREAM_CODEC.encode(buffer, recipe.getOutput());
                    buffer.writeVarInt(recipe.getTime());
                    buffer.writeBoolean(recipe.consumesCast());
                    buffer.writeBoolean(recipe.switchOutputs());
                    buffer.writeBoolean(recipe.isBasin());
                }
            };

    @Override
    public MapCodec<CastingRecipe> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, CastingRecipe> streamCodec() {
        return STREAM_CODEC;
    }
}
