package com.lvdriver.tconstruct_nirvana.fluid;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 流体注册中枢（DeferredRegister）。
 *
 * <p>1.21.1 流体分两套注册表：{@link FluidType}（流体属性）与 {@link Fluid}（流体本体）。
 * 当前为骨架：本会话只建注册表，熔融金属流体在"流体"会话中填充。</p>
 */
public final class ModFluids {

    /** 流体类型注册表（属性：密度/粘度/温度等）。 */
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TConstructNirvana.MODID);

    /** 流体注册表（本体）。 */
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, TConstructNirvana.MODID);

    private ModFluids() {
    }

    public static void register(IEventBus modEventBus) {
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
    }
}
