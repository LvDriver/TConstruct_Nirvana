package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.List;

/**
 * 长方体多方块结构检测基类（1:1 移植自 Tinkers' Antique
 * {@code MultiblockDetection}，去 Mantle 依赖）。
 *
 * <p>以控制器背面（炉内中心）为起点，检测墙/地板/天花板，返回
 * {@link MultiblockStructure}（内部尺寸 + 全部成员方块位置）。</p>
 */
public abstract class MultiblockDetection {

    /** 多方块结构信息。 */
    public static class MultiblockStructure {

        /** x-width（内部宽度，不含墙）。 */
        public final int xd;
        /** y-height（内部高度，不含地板/天花板）。 */
        public final int yd;
        /** z-width（内部深度，不含墙）。 */
        public final int zd;

        /** 全部成员方块位置（含墙体/地板/天花板/控制器）。 */
        public final List<BlockPos> blocks;
        /** 结构最小坐标（含墙体）。 */
        public final BlockPos minPos;
        /** 结构最大坐标（含墙体）。 */
        public final BlockPos maxPos;

        private final AABB bb;

        public MultiblockStructure(int xd, int yd, int zd, List<BlockPos> blocks) {
            this.xd = xd;
            this.yd = yd;
            this.zd = zd;
            this.blocks = blocks;

            int minx = Integer.MAX_VALUE;
            int maxx = Integer.MIN_VALUE;
            int miny = Integer.MAX_VALUE;
            int maxy = Integer.MIN_VALUE;
            int minz = Integer.MAX_VALUE;
            int maxz = Integer.MIN_VALUE;
            for (BlockPos pos : blocks) {
                minx = Math.min(minx, pos.getX());
                maxx = Math.max(maxx, pos.getX());
                miny = Math.min(miny, pos.getY());
                maxy = Math.max(maxy, pos.getY());
                minz = Math.min(minz, pos.getZ());
                maxz = Math.max(maxz, pos.getZ());
            }

            bb = new AABB(minx, miny, minz, maxx + 1, maxy + 1, maxz + 1);
            minPos = new BlockPos(minx, miny, minz);
            maxPos = new BlockPos(maxx, maxy, maxz);
        }

        public AABB getBoundingBox() {
            return bb;
        }
    }

    /**
     * 在给定内部点附近居中探测 x/z 轴（1:1 旧版 detectCenter，简化：本版本
     * 不需要，结构检测从控制器背面开始即可）。
     */
    public abstract MultiblockStructure detectMultiblock(Level world, BlockPos center, int limit);

    /** 沿方向移动到墙体（含墙外第一格）。 */
    protected BlockPos getOuterPos(Level world, BlockPos pos, Direction direction, int limit) {
        for (int i = 0; i < limit && isInnerBlock(world, pos); i++) {
            pos = pos.relative(direction);
        }
        return pos;
    }

    /** 内部块 = 已加载且为空气。 */
    public boolean isInnerBlock(Level world, BlockPos pos) {
        return world.isLoaded(pos) && world.isEmptyBlock(pos);
    }

    /** 该位置是否可作为结构方块（墙/地板/天花板）。 */
    public abstract boolean isValidBlock(Level world, BlockPos pos);

    /** 结构是否值得重新检测（区块已加载）。 */
    public boolean checkIfMultiblockCanBeRechecked(Level world, MultiblockStructure structure) {
        return structure != null && isAreaLoaded(world, structure.minPos, structure.maxPos);
    }

    /** 1.21.1 无 (BlockPos, BlockPos) 的 isAreaLoaded，改用两角所在区块加载检查。 */
    public static boolean isAreaLoaded(Level world, BlockPos from, BlockPos to) {
        return world.hasChunk(from.getX() >> 4, from.getZ() >> 4)
                && world.hasChunk(to.getX() >> 4, to.getZ() >> 4);
    }

    /** 将成员方块与主机绑定（写入 servant 的主机位置）。 */
    public static void assignMultiBlock(Level world, BlockPos master, List<BlockPos> servants) {
        for (BlockPos pos : servants) {
            if (world.isLoaded(pos)) {
                if (world.getBlockEntity(pos) instanceof IServantLogic logic) {
                    BlockPos current = logic.getMasterPosition();
                    if (current == null || !current.equals(master)) {
                        logic.overrideMaster(master);
                        world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                    }
                }
            }
        }
    }
}
