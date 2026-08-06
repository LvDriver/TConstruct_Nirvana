package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.util.HarvestLevels;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 绿宝石（1:1 移植自 Tinkers' Antique {@code ModEmerald}）。
 * 单次应用：+基础耐久 50%、采掘等级 +1（低于钻石时）。
 */
public class ModEmerald extends Modifier {

    public ModEmerald() {
        super("emerald");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x41f384));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        ToolData toolData = ToolHelper.getToolData(stack);
        ToolData base = ToolHelper.getOriginalToolData(stack);
        toolData = toolData.withDurability(toolData.durability() + base.durability() / 2);
        if (toolData.harvestLevel() < HarvestLevels.DIAMOND) {
            toolData = toolData.withHarvestLevel(toolData.harvestLevel() + 1);
        }
        ToolHelper.setToolData(stack, toolData);
    }
}
