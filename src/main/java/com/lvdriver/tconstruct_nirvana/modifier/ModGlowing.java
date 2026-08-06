package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;

/**
 * 发光（1:1 移植自 Tinkers' Antique {@code ModGlowing} 的简化版）。
 * 单次应用；手持且光照 <8 时在相邻位置放置发光方块（简化：萤石），
 * 非创造每次放置消耗 1 耐久。
 */
public class ModGlowing extends Modifier {

    public ModGlowing() {
        super("glowing");
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xffffaa));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    @Override
    public void onUpdate(ItemStack tool, Level world, Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide || !isSelected || !(entity instanceof Player player)
                || player.getAbilities().instabuild) {
            return;
        }
        // 每 20 tick 检查一次
        if (world.getGameTime() % 20 != 0) {
            return;
        }
        BlockPos pos = entity.blockPosition();
        if (world.getBrightness(LightLayer.BLOCK, pos) >= 8) {
            return;
        }
        // 相邻空气位放置萤石
        for (BlockPos neighbor : new BlockPos[]{pos.above(), pos.below(), pos.north(), pos.south(), pos.east(), pos.west()}) {
            if (world.getBlockState(neighbor).isAir()) {
                world.setBlockAndUpdate(neighbor, Blocks.GLOWSTONE.defaultBlockState());
                ToolHelper.damageTool(tool, 1, player);
                return;
            }
        }
    }
}
