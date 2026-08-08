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
 * 史莱姆草皮（1:1 移植自 Tinkers' Antique {@code BlockSlimeGrass}）。
 *
 * <p>属性：{@code type}（下方泥土变体，grass 掉落对应 slime_dirt）+ {@code foliage}
 * （草色变体）。迁移简化：去掉旧版 DirtType.VANILLA（普通泥土上长史莱姆草）与
 * 随机蔓延逻辑（草皮主要来自岛生成，1:1 视觉不受影响）。</p>
 */
public class BlockSlimeGrass extends Block {

    public BlockSlimeGrass(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(SlimeTypes.DIRT_TYPE, SlimeTypes.DirtType.GREEN)
                .setValue(SlimeTypes.FOLIAGE_TYPE, SlimeTypes.FoliageType.BLUE));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(BlockSlimeGrass::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SlimeTypes.DIRT_TYPE, SlimeTypes.FOLIAGE_TYPE);
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction direction, BlockState plant) {
        // 草皮语义：可承载任意植物（1:1 旧版 slimePlantType/Plains 近似）
        return TriState.TRUE;
    }
}
