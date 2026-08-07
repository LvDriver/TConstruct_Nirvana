package com.lvdriver.tconstruct_nirvana.client;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.client.gui.TinkerStationScreen;
import com.lvdriver.tconstruct_nirvana.client.renderer.TinkerProjectileRenderer;
import com.lvdriver.tconstruct_nirvana.entity.ModEntities;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.gui.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

/**
 * 客户端注册事件（MOD 总线，仅客户端）。
 *
 * <p>会话4.5b：弹射物实体渲染器注册（1.21.1 NeoForge 无 RegisterRenderersEvent，
 * 用原版静态 {@link EntityRenderers#register} 在 FMLClientSetupEvent 中注册）；
 * 工具站 GUI 屏幕注册。流体会话：流体渲染属性（贴图 + 染色）注册。</p>
 */
@EventBusSubscriber(modid = TConstructNirvana.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ModClientEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(ModEntities.TINKER_ARROW.get(), TinkerProjectileRenderer::new);
            EntityRenderers.register(ModEntities.TINKER_BOLT.get(), TinkerProjectileRenderer::new);
            EntityRenderers.register(ModEntities.TINKER_SHURIKEN.get(), TinkerProjectileRenderer::new);
        });
    }

    /** 菜单屏幕注册（1.21.1 MenuScreens.register 已私有废弃，官方改走本事件）。 */
    @SubscribeEvent
    public static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.TOOL_STATION.get(), TinkerStationScreen::new);
        event.register(ModMenuTypes.SMELTERY.get(), com.lvdriver.tconstruct_nirvana.client.gui.ScreenSmeltery::new);
    }

    /** 流体渲染属性注册（贴图 + 染色，1:1 旧版 FluidColored 颜色体系）。 */
    @SubscribeEvent
    public static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        for (ModFluids.FluidEntry entry : ModFluids.FLUIDS_ALL) {
            event.registerFluidType(
                    new TConFluidRenderProperties(entry.stillTexture(), entry.flowingTexture(), entry.tintColor()),
                    entry.type().get());
        }
    }

    private ModClientEvents() {
    }
}
