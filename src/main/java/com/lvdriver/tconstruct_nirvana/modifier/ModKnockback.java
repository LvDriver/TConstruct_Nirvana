package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 击退（1:1 移植自 Tinkers' Antique {@code ModKnockback}）。
 * 99 级（每级 10 活塞）；每级 +0.1 击退。
 */
public class ModKnockback extends Modifier {

    public ModKnockback() {
        super("knockback");
        aspects.add(new ModifierAspect.MultiAspect(identifier, 0x9f9f9f, 99, 10, 1));
    }

    @Override
    public float knockBack(ItemStack tool, LivingEntity player, LivingEntity target, float damage,
                           float knockback, float newKnockback, boolean isCritical) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        return newKnockback + level * 0.1f;
    }
}
