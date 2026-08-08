package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.List;
import java.util.Set;

/**
 * 方块战利品表生成（DataGen）。
 *
 * <p>1:1 还原旧版行为：钴/阿迪特矿石与金属块直接掉落方块自身
 * （旧版 {@code BlockOre/BlockMetal} 无自定义掉落，挖掉掉落自身）。</p>
 */
public class TConBlockLoot extends BlockLootSubProvider {

    protected TConBlockLoot(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocks.COBALT_ORE.get());
        dropSelf(ModBlocks.ARDITE_ORE.get());
        dropSelf(ModBlocks.COBALT_BLOCK.get());
        dropSelf(ModBlocks.ARDITE_BLOCK.get());
        dropSelf(ModBlocks.TOOL_STATION.get());
        dropSelf(ModBlocks.TOOL_FORGE.get());

        // 冶炼炉（会话7）：seared 变体/玻璃/储罐/控制器全部掉自身
        for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
            dropSelf(variant.block().get());
        }
        dropSelf(ModBlocks.SEARED_GLASS.get());
        dropSelf(ModBlocks.SEARED_TANK.get());
        dropSelf(ModBlocks.SMELTERY_CONTROLLER.get());

        // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽/排液口掉自身；台阶按单块掉落（1:1 原版 SlabBlock）
        dropSelf(ModBlocks.CASTING_TABLE.get());
        dropSelf(ModBlocks.CASTING_BASIN.get());
        dropSelf(ModBlocks.FAUCET.get());
        dropSelf(ModBlocks.CHANNEL.get());
        dropSelf(ModBlocks.DRAIN.get());
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            dropSelf(stairs.block().get());
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            add(slab.block().get(), createSlabItemTable(slab.block().get()));
        }

        // 史莱姆生态（会话10）：
        // dirt：掉自身（copy type 变体）
        add(ModBlocks.SLIME_DIRT.get(), singleItemWithState(ModItems.SLIME_DIRT_ITEM.get(),
                ModBlocks.SLIME_DIRT.get(), SlimeTypes.DIRT_TYPE));
        // grass：掉对应 slime_dirt（1:1 旧版 damageDropped→slimeDirt；copy type）
        add(ModBlocks.SLIME_GRASS.get(), LootTable.lootTable().withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(ModItems.SLIME_DIRT_ITEM.get())
                        .apply(CopyBlockState.copyState(ModBlocks.SLIME_GRASS.get())
                                .copy(SlimeTypes.DIRT_TYPE)))));
        // leaves：silk touch 掉自身，否则 1/25 掉对应 foliage 树苗（旧版 getSaplingDropChance=25）
        add(ModBlocks.SLIME_LEAVES.get(), LootTable.lootTable()
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.SLIME_LEAVES_ITEM.get())
                                .apply(CopyBlockState.copyState(ModBlocks.SLIME_LEAVES.get())
                                        .copy(SlimeTypes.FOLIAGE_TYPE))
                                .when(hasSilkTouch())))
                .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(ModItems.SLIME_SAPLING_ITEM.get())
                                .apply(CopyBlockState.copyState(ModBlocks.SLIME_LEAVES.get())
                                        .copy(SlimeTypes.FOLIAGE_TYPE))
                                .when(LootItemRandomChanceCondition.randomChance(0.04F)))));
        // grass_tall：无掉落（旧版 getItemDropped=null）
        add(ModBlocks.SLIME_GRASS_TALL.get(), LootTable.lootTable());
        // sapling：掉自身（copy foliage）
        add(ModBlocks.SLIME_SAPLING.get(), singleItemWithState(ModItems.SLIME_SAPLING_ITEM.get(),
                ModBlocks.SLIME_SAPLING.get(), SlimeTypes.FOLIAGE_TYPE));
        // vine：掉自身（简化：旧版需剪刀，1.21.1 直接掉落便于获取）
        add(ModBlocks.SLIME_VINE_BLUE.get(), singleItemWithState(ModItems.SLIME_VINE_BLUE_ITEM.get(),
                ModBlocks.SLIME_VINE_BLUE.get()));
        add(ModBlocks.SLIME_VINE_BLUE_MID.get(), singleItemWithState(ModItems.SLIME_VINE_BLUE_MID_ITEM.get(),
                ModBlocks.SLIME_VINE_BLUE_MID.get()));
        add(ModBlocks.SLIME_VINE_BLUE_END.get(), singleItemWithState(ModItems.SLIME_VINE_BLUE_END_ITEM.get(),
                ModBlocks.SLIME_VINE_BLUE_END.get()));
        add(ModBlocks.SLIME_VINE_PURPLE.get(), singleItemWithState(ModItems.SLIME_VINE_PURPLE_ITEM.get(),
                ModBlocks.SLIME_VINE_PURPLE.get()));
        add(ModBlocks.SLIME_VINE_PURPLE_MID.get(), singleItemWithState(ModItems.SLIME_VINE_PURPLE_MID_ITEM.get(),
                ModBlocks.SLIME_VINE_PURPLE_MID.get()));
        add(ModBlocks.SLIME_VINE_PURPLE_END.get(), singleItemWithState(ModItems.SLIME_VINE_PURPLE_END_ITEM.get(),
                ModBlocks.SLIME_VINE_PURPLE_END.get()));
        // congealed：掉自身（copy type）
        add(ModBlocks.SLIME_CONGEALED.get(), singleItemWithState(ModItems.SLIME_CONGEALED_ITEM.get(),
                ModBlocks.SLIME_CONGEALED.get(), SlimeTypes.SLIME_TYPE));
    }

    /** 单物品掉落（可 copy 指定方块状态属性到 BLOCK_STATE 组件，多状态方块变体保留）。 */
    private static LootTable.Builder singleItemWithState(Item item, Block block,
                                                         net.minecraft.world.level.block.state.properties.Property<?>... props) {
        LootItem.Builder<?> entry = LootItem.lootTableItem(item);
        if (props.length > 0) {
            CopyBlockState.Builder copy = CopyBlockState.copyState(block);
            for (net.minecraft.world.level.block.state.properties.Property<?> prop : props) {
                copy.copy(prop);
            }
            entry = entry.apply(copy);
        }
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry));
    }

    /** 仅处理本 mod 方块，避免遍历全注册表（vanilla 方块无对应 loot builder）。 */
    @Override
    protected Iterable<Block> getKnownBlocks() {
        List<Block> blocks = new java.util.ArrayList<>(List.of(
                ModBlocks.COBALT_ORE.get(),
                ModBlocks.ARDITE_ORE.get(),
                ModBlocks.COBALT_BLOCK.get(),
                ModBlocks.ARDITE_BLOCK.get(),
                ModBlocks.TOOL_STATION.get(),
                ModBlocks.TOOL_FORGE.get(),
                ModBlocks.SEARED_GLASS.get(),
                ModBlocks.SEARED_TANK.get(),
                ModBlocks.SMELTERY_CONTROLLER.get(),
                ModBlocks.CASTING_TABLE.get(),
                ModBlocks.CASTING_BASIN.get(),
                ModBlocks.FAUCET.get(),
                ModBlocks.CHANNEL.get(),
                ModBlocks.DRAIN.get()));
        for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
            blocks.add(variant.block().get());
        }
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            blocks.add(stairs.block().get());
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            blocks.add(slab.block().get());
        }
        // 史莱姆生态（会话10）方块
        blocks.add(ModBlocks.SLIME_DIRT.get());
        blocks.add(ModBlocks.SLIME_GRASS.get());
        blocks.add(ModBlocks.SLIME_LEAVES.get());
        blocks.add(ModBlocks.SLIME_GRASS_TALL.get());
        blocks.add(ModBlocks.SLIME_SAPLING.get());
        blocks.add(ModBlocks.SLIME_VINE_BLUE.get());
        blocks.add(ModBlocks.SLIME_VINE_BLUE_MID.get());
        blocks.add(ModBlocks.SLIME_VINE_BLUE_END.get());
        blocks.add(ModBlocks.SLIME_VINE_PURPLE.get());
        blocks.add(ModBlocks.SLIME_VINE_PURPLE_MID.get());
        blocks.add(ModBlocks.SLIME_VINE_PURPLE_END.get());
        blocks.add(ModBlocks.SLIME_CONGEALED.get());
        return blocks;
    }
}
