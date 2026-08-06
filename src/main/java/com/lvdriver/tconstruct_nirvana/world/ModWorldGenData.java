package com.lvdriver.tconstruct_nirvana.world;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;

import java.util.Set;

/**
 * 世界生成数据（RegistrySetBuilder）。
 *
 * <p>注册 configured/placed feature 的 bootstrap，供
 * {@link DatapackBuiltinEntriesProvider} 在 runData 时输出
 * {@code data/tconstruct_nirvana/worldgen/...} JSON；运行时由游戏作为数据包加载。</p>
 */
public final class ModWorldGenData {

    public static final String MODID = "tconstruct_nirvana";

    /** 世界生成注册表构建器。 */
    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap);

    private ModWorldGenData() {
    }
}
