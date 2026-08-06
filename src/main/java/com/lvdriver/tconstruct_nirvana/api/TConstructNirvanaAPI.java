package com.lvdriver.tconstruct_nirvana.api;

import com.lvdriver.tconstruct_nirvana.api.registry.FluidRegistry;
import com.lvdriver.tconstruct_nirvana.api.registry.MaterialRegistry;
import com.lvdriver.tconstruct_nirvana.api.registry.ModifierRegistry;
import com.lvdriver.tconstruct_nirvana.api.registry.PatternRegistry;
import com.lvdriver.tconstruct_nirvana.api.registry.ToolPartRegistry;
import com.lvdriver.tconstruct_nirvana.impl.FluidRegistryImpl;
import com.lvdriver.tconstruct_nirvana.impl.MaterialRegistryImpl;
import com.lvdriver.tconstruct_nirvana.impl.ModifierRegistryImpl;
import com.lvdriver.tconstruct_nirvana.impl.PatternRegistryImpl;
import com.lvdriver.tconstruct_nirvana.impl.ToolPartRegistryImpl;

/**
 * Tinkers' Construct: Nirvana 附属扩展 API 门面（唯一入口）。
 *
 * <p>附属 mod 通过本类获取全部公开注册表：<ul>
 * <li>{@link #materials()} — 注册自定义材料（部件/模具/冶炼用）；</li>
 * <li>{@link #modifiers()} — 注册自定义修饰符与材料特质；</li>
 * <li>{@link #toolParts()} — 注册自定义部件类型（模具形状自动可用）；</li>
 * <li>{@link #patterns()} — 模具物品 accessor 与形状查询；</li>
 * <li>{@link #fluids()} — 注册可冶炼金属流体并关联材料。</li>
 * </ul>
 * 运行期事件钩子见 {@code api.event} 包（ToolBuildEvent / ModifierTriggerEvent /
 * SmelteryEvent 系列），全部挂 {@code NeoForge.EVENT_BUS}，可用
 * {@code EventPriority} 控制监听顺序。</p>
 *
 * <p><b>稳定 API 承诺</b>：本类方法签名与各 Registry 接口一经发布不再变更；
 * 附属可放心 compileOnly 依赖本 mod 编译产物。调用时机：Mod 构造器与
 * 加载事件均可（部件/流体条目登记须在对应 RegisterEvent 之前）。</p>
 */
public final class TConstructNirvanaAPI {

    private static final MaterialRegistry MATERIALS = new MaterialRegistryImpl();
    private static final ModifierRegistry MODIFIERS = new ModifierRegistryImpl();
    private static final ToolPartRegistry TOOL_PARTS = new ToolPartRegistryImpl();
    private static final PatternRegistry PATTERNS = new PatternRegistryImpl();
    private static final FluidRegistry FLUIDS = new FluidRegistryImpl();

    private TConstructNirvanaAPI() {
    }

    /** 材料注册表（注册/查询自定义材料）。 */
    public static MaterialRegistry materials() {
        return MATERIALS;
    }

    /** 修饰符注册表（注册/查询自定义修饰符与特质）。 */
    public static ModifierRegistry modifiers() {
        return MODIFIERS;
    }

    /** 工具部件注册表（注册/查询自定义部件，模具形状自动可用）。 */
    public static ToolPartRegistry toolParts() {
        return TOOL_PARTS;
    }

    /** 模具注册表（模具物品 accessor 与形状查询）。 */
    public static PatternRegistry patterns() {
        return PATTERNS;
    }

    /** 流体注册表（注册可冶炼金属流体并关联材料）。 */
    public static FluidRegistry fluids() {
        return FLUIDS;
    }
}
