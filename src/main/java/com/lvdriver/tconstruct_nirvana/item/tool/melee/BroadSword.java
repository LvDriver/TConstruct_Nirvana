package com.lvdriver.tconstruct_nirvana.item.tool.melee;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 宽剑（1:1 移植自 Tinkers' Antique {@code BroadSword}）。
 * 部件：工具杆 + 剑刃 + 宽护手；伤害倍率 1.0、攻速 1.6、攻击 +1、耐久 ×1.1、横扫攻击。
 */
public class BroadSword extends SwordToolItem {

    public static final float DURABILITY_MODIFIER = 1.1f;

    public BroadSword() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.SWORD_BLADE.get()),
                PartMaterialType.extra(ModToolParts.WIDE_GUARD.get()));
        addCategory(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 1.0f;
    }

    @Override
    public double attackSpeed() {
        return 1.6d;
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 横扫（1:1 旧版 dealDamage 中的横扫逻辑：非暴击、非疾跑、地面、冷却 >0.9、慢速移动）
        boolean hit = super.hurtEnemy(stack, target, attacker);
        if (hit && !ToolHelper.isBroken(stack) && attacker instanceof net.minecraft.world.entity.player.Player p) {
            double walked = attacker.walkDist - attacker.walkDistO;
            boolean flag = p.getAttackStrengthScale(0.5f) > 0.9f;
            boolean flag2 = attacker.fallDistance > 0.0F && !attacker.onGround() && !attacker.onClimbable()
                    && !attacker.isInWater() && !attacker.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS)
                    && !attacker.isPassenger();
            if (flag && !attacker.isSprinting() && !flag2 && attacker.onGround() && walked < (double) attacker.getSpeed()) {
                for (LivingEntity other : attacker.level().getEntitiesOfClass(LivingEntity.class,
                        target.getBoundingBox().inflate(1.0D, 0.25D, 1.0D))) {
                    if (other != attacker && other != target && !attacker.isAlliedTo(other)
                            && attacker.distanceToSqr(other) < 9.0D) {
                        other.knockback(0.4F, Math.sin(Math.toRadians(attacker.getYRot())),
                                -Math.cos(Math.toRadians(attacker.getYRot())));
                        super.dealDamage(stack, attacker, other, 1f);
                    }
                }
                attacker.level().playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(),
                        SoundEvents.PLAYER_ATTACK_SWEEP, attacker.getSoundSource(), 1.0F, 1.0F);
                attacker.level().broadcastEntityEvent(attacker, (byte) 31);
            }
        }
        return hit;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        ToolData data = buildDefaultTag(materials);
        // 攻击 +1（1:1 旧版，类原版剑基础伤害）
        data = data.withAttack(data.attack() + 1f);
        return data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
    }
}
