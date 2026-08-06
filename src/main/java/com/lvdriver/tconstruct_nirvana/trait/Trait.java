package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.modifier.Modifier;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 材料特性基类（1:1 移植自 Tinkers' Antique {@code AbstractTrait}）。
 *
 * <p>特性是"随材料自动附带"的修饰符：工具组装时由部件材料挂载
 * （{@code TinkerToolItem#addMaterialTraits}）。行为钩子（挖掘/攻击/耐久等）
 * 定义在 {@link Modifier} 基类，由事件分发器（{@code TinkerToolEvents}）
 * 统一按触发时机调用——触发时机与效果与旧版一致。</p>
 */
public abstract class Trait extends Modifier {

    /** tooltip 显示色（ARGB）。 */
    public final int color;

    protected Trait(String identifier, int color) {
        super(identifier);
        this.color = color;
        Traits.register(this);
    }

    /* ---------- 挂载 ---------- */

    /**
     * 挂载特质到工具：仅写入 TRAITS 列表（旧版 {@code AbstractTrait#applyEffect}），
     * 不占用强化槽、不进入 BASE_MODIFIERS。
     */
    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        List<String> traits = new java.util.ArrayList<>(stack.getOrDefault(ModDataComponents.traitsType(), List.of()));
        if (!traits.contains(identifier)) {
            traits.add(identifier);
            stack.set(ModDataComponents.traitsType(), traits);
        }
    }

    /* ---------- 显示 ---------- */

    @Override
    public String getLocalizedName() {
        return Component.translatable(LOC_NAME.formatted(identifier)).getString();
    }
}
