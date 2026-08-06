package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 加固（1:1 移植自 Tinkers' Antique {@code ModReinforced}）。
 * 5 级；每级 20% 概率不消耗耐久；概率 ≥100% 时工具永不损坏。
 */
public class ModReinforced extends Modifier {

    private static final float CHANCE_PER_LEVEL = 0.20f;

    public ModReinforced() {
        super("reinforced");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 5));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x502e83));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    private int getLevel(ItemStack stack) {
        ModifierData data = ToolHelper.getModifierData(stack, identifier);
        return data != null ? data.level() : 0;
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, LivingEntity entity) {
        float chance = getLevel(tool) * CHANCE_PER_LEVEL;
        if (chance >= 1f || chance >= com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat()) {
            newDamage -= damage;
        }
        return Math.max(0, newDamage);
    }
}
