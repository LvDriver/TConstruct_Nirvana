package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 烈焰（1:1 移植自 Tinkers' Antique {@code ModFiery}）。
 * 5 级（每级 25 烈焰粉）；命中着火 1+current/8 秒、额外火焰伤害 current/15。
 */
public class ModFiery extends Modifier {

    public ModFiery() {
        super("fiery");
        aspects.add(new ModifierAspect.MultiAspect(identifier, 0xea9e32, 5, 25, 1));
    }

    @Override
    public void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int current = data != null ? data.level() * 25 : 0;

        int duration = 1 + current / 8;
        target.setRemainingFireTicks(duration * 20);

        float fireDamage = current / 15f;
        if (fireDamage > 0) {
            target.hurt(target.damageSources().source(DamageTypes.ON_FIRE), fireDamage);
        }
    }
}
