package com.lvdriver.tconstruct_nirvana.impl;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.api.registry.FluidRegistry;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * {@link FluidRegistry} 实现：委托 {@link ModFluids} 注册表与材料流体关联。
 * 附属 mod 不应直接引用本类（实现细节，可能变更），请经
 * {@code TConstructNirvanaAPI.fluids()} 获取。
 */
public final class FluidRegistryImpl implements FluidRegistry {

    @Override
    public DeferredRegister<Fluid> fluids() {
        return ModFluids.FLUIDS;
    }

    @Override
    public DeferredRegister<FluidType> fluidTypes() {
        return ModFluids.FLUID_TYPES;
    }

    @Override
    public String getFluidId(String materialId) {
        Material material = ModMaterials.getMaterial(materialId);
        return material != null ? material.getFluidId() : null;
    }

    @Override
    public Material associateFluid(String materialId, String fluidId) {
        Material material = ModMaterials.getMaterial(materialId);
        if (material == null) {
            TConstructNirvana.LOGGER.warn(
                    "TConstructNirvanaAPI.fluids().associateFluid: material '{}' not found, ignored", materialId);
            return null;
        }
        return material.setFluid(fluidId);
    }
}
