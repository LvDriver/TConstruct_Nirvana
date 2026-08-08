package com.lvdriver.tconstruct_nirvana.block.slime;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 史莱姆高草（1:1 移植自 Tinkers' Antique {@code BlockTallSlimeGrass} 简化）。
 *
 * <p>只能在史莱姆泥土/草皮上生长；无掉落（旧版 getItemDropped=null）。
 * 迁移简化：去掉旧版 TYPE（tall_grass/fern 双形态）与剪切交互，仅保留
 * {@code foliage} 变体。</p>
 */
public class BlockTallSlimeGrass extends BushBlock {

    public static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

    public BlockTallSlimeGrass(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SlimeTypes.FOLIAGE_TYPE, SlimeTypes.FoliageType.BLUE));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return simpleCodec(BlockTallSlimeGrass::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SlimeTypes.FOLIAGE_TYPE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        // 旧版 canPlaceBlockAt：只能在 slimeGrass / slimeDirt 上
        Block block = state.getBlock();
        return block instanceof BlockSlimeGrass || block instanceof BlockSlimeDirt;
    }
}
