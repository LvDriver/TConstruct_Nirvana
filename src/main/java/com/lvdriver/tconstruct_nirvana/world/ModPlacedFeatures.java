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

import java.util.List;

/**
 * 矿石放置规则（placed feature）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code NetherOreGenerator}：
 * 每区块约 20 个矿脉尝试（旧版 {@code cobaltRate=20 / arditeRate=20}），
 * Y 范围 0~128（旧版两段随机：y∈[32,96) 与 y∈[0,128)，合并近似为均匀 0~128）。
 * 仅在生物群系判定生效时生成（{@code BiomeFilter.biome()}）。</p>
 */
public final class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> ORE_COBALT = key("ore_cobalt");
    public static final ResourceKey<PlacedFeature> ORE_ARDITE = key("ore_ardite");

    private static ResourceKey<PlacedFeature> key(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath("tconstruct_nirvana", name));
    }

    private ModPlacedFeatures() {
    }

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        Holder<ConfiguredFeature<?, ?>> cobalt = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.ORE_COBALT);
        Holder<ConfiguredFeature<?, ?>> ardite = context.lookup(Registries.CONFIGURED_FEATURE).getOrThrow(ModConfiguredFeatures.ORE_ARDITE);

        context.register(ORE_COBALT, new PlacedFeature(cobalt, netherOrePlacement(20)));
        context.register(ORE_ARDITE, new PlacedFeature(ardite, netherOrePlacement(20)));
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
