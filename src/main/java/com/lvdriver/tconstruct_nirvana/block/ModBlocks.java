package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeCongealed;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeDirt;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeGrass;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeLeaves;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeSapling;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeVine;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockTallSlimeGrass;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
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

    /** 冶炼炉墙体合法方块（1:1 旧版 validSmelteryBlocks 含 seared 全变体）。懒初始化：注册事件前不可 get()。 */
    private static Set<net.minecraft.world.level.block.Block> validSmelteryBlocks;

    public static Set<net.minecraft.world.level.block.Block> getValidSmelteryBlocks() {
        if (validSmelteryBlocks == null) {
            Set<net.minecraft.world.level.block.Block> set = new java.util.HashSet<>();
            for (SearedVariant variant : SEARED_VARIANTS) {
                set.add(variant.block().get());
            }
            set.add(SEARED_TANK.get());
            set.add(SEARED_GLASS.get());
            // 排液口是冶炼炉流体接口（1:1 旧版 smelteryIO 在 validSmelteryBlocks 内）；
            // 缺失会导致玩家把墙换成排液口后结构检测失败、冶炼炉失效
            set.add(DRAIN.get());
            // 1:1 旧版 searedStairsSlabs 也可作墙体
            for (SearedStairsEntry stairs : SEARED_STAIRS) {
                set.add(stairs.block().get());
            }
            for (SearedSlabEntry slab : SEARED_SLABS) {
                set.add(slab.block().get());
            }
            validSmelteryBlocks = Set.copyOf(set);
        }
        return validSmelteryBlocks;
    }

    /* ---------- 浇铸系统（会话8） ---------- */

    /** 浇铸台（模具浇铸：锭/粒/宝石等，1:1 旧版 BlockCasting TYPE=TABLE）。 */
    public static final DeferredBlock<BlockCasting> CASTING_TABLE = BLOCKS.register(
            "casting_table", () -> new BlockCasting(BlockCasting.castingProperties(), false));

    /** 浇铸盆（无模具铸块/清洗，1:1 旧版 BlockCasting TYPE=BASIN）。 */
    public static final DeferredBlock<BlockCasting> CASTING_BASIN = BLOCKS.register(
            "casting_basin", () -> new BlockCasting(BlockCasting.castingProperties(), true));

    /** 龙头（从上方/侧方容器抽液浇注，1:1 旧版 BlockFaucet）。 */
    public static final DeferredBlock<BlockFaucet> FAUCET = BLOCKS.register(
            "faucet", () -> new BlockFaucet(BlockFaucet.faucetProperties()));

    /** 沟槽（流体分配通道，1:1 旧版 BlockChannel）。 */
    public static final DeferredBlock<BlockChannel> CHANNEL = BLOCKS.register(
            "channel", () -> new BlockChannel(BlockChannel.channelProperties()));

    /** 排液口（冶炼炉流体接口，1:1 旧版 BlockSmelteryIO）。 */
    public static final DeferredBlock<BlockDrain> DRAIN = BLOCKS.register(
            "drain", () -> new BlockDrain(BlockDrain.drainProperties()));

    /* ---------- seared 楼梯/台阶（会话8，1:1 旧版 BlockSearedStairs/BlockSearedSlab 拆分） ---------- */

    /** seared 楼梯条目（名称 + 方块，1:1 旧版 searedStairs* 12 个独立方块）。 */
    public record SearedStairsEntry(String name, DeferredBlock<net.minecraft.world.level.block.StairBlock> block) {
    }

    /** seared 台阶条目（名称 + 方块，1:1 旧版 seared_slab/seared_slab2 的 12 变体拆分）。 */
    public record SearedSlabEntry(String name, DeferredBlock<net.minecraft.world.level.block.SlabBlock> block) {
    }

    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_STONE =
            registerSearedStairs("seared_stairs_stone", SEARED_STONE);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_COBBLE =
            registerSearedStairs("seared_stairs_cobble", SEARED_COBBLE);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_PAVER =
            registerSearedStairs("seared_stairs_paver", SEARED_PAVER);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_BRICK =
            registerSearedStairs("seared_stairs_brick", SEARED_BRICK);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_BRICK_CRACKED =
            registerSearedStairs("seared_stairs_brick_cracked", SEARED_BRICK_CRACKED);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_BRICK_FANCY =
            registerSearedStairs("seared_stairs_brick_fancy", SEARED_BRICK_FANCY);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_BRICK_SQUARE =
            registerSearedStairs("seared_stairs_brick_square", SEARED_BRICK_SQUARE);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_BRICK_TRIANGLE =
            registerSearedStairs("seared_stairs_brick_triangle", SEARED_BRICK_TRIANGLE);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_BRICK_SMALL =
            registerSearedStairs("seared_stairs_brick_small", SEARED_BRICK_SMALL);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_ROAD =
            registerSearedStairs("seared_stairs_road", SEARED_ROAD);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_TILE =
            registerSearedStairs("seared_stairs_tile", SEARED_TILE);
    public static final DeferredBlock<net.minecraft.world.level.block.StairBlock> SEARED_STAIRS_CREEPER =
            registerSearedStairs("seared_stairs_creeper", SEARED_CREEPER);

    /** 全部 12 个 seared 楼梯（DataGen/创造页遍历用）。 */
    public static final List<SearedStairsEntry> SEARED_STAIRS = List.of(
            new SearedStairsEntry("stone", SEARED_STAIRS_STONE),
            new SearedStairsEntry("cobble", SEARED_STAIRS_COBBLE),
            new SearedStairsEntry("paver", SEARED_STAIRS_PAVER),
            new SearedStairsEntry("brick", SEARED_STAIRS_BRICK),
            new SearedStairsEntry("brick_cracked", SEARED_STAIRS_BRICK_CRACKED),
            new SearedStairsEntry("brick_fancy", SEARED_STAIRS_BRICK_FANCY),
            new SearedStairsEntry("brick_square", SEARED_STAIRS_BRICK_SQUARE),
            new SearedStairsEntry("brick_triangle", SEARED_STAIRS_BRICK_TRIANGLE),
            new SearedStairsEntry("brick_small", SEARED_STAIRS_BRICK_SMALL),
            new SearedStairsEntry("road", SEARED_STAIRS_ROAD),
            new SearedStairsEntry("tile", SEARED_STAIRS_TILE),
            new SearedStairsEntry("creeper", SEARED_STAIRS_CREEPER));

    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_STONE =
            registerSearedSlab("seared_slab_stone");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_COBBLE =
            registerSearedSlab("seared_slab_cobble");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_PAVER =
            registerSearedSlab("seared_slab_paver");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_BRICK =
            registerSearedSlab("seared_slab_brick");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_BRICK_CRACKED =
            registerSearedSlab("seared_slab_brick_cracked");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_BRICK_FANCY =
            registerSearedSlab("seared_slab_brick_fancy");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_BRICK_SQUARE =
            registerSearedSlab("seared_slab_brick_square");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_BRICK_TRIANGLE =
            registerSearedSlab("seared_slab_brick_triangle");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_BRICK_SMALL =
            registerSearedSlab("seared_slab_brick_small");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_ROAD =
            registerSearedSlab("seared_slab_road");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_TILE =
            registerSearedSlab("seared_slab_tile");
    public static final DeferredBlock<net.minecraft.world.level.block.SlabBlock> SEARED_SLAB_CREEPER =
            registerSearedSlab("seared_slab_creeper");

    /** 全部 12 个 seared 台阶（DataGen/创造页遍历用）。 */
    public static final List<SearedSlabEntry> SEARED_SLABS = List.of(
            new SearedSlabEntry("stone", SEARED_SLAB_STONE),
            new SearedSlabEntry("cobble", SEARED_SLAB_COBBLE),
            new SearedSlabEntry("paver", SEARED_SLAB_PAVER),
            new SearedSlabEntry("brick", SEARED_SLAB_BRICK),
            new SearedSlabEntry("brick_cracked", SEARED_SLAB_BRICK_CRACKED),
            new SearedSlabEntry("brick_fancy", SEARED_SLAB_BRICK_FANCY),
            new SearedSlabEntry("brick_square", SEARED_SLAB_BRICK_SQUARE),
            new SearedSlabEntry("brick_triangle", SEARED_SLAB_BRICK_TRIANGLE),
            new SearedSlabEntry("brick_small", SEARED_SLAB_BRICK_SMALL),
            new SearedSlabEntry("road", SEARED_SLAB_ROAD),
            new SearedSlabEntry("tile", SEARED_SLAB_TILE),
            new SearedSlabEntry("creeper", SEARED_SLAB_CREEPER));

    private static DeferredBlock<net.minecraft.world.level.block.StairBlock> registerSearedStairs(
            String name, DeferredBlock<? extends Block> base) {
        return BLOCKS.register(name, () -> new net.minecraft.world.level.block.StairBlock(
                base.get().defaultBlockState(), BlockSeared.searedProperties()));
    }

    private static DeferredBlock<net.minecraft.world.level.block.SlabBlock> registerSearedSlab(String name) {
        return BLOCKS.register(name, () -> new net.minecraft.world.level.block.SlabBlock(BlockSeared.searedProperties()));
    }

    /** seared 方块（任意变体）判定，结构检测地板/墙体用。 */
    public static boolean isSearedBlock(net.minecraft.world.level.block.Block block) {
        for (SearedVariant variant : SEARED_VARIANTS) {
            if (variant.block().get() == block) {
                return true;
            }
        }
        return false;
    }

    private static DeferredBlock<BlockSeared> registerSeared(String name) {
        return BLOCKS.registerBlock(name, BlockSeared::new, BlockSeared.searedProperties());
    }

    /* ---------- 史莱姆方块（会话10：史莱姆岛生态） ---------- */

    /** 史莱姆泥土（green/blue/purple/magma 4 变体，旧版 BlockSlimeDirt）。 */
    public static final DeferredBlock<BlockSlimeDirt> SLIME_DIRT = BLOCKS.register("slime_dirt",
            () -> new BlockSlimeDirt(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.55F)
                    .sound(SlimeTypes.SLIME_SOUND)));

    /** 史莱姆草皮（type×foliage，旧版 BlockSlimeGrass，grass 掉落对应 slime_dirt）。 */
    public static final DeferredBlock<BlockSlimeGrass> SLIME_GRASS = BLOCKS.register("slime_grass",
            () -> new BlockSlimeGrass(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.65F)
                    .sound(SoundType.GRASS)
                    .friction(0.65F))); // 旧版 slipperiness += 0.05

    /** 史莱姆树叶（blue/purple/orange 3 变体，旧版 BlockSlimeLeaves 简化：无凋零衰减）。 */
    public static final DeferredBlock<BlockSlimeLeaves> SLIME_LEAVES = BLOCKS.register("slime_leaves",
            () -> new BlockSlimeLeaves(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .strength(0.3F)
                    .sound(SoundType.GRASS)
                    .noOcclusion()
                    .isViewBlocking((state, level, pos) -> false)));

    /** 史莱姆高草（旧版 BlockTallSlimeGrass 简化：无掉落，仅 foliage 变体）。 */
    public static final DeferredBlock<BlockTallSlimeGrass> SLIME_GRASS_TALL = BLOCKS.register("slime_grass_tall",
            () -> new BlockTallSlimeGrass(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)
                    .offsetType(BlockBehaviour.OffsetType.XYZ)));

    /** 史莱姆树苗（骨粉催熟按 foliage 生成对应史莱姆树）。 */
    public static final DeferredBlock<BlockSlimeSapling> SLIME_SAPLING = BLOCKS.register("slime_sapling",
            () -> new BlockSlimeSapling(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.PLANT)
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.GRASS)));

    /** 史莱姆藤蔓（蓝/紫 × top/mid/end 各 3 段，1:1 旧版 BlockSlimeVine）。 */
    public static final DeferredBlock<BlockSlimeVine> SLIME_VINE_BLUE = BLOCKS.register("slime_vine_blue",
            () -> new BlockSlimeVine(vineProperties(), () -> ModBlocks.SLIME_VINE_BLUE_MID.get()));
    public static final DeferredBlock<BlockSlimeVine> SLIME_VINE_BLUE_MID = BLOCKS.register("slime_vine_blue_mid",
            () -> new BlockSlimeVine(vineProperties(), () -> ModBlocks.SLIME_VINE_BLUE_END.get()));
    public static final DeferredBlock<BlockSlimeVine> SLIME_VINE_BLUE_END = BLOCKS.register("slime_vine_blue_end",
            () -> new BlockSlimeVine(vineProperties(), null));
    public static final DeferredBlock<BlockSlimeVine> SLIME_VINE_PURPLE = BLOCKS.register("slime_vine_purple",
            () -> new BlockSlimeVine(vineProperties(), () -> ModBlocks.SLIME_VINE_PURPLE_MID.get()));
    public static final DeferredBlock<BlockSlimeVine> SLIME_VINE_PURPLE_MID = BLOCKS.register("slime_vine_purple_mid",
            () -> new BlockSlimeVine(vineProperties(), () -> ModBlocks.SLIME_VINE_PURPLE_END.get()));
    public static final DeferredBlock<BlockSlimeVine> SLIME_VINE_PURPLE_END = BLOCKS.register("slime_vine_purple_end",
            () -> new BlockSlimeVine(vineProperties(), null));

    /** 凝结石块（green/blue/purple/blood/magma 5 变体，树树干/矿池边缘）。 */
    public static final DeferredBlock<BlockSlimeCongealed> SLIME_CONGEALED = BLOCKS.register("slime_congealed",
            () -> new BlockSlimeCongealed(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GREEN)
                    .strength(0.6F)
                    .friction(0.8F)
                    .sound(SlimeTypes.SLIME_SOUND)));

    private static BlockBehaviour.Properties vineProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.PLANT)
                .noCollission()
                .instabreak()
                .sound(SoundType.VINE);
    }

    private ModBlocks() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
