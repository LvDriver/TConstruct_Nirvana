package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块状态与模型生成（DataGen）。
 *
 * <p>贴图沿用旧版资源（1:1）：下界矿石用 {@code nether_ore_cobalt/ardite}，
 * 钴块 cube_all 用 {@code block_cobalt}，阿迪特块 cube_bottom_top 用
 * {@code block_ardite + block_ardite_top}（旧版 metal.json 布局）。</p>
 */
public class TConBlockStateProvider extends BlockStateProvider {

    public TConBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 下界矿石（旧版 ore.json：cube_all + nether_ore_*）
        simpleBlockWithItem(ModBlocks.COBALT_ORE.get(),
                models().cubeAll("cobalt_ore", modLoc("block/nether_ore_cobalt")));
        simpleBlockWithItem(ModBlocks.ARDITE_ORE.get(),
                models().cubeAll("ardite_ore", modLoc("block/nether_ore_ardite")));

        // 金属块（旧版 metal.json：cobalt cube_all，ardite cube_bottom_top）
        simpleBlockWithItem(ModBlocks.COBALT_BLOCK.get(),
                models().cubeAll("cobalt_block", modLoc("block/block_cobalt")));
        simpleBlockWithItem(ModBlocks.ARDITE_BLOCK.get(),
                models().cubeBottomTop("ardite_block",
                        modLoc("block/block_ardite"),
                        modLoc("block/block_ardite_top"),
                        modLoc("block/block_ardite_top")));
    }
}
