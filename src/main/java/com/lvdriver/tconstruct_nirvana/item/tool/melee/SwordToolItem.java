package com.lvdriver.tconstruct_nirvana.item.tool.melee;

import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 剑类工具基类（1:1 移植自 Tinkers' Antique {@code SwordCore}）。
 * 天然有效于蜘蛛网/藤蔓/珊瑚/南瓜/树叶；挖掘速度系数 0.5。
 */
public abstract class SwordToolItem extends TinkerToolItem {

    protected SwordToolItem(Item.Properties properties, PartMaterialType... requiredComponents) {
        super(properties, requiredComponents);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.SWORD_EFFICIENT);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.5f;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        // 蜘蛛网 7.5 倍速（1:1 旧版 SwordCore.getStrVsBlock）
        if (state.is(net.minecraft.world.level.block.Blocks.COBWEB)) {
            return super.getDestroySpeed(stack, state) * 7.5f;
        }
        return super.getDestroySpeed(stack, state);
    }
}
