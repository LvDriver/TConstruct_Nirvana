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

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("worldgen");
        GENERATE_COBALT_ORE = builder
                .comment("Generate Cobalt ore in the Nether. (1:1 with Tinkers' Antique)")
                .define("generateCobaltOre", true);
        GENERATE_ARDITE_ORE = builder
                .comment("Generate Ardite ore in the Nether. (1:1 with Tinkers' Antique)")
                .define("generateArditeOre", true);
        builder.pop();

        SPEC = builder.build();
    }

    private TConConfig() {
    }
}
