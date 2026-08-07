package com.lvdriver.tconstruct_nirvana.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 冶炼炉多方块检测（1:1 移植自 Tinkers' Antique {@code MultiblockSmeltery}）。
 *
 * <p>结构规则：有地板无天花板无独立框架；地板仅允许 seared 方块；
 * 墙体允许 {@code validSmelteryBlocks}（seared/储罐/玻璃）；必须至少包含一个
 * seared_tank（否则结构无效，燃料/液体无从存取）。</p>
 */
public class MultiblockSmeltery extends MultiblockTinker {

    /** 结构内是否检测到储罐。 */
    public boolean hasTank;

    public MultiblockSmeltery(TileMultiblock tile) {
        super(tile, true, false, false);
        this.hasTank = false;
    }

    @Override
    public MultiblockStructure detectMultiblock(Level world, BlockPos center, int limit) {
        hasTank = false;
        MultiblockStructure ret = super.detectMultiblock(world, center, limit);
        if (!hasTank) {
            com.lvdriver.tconstruct_nirvana.TConstructNirvana.LOGGER.debug(
                    "[Smeltery] detect FAIL: no seared_tank in structure");
            return null;
        }
        return ret;
    }

    @Override
    public boolean isValidBlock(Level world, BlockPos pos) {
        // 控制器自身恒有效
        if (pos.equals(tile.getBlockPos())) {
            return true;
        }

        if (!isValidSlave(world, pos)) {
            return false;
        }

        BlockState state = world.getBlockState(pos);

        // 需要至少一个储罐（燃料/液体存取）
        if (state.getBlock() == com.lvdriver.tconstruct_nirvana.block.ModBlocks.SEARED_TANK.get()) {
            hasTank = true;
            return true;
        }

        return com.lvdriver.tconstruct_nirvana.block.ModBlocks.getValidSmelteryBlocks().contains(state.getBlock());
    }

    @Override
    public boolean isFloorBlock(Level world, BlockPos pos) {
        // 地板仅允许 seared 方块（1:1 旧版 isFloorBlock 只认 searedBlock，含全部 12 变体）
        return com.lvdriver.tconstruct_nirvana.block.ModBlocks.isSearedBlock(world.getBlockState(pos).getBlock())
                && isValidBlock(world, pos);
    }
}
