package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块注册中枢（DeferredRegister）。
 *
 * <p>钴/阿迪特矿石与金属块，属性参数 1:1 移植自 Tinkers' Antique：
 * 矿石硬度 10、采掘等级 4（钴级，需 {@code needs_cobalt_tool} tag）；金属块硬度 5、
 * 任意镐可采。矿石在下界生成（见世界生成会话）。</p>
 */
public final class ModBlocks {

    /** 方块注册表。 */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TConstructNirvana.MODID);

    /** 钴矿：硬度 10、钴级采掘（旧版 BlockOre hardness=10 / harvestLevel=4）。 */
    public static final DeferredBlock<BlockTConOre> COBALT_ORE = BLOCKS.registerBlock(
            "cobalt_ore", BlockTConOre::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .strength(10.0F, 10.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    /** 阿迪特矿：硬度 10、钴级采掘（旧版 BlockOre hardness=10 / harvestLevel=4）。 */
    public static final DeferredBlock<BlockTConOre> ARDITE_ORE = BLOCKS.registerBlock(
            "ardite_ore", BlockTConOre::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.NETHER)
                    .strength(10.0F, 10.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.STONE));

    /** 钴块：硬度 5、任意镐可采、信标基座（旧版 BlockMetal hardness=5 / harvestLevel=-1）。 */
    public static final DeferredBlock<BlockTConMetal> COBALT_BLOCK = BLOCKS.registerBlock(
            "cobalt_block", BlockTConMetal::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));

    /** 阿迪特块：硬度 5、任意镐可采、信标基座（旧版 BlockMetal hardness=5 / harvestLevel=-1）。 */
    public static final DeferredBlock<BlockTConMetal> ARDITE_BLOCK = BLOCKS.registerBlock(
            "ardite_block", BlockTConMetal::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 6.0F)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL));

    /** 工具站：木质组装台（1:1 旧版 BlockToolTable，会话4.5b GUI）。 */
    public static final DeferredBlock<BlockToolTable> TOOL_STATION = BLOCKS.registerBlock(
            "tool_station", BlockToolTable::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(2.0F, 3.0F)
                    .sound(SoundType.WOOD));

    /** 锻造厂：金属组装台（1:1 旧版 BlockToolForge 简化，同工具站逻辑）。 */
    public static final DeferredBlock<BlockToolTable> TOOL_FORGE = BLOCKS.registerBlock(
            "tool_forge", BlockToolTable::new,
            BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(5.0F, 6.0F)
                    .sound(SoundType.METAL));

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
