package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 匠魂金属块（钴块/阿迪特块）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code BlockMetal}：硬度 5、任意镐可采
 * （无采掘等级要求，仅需镐类工具）。信标基座能力在 1.21.1 中由
 * {@code minecraft:beacon_base_blocks} tag 表达（见 TConBlockTagsProvider）。</p>
 */
public class BlockTConMetal extends Block {

    public BlockTConMetal(Properties properties) {
        super(properties);
    }
}
