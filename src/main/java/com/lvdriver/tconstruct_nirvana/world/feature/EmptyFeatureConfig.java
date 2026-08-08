package com.lvdriver.tconstruct_nirvana.world.feature;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * 空配置（史莱姆岛/矿池等无参数 feature 共用）。
 */
public record EmptyFeatureConfig() implements FeatureConfiguration {

    public static final Codec<EmptyFeatureConfig> CODEC = Codec.unit(new EmptyFeatureConfig());
}
