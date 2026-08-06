package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * DataComponent 注册中枢（DeferredRegister）。
 *
 * <p>1.21.1 物品数据一律走 DataComponent，禁止 NBT tag。
 * 本会话注册两个自定义组件：</p>
 * <ul>
 *   <li>{@link #PART_MATERIAL}：工具部件所用材料标识（对应旧版 {@code Tags.PART_MATERIAL} NBT，
 *   存 {@link com.lvdriver.tconstruct_nirvana.material.Material#identifier} 字符串）；</li>
 *   <li>{@link #PATTERN_SHAPE}：模具形状（对应旧版 {@code Pattern.TAG_PARTTYPE} NBT，
 *   存部件物品注册名 {@link ResourceLocation}，null 表示空白模具）。</li>
 * </ul>
 */
public final class ModDataComponents {

    /** DataComponent 注册表。 */
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TConstructNirvana.MODID);

    /** 部件所用材料标识（材料无注册表，存 identifier 字符串，见 ModMaterials）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> PART_MATERIAL =
            DATA_COMPONENT_TYPES.register("part_material",
                    () -> DataComponentType.<String>builder().persistent(Codec.STRING).build());

    /** 模具形状 = 部件物品注册名（ResourceLocation）；null 表示空白模具/空白浇铸模具。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> PATTERN_SHAPE =
            DATA_COMPONENT_TYPES.register("pattern_shape",
                    () -> DataComponentType.<ResourceLocation>builder().persistent(ResourceLocation.CODEC).build());

    private ModDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
