package com.lvdriver.tconstruct_nirvana.block.slime;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.neoforged.neoforge.common.util.TriState;

/**
 * 史莱姆泥土（1:1 移植自 Tinkers' Antique {@code BlockSlimeDirt}）。
 *
 * <p>硬度 0.55、slime 音效；可承载任意植物（旧版：史莱姆植物与普通平原植物）。
 * 变体属性 {@code type}：green/blue/purple/magma。</p>
 */
public class BlockSlimeDirt extends Block {

    public BlockSlimeDirt(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SlimeTypes.DIRT_TYPE, SlimeTypes.DirtType.GREEN));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(BlockSlimeDirt::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SlimeTypes.DIRT_TYPE);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction direction, BlockState plant) {
        // 1:1 旧版：可承载史莱姆植物与平原植物（泥土语义，放行为 true）
        return TriState.TRUE;
    }
}
