package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 斩首（1:1 移植自 Tinkers' Antique {@code ModBeheading}）。
 * 10 级；每级 10% 概率掉落头颅（简化版：LivingDropsEvent 中按概率
 * 额外掉落目标头颅；完整生物头掉落逻辑随后续会话完善）。
 */
public class ModBeheading extends Modifier {

    public ModBeheading() {
        super("beheading");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 10));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x10574b));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    /** 斩首概率（1:1 旧版 shouldDropHead：level/10）。 */
    public boolean shouldDropHead(ItemStack tool) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        return level > 0 && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextInt(10) < level;
    }

    /** 检查工具是否带斩首（供死亡事件使用）。 */
    public static boolean hasBeheading(ItemStack stack) {
        return ToolHelper.hasModifier(stack, "beheading");
    }
}
