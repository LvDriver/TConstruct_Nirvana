package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.world.ModWorldGenData;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * DataGen 入口。
 *
 * <p>通过 {@code ./gradlew runData} 触发。本会话注册：世界生成（矿石 configured/placed
 * feature + BiomeModifier）、方块/物品 Tag（矿物词典等价 + 采掘等级）、方块战利品、
 * 方块状态/模型、物品模型与本地化。</p>
 */
@EventBusSubscriber(modid = TConstructNirvana.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class TConDataGen {

    private TConDataGen() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        String modid = TConstructNirvana.MODID;

        /* ---------- 服务端数据 ---------- */

        // 世界生成：configured/placed feature JSON（data/tconstruct_nirvana/worldgen/...）
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(
                packOutput, event.getLookupProvider(), ModWorldGenData.BUILDER, Set.of(modid)));

        // 世界生成：BiomeModifier JSON（下界生成钴/阿迪特矿，1:1 旧版 NetherOreGenerator）
        generator.addProvider(event.includeServer(), new TConBiomeModifiersProvider(packOutput, modid));

        // 方块/物品 Tag（矿物词典等价 + 采掘等级 + 信标基座）
        TConBlockTagsProvider blockTags = new TConBlockTagsProvider(packOutput, event.getLookupProvider(), modid, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new TConItemTagsProvider(
                packOutput, event.getLookupProvider(), blockTags.contentsGetter(), modid, event.getExistingFileHelper()));

        // 方块战利品（掉落自身，1:1 旧版行为）
        generator.addProvider(event.includeServer(), new LootTableProvider(
                packOutput, Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(TConBlockLoot::new, LootContextParamSets.BLOCK)),
                event.getLookupProvider()));

        /* ---------- 客户端数据 ---------- */

        // 方块状态/模型、物品模型、本地化
        generator.addProvider(event.includeClient(), new TConBlockStateProvider(packOutput, modid, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TConItemModelProvider(packOutput, modid, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TConLanguageProvider(packOutput, modid, "en_us", false));
        generator.addProvider(event.includeClient(), new TConLanguageProvider(packOutput, modid, "zh_cn", true));
    }
}
