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
        dropSelf(ModBlocks.TOOL_STATION.get());
        dropSelf(ModBlocks.TOOL_FORGE.get());

        // 冶炼炉（会话7）：seared 变体/玻璃/储罐/控制器全部掉自身
        for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
            dropSelf(variant.block().get());
        }
        dropSelf(ModBlocks.SEARED_GLASS.get());
        dropSelf(ModBlocks.SEARED_TANK.get());
        dropSelf(ModBlocks.SMELTERY_CONTROLLER.get());

        // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽/排液口掉自身；台阶按单块掉落（1:1 原版 SlabBlock）
        dropSelf(ModBlocks.CASTING_TABLE.get());
        dropSelf(ModBlocks.CASTING_BASIN.get());
        dropSelf(ModBlocks.FAUCET.get());
        dropSelf(ModBlocks.CHANNEL.get());
        dropSelf(ModBlocks.DRAIN.get());
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            dropSelf(stairs.block().get());
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            add(slab.block().get(), createSlabItemTable(slab.block().get()));
        }
    }

    /** 仅处理本 mod 方块，避免遍历全注册表（vanilla 方块无对应 loot builder）。 */
    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> blocks = new java.util.ArrayList<>(List.of(
                ModBlocks.COBALT_ORE.get(),
                ModBlocks.ARDITE_ORE.get(),
                ModBlocks.COBALT_BLOCK.get(),
                ModBlocks.ARDITE_BLOCK.get(),
                ModBlocks.TOOL_STATION.get(),
                ModBlocks.TOOL_FORGE.get(),
                ModBlocks.SEARED_GLASS.get(),
                ModBlocks.SEARED_TANK.get(),
                ModBlocks.SMELTERY_CONTROLLER.get(),
                ModBlocks.CASTING_TABLE.get(),
                ModBlocks.CASTING_BASIN.get(),
                ModBlocks.FAUCET.get(),
                ModBlocks.CHANNEL.get(),
                ModBlocks.DRAIN.get()));
        for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
            blocks.add(variant.block().get());
        }
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            blocks.add(stairs.block().get());
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            blocks.add(slab.block().get());
        }
        return blocks;
    }
}
