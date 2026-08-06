package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 物品注册中枢（DeferredRegister）。
 *
 * <p>金属锭/粒（钴、阿迪特）+ 方块物品。锭/粒对应旧版
 * {@code TinkerCommons.ingots/nuggets} 动态物品中的 cobalt/ardite 条目，
 * 1.21.1 拆分为独立物品注册。</p>
 */
public final class ModItems {

    /** 物品注册表。 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TConstructNirvana.MODID);

    /** 钴锭。 */
    public static final DeferredItem<Item> COBALT_INGOT = ITEMS.registerSimpleItem("cobalt_ingot");

    /** 阿迪特锭。 */
    public static final DeferredItem<Item> ARDITE_INGOT = ITEMS.registerSimpleItem("ardite_ingot");

    /** 钴粒。 */
    public static final DeferredItem<Item> COBALT_NUGGET = ITEMS.registerSimpleItem("cobalt_nugget");

    /** 阿迪特粒。 */
    public static final DeferredItem<Item> ARDITE_NUGGET = ITEMS.registerSimpleItem("ardite_nugget");

    // 方块物品（与方块同名自动注册）
    public static final DeferredItem<BlockItem> COBALT_ORE_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.COBALT_ORE);
    public static final DeferredItem<BlockItem> ARDITE_ORE_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.ARDITE_ORE);
    public static final DeferredItem<BlockItem> COBALT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.COBALT_BLOCK);
    public static final DeferredItem<BlockItem> ARDITE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.ARDITE_BLOCK);
    public static final DeferredItem<BlockItem> TOOL_STATION_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.TOOL_STATION);
    public static final DeferredItem<BlockItem> TOOL_FORGE_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.TOOL_FORGE);

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
