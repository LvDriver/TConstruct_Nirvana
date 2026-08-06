package com.lvdriver.tconstruct_nirvana.impl;

import com.lvdriver.tconstruct_nirvana.api.registry.MaterialRegistry;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;

import java.util.List;

/**
 * {@link MaterialRegistry} 实现：委托 {@link ModMaterials} 静态注册表。
 * 附属 mod 不应直接引用本类（实现细节，可能变更），请经
 * {@code TConstructNirvanaAPI.materials()} 获取。
 */
public final class MaterialRegistryImpl implements MaterialRegistry {

    @Override
    public Material register(Material material) {
        return ModMaterials.registerMaterial(material);
    }

    @Override
    public Material get(String identifier) {
        return ModMaterials.getMaterial(identifier);
    }

    @Override
    public Material getOrUnknown(String identifier) {
        return Material.getByIdentifier(identifier);
    }

    @Override
    public List<Material> getAll() {
        return ModMaterials.getAllMaterials();
    }
}
