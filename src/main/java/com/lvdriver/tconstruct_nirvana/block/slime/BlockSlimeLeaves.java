package com.lvdriver.tconstruct_nirvana.block.slime;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * 史莱姆树叶（1:1 移植自 Tinkers' Antique {@code BlockSlimeLeaves} 简化）。
 *
 * <p>迁移简化：1.21.1 的 {@code LeavesBlock} 依赖 distance/persistent 与 log 距离
 * 判定，而史莱姆树树干是凝结石块（非 log），原版逻辑会导致树叶自动凋零；
 * 故改为普通方块 + {@code foliage} 属性，无凋零衰减。掉落（树苗/史莱姆球）
 * 走 loot table（见 TConBlockLoot）。</p>
 */
public class BlockSlimeLeaves extends Block {

    public BlockSlimeLeaves(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SlimeTypes.FOLIAGE_TYPE, SlimeTypes.FoliageType.BLUE));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(BlockSlimeLeaves::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SlimeTypes.FOLIAGE_TYPE);
    }
}
