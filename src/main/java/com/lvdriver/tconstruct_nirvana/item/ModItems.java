package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

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

    // 冶炼炉方块物品（会话7）
    public static final DeferredItem<BlockItem> SEARED_GLASS_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.SEARED_GLASS);
    public static final DeferredItem<BlockItem> SEARED_TANK_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.SEARED_TANK);
    public static final DeferredItem<BlockItem> SMELTERY_CONTROLLER_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.SMELTERY_CONTROLLER);
    public static final List<DeferredItem<BlockItem>> SEARED_VARIANT_ITEMS = ModBlocks.SEARED_VARIANTS.stream()
            .map(v -> ITEMS.registerSimpleBlockItem(v.block()))
            .toList();

    // 浇铸系统方块物品（会话8）
    public static final DeferredItem<BlockItem> CASTING_TABLE_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CASTING_TABLE);
    public static final DeferredItem<BlockItem> CASTING_BASIN_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.CASTING_BASIN);
    public static final DeferredItem<BlockItem> FAUCET_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.FAUCET);
    /** 沟槽物品（自定义：放置后按点击面建立连接，1:1 旧版 ItemChannel）。 */
    public static final DeferredItem<ItemChannel> CHANNEL_ITEM = ITEMS.register("channel",
            () -> new ItemChannel(ModBlocks.CHANNEL.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> DRAIN_ITEM = ITEMS.registerSimpleBlockItem(ModBlocks.DRAIN);

    /** seared 楼梯方块物品（12 个）。 */
    public static final List<DeferredItem<BlockItem>> SEARED_STAIRS_ITEMS = ModBlocks.SEARED_STAIRS.stream()
            .map(s -> ITEMS.registerSimpleBlockItem(s.block()))
            .toList();

    /** seared 台阶方块物品（12 个）。 */
    public static final List<DeferredItem<BlockItem>> SEARED_SLABS_ITEMS = ModBlocks.SEARED_SLABS.stream()
            .map(s -> ITEMS.registerSimpleBlockItem(s.block()))
            .toList();

    private ModItems() {
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
