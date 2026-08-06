package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 迷幻（1:1 移植自 Tinkers' Antique {@code ModShulking} 的简化版）。
 * 单次应用；命中后目标获得漂浮效果。
 */
public class ModShulking extends Modifier {

    public ModShulking() {
        super("shulking");
        aspects.add(new ModifierAspect.MultiAspect(identifier, 0xaaccff, 1, 50, 1));
    }

    @Override
    public void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical) {
        // 1:1 旧版：悬浮 I，时长 current/2 + 10 tick
        ModifierData data = com.lvdriver.tconstruct_nirvana.util.ToolHelper.getModifierData(tool, identifier);
        int current = data != null ? data.level() * 50 : 0;
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.LEVITATION, current / 2 + 10, 0));
    }
}
