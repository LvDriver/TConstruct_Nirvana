package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Set;

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

    /* ---------- 冶炼炉（会话7） ---------- */

    /** seared 变体条目（名称 + 方块，1:1 旧版 SearedType 枚举）。 */
    public record SearedVariant(String name, DeferredBlock<BlockSeared> block) {
    }

    /** seared 方块 12 变体（1:1 旧版 BlockSeared.SearedType，独立注册）。 */
    public static final DeferredBlock<BlockSeared> SEARED_STONE = registerSeared("seared_stone");
    public static final DeferredBlock<BlockSeared> SEARED_COBBLE = registerSeared("seared_cobble");
    public static final DeferredBlock<BlockSeared> SEARED_PAVER = registerSeared("seared_paver");
    public static final DeferredBlock<BlockSeared> SEARED_BRICK = registerSeared("seared_brick");
    public static final DeferredBlock<BlockSeared> SEARED_BRICK_CRACKED = registerSeared("seared_brick_cracked");
    public static final DeferredBlock<BlockSeared> SEARED_BRICK_FANCY = registerSeared("seared_brick_fancy");
    public static final DeferredBlock<BlockSeared> SEARED_BRICK_SQUARE = registerSeared("seared_brick_square");
    public static final DeferredBlock<BlockSeared> SEARED_BRICK_TRIANGLE = registerSeared("seared_brick_triangle");
    public static final DeferredBlock<BlockSeared> SEARED_BRICK_SMALL = registerSeared("seared_brick_small");
    public static final DeferredBlock<BlockSeared> SEARED_ROAD = registerSeared("seared_road");
    public static final DeferredBlock<BlockSeared> SEARED_TILE = registerSeared("seared_tile");
    public static final DeferredBlock<BlockSeared> SEARED_CREEPER = registerSeared("seared_creeper");

    /** 全部 12 个 seared 变体（DataGen/创造页/结构检测遍历用）。 */
    public static final List<SearedVariant> SEARED_VARIANTS = List.of(
            new SearedVariant("stone", SEARED_STONE),
            new SearedVariant("cobble", SEARED_COBBLE),
            new SearedVariant("paver", SEARED_PAVER),
            new SearedVariant("brick", SEARED_BRICK),
            new SearedVariant("brick_cracked", SEARED_BRICK_CRACKED),
            new SearedVariant("brick_fancy", SEARED_BRICK_FANCY),
            new SearedVariant("brick_square", SEARED_BRICK_SQUARE),
            new SearedVariant("brick_triangle", SEARED_BRICK_TRIANGLE),
            new SearedVariant("brick_small", SEARED_BRICK_SMALL),
            new SearedVariant("road", SEARED_ROAD),
            new SearedVariant("tile", SEARED_TILE),
            new SearedVariant("creeper", SEARED_CREEPER));

    /** 冶炼炉墙体主方块（stone 变体，结构检测/浇铸配方用）。 */
    public static final DeferredBlock<BlockSeared> SEARED = SEARED_STONE;

    /** seared 玻璃（冶炼炉可选墙体材料）。 */
    public static final DeferredBlock<BlockSearedGlass> SEARED_GLASS = BLOCKS.registerBlock(
            "seared_glass", BlockSearedGlass::new, BlockSearedGlass.glassProperties());

    /** seared 储罐（燃料罐/液体储罐，冶炼炉必需至少 1 个）。 */
    public static final DeferredBlock<BlockTank> SEARED_TANK = BLOCKS.registerBlock(
            "seared_tank", BlockTank::new, BlockTank.tankProperties());

    /** 冶炼炉控制器（多方块主机）。 */
    public static final DeferredBlock<BlockSmelteryController> SMELTERY_CONTROLLER = BLOCKS.registerBlock(
            "smeltery_controller", BlockSmelteryController::new, BlockSmelteryController.controllerProperties());

    /** 冶炼炉墙体合法方块（1:1 旧版 validSmelteryBlocks）。懒初始化：注册事件前不可 get()。 */
    private static Set<net.minecraft.world.level.block.Block> validSmelteryBlocks;

    public static Set<net.minecraft.world.level.block.Block> getValidSmelteryBlocks() {
        if (validSmelteryBlocks == null) {
            validSmelteryBlocks = Set.of(SEARED.get(), SEARED_TANK.get(), SEARED_GLASS.get());
        }
        return validSmelteryBlocks;
    }

    private static DeferredBlock<BlockSeared> registerSeared(String name) {
        return BLOCKS.registerBlock(name, BlockSeared::new, BlockSeared.searedProperties());
    }

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
