package com.lvdriver.tconstruct_nirvana.impl;

import com.lvdriver.tconstruct_nirvana.api.registry.ToolPartRegistry;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.Optional;

/**
 * {@link ToolPartRegistry} 实现：委托 {@link ModToolParts} 形状注册表。
 * 附属 mod 不应直接引用本类（实现细节，可能变更），请经
 * {@code TConstructNirvanaAPI.toolParts()} 获取。
 */
public final class ToolPartRegistryImpl implements ToolPartRegistry {

    @Override
    public Optional<ToolPart> getPart(ResourceLocation shapeId) {
        return ModToolParts.getPart(shapeId);
    }

    @Override
    public List<DeferredItem<? extends ToolPart>> getAllParts() {
        return ModToolParts.getAllParts();
    }

    @Override
    public DeferredItem<ToolPart> registerPart(String name, int cost, String... statTypes) {
        return ModToolParts.registerPart(name, cost, statTypes);
    }

    @Override
    public void registerPart(ResourceLocation shapeId, DeferredItem<? extends ToolPart> part) {
        ModToolParts.registerPart(shapeId, part);
    }
}
