package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * DataComponent 注册中枢（DeferredRegister）。
 *
 * <p>1.21.1 物品数据一律走 DataComponent，禁止 NBT tag。
 * 当前为骨架：本会话只建注册表，工具部件/修饰符等自定义组件在后续会话中填充。</p>
 */
public final class ModDataComponents {

    /** DataComponent 注册表。 */
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES =
            DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, TConstructNirvana.MODID);

    private ModDataComponents() {
    }

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENT_TYPES.register(modEventBus);
    }
}
