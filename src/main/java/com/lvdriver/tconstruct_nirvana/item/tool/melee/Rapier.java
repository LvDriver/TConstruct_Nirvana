package com.lvdriver.tconstruct_nirvana.item.tool.melee;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 细剑（1:1 移植自 Tinkers' Antique {@code Rapier}）。
 * 部件：工具杆 + 剑刃 + 十字护手；伤害倍率 0.55、攻速 3.0、伤害削减阈值 13、
 * 击退 0.6、耐久 ×0.8；混合伤害（一半无视护甲）。
 */
public class Rapier extends SwordToolItem {

    public static final float DURABILITY_MODIFIER = 0.8f;

    public Rapier() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.SWORD_BLADE.get()),
                PartMaterialType.extra(ModToolParts.CROSS_GUARD.get()));
        addCategory(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 0.55f;
    }

    @Override
    public float damageCutoff() {
        return 13f;
    }

    @Override
    public double attackSpeed() {
        return 3;
    }

    @Override
    public float knockback() {
        return 0.6f;
    }

    /**
     * 混合伤害：一半普通伤害、一半无视护甲（1:1 旧版 dealHybridDamage，
     * 1.21.1 第二段用 magic 伤害类型（无视护甲））。
     */
    public static boolean dealHybridDamage(DamageSource source, Entity target, float damage) {
        if (target instanceof LivingEntity) {
            damage /= 2f;
        }
        boolean hit = target.hurt(source, damage);
        if (hit && target instanceof LivingEntity living) {
            living.hurt(living.damageSources().magic(), damage);
        }
        return hit;
    }

    @Override
    public float modifyDamage(ItemStack stack, LivingEntity attacker, Entity target, float damage) {
        // 混合伤害：一半普通、一半无视护甲（1:1 旧版 dealHybridDamage，第二段用 magic 伤害类型）
        if (target instanceof LivingEntity living) {
            float half = damage / 2f;
            living.hurt(living.damageSources().magic(), half);
            return half;
        }
        return damage;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        ToolData data = buildDefaultTag(materials);
        return data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
    }
}
