package com.lvdriver.tconstruct_nirvana.world;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.world.feature.MagmaSlimeIslandFeature;
import com.lvdriver.tconstruct_nirvana.world.feature.MagmaSlimePoolFeature;
import com.lvdriver.tconstruct_nirvana.world.feature.SlimeIslandFeature;
import com.lvdriver.tconstruct_nirvana.world.feature.SlimePoolFeature;
import com.lvdriver.tconstruct_nirvana.world.feature.SlimeTreeFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

/**
 * 世界生成 Feature 注册（DeferredRegister）。
 *
 * <p>史莱姆岛/树/矿池全部为自定义 Feature（1:1 移植旧版 IWorldGenerator
 * 逻辑），注册后经 configured/placed feature + BiomeModifier 数据驱动装配。</p>
 */
public final class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, TConstructNirvana.MODID);

    /** 史莱姆树（5~8 高，凝结石块树干 + 菱形树冠 + 可选藤蔓）。 */
    public static final Supplier<Feature<com.lvdriver.tconstruct_nirvana.world.feature.SlimeTreeConfig>> SLIME_TREE =
            FEATURES.register("slime_tree", SlimeTreeFeature::new);

    /** 史莱姆浮岛（主世界上空，1/730 chunk，旧版 SlimeIslandGenerator）。 */
    public static final Supplier<Feature<com.lvdriver.tconstruct_nirvana.world.feature.EmptyFeatureConfig>> SLIME_ISLAND =
            FEATURES.register("slime_island", SlimeIslandFeature::new);

    /** 岩浆史莱姆岛（下界岩浆海，1/100 chunk，旧版 MagmaSlimeIslandGenerator）。 */
    public static final Supplier<Feature<com.lvdriver.tconstruct_nirvana.world.feature.EmptyFeatureConfig>> MAGMA_SLIME_ISLAND =
            FEATURES.register("magma_slime_island", MagmaSlimeIslandFeature::new);

    /** 史莱姆矿池（主世界地下，默认关闭，旧版 SlimePoolGenerator）。 */
    public static final Supplier<Feature<com.lvdriver.tconstruct_nirvana.world.feature.EmptyFeatureConfig>> SLIME_POOL =
            FEATURES.register("slime_pool", SlimePoolFeature::new);

    /** 岩浆史莱姆矿池（下界地下，默认关闭，旧版 MagmaSlimePoolGenerator）。 */
    public static final Supplier<Feature<com.lvdriver.tconstruct_nirvana.world.feature.EmptyFeatureConfig>> MAGMA_SLIME_POOL =
            FEATURES.register("magma_slime_pool", MagmaSlimePoolFeature::new);

    private ModFeatures() {
    }

    public static void register(IEventBus modEventBus) {
        FEATURES.register(modEventBus);
    }
}
