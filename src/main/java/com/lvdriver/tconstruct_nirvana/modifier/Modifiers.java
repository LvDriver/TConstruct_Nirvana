package com.lvdriver.tconstruct_nirvana.modifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 修饰符静态注册表（1:1 对应旧版 {@code TinkerRegistry} 的修饰符注册部分）。
 *
 * <p>修饰符为纯逻辑定义（无物品/方块形态），采用与材料系统一致的静态注册：
 * {@link Modifier} 构造时自动登记。附属 mod 扩展 API 在后续会话公开
 * （{@code Modifiers#get} 已可查询）。</p>
 */
public final class Modifiers {

    private static final Map<String, Modifier> MODIFIERS = new LinkedHashMap<>();

    private Modifiers() {
    }

    /** 构造时登记（Modifier 构造函数调用）。 */
    static void register(Modifier modifier) {
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
