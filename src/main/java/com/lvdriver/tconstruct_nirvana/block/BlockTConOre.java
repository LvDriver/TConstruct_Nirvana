package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.world.level.block.Block;

/**
 * 匠魂矿石方块（钴矿/阿迪特矿）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code BlockOre}：硬度 10、
 * 采掘工具为镐、采掘等级 4（钴级，通过 {@code needs_cobalt_tool} tag 控制）。
 * 方块属性（硬度、采掘 tag）在 {@link ModBlocks} 中按 1:1 参数构建。</p>
 */
public class BlockTConOre extends Block {

    public BlockTConOre(Properties properties) {
        super(properties);
    }
}
