package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;

/**
 * 方块战利品表生成（DataGen）。
 *
 * <p>1:1 还原旧版行为：钴/阿迪特矿石与金属块直接掉落方块自身
 * （旧版 {@code BlockOre/BlockMetal} 无自定义掉落，挖掉掉落自身）。</p>
 */
public class TConBlockLoot extends BlockLootSubProvider {

    protected TConBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.COBALT_ORE.get());
        dropSelf(ModBlocks.ARDITE_ORE.get());
        dropSelf(ModBlocks.COBALT_BLOCK.get());
        dropSelf(ModBlocks.ARDITE_BLOCK.get());
    }

    /** 仅处理本 mod 方块，避免遍历全注册表（vanilla 方块无对应 loot builder）。 */
    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
                ModBlocks.COBALT_ORE.get(),
                ModBlocks.ARDITE_ORE.get(),
                ModBlocks.COBALT_BLOCK.get(),
                ModBlocks.ARDITE_BLOCK.get());
    }
}
