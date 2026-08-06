package com.lvdriver.tconstruct_nirvana.fluid;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.ArrayList;
import java.util.List;

/**
 * 流体注册中枢（DeferredRegister）。
 *
 * <p>1.21.1 流体分三件套：{@link FluidType}（属性：密度/粘度/温度/亮度/稀有度，
 * 温度即熔点，1:1 自旧版 {@code Fluid.setTemperature}）、{@link Fluid}（本体，
 * Source + Flowing 两条目）、{@link LiquidBlock}（流体方块）与 {@link BucketItem}（桶）。</p>
 *
 * <p>注册条目 1:1 自旧版 {@code TinkerFluids.setupFluids/registerBlocks}：
 * 熔融金属类密度 2000/粘度 10000/亮度 10（旧版 FluidMolten 默认），
 * 石头类同金属属性（旧版 fluidStone），blood 为水基（密度/粘度 1000），
 * purpleslime 密度/粘度 1600（旧版 fluidSlime）。颜色 1:1 自旧版
 * {@code materialTextColor} 与显式色值，alpha 缺失时补 0xFF（旧版 FluidColored）。</p>
 *
 * <p>注册顺序（{@link #register}）：FLUID_TYPES → FLUIDS → ITEMS（桶）→ BLOCKS（方块），
 * 因 {@code BucketItem}/{@code LiquidBlock} 构造需要已注册的 Fluid 实例。</p>
 */
public final class ModFluids {

    /** 流体类型注册表（属性：密度/粘度/温度等）。 */
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, TConstructNirvana.MODID);

    /** 流体注册表（本体）。 */
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(Registries.FLUID, TConstructNirvana.MODID);

    /** 流体方块注册表（LiquidBlock，与流体同名）。 */
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TConstructNirvana.MODID);

    /** 流体桶注册表（BucketItem，{@code <fluid>_bucket}）。 */
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TConstructNirvana.MODID);

    // 渲染贴图（1:1 旧版 textures/blocks/fluids/*）
    private static final ResourceLocation METAL_STILL = loc("block/molten_metal");
    private static final ResourceLocation METAL_FLOW = loc("block/molten_metal_flow");
    private static final ResourceLocation STONE_STILL = loc("block/liquid_stone");
    private static final ResourceLocation STONE_FLOW = loc("block/liquid_stone_flow");
    private static final ResourceLocation LIQUID_STILL = loc("block/liquid");
    private static final ResourceLocation LIQUID_FLOW = loc("block/liquid_flow");
    private static final ResourceLocation SLIME_STILL = loc("block/liquid_slime");
    private static final ResourceLocation SLIME_FLOW = loc("block/liquid_slime_flow");

    /** 一个流体的全部注册条目（type + 本体 + 方块 + 桶 + 客户端渲染参数）。 */
    public record FluidEntry(
            DeferredHolder<FluidType, FluidType> type,
            DeferredHolder<Fluid, Fluid> still,
            DeferredHolder<Fluid, Fluid> flowing,
            DeferredBlock<LiquidBlock> block,
            DeferredItem<BucketItem> bucket,
            ResourceLocation stillTexture,
            ResourceLocation flowingTexture,
            int tintColor) {

        /** 流体注册名（如 molten_iron）。 */
        public ResourceLocation id() {
            return still.getId();
        }
    }

    /** 全部流体条目（注册顺序 = 创造页/DataGen 遍历顺序）。 */
    public static final List<FluidEntry> FLUIDS_ALL = new ArrayList<>();

    // ============ 熔融金属（1:1 旧版 TinkerFluids.setupFluids） ============

    /** 熔融铁：熔点 769（旧版 iron.setTemperature(769)）。 */
    public static final FluidEntry MOLTEN_IRON = metal("molten_iron", 0xa81212, 769, Rarity.UNCOMMON);
    /** 熔融金：熔点 532，稀有（旧版 gold，RARE）。 */
    public static final FluidEntry MOLTEN_GOLD = metal("molten_gold", 0xf6d609, 532, Rarity.RARE);
    /** 熔融生铁：熔点 600，史诗（旧版 pigIron，EPIC）。 */
    public static final FluidEntry MOLTEN_PIGIRON = metal("molten_pigiron", 0xef9e9b, 600, Rarity.EPIC);
    /** 熔融钴：熔点 950，稀有（旧版 cobalt，RARE）。 */
    public static final FluidEntry MOLTEN_COBALT = metal("molten_cobalt", 0x2882d4, 950, Rarity.RARE);
    /** 熔融阿迪特：熔点 860，稀有（旧版 ardite，RARE）。 */
    public static final FluidEntry MOLTEN_ARDITE = metal("molten_ardite", 0xd14210, 860, Rarity.RARE);
    /** 熔融玛玉灵：熔点 1000，稀有（旧版 manyullyn，RARE）。 */
    public static final FluidEntry MOLTEN_MANYULLYN = metal("molten_manyullyn", 0xa15cf8, 1000, Rarity.RARE);
    /** 熔融骑士史莱姆：熔点 520，史诗（旧版 knightslime，EPIC）。 */
    public static final FluidEntry MOLTEN_KNIGHTSLIME = metal("molten_knightslime", 0xf18ff0, 520, Rarity.EPIC);
    /** 熔融铝黄铜：熔点 500（旧版 alubrass）。 */
    public static final FluidEntry MOLTEN_ALUBRASS = metal("molten_alubrass", 0xf0d467, 500, Rarity.UNCOMMON);
    /** 熔融铝化钢：熔点 900，稀有（旧版 alumite，RARE）。 */
    public static final FluidEntry MOLTEN_ALUMITE = metal("molten_alumite", 0xffa7e9, 900, Rarity.RARE);
    /** 熔融黄铜：熔点 470（旧版 brass）。 */
    public static final FluidEntry MOLTEN_BRASS = metal("molten_brass", 0xede38b, 470, Rarity.UNCOMMON);
    /** 熔融铜：熔点 542（旧版 copper）。 */
    public static final FluidEntry MOLTEN_COPPER = metal("molten_copper", 0xed9f07, 542, Rarity.UNCOMMON);
    /** 熔融锡：熔点 350（旧版 tin）。 */
    public static final FluidEntry MOLTEN_TIN = metal("molten_tin", 0xc1cddc, 350, Rarity.UNCOMMON);
    /** 熔融青铜：熔点 475（旧版 bronze）。 */
    public static final FluidEntry MOLTEN_BRONZE = metal("molten_bronze", 0xe3bd68, 475, Rarity.UNCOMMON);
    /** 熔融锌：熔点 375（旧版 zinc）。 */
    public static final FluidEntry MOLTEN_ZINC = metal("molten_zinc", 0xd3efe8, 375, Rarity.UNCOMMON);
    /** 熔融铅：熔点 400（旧版 lead）。 */
    public static final FluidEntry MOLTEN_LEAD = metal("molten_lead", 0x4d4968, 400, Rarity.UNCOMMON);
    /** 熔融镍：熔点 727（旧版 nickel）。 */
    public static final FluidEntry MOLTEN_NICKEL = metal("molten_nickel", 0xc8d683, 727, Rarity.UNCOMMON);
    /** 熔融银：熔点 480，稀有（旧版 silver，RARE）。 */
    public static final FluidEntry MOLTEN_SILVER = metal("molten_silver", 0xd1ecf6, 480, Rarity.RARE);
    /** 熔融琥珀金：熔点 500，史诗（旧版 electrum，EPIC）。 */
    public static final FluidEntry MOLTEN_ELECTRUM = metal("molten_electrum", 0xe8db49, 500, Rarity.EPIC);
    /** 熔融钢：熔点 681（旧版 steel）。 */
    public static final FluidEntry MOLTEN_STEEL = metal("molten_steel", 0xa7a7a7, 681, Rarity.UNCOMMON);
    /** 熔融铝：熔点 330（旧版 aluminum）。 */
    public static final FluidEntry MOLTEN_ALUMINUM = metal("molten_aluminum", 0xefe0d5, 330, Rarity.UNCOMMON);

    // ============ 熔融石头类（1:1 旧版 fluidStone，金属属性 + stone 贴图） ============

    /** 熔融石头（seared stone）：熔点 800（旧版 searedStone，流体名 stone）。 */
    public static final FluidEntry MOLTEN_STONE = stone("molten_stone", 0x777777, 800);
    /** 熔融黑曜石：熔点 1000。 */
    public static final FluidEntry MOLTEN_OBSIDIAN = stone("molten_obsidian", 0x2c0d59, 1000);
    /** 熔融粘土：熔点 700。 */
    public static final FluidEntry MOLTEN_CLAY = stone("molten_clay", 0xc67453, 700);
    /** 熔融泥土：熔点 500。 */
    public static final FluidEntry MOLTEN_DIRT = stone("molten_dirt", 0xa68564, 500);

    // ============ 经典流体（合金配方输入，1:1 旧版） ============

    /** 血：温度 336，水基（旧版 fluidClassic，密度/粘度 1000，无光）。 */
    public static final FluidEntry BLOOD = liquid("blood", 0x540000, 336);
    /** 紫色史莱姆：温度 370，密度/粘度 1600（旧版 fluidSlime，无光）。 */
    public static final FluidEntry PURPLESLIME = slime("purpleslime", 0xefd236ff, 370);

    private ModFluids() {
    }

    /** 熔融金属（旧版 FluidMolten：密度 2000、粘度 10000、亮度 10）。 */
    private static FluidEntry metal(String name, int color, int temperature, Rarity rarity) {
        return register(name, METAL_STILL, METAL_FLOW, color, 2000, 10000, temperature, 10, rarity);
    }

    /** 熔融石头类（旧版 fluidStone：金属属性 + stone 贴图）。 */
    private static FluidEntry stone(String name, int color, int temperature) {
        return register(name, STONE_STILL, STONE_FLOW, color, 2000, 10000, temperature, 10, Rarity.UNCOMMON);
    }

    /** 水基流体（旧版 fluidClassic：密度/粘度 1000、无光）。 */
    private static FluidEntry liquid(String name, int color, int temperature) {
        return register(name, LIQUID_STILL, LIQUID_FLOW, color, 1000, 1000, temperature, 0, Rarity.COMMON);
    }

    /** 史莱姆流体（旧版 fluidSlime：密度/粘度 1600、无光）。 */
    private static FluidEntry slime(String name, int color, int temperature) {
        return register(name, SLIME_STILL, SLIME_FLOW, color, 1600, 1600, temperature, 0, Rarity.COMMON);
    }

    /**
     * 登记一个流体的全部条目（type/still/flowing/桶/方块）。
     *
     * <p>条目间互相引用全部经 holder（Supplier）延迟解析，静态初始化顺序无关；
     * 实际注册顺序由 RegisterEvent 按各 DeferredRegister 的 register() 调用顺序处理，
     * {@link #register} 保证 FLUIDS 先于 ITEMS/BLOCKS，因 {@code BucketItem}/
     * {@code LiquidBlock} 构造需要 Fluid 实例。</p>
     */
    private static FluidEntry register(String name, ResourceLocation stillTex, ResourceLocation flowTex,
                                       int color, int density, int viscosity, int temperature,
                                       int lightLevel, Rarity rarity) {
        // 旧版 FluidColored：无 alpha 时补 0xFF（不透明）
        final int tint = (color & 0xFF000000) == 0 ? color | 0xFF000000 : color;

        final DeferredHolder<FluidType, FluidType> type = FLUID_TYPES.register(name, () -> new FluidType(
                FluidType.Properties.create()
                        .density(density)
                        .viscosity(viscosity)
                        .temperature(temperature)   // 熔点（温度系统：数据存 FluidType 属性）
                        .lightLevel(lightLevel)
                        .rarity(rarity)));

        // 流体本体：Source + Flowing（互相引用经数组捕获，注册事件中延迟解析）
        final DeferredHolder<Fluid, Fluid>[] still = new DeferredHolder[1];
        final DeferredHolder<Fluid, Fluid>[] flowing = new DeferredHolder[1];
        final DeferredItem<BucketItem>[] bucket = new DeferredItem[1];
        final DeferredBlock<LiquidBlock>[] block = new DeferredBlock[1];

        still[0] = FLUIDS.register(name, () -> new BaseFlowingFluid.Source(
                fluidProps(type, still[0], flowing[0], bucket[0], block[0])));
        flowing[0] = FLUIDS.register(name + "_flowing", () -> new BaseFlowingFluid.Flowing(
                fluidProps(type, still[0], flowing[0], bucket[0], block[0])));

        // 桶（1:1 旧版 FluidRegistry.addBucketForFluid：1 堆叠，倒空返还空桶）
        bucket[0] = ITEMS.register(name + "_bucket", () -> new BucketItem(still[0].get(),
                new Item.Properties().stacksTo(1).craftRemainder(Items.BUCKET)));

        // 流体方块（1:1 旧版 BlockMolten：LiquidBlock，不可破坏/无碰撞/无战利品）
        // （BaseFlowingFluid.Source 即 FlowingFluid 子类，此处按 LiquidBlock 构造签名强转）
        block[0] = BLOCKS.register(name, () -> new LiquidBlock((FlowingFluid) still[0].get(),
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noCollission()
                        .strength(100.0F)
                        .noLootTable()
                        .liquid()));

        FluidEntry entry = new FluidEntry(type, still[0], flowing[0], block[0], bucket[0],
                stillTex, flowTex, tint);
        FLUIDS_ALL.add(entry);
        return entry;
    }

    /** 流体属性：引用全部条目（Supplier 延迟解析，不触发注册前 get）。 */
    private static BaseFlowingFluid.Properties fluidProps(DeferredHolder<FluidType, FluidType> type,
                                                          DeferredHolder<Fluid, Fluid> still,
                                                          DeferredHolder<Fluid, Fluid> flowing,
                                                          DeferredItem<BucketItem> bucket,
                                                          DeferredBlock<LiquidBlock> block) {
        return new BaseFlowingFluid.Properties(type, still, flowing)
                .bucket(bucket)
                .block(block)
                .explosionResistance(100.0F);
    }

    private static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, path);
    }

    public static void register(IEventBus modEventBus) {
        // 顺序关键：桶/方块构造需要 Fluid 实例，FLUIDS 必须先注册
        FLUID_TYPES.register(modEventBus);
        FLUIDS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCKS.register(modEventBus);
    }
}
