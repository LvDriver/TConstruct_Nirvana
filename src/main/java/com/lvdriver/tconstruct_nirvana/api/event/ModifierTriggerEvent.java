package com.lvdriver.tconstruct_nirvana.api.event;

import com.lvdriver.tconstruct_nirvana.modifier.Modifier;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 修饰符触发事件（{@link NeoForge#EVENT_BUS}，只读监听）。
 *
 * <p>触发时机：工具命中实体（{@link Trigger#ATTACK}）与方块破坏后
 * （{@link Trigger#BLOCK_BREAK}），修饰符效果链（{@code damage} / {@code afterHit} /
 * {@code afterBlockBreak} 等）执行后触发。附属可监听此事件实现联动
 * （统计、成就、扩展效果），事件不可取消、不可修改栈（效果已发生）。</p>
 *
 * <p>监听示例：{@code NeoForge.EVENT_BUS.addListener(ModifierTriggerEvent.class, e -> ...)}。</p>
 *
 * <p><b>稳定 API 承诺</b>：字段与 getter 一经发布不变；新增数据只以新 getter 追加。</p>
 */
public class ModifierTriggerEvent extends Event {

    /** 触发类型。 */
    public enum Trigger {
        /** 工具命中实体（原版攻击流程命中确认时）。 */
        ATTACK,
        /** 工具破坏方块后。 */
        BLOCK_BREAK
    }

    /** 触发类型。 */
    private final Trigger trigger;

    /** 触发时持有的工具栈（只读快照）。 */
    private final ItemStack tool;

    /** 使用者（攻击者/破坏者）。 */
    private final LivingEntity user;

    /** 触发链上的活跃修饰符（trait + 修饰符，去重，只读快照）。 */
    private final List<Modifier> modifiers;

    public ModifierTriggerEvent(Trigger trigger, ItemStack tool, LivingEntity user, List<Modifier> modifiers) {
        this.trigger = trigger;
        this.tool = tool;
        this.user = user;
        this.modifiers = List.copyOf(modifiers);
    }

    /** 触发类型（ATTACK / BLOCK_BREAK）。 */
    public Trigger getTrigger() {
        return trigger;
    }

    /** 触发时持有的工具栈（只读快照）。 */
    public ItemStack getTool() {
        return tool;
    }

    /** 使用者（攻击者/破坏者）。 */
    public LivingEntity getUser() {
        return user;
    }

    /** 触发链上的活跃修饰符（trait + 修饰符，去重，只读快照）。 */
    public List<Modifier> getModifiers() {
        return modifiers;
    }
}
