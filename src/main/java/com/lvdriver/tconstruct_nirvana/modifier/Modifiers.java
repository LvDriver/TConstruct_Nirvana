package com.lvdriver.tconstruct_nirvana.modifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 修饰符静态注册表（1:1 对应旧版 {@code TinkerRegistry} 的修饰符注册部分）。
 *
 * <p>修饰符为纯逻辑定义（无物品/方块形态），采用与材料系统一致的静态注册：
 * {@link Modifier} 构造时自动登记。附属扩展经
 * {@code TConstructNirvanaAPI.modifiers()}（{@code api} 包）调用本表。</p>
 */
public final class Modifiers {

    private static final Map<String, Modifier> MODIFIERS = new LinkedHashMap<>();

    private Modifiers() {
    }

    /** 登记修饰符（Modifier 构造函数自动调用；附属显式注册亦经此，重复标识覆盖）。 */
    public static void register(Modifier modifier) {
        MODIFIERS.put(modifier.identifier, modifier);
    }

    /** 按标识查询修饰符，未找到返回 null。 */
    public static Modifier get(String identifier) {
        return MODIFIERS.get(identifier);
    }

    /** 全部已注册修饰符（含 trait），按注册顺序。 */
    public static Collection<Modifier> getAll() {
        return Collections.unmodifiableCollection(MODIFIERS.values());
    }

    /** 已注册数量（调试用）。 */
    public static int size() {
        return MODIFIERS.size();
    }
}
