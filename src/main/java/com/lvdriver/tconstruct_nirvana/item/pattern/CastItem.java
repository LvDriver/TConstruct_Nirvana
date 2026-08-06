package com.lvdriver.tconstruct_nirvana.item.pattern;

import net.minecraft.world.item.Item;

/**
 * 浇铸模具（1:1 移植自 Tinkers' Antique {@code Cast}）。
 *
 * <p>与 {@link PatternItem} 共用形状机制（{@code PATTERN_SHAPE} DataComponent），
 * 语义为冶炼炉浇铸用模具；仅描述 ID（item.{modid}.cast.*）不同，
 * 由 {@link #getDescriptionId(ItemStack)} 天然区分。</p>
 */
public class CastItem extends PatternItem {

    public CastItem(Item.Properties properties) {
        super(properties);
    }
}
