package com.lvdriver.tconstruct_nirvana.block.slime;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

/**
 * 凝结石块（1:1 移植自 Tinkers' Antique {@code BlockSlimeCongealed}）。
 *
 * <p>粘性方块（friction 0.8，走上去很滑）、slime 音效；5 变体
 * （green/blue/purple/blood/magma，去掉旧版 PINK 彩蛋）。作为史莱姆树
 * 树干与矿池边缘方块。</p>
 */
public class BlockSlimeCongealed extends Block {

    public BlockSlimeCongealed(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(SlimeTypes.SLIME_TYPE, SlimeTypes.SlimeType.GREEN));
    }

    @Override
    protected MapCodec<? extends Block> codec() {
        return simpleCodec(BlockSlimeCongealed::new);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(SlimeTypes.SLIME_TYPE);
    }
}
