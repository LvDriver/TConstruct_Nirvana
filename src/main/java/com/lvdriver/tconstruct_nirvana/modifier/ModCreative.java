package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 创造（1:1 移植自 Tinkers' Antique {@code ModCreative}）。
 * 无 aspect（updateNBT 手动 level++）；效果：免费强化槽 +level（返还槽位）。
 */
public class ModCreative extends Modifier {

    public ModCreative() {
        super("creative");
    }

    @Override
    public ModifierData updateNBT(ModifierData data) {
        return data.withLevel(data.level() + 1);
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
        ToolHelper.setToolData(stack, toolData.withModifiers(toolData.modifiers() + data.level()));
    }

    @Override
    public int onToolDamage(ItemStack tool, int damage, int newDamage, net.minecraft.world.entity.LivingEntity entity) {
        return newDamage - damage;
    }
}
