package com.lvdriver.tconstruct_nirvana.world.feature;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 岩浆史莱姆矿池（1:1 移植自 Tinkers' Antique {@code MagmaSlimePoolGenerator}）。
 *
 * <p>下界地下生成 16×8 椭球空腔：下部填岩浆、上部挖空，边缘缀 MAGMA/BLOOD
 * 凝结石块；生成 2~6 只岩浆史莱姆。默认关闭（旧版 genSlimePools=false）。</p>
 */
public class MagmaSlimePoolFeature extends Feature<EmptyFeatureConfig> {

    public MagmaSlimePoolFeature() {
        super(EmptyFeatureConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<EmptyFeatureConfig> context) {
        WorldGenLevel level = context.level();
        RandomSource rand = context.random();
        BlockPos origin = context.origin();

        MutableBlockPos pos = new MutableBlockPos(origin.getX() - 8, origin.getY(), origin.getZ() - 8);
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        while (y > 5 && level.isEmptyBlock(pos)) {
            y--;
            pos.setY(y);
        }
        if (y <= 4) {
            return false;
        }
        y -= 4;
        pos.setY(y);

        boolean[] shape = new boolean[2048];
        int shapeCount = rand.nextInt(4) + 4;
        List<BlockPos> fluidPositions = new ArrayList<>();

        for (int i = 0; i < shapeCount; ++i) {
            double xSize = rand.nextDouble() * 6.0D + 3.0D;
            double ySize = rand.nextDouble() * 4.0D + 2.0D;
            double zSize = rand.nextDouble() * 6.0D + 3.0D;
            double xCenter = rand.nextDouble() * (16.0D - xSize - 2.0D) + 1.0D + xSize / 2.0D;
            double yCenter = rand.nextDouble() * (8.0D - ySize - 4.0D) + 2.0D + ySize / 2.0D;
            double zCenter = rand.nextDouble() * (16.0D - zSize - 2.0D) + 1.0D + zSize / 2.0D;

            for (int xOffset = 1; xOffset < 15; ++xOffset) {
                for (int zOffset = 1; zOffset < 15; ++zOffset) {
                    for (int yOffset = 1; yOffset < 7; ++yOffset) {
                        double xDist = (xOffset - xCenter) / (xSize / 2.0D);
                        double yDist = (yOffset - yCenter) / (ySize / 2.0D);
                        double zDist = (zOffset - zCenter) / (zSize / 2.0D);
                        if (xDist * xDist + yDist * yDist + zDist * zDist < 1.0D) {
                            shape[(xOffset * 16 + zOffset) * 8 + yOffset] = true;
                        }
                    }
                }
            }
        }

        // 边缘检查：y>=4 处不能是液体；y<4 处必须实心（或岩浆）
        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    if (SlimePoolFeature.isEdge(shape, xOffset, zOffset, yOffset)) {
                        pos.set(x + xOffset, y + yOffset, z + zOffset);
                        BlockState state = level.getBlockState(pos);
                        if (yOffset >= 4 && !state.getFluidState().isEmpty()) {
                            return false;
                        }
                        if (yOffset < 4 && !state.isSolid() && state.getBlock() != Blocks.LAVA) {
                            return false;
                        }
                    }
                }
            }
        }

        // 填充：y<4 岩浆，y>=4 空气
        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    if (shape[(xOffset * 16 + zOffset) * 8 + yOffset]) {
                        pos.set(x + xOffset, y + yOffset, z + zOffset);
                        if (yOffset < 4) {
                            fluidPositions.add(pos.immutable());
                        }
                        level.setBlock(pos, yOffset >= 4 ? Blocks.AIR.defaultBlockState() : Blocks.LAVA.defaultBlockState(), 2);
                    }
                }
            }
        }

        // 边缘凝结石块（1:1 旧版：全部 MAGMA 凝结石块）
        BlockState magmaBlock = ModBlocks.SLIME_CONGEALED.get().defaultBlockState()
                .setValue(SlimeTypes.SLIME_TYPE, SlimeTypes.SlimeType.MAGMA);
        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    if (SlimePoolFeature.isEdge(shape, xOffset, zOffset, yOffset)
                            && (yOffset < 4 || rand.nextInt(2) != 0)) {
                        pos.set(x + xOffset, y + yOffset, z + zOffset);
                        if (level.getBlockState(pos).isSolid()) {
                            pos.setY(y + yOffset + 1);
                            if (level.getBlockState(pos).getBlock() != Blocks.LAVA) {
                                pos.setY(y + yOffset);
                                level.setBlock(pos, magmaBlock, 2);
                            }
                        }
                    }
                }
            }
        }

        // 生成岩浆史莱姆（旧版 2~6 只）
        if (!fluidPositions.isEmpty()) {
            int magmaCubeCount = rand.nextInt(5) + 2;
            Collections.shuffle(fluidPositions, new java.util.Random(rand.nextLong()));
            for (int i = 0; i < Math.min(magmaCubeCount, fluidPositions.size()); i++) {
                BlockPos spawnPos = fluidPositions.get(i);
                if (level.isEmptyBlock(spawnPos.above()) && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    MagmaCube magmaCube = new MagmaCube(net.minecraft.world.entity.EntityType.MAGMA_CUBE, serverLevel);
                    magmaCube.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                    serverLevel.addFreshEntity(magmaCube);
                }
            }
        }
        return true;
    }
}
