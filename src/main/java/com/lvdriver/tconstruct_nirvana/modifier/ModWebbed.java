package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 蛛网（1:1 移植自 Tinkers' Antique {@code ModWebbed} 的简化版）。
 * 单次应用；命中后目标获得缓慢效果。
 */
public class ModWebbed extends Modifier {

    public ModWebbed() {
        super("webbed");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 3));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xffffff));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    @Override
    public void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical) {
        // 1:1 旧版：缓慢 II，时长 level × 60 tick
        ModifierData data = com.lvdriver.tconstruct_nirvana.util.ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, level * 60, 1));
    }
}
