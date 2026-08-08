package com.lvdriver.tconstruct_nirvana.client.model;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

/**
 * 自定义模型加载器注册（MOD 总线，仅客户端）。
 *
 * <p>注册 {@code tconstruct_nirvana:tool} 几何加载器，工具物品模型 JSON
 * 经 {@link ToolModelLoader} 解析为多层材质模型（见 {@link ToolUnbakedGeometry}）。</p>
 */
@EventBusSubscriber(modid = TConstructNirvana.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModModelLoaders {

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "tool"), new ToolModelLoader());
    }

    private ModModelLoaders() {
    }
}
