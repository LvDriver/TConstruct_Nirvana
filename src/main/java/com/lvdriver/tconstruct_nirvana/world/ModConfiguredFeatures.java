package com.lvdriver.tconstruct_nirvana.world;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.world.feature.EmptyFeatureConfig;
import com.lvdriver.tconstruct_nirvana.world.feature.SlimeTreeConfig;
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
 * 世界生成 configured feature（DataGen bootstrap）。
 *
 * <p>矿石 1:1 移植自 Tinkers' Antique {@code NetherOreGenerator}（矿脉大小 5、
 * 替换下界岩）；史莱姆生态 1:1 移植自 {@code SlimeIslandGenerator} /
 * {@code SlimeTreeGenerator} / {@code SlimePoolGenerator} 等（自定义 Feature
 * 类承载生成逻辑，此处仅装配注册）。</p>
 */
public final class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_COBALT = key("ore_cobalt");
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_ARDITE = key("ore_ardite");

    /** 史莱姆树（树苗催熟用；岛内树直接调 Feature 静态方法，不需 configured）。 */
    public static final ResourceKey<ConfiguredFeature<?, ?>> SLIME_TREE_BLUE = key("slime_tree_blue");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SLIME_TREE_PURPLE = key("slime_tree_purple");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SLIME_TREE_ORANGE = key("slime_tree_orange");

    /** 史莱姆浮岛 / 岩浆岛 / 矿池 / 岩浆池。 */
    public static final ResourceKey<ConfiguredFeature<?, ?>> SLIME_ISLAND = key("slime_island");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAGMA_SLIME_ISLAND = key("magma_slime_island");
    public static final ResourceKey<ConfiguredFeature<?, ?>> SLIME_POOL = key("slime_pool");
    public static final ResourceKey<ConfiguredFeature<?, ?>> MAGMA_SLIME_POOL = key("magma_slime_pool");

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

        // 史莱姆树（树苗催熟：无藤蔓，1:1 旧版 BlockSlimeSapling.generateTree）
        context.register(SLIME_TREE_BLUE, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.SLIME_TREE.get(),
                new SlimeTreeConfig(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType.BLUE, false, false)));
        context.register(SLIME_TREE_PURPLE, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.SLIME_TREE.get(),
                new SlimeTreeConfig(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType.PURPLE, false, false)));
        context.register(SLIME_TREE_ORANGE, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.SLIME_TREE.get(),
                new SlimeTreeConfig(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType.ORANGE, false, false)));

        // 岛/池（自定义 Feature，空配置）
        context.register(SLIME_ISLAND, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.SLIME_ISLAND.get(), new EmptyFeatureConfig()));
        context.register(MAGMA_SLIME_ISLAND, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.MAGMA_SLIME_ISLAND.get(), new EmptyFeatureConfig()));
        context.register(SLIME_POOL, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.SLIME_POOL.get(), new EmptyFeatureConfig()));
        context.register(MAGMA_SLIME_POOL, new ConfiguredFeature<>(
                com.lvdriver.tconstruct_nirvana.world.ModFeatures.MAGMA_SLIME_POOL.get(), new EmptyFeatureConfig()));
    }
}
