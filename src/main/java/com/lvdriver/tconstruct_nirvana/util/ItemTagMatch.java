package com.lvdriver.tconstruct_nirvana.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 材料-物品匹配器（1.21.1 版 RecipeMatch，替代旧版 Mantle {@code RecipeMatch}）。
 *
 * <p>旧版匠魂的 {@code RecipeMatch} 以矿物词典（OreDictionary）/具体 ItemStack
 * 匹配材料与修饰符的物品；1.21.1 中矿物词典已由 {@link TagKey} 承担，故本类以
 * {@code TagKey<Item>} 表达"某类物品属于某材料"的关联，并附带该物品的价值
 * （{@code amount}，单位 mb，用于液体换算与部件制作）。</p>
 *
 * <p>使用约束：{@link #matches(ItemStack)} 依赖运行期 tag 内容（数据包加载后），
 * 禁止在 Mod 构造器 / CommonSetup 等数据包加载前的阶段调用（此时 tag 内容为空，
 * 匹配恒为 false）。</p>
 *
 * @param tag    关联的物品 tag（如 {@code c:ingots/cobalt}），匹配时校验物品是否携带
 * @param amount 该 tag 下单个物品代表的价值（mb），如锭=144、粒=16、碎片=36
 */
public record ItemTagMatch(TagKey<Item> tag, int amount) {

    /** 该物品是否命中此匹配器（物品携带 {@link #tag}）。空栈恒为 false。 */
    public boolean matches(ItemStack stack) {
        return !stack.isEmpty() && stack.is(tag);
    }

    /** JSON 编解码（熔炼等数据驱动配方输入用，{@code {"tag":..,"amount":..}}）。 */
    public static final Codec<ItemTagMatch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            TagKey.codec(Registries.ITEM).fieldOf("tag").forGetter(ItemTagMatch::tag),
            Codec.INT.fieldOf("amount").forGetter(ItemTagMatch::amount)
    ).apply(instance, ItemTagMatch::new));

    /** 网络同步编解码（与 {@link #CODEC} 同构）。 */
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemTagMatch> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC.map(
                            loc -> TagKey.create(Registries.ITEM, loc), TagKey::location),
                    ItemTagMatch::tag,
                    ByteBufCodecs.VAR_INT, ItemTagMatch::amount,
                    ItemTagMatch::new);

    @Override
    public String toString() {
        return "ItemTagMatch{" + tag.location() + "=" + amount + '}';
    }
}
