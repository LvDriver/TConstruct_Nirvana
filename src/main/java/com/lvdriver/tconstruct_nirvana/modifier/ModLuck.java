package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 幸运（1:1 移植自 Tinkers' Antique {@code ModLuck}）。
 * 3 级（每级 1 青金石块）；挖掘时按等级概率翻倍掉落（简化版）。
 */
public class ModLuck extends Modifier {

    public ModLuck() {
        super("luck");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 3));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x2d51e2));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    /** 幸运等级（1:1 旧版 getLuckLevel）。 */
    public int getLuckLevel(ItemStack tool) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        return data != null ? data.level() : 0;
    }
}
