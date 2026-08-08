package com.lvdriver.tconstruct_nirvana.world;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;

import java.util.List;

/**
 * 放置规则（placed feature）。
 *
 * <p>矿石：每区块约 20 个矿脉尝试（旧版 {@code cobaltRate=20 / arditeRate=20}），
 * Y 0~128；史莱姆岛 1/730 chunk（旧版 slimeIslandsRate）、岩浆岛 1/100
 * （magmaIslandsRate）、矿池 1/30（slimePoolRate，Y 0~64）、岩浆池 1/30
 * （magmaPoolRate，Y 0~128）。岛的实际 Y 由 Feature 内部重算（旧版
 * 地表 + 61~110 / 岩浆面 31），此处高度范围仅占位。</p>
 */
public final class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> ORE_COBALT = key("ore_cobalt");
    public static final ResourceKey<PlacedFeature> ORE_ARDITE = key("ore_ardite");

    public static final ResourceKey<PlacedFeature> SLIME_ISLAND = key("slime_island");
    public static final ResourceKey<PlacedFeature> MAGMA_SLIME_ISLAND = key("magma_slime_island");
    public static final ResourceKey<PlacedFeature> SLIME_POOL = key("slime_pool");
    public static final ResourceKey<PlacedFeature> MAGMA_SLIME_POOL = key("magma_slime_pool");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("tconstruct_nirvana", name));
    }

    private ModPlacedFeatures() {
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        Holder<ConfiguredFeature<?, ?>> cobalt = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.ORE_COBALT);
        Holder<ConfiguredFeature<?, ?>> ardite = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.ORE_ARDITE);
        Holder<ConfiguredFeature<?, ?>> island = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.SLIME_ISLAND);
        Holder<ConfiguredFeature<?, ?>> magmaIsland = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.MAGMA_SLIME_ISLAND);
        Holder<ConfiguredFeature<?, ?>> pool = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.SLIME_POOL);
        Holder<ConfiguredFeature<?, ?>> magmaPool = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.MAGMA_SLIME_POOL);

        context.register(ORE_COBALT, new PlacedFeature(cobalt, netherOrePlacement(20)));
        context.register(ORE_ARDITE, new PlacedFeature(ardite, netherOrePlacement(20)));

        // 史莱姆岛：1/730 chunk（旧版 nextInt(730)==0），y 由 Feature 内部重算
        context.register(SLIME_ISLAND, new PlacedFeature(island, List.of(
                RarityFilter.onAverageOnceEvery(730),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255)),
                BiomeFilter.biome())));
        // 岩浆岛：1/100 chunk
        context.register(MAGMA_SLIME_ISLAND, new PlacedFeature(magmaIsland, List.of(
                RarityFilter.onAverageOnceEvery(100),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(255)),
                BiomeFilter.biome())));
        // 矿池：1/30 chunk、Y 0~64（旧版 slimePoolRate=30 / slimePoolHeightMax=64）
        context.register(SLIME_POOL, new PlacedFeature(pool, List.of(
                RarityFilter.onAverageOnceEvery(30),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(64)),
                BiomeFilter.biome())));
        // 岩浆池：1/30 chunk、Y 0~128
        context.register(MAGMA_SLIME_POOL, new PlacedFeature(magmaPool, List.of(
                RarityFilter.onAverageOnceEvery(30),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(128)),
                BiomeFilter.biome())));
    }

    /** 下界矿石放置：每区块 20 次尝试、方块内随机、Y 0~128 均匀、生物群系过滤。 */
    private static List<PlacementModifier> netherOrePlacement(int count) {
        return List.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(128)),
                BiomeFilter.biome());
    }
}
