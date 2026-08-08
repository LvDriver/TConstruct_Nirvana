package com.lvdriver.tconstruct_nirvana.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * TCon 通用配置（COMMON）。
 *
 * <p>当前为骨架：预留矿物生成开关，后续会话按需扩展（冶炼炉行为、世界生成等）。</p>
 */
public final class TConConfig {

    public static final ModConfigSpec SPEC;

    /** 是否在主世界生成钴矿（旧版钴矿生成于下界，见旧代码 OverworldOreGenerator/NetherOreGenerator）。 */
    public static final ModConfigSpec.BooleanValue GENERATE_COBALT_ORE;

    /** 是否生成阿迪特矿。 */
    public static final ModConfigSpec.BooleanValue GENERATE_ARDITE_ORE;

    /** 是否生成史莱姆岛（旧版 genSlimeIslands：主世界浮岛 + 下界岩浆岛，默认开）。 */
    public static final ModConfigSpec.BooleanValue GENERATE_SLIME_ISLANDS;

    /** 是否生成史莱姆矿池（旧版 genSlimePools：主世界池 + 下界岩浆池，默认关）。 */
    public static final ModConfigSpec.BooleanValue GENERATE_SLIME_POOLS;

    /* ---------- smeltery 分组（1:1 旧版 Config 的冶炼炉相关项） ---------- */

    /** 是否允许龙头抽取气体流体（1:1 旧版 Config.drainGaseousFluids = true）。 */
    public static final ModConfigSpec.BooleanValue DRAIN_GASEOUS_FLUIDS;

    /** 冶炼炉加热物品的 tick 间隔（1:1 旧版 Config.heatItemsTickrateSmeltery = 4）。 */
    public static final ModConfigSpec.IntValue HEAT_ITEMS_TICKRATE_SMELTERY;

    /** 龙头每 tick 最大液体传输量 mb（1:1 旧版 Config.liquidTransferRate = 6）。 */
    public static final ModConfigSpec.IntValue LIQUID_TRANSFER_RATE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("worldgen");
        GENERATE_COBALT_ORE = builder
                .comment("Generate Cobalt ore in the Nether. (1:1 with Tinkers' Antique)")
                .define("generateCobaltOre", true);
        GENERATE_ARDITE_ORE = builder
                .comment("Generate Ardite ore in the Nether. (1:1 with Tinkers' Antique)")
                .define("generateArditeOre", true);
        GENERATE_SLIME_ISLANDS = builder
                .comment("Generate slime islands in the Overworld and magma slime islands in the Nether. (1:1 with Tinkers' Antique genSlimeIslands; requires /reload after change)")
                .define("generateSlimeIslands", true);
        GENERATE_SLIME_POOLS = builder
                .comment("Generate slime pools in the Overworld and magma slime pools in the Nether. Disabled by default, 1:1 with Tinkers' Antique. (requires /reload after change)")
                .define("generateSlimePools", false);
        builder.pop();

        // 冶炼炉行为（旧版 Config 的 smeltery 分类；容量由结构大小决定（槽数×8 锭），
        // 旧版无容量配置项，1:1 不做配置）
        builder.push("smeltery");
        DRAIN_GASEOUS_FLUIDS = builder
                .comment("Allow draining gaseous fluids through the faucet. (1:1 with Tinkers' Antique Config.drainGaseousFluids)")
                .define("drainGaseousFluids", true);
        HEAT_ITEMS_TICKRATE_SMELTERY = builder
                .comment("Tick interval for heating items in the smeltery. (1:1 with Tinkers' Antique Config.heatItemsTickrateSmeltery; effective within the 20-tick update cycle)")
                .defineInRange("heatItemsTickrateSmeltery", 4, 1, 20);
        LIQUID_TRANSFER_RATE = builder
                .comment("Max fluid amount the faucet transfers per tick, in mb. (1:1 with Tinkers' Antique Config.liquidTransferRate)")
                .defineInRange("liquidTransferRate", 6, 1, 64);
        builder.pop();

        SPEC = builder.build();
    }

    private TConConfig() {
    }
}
