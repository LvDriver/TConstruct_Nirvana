package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

/**
 * seared 储罐方块（1:1 移植自 Tinkers' Antique {@code BlockTank} 简化版）。
 *
 * <p>冶炼炉结构内的液体储罐/燃料罐（BE = {@link TileTank}，4000mb）。
 * 旧版有 TANK/GAUGE/WINDOW 三种 meta，本版只做 TANK 一种（1:1 行为核心）。</p>
 */
public class BlockTank extends BaseEntityBlock {

    public BlockTank(Properties properties) {
        super(properties);
    }

    public static Block.Properties tankProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0F, 20.0F)
                .sound(SoundType.METAL)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BlockTank::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileTank(pos, state);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /** 液体亮度（1:1 旧版 getLightValue 由液体决定）。 */
    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TileTank tank && tank.containsFluid()) {
            return tank.getBrightness();
        }
        return 0;
    }

    /** 红石比较器（1:1 旧版 comparatorStrength）。 */
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, net.minecraft.world.level.Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof TileTank tank) {
            return tank.comparatorStrength();
        }
        return 0;
    }
}
