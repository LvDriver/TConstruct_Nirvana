package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 长方体多方块检测（1:1 移植自 Tinkers' Antique {@code MultiblockCuboid}，
 * 去 Mantle 依赖）。
 *
 * <p>检测流程：从控制器背面（炉内中心）向下找地板 → 水平扩展找 4 面墙 →
 * 逐层向上 → 天花板（可选）。结构可无天花板（冶炼炉），框架可选（冶炼炉无
 * 独立框架，墙/地板/天花板全部用同种方块）。</p>
 */
public abstract class MultiblockCuboid extends MultiblockDetection {

    /** 是否需要天花板。 */
    public final boolean hasCeiling;
    /** 是否需要地板。 */
    public final boolean hasFloor;
    /** 是否需要独立框架（角落优先校验）。 */
    public final boolean hasFrame;

    public MultiblockCuboid(boolean hasFloor, boolean hasFrame, boolean hasCeiling) {
        this.hasCeiling = hasCeiling;
        this.hasFloor = hasFloor;
        this.hasFrame = hasFrame;
    }

    /**
     * 检测长方体多方块结构。
     *
     * @param world  世界
     * @param center 主机（控制器）背面的炉内中心位置
     * @param limit  最大内部尺寸（每轴）
     * @return 结构信息；未找到返回 null
     */
    @Override
    public MultiblockStructure detectMultiblock(Level world, BlockPos center, int limit) {
        List<BlockPos> subBlocks = new ArrayList<>();

        // 向下移动到底部
        int masterY = center.getY();
        center = getOuterPos(world, center, Direction.DOWN, 64).above();

        // 无框架时：主机若低于最低内部位置则结构非法
        if (!hasFrame && masterY < center.getY()) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Smeltery] detect FAIL: master below lowest internal pos (masterY={}, floorInnerY={})", masterY, center.getY());
            return null;
        }

        // 到 4 面墙的距离（含墙体方块）
        int[] edges = new int[4];
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockPos pos = getOuterPos(world, center, direction, limit);
            edges[direction.get2DDataValue()] = (pos.getX() - center.getX()) + (pos.getZ() - center.getZ());
        }

        // 墙太远？
        int xd = (edges[Direction.SOUTH.get2DDataValue()] - edges[Direction.NORTH.get2DDataValue()]) - 1;
        int zd = (edges[Direction.EAST.get2DDataValue()] - edges[Direction.WEST.get2DDataValue()]) - 1;
        if (xd > limit || zd > limit) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Smeltery] detect FAIL: walls too far (xd={}, zd={}, limit={})", xd, zd, limit);
            return null;
        }

        // 地板
        if (hasFloor) {
            if (!detectFloor(world, center.below(), edges, subBlocks)) {
                com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                        "[Smeltery] detect FAIL: floor invalid at y={}", center.below().getY());
                return null;
            }
        }

        // 逐层向上（层内校验墙）
        int height = 0;
        for (; height + center.getY() < world.getHeight(); height++) {
            if (!detectLayer(world, center.above(height), height, edges, subBlocks)) {
                break;
            }
        }

        // 无墙？
        if (height < 1 + masterY - center.getY()) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Smeltery] detect FAIL: walls too short (height={}, needed={})", height, 1 + masterY - center.getY());
            return null;
        }

        // 天花板
        if (hasCeiling) {
            if (!detectCeiling(world, center.above(height), edges, subBlocks)) {
                return null;
            }
        }

        return new MultiblockStructure(xd, height, zd, subBlocks);
    }

    /* 各部位方块判定（默认全用 isValidBlock，子类可覆写限制） */

    public boolean isFloorBlock(Level world, BlockPos pos) {
        return isValidBlock(world, pos);
    }

    public boolean isCeilingBlock(Level world, BlockPos pos) {
        return isValidBlock(world, pos);
    }

    public boolean isFrameBlock(Level world, BlockPos pos, EnumFrameType type) {
        return isValidBlock(world, pos);
    }

    public boolean isWallBlock(Level world, BlockPos pos) {
        return isValidBlock(world, pos);
    }

    /* 检测实现 */

    protected boolean detectFloor(Level world, BlockPos center, int[] edges, List<BlockPos> subBlocks) {
        return detectPlaneXZ(world, center, edges, false, subBlocks);
    }

    private boolean detectCeiling(Level world, BlockPos center, int[] edges, List<BlockPos> subBlocks) {
        return detectPlaneXZ(world, center, edges, true, subBlocks);
    }

    protected boolean detectPlaneXZ(Level world, BlockPos center, int[] edges, boolean ceiling, List<BlockPos> subBlocks) {
        // edges 顺序: [south, west, north, east]（2D data value）
        BlockPos from = center.offset(edges[1], 0, edges[2]);
        BlockPos to = center.offset(edges[3], 0, edges[0]);

        if (!MultiblockDetection.isAreaLoaded(world, from, to)) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Smeltery] detect: plane not loaded at y={}", center.getY());
            return false;
        }

        List<BlockPos> candidates = new ArrayList<>();

        if (hasFrame) {
            // 校验框架（边线）
            List<BlockPos> frame = new ArrayList<>();
            for (int x = 0; x <= to.getX() - from.getX(); x++) {
                frame.add(from.offset(x, 0, 0));
                frame.add(to.offset(-x, 0, 0));
            }
            for (int z = 1; z < to.getZ() - from.getZ(); z++) {
                frame.add(from.offset(0, 0, z));
                frame.add(to.offset(0, 0, -z));
            }
            for (BlockPos pos : frame) {
                if (!isFrameBlock(world, pos, ceiling ? EnumFrameType.CEILING : EnumFrameType.FLOOR)) {
                    return false;
                }
                candidates.add(pos);
            }
        }

        // 校验平面内部
        from = from.offset(1, 0, 1);
        to = to.offset(-1, 0, -1);
        for (int z = from.getZ(); z <= to.getZ(); z++) {
            for (int x = from.getX(); x <= to.getX(); x++) {
                BlockPos pos = new BlockPos(x, from.getY(), z);
                if (ceiling) {
                    if (!isCeilingBlock(world, pos)) {
                        com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                                "[Smeltery] detect: ceiling block invalid at {}", pos);
                        return false;
                    }
                } else {
                    if (!isFloorBlock(world, pos)) {
                        com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                                "[Smeltery] detect: floor block invalid at {} ({})", pos, world.getBlockState(pos));
                        return false;
                    }
                }
                candidates.add(pos);
            }
        }

        subBlocks.addAll(candidates);
        return true;
    }

    protected boolean detectLayer(Level world, BlockPos center, int layer, int[] edges, List<BlockPos> subBlocks) {
        BlockPos from = center.offset(edges[1], 0, edges[2]);
        BlockPos to = center.offset(edges[3], 0, edges[0]);
        if (!MultiblockDetection.isAreaLoaded(world, from, to)) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Smeltery] detect: layer not loaded at y={}", center.getY());
            return false;
        }
        List<BlockPos> candidates = new ArrayList<>();

        if (hasFrame) {
            // 框架：4 角
            List<BlockPos> frame = new ArrayList<>();
            frame.add(from);
            frame.add(to);
            frame.add(new BlockPos(to.getX(), from.getY(), from.getZ()));
            frame.add(new BlockPos(from.getX(), from.getY(), to.getZ()));
            for (BlockPos pos : frame) {
                if (!isFrameBlock(world, pos, EnumFrameType.WALL)) {
                    return false;
                }
                candidates.add(pos);
            }
        }

        // 层内空气（内部）
        List<BlockPos> blocks = new ArrayList<>();
        for (int x = edges[1] + 1; x < edges[3]; x++) {
            for (int z = edges[2] + 1; z < edges[0]; z++) {
                blocks.add(center.offset(x, 0, z));
            }
        }
        for (BlockPos pos : blocks) {
            if (!isInnerBlock(world, pos)) {
                com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                        "[Smeltery] detect: layer y={} inner not air at {} ({})", center.getY(), pos, world.getBlockState(pos));
                return false;
            }
        }

        // 4 面墙
        blocks.clear();
        for (int x = edges[1] + 1; x < edges[3]; x++) {
            blocks.add(center.offset(x, 0, edges[2]));
            blocks.add(center.offset(x, 0, edges[0]));
        }
        for (int z = edges[2] + 1; z < edges[0]; z++) {
            blocks.add(center.offset(edges[1], 0, z));
            blocks.add(center.offset(edges[3], 0, z));
        }
        for (BlockPos pos : blocks) {
            if (!isWallBlock(world, pos)) {
                com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                        "[Smeltery] detect: layer y={} wall invalid at {} ({})", center.getY(), pos, world.getBlockState(pos));
                return false;
            }
            candidates.add(pos);
        }

        subBlocks.addAll(candidates);
        return true;
    }

    public enum EnumFrameType {
        FLOOR,
        CEILING,
        WALL
    }
}
