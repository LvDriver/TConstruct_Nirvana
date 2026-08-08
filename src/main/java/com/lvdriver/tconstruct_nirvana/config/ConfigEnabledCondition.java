package com.lvdriver.tconstruct_nirvana.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.conditions.ICondition;

/**
 * 配置开关数据包条件（{@code tconstruct_nirvana:config_enabled}）。
 *
 * <p>NeoForge 的 BiomeModifier 等数据包注册表 JSON 支持 {@code neoforge:conditions}，
 * 但内置条件（mod_loaded 等）无法读运行时 config。此条件按 {@code key} 读取
 * {@link TConConfig} 对应开关：config 修改后执行 {@code /reload} 即生效。
 * 1:1 还原旧版 {@code Config.genSlimeIslands / genSlimePools / genCobalt / genArdite}
 * 的世界生成开关行为。</p>
 */
public record ConfigEnabledCondition(String key) implements ICondition {

    public static final MapCodec<ConfigEnabledCondition> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder
                    .group(com.mojang.serialization.Codec.STRING.fieldOf("key").forGetter(ConfigEnabledCondition::key))
                    .apply(builder, ConfigEnabledCondition::new));

    @Override
    public boolean test(IContext context) {
        return switch (key) {
            case "generateCobaltOre" -> TConConfig.GENERATE_COBALT_ORE.get();
            case "generateArditeOre" -> TConConfig.GENERATE_ARDITE_ORE.get();
            case "generateSlimeIslands" -> TConConfig.GENERATE_SLIME_ISLANDS.get();
            case "generateSlimePools" -> TConConfig.GENERATE_SLIME_POOLS.get();
            default -> false;
        };
    }

    @Override
    public MapCodec<? extends ICondition> codec() {
        return CODEC;
    }

    @Override
    public String toString() {
        return "config_enabled(\"" + key + "\")";
    }
}
