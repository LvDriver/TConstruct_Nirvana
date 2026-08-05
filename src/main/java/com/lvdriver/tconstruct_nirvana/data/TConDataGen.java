package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

/**
 * DataGen 入口。
 *
 * <p>通过 {@code ./gradlew runData} 触发。当前为骨架：仅建立挂载点，
 * 各 DataProvider（方块状态/物品模型/loot/语言/配方等）在后续会话中注册。</p>
 */
@EventBusSubscriber(modid = TConstructNirvana.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class TConDataGen {

    private TConDataGen() {
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        // TODO 后续会话注册各 DataProvider，例如：
        // generator.addProvider(event.includeClient(), new TConItemModelProvider(packOutput));
        // generator.addProvider(event.includeServer(), new TConRecipeProvider(packOutput));
        // generator.addProvider(event.includeServer(), new TConLanguageProvider(packOutput, "en_us"));
    }
}
