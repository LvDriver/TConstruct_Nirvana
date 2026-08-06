package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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

    /* ---------- 工具数据组件（1:1 旧版 Tags：BASE_DATA / TOOL_DATA / TOOL_DATA_ORIG / 修饰符 / 特质） ---------- */

    /** 计算后的工具基础属性（旧版 {@code Tags.TOOL_DATA}），随修饰符应用更新。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolData>> TOOL_DATA =
            DATA_COMPONENT_TYPES.register("tool_data",
                    () -> DataComponentType.<ToolData>builder().persistent(ToolData.CODEC).build());

    /** 合成时的原始工具属性副本（旧版 {@code Tags.TOOL_DATA_ORIG}），用于修复与 tooltip。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ToolData>> TOOL_DATA_ORIG =
            DATA_COMPONENT_TYPES.register("tool_data_orig",
                    () -> DataComponentType.<ToolData>builder().persistent(ToolData.CODEC).build());

    /** 组装工具所用材料标识列表（按槽位顺序，旧版 {@code Tags.BASE_MATERIALS}）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> BASE_MATERIALS =
            DATA_COMPONENT_TYPES.register("base_materials",
                    () -> DataComponentType.<List<String>>builder().persistent(Codec.STRING.listOf()).build());

    /** 已应用修饰符标识列表（旧版 {@code Tags.BASE_MODIFIERS}），用于免费槽计算与重放。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> BASE_MODIFIERS =
            DATA_COMPONENT_TYPES.register("base_modifiers",
                    () -> DataComponentType.<List<String>>builder().persistent(Codec.STRING.listOf()).build());

    /** 修饰符/特质实例数据（旧版 {@code Tags.TOOL_MODIFIERS}，含等级/颜色/附加信息）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<ModifierData>>> MODIFIERS =
            DATA_COMPONENT_TYPES.register("modifiers",
                    () -> DataComponentType.<List<ModifierData>>builder().persistent(ModifierData.CODEC.listOf()).build());

    /** 工具携带的特质标识列表（旧版 {@code Tags.TOOL_TRAITS}，由材料挂载）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<String>>> TRAITS =
            DATA_COMPONENT_TYPES.register("traits",
                    () -> DataComponentType.<List<String>>builder().persistent(Codec.STRING.listOf()).build());

    /** 工具是否损坏（旧版 {@code Tags.BROKEN}，耐久归零后失效）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> BROKEN =
            DATA_COMPONENT_TYPES.register("broken",
                    () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());

    /** 累计修复次数（旧版 {@code Tags.REPAIR_COUNT}，修复收益递减用）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> REPAIR_COUNT =
            DATA_COMPONENT_TYPES.register("repair_count",
                    () -> DataComponentType.<Integer>builder().persistent(Codec.INT).build());

    /** 自定义显示名开关（旧版 {@code Tags.RESET_FLAG}，名称变更时重置工具动画）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> RESET_FLAG =
            DATA_COMPONENT_TYPES.register("reset_flag",
                    () -> DataComponentType.<Boolean>builder().persistent(Codec.BOOL).build());

    /** Mattock 双采掘等级 [axeLevel, shovelLevel]（1:1 旧版 MattockToolNBT）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<List<Integer>>> MATTOCK_LEVELS =
            DATA_COMPONENT_TYPES.register("mattock_levels",
                    () -> DataComponentType.<List<Integer>>builder().persistent(Codec.INT.listOf()).build());

    /** 弓弩投射数据（1:1 旧版 ProjectileLauncherNBT 的 drawSpeed/range/bonusDamage）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<LauncherData>> LAUNCHER_DATA =
            DATA_COMPONENT_TYPES.register("launcher_data",
                    () -> DataComponentType.<LauncherData>builder().persistent(LauncherData.CODEC).build());

    /** 弹射物精准度（1:1 旧版 ProjectileNBT.accuracy，箭/弩矢/手里剑用）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Float>> ACCURACY =
            DATA_COMPONENT_TYPES.register("accuracy",
                    () -> DataComponentType.<Float>builder().persistent(Codec.FLOAT).build());

    /** 特质等级存储（identifier → 当前等级，momentum/insatiable/splintering 等动态等级用）。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<java.util.Map<String, Integer>>> TRAIT_LEVELS =
            DATA_COMPONENT_TYPES.register("trait_levels",
                    () -> DataComponentType.<java.util.Map<String, Integer>>builder()
                            .persistent(com.mojang.serialization.Codec.unboundedMap(Codec.STRING, Codec.INT)).build());

    /** 强类型访问器（绕过 DeferredHolder 通配符推断问题）。 */
    @SuppressWarnings("unchecked")
    public static DataComponentType<java.util.Map<String, Integer>> traitLevelsType() {
        return (DataComponentType<java.util.Map<String, Integer>>) (DataComponentType<?>) TRAIT_LEVELS.get();
    }

    /** 强类型访问器（TRAITS 组件）。 */
    @SuppressWarnings("unchecked")
    public static DataComponentType<List<String>> traitsType() {
        return (DataComponentType<List<String>>) (DataComponentType<?>) TRAITS.get();
    }

    /** 强类型访问器（BASE_MATERIALS 组件）。 */
    @SuppressWarnings("unchecked")
    public static DataComponentType<List<String>> baseMaterialsType() {
        return (DataComponentType<List<String>>) (DataComponentType<?>) BASE_MATERIALS.get();
    }

    private ModDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
