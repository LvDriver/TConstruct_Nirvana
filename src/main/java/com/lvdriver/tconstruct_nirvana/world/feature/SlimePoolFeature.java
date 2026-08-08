package com.lvdriver.tconstruct_nirvana.world.feature;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 史莱姆矿池（1:1 移植自 Tinkers' Antique {@code SlimePoolGenerator}）。
 *
 * <p>地下生成 16×8 区域的椭球空腔：下部填绿色史莱姆流体、上部挖空，
 * 边缘缀绿色凝结石块；生成 2~6 只史莱姆。1:1 旧版默认关闭
 * （genSlimePools=false，由 TConConfig 开关 + datapack condition 控制）。
 * 迁移简化：旧版"温湿生物群系额外 1/4 概率池"省略（主世界统一 1/30）。</p>
 */
public class SlimePoolFeature extends Feature<EmptyFeatureConfig> {

    public SlimePoolFeature() {
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

        // 向下找第一个非空气块（旧版 while y>5 && isAir）
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

        // 边缘检查：y>=4 处不能是液体；y<4 处必须实心（或绿色史莱姆流体）
        BlockState greenFluid = ModFluids.GREEN_SLIME.still().get().defaultFluidState().createLegacyBlock();
        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    if (isEdge(shape, xOffset, zOffset, yOffset)) {
                        pos.set(x + xOffset, y + yOffset, z + zOffset);
                        BlockState state = level.getBlockState(pos);
                        if (yOffset >= 4 && !state.getFluidState().isEmpty()) {
                            return false;
                        }
                        if (yOffset < 4 && !state.isSolid() && state.getBlock() != greenFluid.getBlock()) {
                            return false;
                        }
                    }
                }
            }
        }

        // 填充：y<4 绿色史莱姆流体，y>=4 空气
        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 0; yOffset < 8; ++yOffset) {
                    if (shape[(xOffset * 16 + zOffset) * 8 + yOffset]) {
                        pos.set(x + xOffset, y + yOffset, z + zOffset);
                        if (yOffset < 4) {
                            fluidPositions.add(pos.immutable());
                        }
                        level.setBlock(pos, yOffset >= 4 ? Blocks.AIR.defaultBlockState() : greenFluid, 2);
                    }
                }
            }
        }

        // 边缘：液体附近换草皮/菌丝，其余缀绿色凝结石块
        for (int xOffset = 0; xOffset < 16; ++xOffset) {
            for (int zOffset = 0; zOffset < 16; ++zOffset) {
                for (int yOffset = 4; yOffset < 8; ++yOffset) {
                    if (shape[(xOffset * 16 + zOffset) * 8 + yOffset]) {
                        pos.set(x + xOffset, y + yOffset - 1, z + zOffset);
                        if (level.getBlockState(pos).getBlock() == Blocks.DIRT) {
                            pos.setY(y + yOffset);
                            if (level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) > pos.getY()) {
                                pos.setY(0);
                                // 简化：无生物群系 topBlock 判定，统一用草方块
                                pos.setY(y + yOffset - 1);
                                level.setBlock(pos, Blocks.GRASS_BLOCK.defaultBlockState(), 2);
                            }
                        }
                    }
                }
            }
        }

        // 边缘凝结石块（旧版 fluid==WATER 时才做；绿色史莱姆流体非水，此分支旧版不执行——保留 1:1）
        // （绿色史莱姆流体 material 非 WATER，旧版该 if 恒假，故不生成边缘 congealed）

        // 生成史莱姆（旧版 2~6 只）
        if (!fluidPositions.isEmpty()) {
            int slimeCount = rand.nextInt(5) + 2;
            Collections.shuffle(fluidPositions, new java.util.Random(rand.nextLong()));
            for (int i = 0; i < Math.min(slimeCount, fluidPositions.size()); i++) {
                BlockPos spawnPos = fluidPositions.get(i);
                if (level.isEmptyBlock(spawnPos.above()) && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                    Slime slime = new Slime(net.minecraft.world.entity.EntityType.SLIME, serverLevel);
                    slime.setPos(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
                    serverLevel.addFreshEntity(slime);
                }
            }
        }
        return true;
    }

    /** 边缘判定（包可见，MagmaSlimePoolFeature 复用）。 */
    static boolean isEdge(boolean[] shape, int xOffset, int zOffset, int yOffset) {
        return !shape[(xOffset * 16 + zOffset) * 8 + yOffset]
                && (xOffset < 15 && shape[((xOffset + 1) * 16 + zOffset) * 8 + yOffset]
                || xOffset > 0 && shape[((xOffset - 1) * 16 + zOffset) * 8 + yOffset]
                || zOffset < 15 && shape[(xOffset * 16 + zOffset + 1) * 8 + yOffset]
                || zOffset > 0 && shape[(xOffset * 16 + (zOffset - 1)) * 8 + yOffset]
                || yOffset < 7 && shape[(xOffset * 16 + zOffset) * 8 + yOffset + 1]
                || yOffset > 0 && shape[(xOffset * 16 + zOffset) * 8 + (yOffset - 1)]);
    }
}
