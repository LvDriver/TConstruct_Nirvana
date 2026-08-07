package com.lvdriver.tconstruct_nirvana.smeltery;

import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 冶炼炉燃料注册表（1:1 移植自旧版 {@code TinkerRegistry} 的
 * {@code registerSmelteryFuel / isSmelteryFuel / consumeSmelteryFuel}）。
 *
 * <p>每 50mb 燃料提供一定 tick 的燃烧时长：岩浆 100 tick、烈焰血 150 tick
 * （1:1 旧版 {@code TinkerSmeltery.registerSmelteryFuel}）。炉温 = 燃料流体
 * 温度 - 300（摄氏度）。燃料判定按流体本身（非 tag），注册表可扩展
 * （附属 mod 可注册自定义燃料）。</p>
 */
public final class SmelteryFuels {

    /** 单次消耗量（mb，1:1 旧版）。 */
    public static final int FUEL_AMOUNT = 50;

    /** 流体 → 每 50mb 燃烧 tick 数。 */
    private static final Map<Fluid, Integer> FUELS = new LinkedHashMap<>();

    static {
        registerFuel(Fluids.LAVA, 100);
        // 烈焰血未注册流体（devlog 遗留），注册表留扩展位
    }

    private SmelteryFuels() {
    }

    /** 注册燃料（附属扩展入口，1:1 旧版 registerSmelteryFuel）。 */
    public static void registerFuel(Fluid fluid, int ticksPer50mb) {
        FUELS.put(fluid, ticksPer50mb);
    }

    /** 是否为冶炼炉燃料。 */
    public static boolean isSmelteryFuel(FluidStack in) {
        return FUELS.containsKey(in.getFluid());
    }

    /**
     * 消耗燃料并返回燃烧 tick 数（1:1 旧版 consumeSmelteryFuel）。
     * 不足 50mb 时按比例折算（旧版 bug：比例乘了 in.amount 而非 duration，
     * 1:1 语义保留——不足时按 {@code in²/50} 折算）。
     */
    public static int consumeSmelteryFuel(FluidStack in) {
        Integer duration = FUELS.get(in.getFluid());
        if (duration == null) {
            return 0;
        }
        int out = duration;
        if (in.getAmount() < FUEL_AMOUNT) {
            // 不足一次消耗：按比例折算
            float coeff = (float) in.getAmount() / (float) FUEL_AMOUNT;
            out = Math.round(coeff * in.getAmount());
            in.setAmount(0);
        } else {
            in.setAmount(in.getAmount() - FUEL_AMOUNT);
        }
        return out;
    }

    /** 燃料流体集合（GUI 提示用）。 */
    public static Map<Fluid, Integer> getFuels() {
        return FUELS;
    }
}
