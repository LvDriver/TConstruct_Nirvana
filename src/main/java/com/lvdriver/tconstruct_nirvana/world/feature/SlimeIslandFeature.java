package com.lvdriver.tconstruct_nirvana.world.feature;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeDirt;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeGrass;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeVine;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.material.Fluid;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

/**
 * 史莱姆浮岛（1:1 移植自 Tinkers' Antique {@code SlimeIslandGenerator}）。
 *
 * <p>主世界天空生成：椭圆岛体 + 底部/顶部随机侵蚀 + 草皮 + 史莱姆流体湖 +
 * 高草 + 3 棵史莱姆树 + 藤蔓。岛类型随机：紫色 20%（rnr≤1）、绿色 40%
 * （rnr<6）、蓝色 40%。放置点 y = 地表高度 + 61~110（旧版
 * getHeight + 50 + rand(50) + 11），频率 1/730 chunk（旧版 slimeIslandsRate）。
 * 旧版"3×3 chunk 预标记 + per-world 已生成记录"（供怪物生成判定）无法在
 * 数据驱动 feature 中 1:1，已简化（怪物生成依赖留待实体会话）。</p>
 */
public class SlimeIslandFeature extends Feature<EmptyFeatureConfig> {

    /** 表面/底部侵蚀异常概率（旧版 RANDOMNESS=1 即 1/100）。 */
    private static final int RANDOMNESS = 1;

    public SlimeIslandFeature() {
        super(EmptyFeatureConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<EmptyFeatureConfig> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();

        // 1:1 generateIslandInChunk：x/z 由 placement 随机，y 重算为地表上方
        int x = origin.getX();
        int z = origin.getZ();
        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING, x, z) + 61 + random.nextInt(50);

        // 岛类型（rnr：0-1 紫、2-5 绿、6-9 蓝）
        SlimeTypes.FoliageType grass = SlimeTypes.FoliageType.BLUE;
        SlimeTypes.DirtType dirt = SlimeTypes.DirtType.BLUE;
        SlimeTypes.FoliageType treeFoliage = SlimeTypes.FoliageType.PURPLE; // 紫树在蓝/绿岛
        SlimeTypes.FoliageType plantFoliage = SlimeTypes.FoliageType.PURPLE;
        SlimeTypes.SlimeType lakeType = SlimeTypes.SlimeType.BLUE;
        boolean purpleVine = false;
        int rnr = random.nextInt(10);
        if (rnr <= 1) {
            grass = SlimeTypes.FoliageType.PURPLE;
            dirt = SlimeTypes.DirtType.PURPLE;
            treeFoliage = SlimeTypes.FoliageType.BLUE; // 蓝树在紫草
            plantFoliage = SlimeTypes.FoliageType.BLUE;
            lakeType = SlimeTypes.SlimeType.PURPLE;
            purpleVine = true;
        } else if (rnr < 6) {
            dirt = SlimeTypes.DirtType.GREEN;
            lakeType = SlimeTypes.SlimeType.GREEN;
        }

        BlockState dirtState = ModBlocks.SLIME_DIRT.get().defaultBlockState().setValue(SlimeTypes.DIRT_TYPE, dirt);
        BlockState grassState = ModBlocks.SLIME_GRASS.get().defaultBlockState()
                .setValue(SlimeTypes.DIRT_TYPE, dirt)
                .setValue(SlimeTypes.FOLIAGE_TYPE, grass);
        BlockState vineState = ModBlocks.SLIME_VINE_BLUE.get().defaultBlockState();
        if (purpleVine) {
            vineState = ModBlocks.SLIME_VINE_PURPLE.get().defaultBlockState();
        }

        generateIsland(level, random, x, z, y, dirtState, grassState, vineState,
                lakeType, treeFoliage, plantFoliage);
        return true;
    }

    /** 1:1 旧版 SlimeIslandGenerator.generateIsland（含湖/植物/树/藤蔓）。 */
    protected void generateIsland(WorldGenLevel level, RandomSource random, int xPos, int zPos, int ySurfacePos,
                                  BlockState dirt, BlockState grass, BlockState vine,
                                  SlimeTypes.SlimeType lakeType, SlimeTypes.FoliageType treeFoliage,
                                  SlimeTypes.FoliageType plantFoliage) {
        int xRange = 20 + random.nextInt(13);
        int zRange = 20 + random.nextInt(13);
        int yRange = 11 + random.nextInt(3);
        int height = yRange;

        int yBottom = ySurfacePos - yRange;
        BlockPos start = new BlockPos(xPos - xRange / 2, yBottom, zPos - zRange / 2);
        BlockPos center = new BlockPos(xPos, yBottom + height, zPos);

        Ellipse2D.Double ellipse = new Ellipse2D.Double(0, 0, xRange, zRange);

        // 基本椭圆柱体
        for (int x = 0; x <= xRange; x++) {
            for (int z = 0; z <= zRange; z++) {
                if (!ellipse.contains(x, z)) {
                    continue;
                }
                for (int y = 0; y <= yRange; y++) {
                    level.setBlock(start.offset(x, y, z), dirt, 2);
                }
            }
        }

        // 底部侵蚀（8 层，1:1 旧版对角线对称检查 + 1/100 随机）
        int erodeHeight = 8;
        for (int x = 0; x <= xRange; x++) {
            for (int z = 0; z <= zRange; z++) {
                for (int y = 0; y <= erodeHeight; y++) {
                    BlockPos pos1 = start.offset(x, erodeHeight - y, z);
                    BlockPos pos2 = start.offset(xRange - x, erodeHeight - y, zRange - z);
                    for (BlockPos pos : new BlockPos[]{pos1, pos2}) {
                        if (level.getBlockState(pos) == dirt) {
                            BlockState up1 = level.getBlockState(pos.offset(-1, 1, 0));
                            BlockState up2 = level.getBlockState(pos.offset(1, 1, 0));
                            BlockState up3 = level.getBlockState(pos.offset(0, 1, -1));
                            BlockState up4 = level.getBlockState(pos.offset(-1, 1, 1));
                            if (up1 != dirt || up2 != dirt || up3 != dirt || up4 != dirt || random.nextInt(100) <= RANDOMNESS) {
                                level.setBlock(pos, air(), 2);
                            }
                        }
                    }
                }
            }
        }

        // 顶部侵蚀（2 层）
        erodeHeight = 2;
        for (int x = 0; x <= xRange; x++) {
            for (int z = 0; z <= zRange; z++) {
                for (int y = 0; y <= erodeHeight; y++) {
                    BlockPos pos1 = start.offset(x, y + height - erodeHeight + 2, z);
                    BlockPos pos2 = start.offset(xRange - x, y + height - erodeHeight + 2, zRange - z);
                    for (BlockPos pos : new BlockPos[]{pos1, pos2}) {
                        BlockPos below = pos.below();
                        if (level.getBlockState(below.north()) != dirt
                                || level.getBlockState(below.east()) != dirt
                                || level.getBlockState(below.south()) != dirt
                                || level.getBlockState(below.west()) != dirt) {
                            level.setBlock(pos, air(), 2);
                        }
                    }
                }
            }
        }

        // 草皮：从顶向下找 dirt 且上方空气
        for (int x = 0; x <= xRange; x++) {
            for (int z = 0; z <= zRange; z++) {
                BlockPos top = start.offset(x, height, z);
                for (int y = 0; y <= height; y++) {
                    BlockPos pos = top.below(y);
                    if (level.getBlockState(pos) == dirt && level.isEmptyBlock(pos.above())) {
                        level.setBlock(pos, grass, 2);
                        break;
                    }
                }
            }
        }

        // 湖（液体 = 对应史莱姆流体块）
        generateLake(level, random, center, lakeType);

        // 植物（128 次尝试，1:1 generatePlants）
        BlockPos from = start.offset(0, height + 1, 0);
        BlockPos to = start.offset(xRange, height - 3, zRange);
        generatePlants(level, random, from, to, 128, plantFoliage);

        // 树 ×3（旧版 treeGen：蓝/绿岛长紫树挂紫藤蔓，紫岛长蓝树挂蓝藤蔓；岩浆岛无藤蔓）
        SlimeTreeConfig treeConfig = new SlimeTreeConfig(treeFoliage, treesHaveVines(),
                treeFoliage == SlimeTypes.FoliageType.PURPLE);
        for (int i = 0; i < 3; i++) {
            BlockPos pos = start.offset(random.nextInt(xRange), height, random.nextInt(zRange));
            SlimeTreeFeature.placeTree(level, random, pos, treeConfig);
        }

        // 藤蔓 ×30（旧版 tryPlacingVine + 向下生长；岩浆岛 vine=null 跳过）
        if (vine != null) {
            for (int i = 0; i < 30; i++) {
                BlockPos pos = start.offset(-1 + random.nextInt(xRange + 2), 0, -1 + random.nextInt(zRange + 2));
                tryPlacingVine(random, level, pos, height, vine);
            }
        }
    }

    /** 空气块（主世界岛；岩浆岛覆写为岩浆，1:1 旧版 air 字段）。 */
    protected BlockState air() {
        return Blocks.AIR.defaultBlockState();
    }

    /** 岛上的树是否挂藤蔓（岩浆岛无藤蔓，1:1 旧版 treeGenMagma vine=null）。 */
    protected boolean treesHaveVines() {
        return true;
    }

    /** 湖液体（主世界：对应史莱姆流体；岩浆岛：岩浆，1:1 旧版 lakeGen 配置）。 */
    protected BlockState lakeLiquid(SlimeTypes.SlimeType type) {
        if (type == SlimeTypes.SlimeType.MAGMA) {
            return Blocks.LAVA.defaultBlockState();
        }
        Fluid fluid = type == SlimeTypes.SlimeType.PURPLE
                ? ModFluids.PURPLESLIME.still().get()
                : ModFluids.BLUE_SLIME.still().get();
        return fluid.defaultFluidState().createLegacyBlock();
    }

    /** 1:1 旧版 SlimeLakeGenerator.generateLake（椭圆球体湖 + 凝结石块边缘）。 */
    private void generateLake(WorldGenLevel level, RandomSource random, BlockPos center, SlimeTypes.SlimeType type) {
        // 液体：对应史莱姆流体块（旧版 blueslime/purpleSlime 流体块；green 湖液体也用蓝色流体；岩浆岛用岩浆）
        BlockState liquid = lakeLiquid(type);
        BlockState lakeBottom = ModBlocks.SLIME_CONGEALED.get().defaultBlockState()
                .setValue(SlimeTypes.SLIME_TYPE, type);
        BlockState[] slimeBlocks = switch (type) {
            case PURPLE -> new BlockState[]{lakeBottom};
            case MAGMA -> new BlockState[]{lakeBottom,
                    ModBlocks.SLIME_CONGEALED.get().defaultBlockState().setValue(SlimeTypes.SLIME_TYPE, SlimeTypes.SlimeType.BLOOD)};
            default -> new BlockState[]{lakeBottom,
                    ModBlocks.SLIME_CONGEALED.get().defaultBlockState().setValue(SlimeTypes.SLIME_TYPE, SlimeTypes.SlimeType.BLUE)};
        };

        BlockPos pos = center;
        while (pos.getY() > 5 && level.isEmptyBlock(pos)) {
            pos = pos.below();
        }
        if (pos.getY() <= 4) {
            return;
        }
        pos = pos.offset(-8, -4, -8);

        boolean[] grid = new boolean[16 * 16 * 8];
        int spots = random.nextInt(4) + 4;
        for (int i = 0; i < spots; i++) {
            double xr = random.nextDouble() * 6 + 3;
            double yr = random.nextDouble() * 4 + 2;
            double zr = random.nextDouble() * 6 + 3;
            double xp = random.nextDouble() * (16 - xr - 2) + 1 + xr / 2;
            double yp = random.nextDouble() * (8 - yr - 4) + 2 + yr / 2;
            double zp = random.nextDouble() * (16 - zr - 2) + 1 + zr / 2;
            for (int xx = 1; xx < 15; xx++) {
                for (int zz = 1; zz < 15; zz++) {
                    for (int yy = 1; yy < 7; yy++) {
                        double xd = (xx - xp) / (xr / 2);
                        double yd = (yy - yp) / (yr / 2);
                        double zd = (zz - zp) / (zr / 2);
                        if (xd * xd + yd * yd + zd * zd < 1) {
                            grid[(xx * 16 + zz) * 8 + yy] = true;
                        }
                    }
                }
            }
        }

        // 边缘检查：上方不能是液体（防穿透地面）
        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                for (int yy = 0; yy < 8; yy++) {
                    if (isEdge(grid, xx, zz, yy)) {
                        BlockState state = level.getBlockState(pos.offset(xx, yy, zz));
                        if (yy >= 4 && !state.getFluidState().isEmpty()) {
                            return;
                        }
                    }
                }
            }
        }

        // 填充：yy>=4 空气，yy<4 液体（下方非空气防洞）
        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                for (int yy = 0; yy < 8; yy++) {
                    if (grid[(xx * 16 + zz) * 8 + yy]) {
                        BlockPos p = pos.offset(xx, yy, zz);
                        if (!level.isEmptyBlock(p.below())) {
                            level.setBlock(p, yy >= 4 ? Blocks.AIR.defaultBlockState() : liquid, 2);
                        }
                    }
                }
            }
        }

        // 边缘凝结石块（旧版：液体下方 10% 换湖底块；其他边缘随机 slimeBlocks）
        for (int xx = 0; xx < 16; xx++) {
            for (int zz = 0; zz < 16; zz++) {
                for (int yy = 0; yy < 8; yy++) {
                    if (isEdge(grid, xx, zz, yy)) {
                        BlockPos p = pos.offset(xx, yy, zz);
                        if ((yy < 4 || random.nextInt(2) != 0) && level.getBlockState(p).isSolid()) {
                            BlockState down = level.getBlockState(p.above());
                            if (!down.getFluidState().isEmpty()) {
                                if (random.nextInt(10) == 0) {
                                    level.setBlock(p, lakeBottom, 2);
                                }
                            } else {
                                level.setBlock(p, slimeBlocks[random.nextInt(slimeBlocks.length)], 2);
                            }
                        }
                    }
                }
            }
        }
    }

    private static boolean isEdge(boolean[] grid, int xx, int zz, int yy) {
        return !grid[(xx * 16 + zz) * 8 + yy] && (
                (xx < 15 && grid[((xx + 1) * 16 + zz) * 8 + yy])
                        || (xx > 0 && grid[((xx - 1) * 16 + zz) * 8 + yy])
                        || (zz < 15 && grid[(xx * 16 + zz + 1) * 8 + yy])
                        || (zz > 0 && grid[(xx * 16 + (zz - 1)) * 8 + yy])
                        || (yy < 7 && grid[(xx * 16 + zz) * 8 + yy + 1])
                        || (yy > 0 && grid[(xx * 16 + zz) * 8 + (yy - 1)]));
    }

    /** 1:1 旧版 SlimePlantGenerator.generatePlants（foliage 高草，向下找地）。 */
    private void generatePlants(WorldGenLevel level, RandomSource random, BlockPos from, BlockPos to,
                                int attempts, SlimeTypes.FoliageType foliage) {
        // 旧版语义：to.y = height-3 恒小于 from.y = height+1，yd 恒为负，
        // 下方 j 循环不执行，植物直接放在 from.y（岛表面上方 1 格），靠 canBlockStay 过滤。
        // 注意不能加 yd<=0 守卫（旧版无此检查，加了植物永不生成）。
        int xd = to.getX() - from.getX();
        int yd = to.getY() - from.getY();
        int zd = to.getZ() - from.getZ();
        BlockState state = ModBlocks.SLIME_GRASS_TALL.get().defaultBlockState()
                .setValue(SlimeTypes.FOLIAGE_TYPE, foliage);

        for (int i = 0; i < attempts; i++) {
            BlockPos pos = from.offset(random.nextInt(xd), 0, random.nextInt(zd));
            for (int j = 0; j < yd && level.isEmptyBlock(pos.below()); j++) {
                pos = pos.below();
            }
            if (level.isEmptyBlock(pos)) {
                BlockState soil = level.getBlockState(pos.below());
                if (soil.getBlock() instanceof BlockSlimeGrass || soil.getBlock() instanceof BlockSlimeDirt) {
                    level.setBlock(pos, state, 2);
                }
            }
        }
    }

    /** 1:1 旧版 tryPlacingVine：向上找可附着处放藤蔓并向下生长。 */
    protected void tryPlacingVine(RandomSource random, WorldGenLevel level, BlockPos below, int limit, BlockState vine) {
        BlockPos pos = below;
        BlockPos candidate = null;
        for (int i = 0; i < limit; i++) {
            if (canPlaceVineOnSide(level, pos, Direction.NORTH)
                    || canPlaceVineOnSide(level, pos, Direction.EAST)
                    || canPlaceVineOnSide(level, pos, Direction.SOUTH)
                    || canPlaceVineOnSide(level, pos, Direction.WEST)) {
                if (candidate == null || random.nextInt(10) == 0) {
                    candidate = pos;
                }
            }
            pos = pos.above();
        }

        if (candidate != null) {
            // 放置 top 段（自动计算侧面附着）
            BlockState placed = vine;
            for (Direction side : Direction.Plane.HORIZONTAL) {
                placed = placed.setValue(net.minecraft.world.level.block.VineBlock.getPropertyForFace(side),
                        isAcceptableNeighbor(level, candidate.relative(side), side.getOpposite()));
            }
            level.setBlock(candidate, placed, 2);

            // 向下生长（旧版 grow 循环）
            pos = candidate;
            for (int size = random.nextInt(8); size >= 0; size--) {
                if (!(level.getBlockState(pos).getBlock() instanceof BlockSlimeVine)) {
                    break;
                }
                ((BlockSlimeVine) level.getBlockState(pos).getBlock()).grow(level, random, pos, level.getBlockState(pos));
                pos = pos.below();
            }
        }
    }

    private boolean canPlaceVineOnSide(WorldGenLevel level, BlockPos pos, Direction side) {
        return isAcceptableNeighbor(level, pos.relative(side.getOpposite()), side)
                || level.getBlockState(pos.above()).getBlock() instanceof BlockSlimeVine;
    }

    private static boolean isAcceptableNeighbor(WorldGenLevel level, BlockPos pos, Direction side) {
        return level.getBlockState(pos).isFaceSturdy(level, pos, side);
    }
}
