package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * seared 玻璃方块（1:1 移植自 Tinkers' Antique {@code BlockSearedGlass} 简化版）。
 *
 * <p>冶炼炉墙体可选材料：透明方块，1.21.1 用 noOcclusion + cutout 渲染
 * （旧版 CTM 特殊渲染，本版简化；贴图沿用旧版 seared_window_side/top）。</p>
 */
public class BlockSearedGlass extends Block {

    public BlockSearedGlass(Properties properties) {
        super(properties);
    }

    /** 玻璃属性（1:1 旧版 hardness=0.3F，sound=GLASS，noOcclusion 透明）。 */
    public static Block.Properties glassProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn((state, level, pos, type) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false);
    }
}
