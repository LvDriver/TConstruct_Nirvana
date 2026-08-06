package com.lvdriver.tconstruct_nirvana.util;

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

    @Override
    public String toString() {
        return "ItemTagMatch{" + tag.location() + "=" + amount + '}';
    }
}
