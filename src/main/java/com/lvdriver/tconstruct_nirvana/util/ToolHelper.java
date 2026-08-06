package com.lvdriver.tconstruct_nirvana.util;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.trait.Trait;
import com.lvdriver.tconstruct_nirvana.trait.Traits;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 工具行为核心（1:1 移植自 Tinkers' Antique {@code ToolHelper}，DataComponent 版）。
 *
 * <p>承载工具数据读写、伤害削减、耐久与修复、挖掘速度计算等运行期逻辑。
 * 攻击链在 1.21.1 走原版流程 + NeoForge 事件（见 {@code TinkerToolEvents}），
 * 本类不再持有自定义攻击入口。</p>
 */
public final class ToolHelper {

    private ToolHelper() {
    }

    /* ---------- 数据读取 ---------- */

    public static ToolData getToolData(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TOOL_DATA, ToolData.empty());
    }

    public static ToolData getOriginalToolData(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.TOOL_DATA_ORIG, ToolData.empty());
    }

    /** 写入工具属性并同步运行时耐久上限（MAX_DAMAGE 组件）。 */
    public static void setToolData(ItemStack stack, ToolData data) {
        stack.set(ModDataComponents.TOOL_DATA, data);
        stack.set(DataComponents.MAX_DAMAGE, Math.max(1, data.durability()));
    }

    public static int getDurabilityStat(ItemStack stack) {
        return getToolData(stack).durability();
    }

    public static int getHarvestLevelStat(ItemStack stack) {
        return getToolData(stack).harvestLevel();
    }

    public static float getAttackStat(ItemStack stack) {
        return getToolData(stack).attack();
    }

    public static float getMiningSpeedStat(ItemStack stack) {
        return getToolData(stack).speed();
    }

    public static float getAttackSpeedStat(ItemStack stack) {
        return getToolData(stack).attackSpeedMultiplier();
    }

    public static int getFreeModifiers(ItemStack stack) {
        return getToolData(stack).modifiers();
    }

    public static List<Material> getMaterials(ItemStack stack) {
        List<String> ids = stack.getOrDefault(ModDataComponents.BASE_MATERIALS, List.of());
        List<Material> materials = new ArrayList<>(ids.size());
        for (String id : ids) {
            materials.add(Material.getByIdentifier(id));
        }
        return materials;
    }

    /** 已应用修饰符标识列表（含 trait，旧版 base modifiers）。 */
    public static List<String> getBaseModifiers(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BASE_MODIFIERS, List.of());
    }

    /** 修饰符实例数据列表（含等级/颜色，旧版 tool modifiers）。 */
    public static List<ModifierData> getModifierData(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.MODIFIERS, List.of());
    }

    /** 工具特质实例列表（有序、去重，旧版 getTraitsOrdered；已并入 getActiveModifiers 分发链）。 */
    public static List<Trait> getTraits(ItemStack stack) {
        List<String> ids = stack.getOrDefault(ModDataComponents.traitsType(), java.util.List.<String>of());
        Set<Trait> seen = new LinkedHashSet<>();
        for (String id : ids) {
            Trait trait = Traits.get(id);
            if (trait != null) {
                seen.add(trait);
            }
        }
        return new ArrayList<>(seen);
    }

    /**
     * 工具上全部活跃修饰符实例（trait + 已应用修饰符，去重，旧版 base modifiers 对应物）。
     * 事件分发与耐久链遍历用（necrotic/fiery 等 Modifier 也是活跃钩子）。
     */
    public static List<com.lvdriver.tconstruct_nirvana.modifier.Modifier> getActiveModifiers(ItemStack stack) {
        Set<com.lvdriver.tconstruct_nirvana.modifier.Modifier> seen = new LinkedHashSet<>();
        for (ModifierData data : getModifierData(stack)) {
            com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier = com.lvdriver.tconstruct_nirvana.modifier.Modifiers.get(data.identifier());
            if (modifier != null) {
                seen.add(modifier);
            }
        }
        // 未登记数据的纯 trait（材料挂载路径已写 MODIFIERS，双保险）
        for (String id : stack.getOrDefault(ModDataComponents.traitsType(), java.util.List.<String>of())) {
            com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier = com.lvdriver.tconstruct_nirvana.modifier.Modifiers.get(id);
            if (modifier != null) {
                seen.add(modifier);
            }
        }
        return new ArrayList<>(seen);
    }

    public static boolean isBroken(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BROKEN, false);
    }

    /* ---------- 属性组件（1.21.1 动态攻击/攻速 → ATTRIBUTE_MODIFIERS 组件） ---------- */

    /** 按当前 ToolData 刷新攻击/攻速属性组件（buildItem 与修饰符变更后调用）。 */
    public static void updateAttributes(ItemStack stack) {
        if (isBroken(stack)) {
            stack.remove(DataComponents.ATTRIBUTE_MODIFIERS);
            return;
        }
        if (!(stack.getItem() instanceof TinkerToolItem tool)) {
            return;
        }
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct_nirvana", "attack_damage"),
                        getActualAttack(stack), AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct_nirvana", "attack_speed"),
                        getActualAttackSpeed(stack) - 4.0, AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND);
        stack.set(DataComponents.ATTRIBUTE_MODIFIERS, builder.build());
    }

    /* ---------- 耐久 ---------- */

    /** 消耗耐久（旧版 damageTool），经由活跃修饰符 onToolDamage 链；损耗至上限标记损坏。 */
    public static void damageTool(ItemStack stack, int amount, LivingEntity entity) {
        if (amount <= 0 || isBroken(stack)) {
            return;
        }
        int damage = amount;
        int newDamage = stack.getDamageValue() + damage;
        for (com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier : getActiveModifiers(stack)) {
            newDamage = modifier.onToolDamage(stack, damage, newDamage, entity);
        }
        // 1:1 旧版 clamp：损耗不越过最大耐久
        int max = stack.getMaxDamage();
        newDamage = Math.min(newDamage, max);
        if (newDamage != stack.getDamageValue()) {
            stack.setDamageValue(newDamage);
        }
        if (stack.getDamageValue() >= max) {
            breakTool(stack, entity);
            // 损坏后移除攻击属性（1.21.1 属性面板同步）
            updateAttributes(stack);
        }
    }

    /** 修复耐久（旧版 repairTool），经由活跃修饰符 onToolHeal 链。 */
    public static void repairTool(ItemStack stack, int amount) {
        repairTool(stack, amount, null);
    }

    public static void repairTool(ItemStack stack, int amount, LivingEntity entity) {
        // 1:1 旧版 repairTool 语义：先解除损坏（unbreakTool），再修复损伤
        if (isBroken(stack) && amount > 0) {
            stack.remove(ModDataComponents.BROKEN);
            // 恢复攻击属性（与 damageTool 损坏分支对称，1.21.1 属性面板同步）
            updateAttributes(stack);
        }
        int newAmount = Math.max(0, stack.getDamageValue() - amount);
        for (com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier : getActiveModifiers(stack)) {
            newAmount = modifier.onToolHeal(stack, amount, newAmount, entity);
        }
        stack.setDamageValue(newAmount);
    }

    /** 标记工具损坏（旧版 breakTool）。 */
    public static void breakTool(ItemStack stack, LivingEntity entity) {
        stack.set(ModDataComponents.BROKEN, true);
    }

    /* ---------- 实际值换算（1:1 旧版） ---------- */

    public static float getActualAttack(ItemStack stack) {
        float damage = getAttackStat(stack);
        if (stack.getItem() instanceof TinkerToolItem tool) {
            damage *= tool.damagePotential();
        }
        return damage;
    }

    public static float getActualAttackSpeed(ItemStack stack) {
        float speed = getAttackSpeedStat(stack);
        if (stack.getItem() instanceof TinkerToolItem tool) {
            speed *= tool.attackSpeed();
        }
        return speed;
    }

    public static float getActualMiningSpeed(ItemStack stack) {
        float speed = getMiningSpeedStat(stack);
        if (stack.getItem() instanceof TinkerToolItem tool) {
            speed *= tool.miningSpeedModifier();
        }
        return speed;
    }

    /**
     * 伤害削减（旧版 calcCutoffDamage）：超出 cutoff 的部分按 0.9 逐档衰减累加。
     */
    public static float calcCutoffDamage(float damage, float cutoff) {
        float p = 1f;
        float d = damage;
        damage = 0f;
        while (d > cutoff) {
            damage += p * cutoff;
            if (p > 0.001f) {
                p *= 0.9f;
            } else {
                damage += p * cutoff * ((d / cutoff) - 1f);
                return damage;
            }
            d -= cutoff;
        }
        damage += p * d;
        return damage;
    }

    /* ---------- 挖掘 ---------- */

    /** 工具是否有效于该方块（旧版 isToolEffective：tool class → 1.21 mineable tag）。 */
    public static boolean isToolEffective(ItemStack stack, BlockState state) {
        for (TagKey<net.minecraft.world.level.block.Block> tag : TConTags.MINEABLE_TAGS) {
            if (state.is(tag) && stack.is(TConTags.itemTagFor(tag))) {
                return true;
            }
        }
        return false;
    }

    /** 方块所需采掘等级（1.21.1 needs_* tag → 旧版等级常量；供 HarvestCheck 与 AOE 校验复用）。 */
    public static int requiredHarvestLevel(BlockState state) {
        if (state.is(TConTags.NEEDS_COBALT_TOOL)) {
            return HarvestLevels.COBALT;
        }
        if (state.is(BlockTags.NEEDS_DIAMOND_TOOL)) {
            return HarvestLevels.DIAMOND;
        }
        if (state.is(BlockTags.NEEDS_IRON_TOOL)) {
            return HarvestLevels.IRON;
        }
        return HarvestLevels.STONE;
    }

    /** 挖掘速度（旧版 calcDigSpeed）。broken → 0.3；无效 → 1.0；否则速度×系数。 */
    public static float calcDigSpeed(ItemStack stack, BlockState state) {
        if (state == null) {
            return 0f;
        }
        if (stack.getComponents().isEmpty()) {
            return 1f;
        }
        if (isBroken(stack)) {
            return 0.3f;
        }
        if (!(stack.getItem() instanceof TinkerToolItem tool)) {
            return 1f;
        }
        if (!(tool.isEffective(state) || isToolEffective(stack, state))) {
            return 1f;
        }
        return getActualMiningSpeed(stack);
    }

    /* ---------- 修饰符工具方法 ---------- */

    /** 修饰符是否已应用（旧版 TinkerUtil.hasModifier）。 */
    public static boolean hasModifier(ItemStack stack, String identifier) {
        return getBaseModifiers(stack).contains(identifier);
    }

    /** 在修饰符数据列表中查找索引（旧版 getIndexInList）。 */
    public static int getIndexInList(List<ModifierData> list, String identifier) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).identifier().equals(identifier)) {
                return i;
            }
        }
        return -1;
    }

    /** 修饰符实例数据（按 identifier，缺失返回 null）。 */
    public static ModifierData getModifierData(ItemStack stack, String identifier) {
        for (ModifierData data : getModifierData(stack)) {
            if (data.identifier().equals(identifier)) {
                return data;
            }
        }
        return null;
    }
}
