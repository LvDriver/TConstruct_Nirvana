package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 凋零之骨（1:1 移植自 Tinkers' Antique {@code ModNecrotic}）。
 * 5 级；命中后按造成伤害的 5%×level 吸血。
 */
public class ModNecrotic extends Modifier {

    public ModNecrotic() {
        super("necrotic");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 5));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x5e0000));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt,
                         boolean wasCritical, boolean wasHit) {
        if (wasHit) {
            ModifierData data = ToolHelper.getModifierData(tool, identifier);
            int level = data != null ? data.level() : 0;
            float amount = damageDealt * (0.05F * level);
            if (amount > 0) {
                player.heal(amount);
            }
        }
    }
}
