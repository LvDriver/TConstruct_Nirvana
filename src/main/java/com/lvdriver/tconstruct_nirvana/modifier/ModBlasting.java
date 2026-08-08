package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * 爆破（1:1 移植自 Tinkers' Antique {@code ModBlasting} 的简化版）。
 * 3 级（首次免费）；挖掘速度按方块硬度折算、掉落概率降低。
 */
public class ModBlasting extends Modifier {

    public ModBlasting() {
        super("blasting");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 3));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xffaa23));
        aspects.add(new ModifierAspect.FreeFirstModifierAspect(identifier, 1));
        // 1:1 旧版 ModBlasting.canApplyTogether：拒 luck、silktouch、squeaky、trait autosmelt
        // （mod_autosmelt 为同效果的修饰符版，一并互斥，review 会话11 补充）
        aspects.add(new ModifierAspect.ExclusiveAspect("luck", "silktouch", "squeaky", "autosmelt", "mod_autosmelt"));
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        if (level <= 0) {
            return;
        }
        // 1:1 旧版：速度 × 硬度 / 等级除数（1级÷10、2级÷5、3级÷1.1），再按 level/3 与原速度加权
        float hardness = event.getState().getDestroySpeed(event.getEntity().level(), event.getEntity().blockPosition());
        if (hardness <= 0) {
            return;
        }
        float divisor = level == 1 ? 10f : level == 2 ? 5f : 1.1f;
        float newSpeed = event.getNewSpeed() * hardness / divisor;
        float weight = level / 3f;
        event.setNewSpeed(event.getNewSpeed() * (1f - weight) + newSpeed * weight);
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockDropsEvent event) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        if (level <= 0) {
            return;
        }
        // 掉落概率 ×(1 - level/3)（1:1 旧版）
        float chance = 1f - level / 3f;
        for (net.minecraft.world.entity.item.ItemEntity entity : java.util.List.copyOf(event.getDrops())) {
            if (com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() > chance) {
                event.getDrops().remove(entity);
            }
        }
    }
}
