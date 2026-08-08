package com.lvdriver.tconstruct_nirvana.block.slime;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import java.util.function.Supplier;

/**
 * 史莱姆藤蔓（1:1 移植自 Tinkers' Antique {@code BlockSlimeVine}）。
 *
 * <p>与旧版一致按生长段注册 3 个方块（top/mid/end，蓝/紫各一套）：
 * 随机 tick 向下生长，同一列超过 2 个中间段后升级为下一段方块
 * （top→mid→end），end 不再生长。迁移差异：1.21.1 原版 VineBlock 的
 * 随机生长/存活逻辑与旧版 3 段体系不同，故覆写 randomTick 与 canSurvive
 * 还原旧版行为。</p>
 */
public class BlockSlimeVine extends VineBlock {

    private final Supplier<Block> nextStage;

    /**
     * @param nextStage 下一生长段方块（end 段传 null）
     */
    public BlockSlimeVine(BlockBehaviour.Properties properties, Supplier<Block> nextStage) {
        super(properties);
        this.nextStage = nextStage;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // 旧版 updateTick：25% 概率生长
        if (random.nextInt(4) == 0) {
            grow(level, random, pos, state);
        }
    }

    public void grow(net.minecraft.world.level.LevelAccessor level, RandomSource random, BlockPos pos, BlockState state) {
        // end 段不生长
        if (nextStage == null) {
            return;
        }
        // 只向下生长
        BlockPos below = pos.below();
        if (!level.isEmptyBlock(below)) {
            return;
        }
        // 自由悬挂（无侧面附着）时：同列计数超过 2 段或 50% 概率升级为下一段
        if (freeFloating(level, pos, state)) {
            int i = 0;
            while (level.getBlockState(pos.above(i)).getBlock() == this) {
                i++;
            }
            if (i > 2 || random.nextInt(2) == 0) {
                BlockState next = nextStage.get().defaultBlockState()
                        .setValue(NORTH, state.getValue(NORTH))
                        .setValue(EAST, state.getValue(EAST))
                        .setValue(SOUTH, state.getValue(SOUTH))
                        .setValue(WEST, state.getValue(WEST));
                state = next;
            }
        }
        level.setBlock(below, state, 2);
    }

    /** 旧版 canSurvive：4 面任一附着 或 上方是任意藤蔓（悬挂串）。 */
    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BooleanProperty prop = getPropertyForFace(side);
            if (state.getValue(prop) && isAcceptableNeighbour(level, pos.relative(side), side.getOpposite())) {
                return true;
            }
        }
        return level.getBlockState(pos.above()).getBlock() instanceof VineBlock;
    }

    /** 旧版 freeFloating：4 个侧面均无可用附着面（仅靠上方藤蔓悬挂）。 */
    private boolean freeFloating(net.minecraft.world.level.LevelAccessor level, BlockPos pos, BlockState state) {
        for (Direction side : Direction.Plane.HORIZONTAL) {
            BooleanProperty prop = getPropertyForFace(side);
            if (state.getValue(prop) && isAcceptableNeighbour(level, pos.relative(side), side.getOpposite())) {
                return false;
            }
        }
        return true;
    }
}
