package com.lvdriver.tconstruct_nirvana.api.registry;

import com.lvdriver.tconstruct_nirvana.modifier.Modifier;

import java.util.Collection;

/**
 * 修饰符注册表 API（附属 mod 扩展入口）。
 *
 * <p>允许附属 mod 注册自定义修饰符（增强/武器效果）与材料特质（Trait，
 * Trait 继承 {@link Modifier}）。修饰符构造时已自动登记（{@code Modifiers} 静态注册表），
 * 本接口提供显式登记与查询入口。</p>
 *
 * <p><b>稳定 API 承诺</b>：本接口一经发布不再变更方法签名；新增能力只以 default
 * 方法或新接口形式追加。获取实现：{@code TConstructNirvanaAPI.modifiers()}。</p>
 *
 * <p>扩展修饰符：继承 {@link Modifier} 并实现钩子（{@code applyEffect} /
 * {@code damage} / {@code onToolDamage} 等），或 {@code new ModifierAspect} 实现约束；
 * 然后调用 {@link #register}（重复 identifier 覆盖，与旧版 TinkerRegistry 一致）。
 * 附属自有修饰符的 lang 键：{@code modifier.<identifier>.name}。</p>
 */
public interface ModifierRegistry {

    /** 显式登记修饰符（构造已自动登记时可忽略；重复 identifier 覆盖旧条目）。 */
    void register(Modifier modifier);

    /** 按标识查询修饰符，未找到返回 null。 */
    Modifier get(String identifier);

    /** 全部已注册修饰符（含 trait），只读视图，按注册顺序。 */
    Collection<Modifier> getAll();
}
