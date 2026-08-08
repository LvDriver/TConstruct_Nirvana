package com.lvdriver.tconstruct_nirvana.world.feature;

import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

/**
 * 史莱姆树配置（1:1 旧版 SlimeTreeGenerator 构造参数）。
 *
 * <p>{@code foliage} 决定树叶变体与树干凝结石块类型（ORANGE→MAGMA，旧版
 * BlockSlimeSapling.generateTree 映射）；{@code vines} 是否挂藤蔓（岛上树挂、
 * 树苗催熟不挂）；{@code purpleVines} 藤蔓颜色（旧版 treeGenPurple 挂紫藤蔓，
 * treeGenBlue 挂蓝藤蔓）。</p>
 */
public record SlimeTreeConfig(SlimeTypes.FoliageType foliage, boolean vines, boolean purpleVines) implements FeatureConfiguration {

    private static final Codec<SlimeTypes.FoliageType> FOLIAGE_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                try {
                    return com.mojang.serialization.DataResult.success(SlimeTypes.FoliageType.valueOf(s));
                } catch (IllegalArgumentException e) {
                    return com.mojang.serialization.DataResult.error(() -> "Invalid foliage type: " + s);
                }
            },
            SlimeTypes.FoliageType::name);

    public static final Codec<SlimeTreeConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FOLIAGE_CODEC.fieldOf("foliage").forGetter(SlimeTreeConfig::foliage),
            Codec.BOOL.fieldOf("vines").forGetter(SlimeTreeConfig::vines),
            Codec.BOOL.fieldOf("purple_vines").forGetter(SlimeTreeConfig::purpleVines))
            .apply(instance, SlimeTreeConfig::new));
}
