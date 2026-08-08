package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * 精准采集（1:1 移植自 Tinkers' Antique {@code ModSilktouch} 的简化版）。
 * 单次应用；方块掉落改为掉落方块本身（简化：BlockDropsEvent 替换掉落物）。
 * 完整实现（与 autosmelt 互斥、经验处理）随后续会话完善。
 */
public class ModSilktouch extends Modifier {

    public ModSilktouch() {
        super("silktouch");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xdddddd));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
        // 1:1 旧版 ModSilktouch.canApplyTogether：拒 squeaky、luck
        aspects.add(new ModifierAspect.ExclusiveAspect("squeaky", "luck"));
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockDropsEvent event) {
        if (com.lvdriver.tconstruct_nirvana.util.ToolHelper.hasModifier(tool, identifier)) {
            net.minecraft.world.item.ItemStack blockItem = new net.minecraft.world.item.ItemStack(event.getState().getBlock());
            event.getDrops().clear();
            event.getDrops().add(new net.minecraft.world.entity.item.ItemEntity(
                    event.getLevel(), event.getPos().getX() + 0.5, event.getPos().getY() + 0.5, event.getPos().getZ() + 0.5, blockItem));
            event.setDroppedExperience(0);
        }
    }
}
