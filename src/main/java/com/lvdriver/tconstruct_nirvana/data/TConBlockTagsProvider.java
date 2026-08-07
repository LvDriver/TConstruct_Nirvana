package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 方块 Tag 生成（DataGen）。
 *
 * <p>矿石/金属块标记为镐可采；钴/阿迪特矿按 1:1 采掘等级 4 标记
 * {@code needs_cobalt_tool}（旧版 {@code Config.netherOresMiningLevel=4}）；
 * 金属块为信标基座（1:1 旧版 {@code BlockMetal.isBeaconBase=true}）；
 * 另输出矿物词典等价 common tag（c:ores/*、c:storage_blocks/*）。</p>
 */
public class TConBlockTagsProvider extends BlockTagsProvider {

    public TConBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 镐可采（旧版 getHarvestTool = "pickaxe"）
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.COBALT_ORE.get())
                .add(ModBlocks.ARDITE_ORE.get())
                .add(ModBlocks.COBALT_BLOCK.get())
                .add(ModBlocks.ARDITE_BLOCK.get())
                .add(ModBlocks.TOOL_FORGE.get());

        // 冶炼炉（会话7）：seared 系列 + 储罐 + 控制器镐可采，玻璃镐可采
        for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(variant.block().get());
        }
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.SEARED_GLASS.get())
                .add(ModBlocks.SEARED_TANK.get())
                .add(ModBlocks.SMELTERY_CONTROLLER.get());

        // 斧可采（工具站木质）
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(ModBlocks.TOOL_STATION.get());

        // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽/排液口 + seared 楼梯/台阶镐可采
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CASTING_TABLE.get())
                .add(ModBlocks.CASTING_BASIN.get())
                .add(ModBlocks.FAUCET.get())
                .add(ModBlocks.CHANNEL.get())
                .add(ModBlocks.DRAIN.get());
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(stairs.block().get());
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(slab.block().get());
        }

        // 钴级采掘（旧版 harvestLevel = 4）
        tag(TConTags.NEEDS_COBALT_TOOL)
                .add(ModBlocks.COBALT_ORE.get())
                .add(ModBlocks.ARDITE_ORE.get());

        // 信标基座（1:1 旧版 BlockMetal.isBeaconBase = true）
        tag(BlockTags.BEACON_BASE_BLOCKS)
                .add(ModBlocks.COBALT_BLOCK.get())
                .add(ModBlocks.ARDITE_BLOCK.get());

        // 矿物词典等价：矿石（旧版 oreCobalt / oreArdite）
        tag(TConTags.ORES_COBALT).add(ModBlocks.COBALT_ORE.get());
        tag(TConTags.ORES_ARDITE).add(ModBlocks.ARDITE_ORE.get());

        // 矿物词典等价：金属块（旧版 blockCobalt / blockArdite）
        tag(TConTags.STORAGE_BLOCKS_COBALT).add(ModBlocks.COBALT_BLOCK.get());
        tag(TConTags.STORAGE_BLOCKS_ARDITE).add(ModBlocks.ARDITE_BLOCK.get());
    }
}
