package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

/**
 * seared 方块（1:1 移植自 Tinkers' Antique {@code BlockSeared} 简化版）。
 *
 * <p>旧版 12 变体（stone/cobble/paver/brick/...）为同一方块 + meta；
 * 1.21.1 无 meta，改为注册 12 个独立方块（贴图/名称各自对应，行为一致）。
 * 硬度 3 / 抗爆 20 / 金属音（1:1 旧版）。</p>
 */
public class BlockSeared extends Block {

    public BlockSeared(Properties properties) {
        super(properties);
    }

    /** 12 个变体的注册名（1:1 旧版 SearedType 枚举名）。 */
    public static final String[] VARIANTS = {
            "stone", "cobble", "paver", "brick", "brick_cracked", "brick_fancy",
            "brick_square", "brick_triangle", "brick_small", "road", "tile", "creeper"
    };

    /** seared 方块属性（1:1 旧版 hardness=3F/resistance=20F/SoundType.METAL）。 */
    public static Block.Properties searedProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(3.0F, 20.0F)
                .sound(SoundType.METAL);
    }
}
