package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 菜单类型注册中枢（DeferredRegister）。
 *
 * <p>会话4.5b：工具站/锻造厂共用 {@link TinkerStationMenu}（旧版
 * {@code ContainerToolStation / ContainerToolForge} 简化版）。
 * 客户端 MenuType 工厂以空容器重建，槽内容由服务端广播同步。</p>
 */
public final class ModMenuTypes {

    /** 菜单类型注册表。 */
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(Registries.MENU, TConstructNirvana.MODID);

    /** 工具站/锻造厂组装菜单。 */
    public static final DeferredHolder<MenuType<?>, MenuType<TinkerStationMenu>> TOOL_STATION =
            MENU_TYPES.register("tool_station", () -> new MenuType<>(
                    TinkerStationMenu::new, net.minecraft.world.flag.FeatureFlags.REGISTRY.allFlags()));

    /** 冶炼炉菜单（会话7）。 */
    public static final DeferredHolder<MenuType<?>, MenuType<ContainerSmeltery>> SMELTERY =
            MENU_TYPES.register("smeltery", () -> new MenuType<>(
                    ContainerSmeltery::new, net.minecraft.world.flag.FeatureFlags.REGISTRY.allFlags()));

    private ModMenuTypes() {
    }

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }
}
