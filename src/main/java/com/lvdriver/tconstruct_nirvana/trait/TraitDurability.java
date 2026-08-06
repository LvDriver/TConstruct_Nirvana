package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 耐久类特质（1:1 移植自 Tinkers' Antique 各 Trait 的 onToolDamage/onToolHeal 钩子）。
 * 文件内多个 package-private 类，由 {@code ModTraits} 统一实例化注册。
 */

/** 杜瑞托斯（duritos）：10% 双倍消耗、40% 无消耗、50% 正常（平均省 30%）。 */
class TraitDuritos extends Trait {
    TraitDuritos() {
        super("duritos", 0xff55ff);
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, LivingEntity entity) {
        float roll = com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat();
        if (roll < 0.1f) {
            return newDamage + damage; // 双倍
        }
        if (roll < 0.5f) {
            return newDamage - damage; // 无消耗
        }
        return newDamage;
    }
}

/** 致密（dense）：低耐久时高概率减免伤害。 */
class TraitDense extends Trait {
    TraitDense() {
        super("dense", 0xffffff);
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, LivingEntity entity) {
        int max = tool.getMaxDamage();
        int current = tool.getDamageValue();
        float chance = (float) Math.pow(0.75 * (1 - (float) current / max), 3);
        if (com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < chance) {
            return newDamage - Math.max(damage / 2, 1);
        }
        return newDamage;
    }
}

/** 廉价（cheap）：修复效果 -5%（1:1 旧版：newAmount + amount×5/100，修复量更少）。 */
class TraitCheap extends Trait {
    TraitCheap() {
        super("cheap", 0x555555);
    }

    @Override
    public int onToolHeal(ItemStack tool, int amount, int newAmount, LivingEntity entity) {
        return newAmount + amount * 5 / 100;
    }
}

/** 守财奴（cheapskate）：合成耐久 ×80%。 */
class TraitCheapskate extends Trait {
    TraitCheapskate() {
        super("cheapskate", 0xaaaaaa);
    }

    @Override
    public void applyEffect(ItemStack stack, com.lvdriver.tconstruct_nirvana.data.ModifierData data) {
        super.applyEffect(stack, data);
        com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
        ToolHelper.setToolData(stack, toolData.withDurability(Math.max(1, Math.round(toolData.durability() * 0.8f))));
    }
}

/** 生态（ecological）：手持时每 tick 1/400 概率修复 1 点。 */
class TraitEcological extends Trait {
    TraitEcological() {
        super("ecological", 0x55ff55);
    }

    @Override
    public void onUpdate(ItemStack tool, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity,
                         int itemSlot, boolean isSelected) {
        if (world.isClientSide || !isSelected || !(entity instanceof LivingEntity living)) {
            return;
        }
        if (living.getUseItem() == tool) {
            return; // 使用中不修复
        }
        if (tool.getDamageValue() > 0 && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 1f / 400f) {
            ToolHelper.repairTool(tool, 1, living);
        }
    }
}

/** 石肤（petramor）：破坏石头 10% 概率修复 5 耐久。 */
class TraitPetramor extends Trait {
    TraitPetramor() {
        super("petramor", 0xff5555);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, net.minecraft.world.level.Level world,
                                net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos,
                                LivingEntity player, boolean wasEffective) {
        if (wasEffective && state.is(net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD)
                && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 0.1f) {
            ToolHelper.repairTool(tool, 5, player);
        }
    }
}

/** 沉重（heavy）：+1 击退抗性（属性组件注入，简化：applyEffect 加击退抗性）。 */
class TraitHeavy extends Trait {
    TraitHeavy() {
        super("heavy", 0xffffff);
    }

    @Override
    public void applyEffect(ItemStack stack, com.lvdriver.tconstruct_nirvana.data.ModifierData data) {
        super.applyEffect(stack, data);
        // 简化：击退抗性经属性组件注入（保留已有条目，1:1 完整实现为 getAttributeModifiers 钩子）
        stack.update(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS,
                net.minecraft.world.item.component.ItemAttributeModifiers.EMPTY,
                mods -> mods.withModifierAdded(net.minecraft.world.entity.ai.attributes.Attributes.KNOCKBACK_RESISTANCE,
                        new net.minecraft.world.entity.ai.attributes.AttributeModifier(
                                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("tconstruct_nirvana", "heavy_knockback"),
                                1.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE),
                        net.minecraft.world.entity.EquipmentSlotGroup.MAINHAND));
    }
}

/** 僵硬（stiff）：格挡时伤害 -1（下限 1）。 */
class TraitStiff extends Trait {
    TraitStiff() {
        super("stiff", 0xffffff);
    }

    @Override
    public void onBlock(ItemStack tool, net.minecraft.world.entity.player.Player player,
                        net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        event.setAmount(Math.max(1f, event.getAmount() - 1f));
    }
}
