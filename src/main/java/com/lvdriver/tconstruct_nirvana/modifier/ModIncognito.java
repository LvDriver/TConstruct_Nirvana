package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.item.ItemStack;

/**
 * 隐匿（1:1 移植自 Tinkers' Antique {@code ModIncognito} 的简化版）。
 * 单次应用；隐藏工具信息（tooltip 隐藏由显示逻辑读取，随后续会话完善）。
 */
public class ModIncognito extends Modifier {

    public ModIncognito() {
        super("incognito");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x575757));
        // 不消耗强化槽（1:1 旧版无 freeModifier）
    }
}
