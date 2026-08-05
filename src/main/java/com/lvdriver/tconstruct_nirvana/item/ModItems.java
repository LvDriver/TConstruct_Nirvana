package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册中枢（DeferredRegister）。
 *
 * <p>当前为骨架：本会话只建注册表，具体物品在"材料与矿物系统"会话中填充。</p>
 */
public final class ModItems {

    /** 物品注册表。 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TConstructNirvana.MODID);

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
