package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.util.HarvestLevels;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 钻石（1:1 移植自 Tinkers' Antique {@code ModDiamond}）。
 * 单次应用：+500 耐久、采掘等级 +1（低于黑曜石时）、攻击 +1.0、速度 +0.5。
 */
public class ModDiamond extends Modifier {

    public ModDiamond() {
        super("diamond");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x8cf4e2));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        ToolData toolData = ToolHelper.getToolData(stack);
        toolData = toolData.withDurability(toolData.durability() + 500);
        if (toolData.harvestLevel() < HarvestLevels.OBSIDIAN) {
            toolData = toolData.withHarvestLevel(toolData.harvestLevel() + 1);
        }
        toolData = toolData.withAttack(toolData.attack() + 1f);
        toolData = toolData.withSpeed(toolData.speed() + 0.5f);
        ToolHelper.setToolData(stack, toolData);
    }
}
