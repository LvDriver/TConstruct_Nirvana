package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.item.ItemStack;

/**
 * 灵魂绑定（1:1 移植自 Tinkers' Antique {@code ModSoulbound} 的简化版）。
 * 单次应用；死亡不掉落（完整实现依赖死亡事件保留物品，随后续会话完善）。
 */
public class ModSoulbound extends Modifier {

    public ModSoulbound() {
        super("soulbound");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xf5fbac));
        // 不消耗强化槽（1:1 旧版无 freeModifier）
    }
}
