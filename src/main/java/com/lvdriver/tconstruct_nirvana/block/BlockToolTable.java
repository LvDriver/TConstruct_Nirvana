package com.lvdriver.tconstruct_nirvana.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 工具站/锻造厂方块（1:1 移植自 Tinkers' Antique {@code BlockToolTable} 简化版）。
 *
 * <p>工具站（tool_station，木质）与锻造厂（tool_forge，金属质）共用本类，
 * 仅属性不同。右键打开组装 GUI（{@link com.lvdriver.tconstruct_nirvana.gui.TinkerStationMenu}）；
 * 5 个部件槽 + 结果槽，组装逻辑复用
 * {@link com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem#buildItemFromStacks}。</p>
 */
public class BlockToolTable extends BaseEntityBlock {

    public BlockToolTable(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BlockToolTable::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToolTableBlockEntity(pos, state);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            player.openMenu(state.getMenuProvider(level, pos));
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return (MenuProvider) level.getBlockEntity(pos);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
