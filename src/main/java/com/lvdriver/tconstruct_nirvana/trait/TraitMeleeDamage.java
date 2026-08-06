package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 近战伤害类特质（1:1 移植自 Tinkers' Antique 各 Trait 的 damage 钩子）。
 * 文件内多个 package-private 类，由 {@code ModTraits} 统一实例化注册。
 */

/** 冷血（coldblooded）：目标满血时 +50% 伤害。 */
class TraitColdblooded extends Trait {
    TraitColdblooded() {
        super("coldblooded", 0xff0000);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        if (target.getHealth() >= target.getMaxHealth()) {
            return newDamage + damage / 2f;
        }
        return newDamage;
    }
}

/** 狂暴（raging）：血量越低伤害越高 +2.5×level×(1-血量比)。 */
class TraitRaging extends TraitLeveled {
    TraitRaging(int levels) {
        super("raging", levels, 0xc70000, 3);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        float hpRatio = 1f - player.getHealth() / player.getMaxHealth();
        return newDamage + 2.5f * getLevel() * hpRatio;
    }
}

/** 地狱（hellish）：目标不免疫火焰时 +4 伤害。 */
class TraitHellish extends Trait {
    TraitHellish() {
        super("hellish", 0xff0000);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        if (!target.fireImmune()) {
            return newDamage + 4;
        }
        return newDamage;
    }
}

/** 神圣（holy）：对亡灵 +5 伤害，命中亡灵附加虚弱 I 50 tick。 */
class TraitHoly extends Trait {
    TraitHoly() {
        super("holy", 0xffffff);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        if (target.getType().is(EntityTypeTags.UNDEAD)) {
            return newDamage + 5;
        }
        return newDamage;
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target.getType().is(EntityTypeTags.UNDEAD)) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.WEAKNESS, 50, 0));
        }
    }
}

/** 贪婪（insatiable）：每级 +1/3 伤害，上限 10 级；额外耐久消耗。 */
class TraitInsatiable extends Trait {
    TraitInsatiable() {
        super("insatiable", 0xaa00aa);
    }

    private int getLevel(ItemStack tool) {
        return tool.getOrDefault(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.<String, Integer>of())
                .getOrDefault(identifier, 0);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        return newDamage + getLevel(tool) / 3f;
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit) {
            int level = getLevel(tool);
            var levels = new java.util.LinkedHashMap<>(tool.getOrDefault(
                    com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.of()));
            levels.put(identifier, Math.min(10, level + 1));
            tool.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), levels);
        }
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, LivingEntity entity) {
        return newDamage + getLevel(tool) / 3;
    }
}

/** 过热（superheat）：目标燃烧时 +35% 伤害。 */
class TraitSuperheat extends Trait {
    TraitSuperheat() {
        super("superheat", 0xffffff);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        if (target.isOnFire()) {
            return newDamage + damage * 0.35f;
        }
        return newDamage;
    }
}

/** 裂片（splintering）：每级 +0.3×(等级+1)，上限 5 级。 */
class TraitSplintering extends Trait {
    TraitSplintering() {
        super("splintering", 0xffffff);
    }

    private int getLevel(ItemStack tool) {
        return tool.getOrDefault(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.<String, Integer>of())
                .getOrDefault(identifier, 0);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        return newDamage + 0.3f * (getLevel(tool) + 1);
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit) {
            int level = getLevel(tool);
            var levels = new java.util.LinkedHashMap<>(tool.getOrDefault(
                    com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.of()));
            levels.put(identifier, Math.min(5, level + 1));
            tool.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), levels);
        }
    }
}

/** 碎裂（fractured）：固定 +1.5 伤害（1:1 旧版 TraitBonusDamage("fractured", 1.5f)）。 */
class TraitFractured extends Trait {
    TraitFractured() {
        super("fractured", 0xffffff);
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        super.applyEffect(stack, data);
        com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
        ToolHelper.setToolData(stack, toolData.withAttack(toolData.attack() + 1.5f));
    }
}

/** 粗制（crude）：目标无护甲时 +5%×level 伤害。 */
class TraitCrude extends TraitLeveled {
    TraitCrude(int levels) {
        super("crude", levels, 0x424242, 3);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        if (target.getArmorValue() == 0) {
            return newDamage + damage * 0.05f * getLevel();
        }
        return newDamage;
    }
}
