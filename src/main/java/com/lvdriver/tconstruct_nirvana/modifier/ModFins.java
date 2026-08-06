package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.item.ItemStack;

/**
 * 鱼鳍（1:1 移植自 Tinkers' Antique {@code ModFins} 的简化版）。
 * 单次应用；仅限弹射物（箭/弩矢）；水中减速抵抗（完整弹射物水中行为
 * 依赖自定义弹射物实体，随后续会话接线）。
 */
public class ModFins extends Modifier {

    public ModFins() {
        super("fins");
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xabcdef));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    @Override
    protected boolean canApplyCustom(ItemStack stack) {
        // 仅弹射物可用（1:1 旧版 projectileOnly）
        return stack.getItem() instanceof com.lvdriver.tconstruct_nirvana.item.tool.ranged.ProjectileToolItem;
    }
}
