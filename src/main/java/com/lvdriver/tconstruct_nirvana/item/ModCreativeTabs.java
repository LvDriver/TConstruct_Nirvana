package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式标签页注册中枢（DeferredRegister）。
 *
 * <p>当前为骨架：本会话只建注册表，"材料与矿物系统"会话中注册 TCon 标签页并放入物品。</p>
 */
public final class ModCreativeTabs {

    /** 创造模式标签页注册表。 */
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TConstructNirvana.MODID);

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
