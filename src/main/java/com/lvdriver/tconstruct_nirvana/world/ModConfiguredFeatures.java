package com.lvdriver.tconstruct_nirvana.world;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockStateMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

/**
 * 矿石矿脉配置（configured feature）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code NetherOreGenerator}：
 * 矿脉大小 5、替换目标为下界岩（旧版 {@code BlockMatcher.forBlock(Blocks.NETHERRACK)}）。
 * 生成数量与 Y 范围见 {@link ModPlacedFeatures}。</p>
 */
public final class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COBALT = key("ore_cobalt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ARDITE = key("ore_ardite");

    private static ResourceKey<ConfiguredFeature<?, ?>> key(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE,
                ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, name));
    }

    private ModConfiguredFeatures() {
    }

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        BlockState cobaltOre = ModBlocks.COBALT_ORE.get().defaultBlockState();
        BlockState arditeOre = ModBlocks.ARDITE_ORE.get().defaultBlockState();

        // 精确匹配下界岩方块（旧版 BlockMatcher.forBlock(Blocks.NETHERRACK)；
        // 注意 vanilla 1.21.1 无 minecraft:netherrack tag，不能用 TagMatchTest）
        RuleTest netherrack = new BlockStateMatchTest(Blocks.NETHERRACK.defaultBlockState());

        // 矿脉大小 5（旧版 WorldGenMinable(block, 5, ...)）
        context.register(ORE_COBALT, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(netherrack, cobaltOre, 5)));
        context.register(ORE_ARDITE, new ConfiguredFeature<>(Feature.ORE, new OreConfiguration(netherrack, arditeOre, 5)));
    }
}
