package com.lvdriver.tconstruct_nirvana.world.feature;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 岩浆史莱姆岛（1:1 移植自 Tinkers' Antique {@code MagmaSlimeIslandGenerator}）。
 *
 * <p>下界岩浆海上（y=32，岩浆面 31 上 1 格）生成：要求 5 格（中心+四方向）
 * 均为岩浆才生成。岛体侵蚀镂空用岩浆填充（旧版 air=LAVA）；草皮为
 * ORANGE foliage + MAGMA 泥土；树为 MAGMA 凝结石块树干 + 橙色树叶（无藤蔓）；
 * 湖为岩浆 + MAGMA/BLOOD 凝结石块。频率 1/100 chunk（旧版 magmaIslandsRate）。</p>
 */
public class MagmaSlimeIslandFeature extends SlimeIslandFeature {

    public MagmaSlimeIslandFeature() {
        super();
    }

    @Override
    public boolean place(FeaturePlaceContext<EmptyFeatureConfig> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        int x = origin.getX();
        int z = origin.getZ();
        int y = 31; // 岩浆湖表面在 32（旧版注释：lava lake surface is at 32）

        BlockPos pos = new BlockPos(x, y, z);
        if (!isLava(level, pos) || !isLava(level, pos.north()) || !isLava(level, pos.east())
                || !isLava(level, pos.south()) || !isLava(level, pos.west())) {
            return false;
        }

        BlockState dirtState = ModBlocks.SLIME_DIRT.get().defaultBlockState()
                .setValue(SlimeTypes.DIRT_TYPE, SlimeTypes.DirtType.MAGMA);
        BlockState grassState = ModBlocks.SLIME_GRASS.get().defaultBlockState()
                .setValue(SlimeTypes.DIRT_TYPE, SlimeTypes.DirtType.MAGMA)
                .setValue(SlimeTypes.FOLIAGE_TYPE, SlimeTypes.FoliageType.ORANGE);

        generateIsland(level, random, x, z, y + 1, dirtState, grassState, null,
                SlimeTypes.SlimeType.MAGMA, SlimeTypes.FoliageType.ORANGE, SlimeTypes.FoliageType.ORANGE);
        return true;
    }

    @Override
    protected BlockState air() {
        return Blocks.LAVA.defaultBlockState();
    }

    @Override
    protected boolean treesHaveVines() {
        return false; // 旧版 treeGenMagma vine=null
    }

    private static boolean isLava(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == Blocks.LAVA;
    }
}
