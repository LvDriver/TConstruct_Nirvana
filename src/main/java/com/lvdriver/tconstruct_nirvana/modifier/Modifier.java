package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.LinkedList;
import java.util.List;

/**
 * 修饰符基类（1:1 移植自 Tinkers' Antique {@code Modifier}，DataComponent 版）。
 *
 * <p>修饰符 = 效果（{@link #applyEffect}）+ 约束（{@link ModifierAspect}）。
 * 应用流程：写入 BASE_MODIFIERS → 更新 MODIFIERS 数据（aspect 维护等级/槽/颜色）→
 * 执行 {@code applyEffect} 修改工具属性。注册采用静态注册表
 * （{@link Modifiers}），与材料系统一致（纯数据定义，无需 Registry）。</p>
 */
public abstract class Modifier {

    /** 名称 key 前缀（1:1 旧版 {@code Modifier.LOC_Name}）。 */
    public static final String LOC_NAME = "modifier.%s.name";
    /** 描述 key 前缀（1:1 旧版 {@code Modifier.LOC_Desc}）。 */
    public static final String LOC_DESC = "modifier.%s.desc";

    /** 注册标识（唯一，材料 trait 挂载亦用此值）。 */
    public final String identifier;

    /** 约束列表。 */
    protected final List<ModifierAspect> aspects = new LinkedList<>();

    protected Modifier(String identifier) {
        this.identifier = identifier;
        Modifiers.register(this);
    }

    public String getIdentifier() {
        return identifier;
    }

    /* ---------- 应用 ---------- */

    /** 是否可以应用到工具上（aspects 校验 + 自定义校验）。 */
    public boolean canApply(ItemStack stack, ItemStack original) {
        for (ModifierAspect aspect : aspects) {
            if (!aspect.canApply(stack, original)) {
                return false;
            }
        }
        return canApplyCustom(stack);
    }

    /** 应用修饰符：登记 + 更新数据 + 执行效果。 */
    public void apply(ItemStack stack) {
        // 登记到 base modifiers（旧版 apply 的 tagList.appendTag）
        List<String> baseMods = new java.util.ArrayList<>(ToolHelper.getBaseModifiers(stack));
        if (!baseMods.contains(identifier)) {
            baseMods.add(identifier);
            stack.set(ModDataComponents.BASE_MODIFIERS, baseMods);
        }

        // 更新/新增实例数据
        List<ModifierData> modifierList = new java.util.ArrayList<>(ToolHelper.getModifierData(stack));
        int index = ToolHelper.getIndexInList(modifierList, identifier);
        ModifierData data = index >= 0 ? modifierList.get(index) : new ModifierData(identifier);

        for (ModifierAspect aspect : aspects) {
            data = aspect.updateNBT(stack, data);
        }
        data = updateNBT(data);

        if (index >= 0) {
            modifierList.set(index, data);
        } else {
            modifierList.add(data);
        }
        stack.set(ModDataComponents.MODIFIERS, modifierList);

        applyEffect(stack, data);

        // 属性组件刷新（1:1 旧版 getAttributeModifiers 动态读取；1.21.1 组件方案需显式同步）
        if (stack.getItem() instanceof com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem) {
            com.lvdriver.tconstruct_nirvana.util.ToolHelper.updateAttributes(stack);
        }
    }

    /** 修改工具属性的实际效果（子类覆写，如改 TOOL_DATA）。 */
    public void applyEffect(ItemStack stack, ModifierData data) {
    }

    /** 自定义应用校验（默认通过）。 */
    protected boolean canApplyCustom(ItemStack stack) {
        return true;
    }

    /** 应用时的数据更新（默认原样）。 */
    public ModifierData updateNBT(ModifierData data) {
        return data;
    }

    /* ---------- 工具行为钩子（Trait 与 Modifier 共用，事件分发器统一调用） ---------- */

    /** 每 tick 更新（物品在任意栏位时）。 */
    public void onUpdate(ItemStack tool, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity, int itemSlot, boolean isSelected) {
    }

    /** 挖掘速度修正（BreakSpeed 事件）。 */
    public void miningSpeed(ItemStack tool, net.neoforged.neoforge.event.entity.player.PlayerEvent.BreakSpeed event) {
    }

    /** 方块破坏前（BreakEvent 事件）。 */
    public void beforeBlockBreak(ItemStack tool, net.neoforged.neoforge.event.level.BlockEvent.BreakEvent event) {
    }

    /** 方块破坏后。 */
    public void afterBlockBreak(ItemStack tool, net.minecraft.world.level.Level world, net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.LivingEntity player, boolean wasEffective) {
    }

    /** 方块掉落结算（BlockDropsEvent）。 */
    public void blockHarvestDrops(ItemStack tool, net.neoforged.neoforge.event.level.BlockDropsEvent event) {
    }

    /** 是否强制暴击。 */
    public boolean isCriticalHit(ItemStack tool, net.minecraft.world.entity.LivingEntity player, net.minecraft.world.entity.LivingEntity target) {
        return false;
    }

    /** 伤害修正（链式，newDamage 为当前累计值）。 */
    public float damage(ItemStack tool, net.minecraft.world.entity.LivingEntity player, net.minecraft.world.entity.LivingEntity target, float damage, float newDamage, boolean isCritical) {
        return newDamage;
    }

    /** 命中前。 */
    public void onHit(ItemStack tool, net.minecraft.world.entity.LivingEntity player, net.minecraft.world.entity.LivingEntity target, float damage, boolean isCritical) {
    }

    /** 命中后。 */
    public void afterHit(ItemStack tool, net.minecraft.world.entity.LivingEntity player, net.minecraft.world.entity.LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
    }

    /** 击退修正（链式）。 */
    public float knockBack(ItemStack tool, net.minecraft.world.entity.LivingEntity player, net.minecraft.world.entity.LivingEntity target, float damage, float knockback, float newKnockback, boolean isCritical) {
        return newKnockback;
    }

    /** 玩家格挡（LivingIncomingDamageEvent）。 */
    public void onBlock(ItemStack tool, net.minecraft.world.entity.player.Player player, net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
    }

    /** 玩家受伤（工具在任意栏位时）。 */
    public void onPlayerHurt(ItemStack tool, net.minecraft.world.entity.player.Player player, net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
    }

    /** 耐久损耗修正（链式，newDamage 为损耗后累计值）。 */
    public int onToolDamage(ItemStack tool, int damage, int newDamage, net.minecraft.world.entity.LivingEntity entity) {
        return newDamage;
    }

    /** 修复量修正（链式，newAmount 为修复后损伤值）。 */
    public int onToolHeal(ItemStack tool, int amount, int newAmount, net.minecraft.world.entity.LivingEntity entity) {
        return newAmount;
    }

    /** 修复完成回调。 */
    public void onRepair(ItemStack tool, int amount) {
    }

    /* ---------- 显示 ---------- */

    public String getLocalizedName() {
        return Component.translatable(LOC_NAME.formatted(identifier)).getString();
    }

    public String getLocalizedDesc() {
        return Component.translatable(LOC_DESC.formatted(identifier)).getString();
    }

    @Override
    public String toString() {
        return "Modifier{" + identifier + '}';
    }
}
