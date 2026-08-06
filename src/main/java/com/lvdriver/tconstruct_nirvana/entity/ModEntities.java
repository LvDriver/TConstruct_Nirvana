package com.lvdriver.tconstruct_nirvana.entity;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 实体注册中枢（DeferredRegister）。
 *
 * <p>会话4.5b 注册三个自定义弹射物实体（1:1 移植自旧版
 * {@code EntityArrow / EntityBolt / EntityShuriken}）：
 * 箭、弩矢、手里剑。均继承 {@link TinkerProjectileBase}。</p>
 */
public final class ModEntities {

    /** 实体类型注册表。 */
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, TConstructNirvana.MODID);

    /** 箭弹射物（弓发射，1:1 旧版 {@code EntityArrow}）。 */
    public static final DeferredHolder<EntityType<?>, EntityType<TinkerArrow>> TINKER_ARROW =
            ENTITY_TYPES.register("arrow", () -> EntityType.Builder.<TinkerArrow>of(TinkerArrow::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("arrow"));

    /** 弩矢弹射物（弩发射，1:1 旧版 {@code EntityBolt}，重力 0.065 / 阻力 0.985）。 */
    public static final DeferredHolder<EntityType<?>, EntityType<TinkerBolt>> TINKER_BOLT =
            ENTITY_TYPES.register("bolt", () -> EntityType.Builder.<TinkerBolt>of(TinkerBolt::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("bolt"));

    /** 手里剑弹射物（投掷，1:1 旧版 {@code EntityShuriken}，体积 0.3×0.1、动态重力）。 */
    public static final DeferredHolder<EntityType<?>, EntityType<TinkerShuriken>> TINKER_SHURIKEN =
            ENTITY_TYPES.register("shuriken", () -> EntityType.Builder.<TinkerShuriken>of(TinkerShuriken::new, MobCategory.MISC)
                    .sized(0.3f, 0.1f)
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("shuriken"));

    private ModEntities() {
    }

    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
    }
}
