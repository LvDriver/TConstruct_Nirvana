package com.lvdriver.tconstruct_nirvana.impl;

import com.lvdriver.tconstruct_nirvana.api.registry.PatternRegistry;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * {@link PatternRegistry} 实现：模具物品 accessor 与形状查询。
 * 附属 mod 不应直接引用本类（实现细节，可能变更），请经
 * {@code TConstructNirvanaAPI.patterns()} 获取。
 */
public final class PatternRegistryImpl implements PatternRegistry {

    @Override
    public Item getPattern() {
        return ModPatterns.PATTERN.get();
    }

    @Override
    public Item getCast() {
        return ModPatterns.CAST.get();
    }

    @Override
    public boolean isKnownShape(ResourceLocation shapeId) {
        return ModToolParts.getPart(shapeId).isPresent();
    }
}
