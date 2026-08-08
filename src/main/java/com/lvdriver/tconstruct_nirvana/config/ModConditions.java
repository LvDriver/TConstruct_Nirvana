package com.lvdriver.tconstruct_nirvana.config;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

/**
 * 数据包条件注册（DeferredRegister）。
 *
 * <p>注册 {@link ConfigEnabledCondition} 到 NeoForge 的 condition codec 注册表
 * （{@code neoforge:condition_codecs}），供 BiomeModifier JSON 的
 * {@code neoforge:conditions} 引用（类型名 {@code tconstruct_nirvana:config_enabled}）。</p>
 */
public final class ModConditions {

    public static final DeferredRegister<com.mojang.serialization.MapCodec<? extends net.neoforged.neoforge.common.conditions.ICondition>> CONDITIONS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, TConstructNirvana.MODID);

    static {
        CONDITIONS.register("config_enabled", () -> ConfigEnabledCondition.CODEC);
    }

    private ModConditions() {
    }

    public static void register(IEventBus modEventBus) {
        CONDITIONS.register(modEventBus);
    }
}
