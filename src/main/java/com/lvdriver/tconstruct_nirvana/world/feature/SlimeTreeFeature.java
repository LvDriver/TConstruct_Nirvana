package com.lvdriver.tconstruct_nirvana.world.feature;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * 史莱姆树（1:1 移植自 Tinkers' Antique {@code SlimeTreeGenerator}）。
 *
 * <p>高度 5+随机 4；树干为对应变体凝结石块（非 log）；树冠 4 层菱形 +
 * 四角削除 + drippers；可选藤蔓。生成前向下寻找史莱姆泥土/草皮地面。</p>
 */
public class SlimeTreeFeature extends Feature<SlimeTreeConfig> {

    public SlimeTreeFeature() {
        super(SlimeTreeConfig.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<SlimeTreeConfig> context) {
        return placeTree(context.level(), context.random(), context.origin(), context.config());
    }

    /** 生成一棵史莱姆树（岛生成/树苗催熟共用，1:1 旧版 SlimeTreeGenerator.generateTree）。 */
    public static boolean placeTree(WorldGenLevel level, RandomSource random, BlockPos origin, SlimeTreeConfig config) {
        int height = random.nextInt(4) + 5; // 旧版 nextInt(treeHeightRange) + minTreeHeight = 5~8
        BlockPos pos = findGround(level, origin);
        if (pos.getY() < level.getMinBuildHeight()) {
            return false;
        }

        BlockState soil = level.getBlockState(pos.below());
        boolean isSoil = soil.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeDirt
                || soil.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeGrass;
        if (!isSoil) {
            return false;
        }

        // 树干：凝结石块（旧版 slimeGreen→SlimeType.GREEN；ORANGE→MAGMA）
        SlimeTypes.SlimeType logType = config.foliage() == SlimeTypes.FoliageType.ORANGE
                ? SlimeTypes.SlimeType.MAGMA : SlimeTypes.SlimeType.GREEN;
        BlockState log = ModBlocks.SLIME_CONGEALED.get().defaultBlockState().setValue(SlimeTypes.SLIME_TYPE, logType);
        BlockState leaves = ModBlocks.SLIME_LEAVES.get().defaultBlockState()
                .setValue(SlimeTypes.FOLIAGE_TYPE, config.foliage());
        BlockState vine = config.vines() ? (config.purpleVines()
                ? ModBlocks.SLIME_VINE_PURPLE_MID.get().defaultBlockState()
                : ModBlocks.SLIME_VINE_BLUE_MID.get().defaultBlockState()) : null;

        placeTrunk(level, pos, height, log);
        placeCanopy(level, random, pos, height, leaves, vine);
        return true;
    }

    /** 旧版 findGround：从 pos 向下找史莱姆泥土/草皮，返回其上方一格。 */
    private static BlockPos findGround(WorldGenLevel level, BlockPos pos) {
        BlockPos p = pos;
        while (p.getY() > level.getMinBuildHeight()) {
            BlockState state = level.getBlockState(p);
            BlockState up = level.getBlockState(p.above());
            if ((state.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeDirt
                    || state.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeGrass)
                    && up.isAir()) {
                return p.above();
            }
            p = p.below();
        }
        return p;
    }

    /** 旧版 placeTrunk：仅在空气/可替换/树叶处放置树干。 */
    private static void placeTrunk(WorldGenLevel level, BlockPos pos, int height, BlockState log) {
        BlockPos p = pos;
        while (height > 0) {
            BlockState state = level.getBlockState(p);
            if (state.isAir() || state.canBeReplaced() || state.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeLeaves) {
                level.setBlock(p, log, 2);
            }
            p = p.above();
            height--;
        }
    }

    /** 旧版 placeCanopy：4 层菱形树冠 + 削角 + drippers + 藤蔓。 */
    private static void placeCanopy(WorldGenLevel level, RandomSource random, BlockPos pos, int height,
                             BlockState leaves, BlockState vine) {
        BlockPos p = pos.above(height);
        for (int i = 0; i < 4; i++) {
            placeDiamondLayer(level, p.below(i), i + 1, leaves);
        }

        BlockState air = Blocks.AIR.defaultBlockState();
        p = p.below(3);
        setBlockAndMetadata(level, p.offset(4, 0, 0), air);
        setBlockAndMetadata(level, p.offset(-4, 0, 0), air);
        setBlockAndMetadata(level, p.offset(0, 0, 4), air);
        setBlockAndMetadata(level, p.offset(0, 0, -4), air);
        if (vine != null) {
            setBlockAndMetadata(level, p.offset(1, 0, 1), air);
            setBlockAndMetadata(level, p.offset(1, 0, -1), air);
            setBlockAndMetadata(level, p.offset(-1, 0, 1), air);
            setBlockAndMetadata(level, p.offset(-1, 0, -1), air);
        }

        // drippers（旧版注释：叶子衰减距离限制只能下探一格）
        p = p.below();
        setBlockAndMetadata(level, p.offset(3, 0, 0), leaves);
        setBlockAndMetadata(level, p.offset(-3, 0, 0), leaves);
        setBlockAndMetadata(level, p.offset(0, 0, -3), leaves);
        setBlockAndMetadata(level, p.offset(0, 0, 3), leaves);
        if (vine == null) {
            setBlockAndMetadata(level, p.offset(1, 0, 1), leaves);
            setBlockAndMetadata(level, p.offset(1, 0, -1), leaves);
            setBlockAndMetadata(level, p.offset(-1, 0, 1), leaves);
            setBlockAndMetadata(level, p.offset(-1, 0, -1), leaves);
        }

        // 藤蔓（随机 1~3 个方向）
        if (vine != null) {
            p = p.below();
            setBlockAndMetadata(level, p.offset(3, 0, 0), randomizedVine(random, vine));
            setBlockAndMetadata(level, p.offset(-3, 0, 0), randomizedVine(random, vine));
            setBlockAndMetadata(level, p.offset(0, 0, -3), randomizedVine(random, vine));
            setBlockAndMetadata(level, p.offset(0, 0, 3), randomizedVine(random, vine));
            for (int dx : new int[]{2, -2}) {
                for (int dz : new int[]{2, -2}) {
                    BlockState randomVine = randomizedVine(random, vine);
                    setBlockAndMetadata(level, p.offset(dx, 1, dz), randomVine);
                    setBlockAndMetadata(level, p.offset(dx, 0, dz), randomVine);
                }
            }
        }
    }

    private static BlockState randomizedVine(RandomSource random, BlockState vine) {
        // 旧版 getRandomizedVine：清 4 方向后随机开 1~3 个
        BlockState state = vine;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            state = state.setValue(net.minecraft.world.level.block.VineBlock.getPropertyForFace(side), false);
        }
        Direction[] sides = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
        for (int i = random.nextInt(3) + 1; i > 0; i--) {
            state = state.setValue(net.minecraft.world.level.block.VineBlock.getPropertyForFace(sides[random.nextInt(sides.length)]), true);
        }
        return state;
    }

    private static void placeDiamondLayer(WorldGenLevel level, BlockPos pos, int range, BlockState leaves) {
        for (int x = -range; x <= range; x++) {
            for (int z = -range; z <= range; z++) {
                if (Math.abs(x) + Math.abs(z) <= range) {
                    setBlockAndMetadata(level, pos.offset(x, 0, z), leaves);
                }
            }
        }
    }

    private static void setBlockAndMetadata(WorldGenLevel level, BlockPos pos, BlockState stateNew) {
        BlockState state = level.getBlockState(pos);
        if (state.isAir() || state.canBeReplaced()
                || state.getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeLeaves) {
            level.setBlock(pos, stateNew, 2);
        }
    }
}
