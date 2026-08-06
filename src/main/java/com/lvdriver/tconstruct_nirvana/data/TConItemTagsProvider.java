package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 物品 Tag 生成（DataGen）。
 *
 * <p>锭/粒输出矿物词典等价 common tag（c:ingots/*、c:nuggets/*）；
 * 方块物品 tag 通过 {@code copy} 从方块侧同步（矿石、金属块、needs_cobalt_tool）。</p>
 */
public class TConItemTagsProvider extends ItemTagsProvider {

    public TConItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                CompletableFuture<TagLookup<Block>> blockTags, String modId,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 矿物词典等价：锭（旧版 ingotCobalt / ingotArdite）
        tag(TConTags.INGOTS_COBALT).add(ModItems.COBALT_INGOT.get());
        tag(TConTags.INGOTS_ARDITE).add(ModItems.ARDITE_INGOT.get());

        // 矿物词典等价：粒（旧版 nuggetCobalt / nuggetArdite）
        tag(TConTags.NUGGETS_COBALT).add(ModItems.COBALT_NUGGET.get());
        tag(TConTags.NUGGETS_ARDITE).add(ModItems.ARDITE_NUGGET.get());

        // 方块物品同步方块侧 tag
        copy(TConTags.NEEDS_COBALT_TOOL, TConTags.NEEDS_COBALT_TOOL_ITEMS);
        copy(TConTags.ORES_COBALT, TConTags.ORES_COBALT_ITEMS);
        copy(TConTags.ORES_ARDITE, TConTags.ORES_ARDITE_ITEMS);
        copy(TConTags.STORAGE_BLOCKS_COBALT, TConTags.STORAGE_BLOCKS_COBALT_ITEMS);
        copy(TConTags.STORAGE_BLOCKS_ARDITE, TConTags.STORAGE_BLOCKS_ARDITE_ITEMS);

        // 全部工具部件（部件-模具关联的部件侧集合）
        for (var part : ModToolParts.getAllParts()) {
            tag(TConTags.TOOL_PARTS).add(part.get());
        }
    }
}
