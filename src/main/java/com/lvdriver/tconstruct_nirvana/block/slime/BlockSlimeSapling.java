package com.lvdriver.tconstruct_nirvana.block.slime;

import com.lvdriver.tconstruct_nirvana.world.ModConfiguredFeatures;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 史莱姆树苗（1:1 移植自 Tinkers' Antique {@code BlockSlimeSapling}）。
 *
 * <p>只能在史莱姆泥土/草皮上种植；骨粉催熟按 {@code foliage} 变体生成对应
 * 史莱姆树（蓝色/紫色/橙色树叶，旧版 SlimeTreeGenerator 5+4 随机高度，
 * 树干为凝结石块）。</p>
 */
public class BlockSlimeSapling extends BushBlock implements BonemealableBlock {

    public static final VoxelShape SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

    public BlockSlimeSapling(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SlimeTypes.FOLIAGE_TYPE, SlimeTypes.FoliageType.BLUE));
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return simpleCodec(BlockSlimeSapling::new);
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
        Block block = state.getBlock();
        return block instanceof BlockSlimeGrass || block instanceof BlockSlimeDirt;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        // 1:1 旧版 generateTree：按 foliage 选树配置 → 清掉树苗 → 生成树 → 失败恢复
        ResourceKey<ConfiguredFeature<?, ?>> key = switch (state.getValue(SlimeTypes.FOLIAGE_TYPE)) {
            case BLUE -> ModConfiguredFeatures.SLIME_TREE_BLUE;
            case PURPLE -> ModConfiguredFeatures.SLIME_TREE_PURPLE;
            case ORANGE -> ModConfiguredFeatures.SLIME_TREE_ORANGE;
        };
        Holder<ConfiguredFeature<?, ?>> holder = level.registryAccess()
                .registryOrThrow(Registries.CONFIGURED_FEATURE).getHolderOrThrow(key);

        level.setBlock(pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 4);
        if (!holder.value().place(level, level.getChunkSource().getGenerator(), random, pos)) {
            level.setBlock(pos, state, 4);
        }
    }
}
