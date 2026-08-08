package com.lvdriver.tconstruct_nirvana.client;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.block.ModBlockEntities;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import com.lvdriver.tconstruct_nirvana.client.gui.TinkerStationScreen;
import com.lvdriver.tconstruct_nirvana.client.renderer.TileCastingRenderer;
import com.lvdriver.tconstruct_nirvana.client.renderer.TileChannelRenderer;
import com.lvdriver.tconstruct_nirvana.client.renderer.TileFaucetRenderer;
import com.lvdriver.tconstruct_nirvana.client.renderer.TinkerProjectileRenderer;
import com.lvdriver.tconstruct_nirvana.entity.ModEntities;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.gui.ModMenuTypes;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
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
            // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽渲染器
            BlockEntityRenderers.register(ModBlockEntities.CASTING_TABLE.get(), TileCastingRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.CASTING_BASIN.get(), TileCastingRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.FAUCET.get(), TileFaucetRenderer::new);
            BlockEntityRenderers.register(ModBlockEntities.CHANNEL.get(), TileChannelRenderer::new);
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

    /** 史莱姆 foliage 染色（1:1 旧版 SlimeColorizer.getColorStatic：BLUE=0x2aec81、PURPLE=0xa92dff、ORANGE=0xd09800）。 */
    @SubscribeEvent
    public static void onRegisterBlockColors(net.neoforged.neoforge.client.event.RegisterColorHandlersEvent.Block event) {
        event.getBlockColors().register((state, level, pos, tintIndex) -> {
            SlimeTypes.FoliageType foliage = state.getValue(SlimeTypes.FOLIAGE_TYPE);
            return switch (foliage) {
                case PURPLE -> 0xa92dff;
                case ORANGE -> 0xd09800;
                default -> 0x2aec81;
            };
        }, ModBlocks.SLIME_GRASS.get(), ModBlocks.SLIME_LEAVES.get());
    }

    private ModClientEvents() {
    }
}
