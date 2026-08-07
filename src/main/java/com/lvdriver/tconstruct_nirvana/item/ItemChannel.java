package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.block.TileChannel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;

/**
 * 沟槽方块物品（1:1 移植自 Tinkers' Antique {@code ItemChannel}）。
 *
 * <p>放置后按点击面自动建立连接（点击面反向 = 本沟槽与邻居的连接方向；
 * 潜行放置方向取反，1:1 旧版）。</p>
 */
public class ItemChannel extends BlockItem {

    public ItemChannel(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult result = super.place(context);
        // 放置成功后按点击面建立连接（1:1 旧版 ItemChannel.onItemUse）
        if (result.consumesAction()
                && context.getLevel().getBlockEntity(context.getClickedPos()) instanceof TileChannel channel) {
            channel.onPlaceBlock(context.getClickedFace(),
                    context.getPlayer() != null && context.getPlayer().isShiftKeyDown());
        }
        return result;
    }
}
