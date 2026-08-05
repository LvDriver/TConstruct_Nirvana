package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块注册中枢（DeferredRegister）。
 *
 * <p>当前为骨架：本会话只建注册表，具体方块（钴矿/阿迪特矿/金属块等）在后续会话中填充。</p>
 */
public final class ModBlocks {

    /** 方块注册表。 */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TConstructNirvana.MODID);

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
