package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式标签页注册中枢（DeferredRegister）。
 *
 * <p>TCon 标签页：放入当前会话注册的全部物品（钴/阿迪特矿石、金属块、锭、粒），
 * 后续会话注册的新物品按组追加。</p>
 */
public final class ModCreativeTabs {

    /** 创造模式标签页注册表。 */
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TConstructNirvana.MODID);

    /** TCon 主标签页。 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TCON_TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tconstruct_nirvana"))
            .icon(() -> new ItemStack(ModItems.COBALT_INGOT.get()))
            .displayItems((parameters, output) -> {
                // 矿石
                output.accept(ModBlocks.COBALT_ORE.get());
                output.accept(ModBlocks.ARDITE_ORE.get());
                // 金属块
                output.accept(ModBlocks.COBALT_BLOCK.get());
                output.accept(ModBlocks.ARDITE_BLOCK.get());
                // 锭
                output.accept(ModItems.COBALT_INGOT.get());
                output.accept(ModItems.ARDITE_INGOT.get());
                // 粒
                output.accept(ModItems.COBALT_NUGGET.get());
                output.accept(ModItems.ARDITE_NUGGET.get());
            })
            .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
