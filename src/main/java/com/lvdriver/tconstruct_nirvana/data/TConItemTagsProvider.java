package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
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

        // 材料-物品关联自定义 tag：原版物品加入（无 c: 对应项的 oredict 替代）
        tag(TConTags.FLINT).add(net.minecraft.world.item.Items.FLINT);
        tag(TConTags.CACTUS).add(net.minecraft.world.item.Items.CACTUS);
        tag(TConTags.STORAGE_BLOCKS_PRISMARINE).add(net.minecraft.world.item.Items.PRISMARINE);
        tag(TConTags.STORAGE_BLOCKS_PRISMARINE_BRICKS).add(net.minecraft.world.item.Items.PRISMARINE_BRICKS);
        tag(TConTags.STORAGE_BLOCKS_DARK_PRISMARINE).add(net.minecraft.world.item.Items.DARK_PRISMARINE);
        tag(TConTags.BONE_MEAL).add(net.minecraft.world.item.Items.BONE_MEAL);
        tag(TConTags.PAPER).add(net.minecraft.world.item.Items.PAPER);
        tag(TConTags.SPONGES).add(net.minecraft.world.item.Items.SPONGE);

        // 熔炼配方输入 tag：原版物品加入（1:1 旧版 registerMeltingCasting 的具体物品输入）
        tag(TConTags.ICE).add(net.minecraft.world.item.Items.ICE);
        tag(TConTags.PACKED_ICE).add(net.minecraft.world.item.Items.PACKED_ICE);
        tag(TConTags.SNOW).add(net.minecraft.world.item.Items.SNOW);
        tag(TConTags.SNOWBALLS).add(net.minecraft.world.item.Items.SNOWBALL);
        tag(TConTags.ROTTEN_FLESH).add(net.minecraft.world.item.Items.ROTTEN_FLESH);
        tag(TConTags.SPIDER_EYES).add(net.minecraft.world.item.Items.SPIDER_EYE);
        tag(TConTags.PUFFERFISH).add(net.minecraft.world.item.Items.PUFFERFISH);
        tag(TConTags.RAILS).add(net.minecraft.world.item.Items.RAIL);
        tag(TConTags.ACTIVATOR_RAILS).add(net.minecraft.world.item.Items.ACTIVATOR_RAIL);
        tag(TConTags.DETECTOR_RAILS).add(net.minecraft.world.item.Items.DETECTOR_RAIL);
        tag(TConTags.GOLDEN_RAILS).add(net.minecraft.world.item.Items.POWERED_RAIL);
        tag(TConTags.IRON_HORSE_ARMORS).add(net.minecraft.world.item.Items.IRON_HORSE_ARMOR);
        tag(TConTags.GOLDEN_HORSE_ARMORS).add(net.minecraft.world.item.Items.GOLDEN_HORSE_ARMOR);
        tag(TConTags.CLAY).add(net.minecraft.world.item.Items.CLAY_BALL);
        tag(TConTags.CLAY_BLOCKS).add(net.minecraft.world.item.Items.CLAY);
        tag(TConTags.BONE_BLOCKS).add(net.minecraft.world.item.Items.BONE_BLOCK);
        tag(TConTags.SAND).add(net.minecraft.world.item.Items.SAND);
        // 染色陶瓦 16 色（旧版 STAINED_HARDENED_CLAY wildcard；不含普通陶瓦，防无操作清洗）
        tag(TConTags.STAINED_TERRACOTTA)
                .add(net.minecraft.world.item.Items.WHITE_TERRACOTTA)
                .add(net.minecraft.world.item.Items.ORANGE_TERRACOTTA)
                .add(net.minecraft.world.item.Items.MAGENTA_TERRACOTTA)
                .add(net.minecraft.world.item.Items.LIGHT_BLUE_TERRACOTTA)
                .add(net.minecraft.world.item.Items.YELLOW_TERRACOTTA)
                .add(net.minecraft.world.item.Items.LIME_TERRACOTTA)
                .add(net.minecraft.world.item.Items.PINK_TERRACOTTA)
                .add(net.minecraft.world.item.Items.GRAY_TERRACOTTA)
                .add(net.minecraft.world.item.Items.LIGHT_GRAY_TERRACOTTA)
                .add(net.minecraft.world.item.Items.CYAN_TERRACOTTA)
                .add(net.minecraft.world.item.Items.PURPLE_TERRACOTTA)
                .add(net.minecraft.world.item.Items.BLUE_TERRACOTTA)
                .add(net.minecraft.world.item.Items.BROWN_TERRACOTTA)
                .add(net.minecraft.world.item.Items.GREEN_TERRACOTTA)
                .add(net.minecraft.world.item.Items.RED_TERRACOTTA)
                .add(net.minecraft.world.item.Items.BLACK_TERRACOTTA);
        tag(TConTags.VINES).add(net.minecraft.world.item.Items.VINE);
        tag(TConTags.PACKED_ICE).add(net.minecraft.world.item.Items.PACKED_ICE);
        tag(TConTags.END_RODS).add(net.minecraft.world.item.Items.END_ROD);

        // 工具 mineable tag（物品侧，1:1 旧版 tool class → 1.21.1 mineable 体系）
        for (var entry : com.lvdriver.tconstruct_nirvana.item.tool.ModTools.entries()) {
            TinkerToolItem tool = (TinkerToolItem) entry.item().get();
            for (var mineable : tool.getMineableTags()) {
                tag(mineable).add(entry.item().get());
            }
        }
        // 钴级工具（needs_cobalt_tool 物品侧，工具采掘等级 ≥ 4 时由 HarvestCheck 判定，tag 供附属使用）
        for (var entry : com.lvdriver.tconstruct_nirvana.item.tool.ModTools.entries()) {
            tag(TConTags.NEEDS_COBALT_TOOL_ITEMS).add(entry.item().get());
        }
    }
}
