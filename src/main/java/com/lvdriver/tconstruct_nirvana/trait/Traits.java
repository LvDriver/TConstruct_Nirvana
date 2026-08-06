package com.lvdriver.tconstruct_nirvana.trait;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Trait 静态注册表（1:1 对应旧版 {@code TinkerTraits} 静态实例 + {@code TinkerRegistry} trait 注册）。
 *
 * <p>identifier 与材料挂载字符串一致（分级 trait 带等级后缀，如 {@code magnetic1}/ {@code magnetic2}）。
 * 工具组装时按 identifier 从本表解析 Trait 实例挂载到工具。</p>
 */
public final class Traits {

    private static final Map<String, Trait> TRAITS = new LinkedHashMap<>();

    private Traits() {
    }

    /** 构造时登记（Trait 构造函数经 Modifiers 链路调用）。 */
    public static void register(Trait trait) {
        TRAITS.put(trait.identifier, trait);
    }

    /** 按标识查询 trait，未找到返回 null。 */
    public static Trait get(String identifier) {
        return TRAITS.get(identifier);
    }

    /** 全部已注册 trait。 */
    public static Collection<Trait> getAll() {
        return Collections.unmodifiableCollection(TRAITS.values());
    }
}
