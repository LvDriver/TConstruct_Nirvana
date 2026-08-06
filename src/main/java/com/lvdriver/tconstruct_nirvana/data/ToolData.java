package com.lvdriver.tconstruct_nirvana.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 工具属性数据（1:1 移植自 Tinkers' Antique {@code ToolNBT}，DataComponent 版）。
 *
 * <p>承载工具合成后的基础属性，由部件材料属性按固定顺序计算
 * （head → extra → handle，见各计算方法的 javadoc）。修饰符在
 * {@code TOOL_DATA} 上累加修改，合成时的原始值存于 {@code TOOL_DATA_ORIG}。</p>
 *
 * <p>字段与旧版一一对应：durability / harvestLevel / attack / speed /
 * attackSpeedMultiplier / modifiers（免费修饰符数）。</p>
 */
public record ToolData(
        int durability,
        int harvestLevel,
        float attack,
        float speed,
        float attackSpeedMultiplier,
        int modifiers) {

    public static final Codec<ToolData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("durability").forGetter(ToolData::durability),
            Codec.INT.fieldOf("harvest_level").forGetter(ToolData::harvestLevel),
            Codec.FLOAT.fieldOf("attack").forGetter(ToolData::attack),
            Codec.FLOAT.fieldOf("speed").forGetter(ToolData::speed),
            Codec.FLOAT.fieldOf("attack_speed_multiplier").forGetter(ToolData::attackSpeedMultiplier),
            Codec.INT.fieldOf("modifiers").forGetter(ToolData::modifiers)
    ).apply(instance, ToolData::new));

    /** 空工具数据（各字段为 0/1，等待 head() 填充）。 */
    public static ToolData empty() {
        return new ToolData(0, 0, 0, 0, 1f, 0);
    }

    /* ---------- 合成公式（1:1 旧版 ToolNBT，调用顺序固定） ---------- */

    /**
     * 头部属性合成（旧版 {@code ToolNBT.head}，须最先调用）：
     * <pre>
     * durability   = max(1, Σ头部耐久 / 头部数)     // 取平均
     * harvestLevel = max(各头部采掘等级)            // 取最高
     * attack       = Σ头部攻击 / 头部数            // 取平均
     * speed        = Σ头部挖掘速度 / 头部数         // 取平均
     * </pre>
     */
    public ToolData head(int[] durability, int[] harvestLevels, float[] attack, float[] speed) {
        int count = Math.max(1, durability.length);
        int dur = 0;
        int harvest = 0;
        float atk = 0f;
        float spd = 0f;
        for (int i = 0; i < count; i++) {
            dur += durability[i];
            atk += attack[i];
            spd += speed[i];
            harvest = Math.max(harvest, harvestLevels[i]);
        }
        return new ToolData(Math.max(1, dur / count), harvest, atk / count, spd / count, 1f, 0);
    }

    /** 旧版 {@code ToolNBT.head} 单头部便捷版。 */
    public ToolData head(int durability, int harvestLevel, float attack, float speed) {
        return head(new int[]{durability}, new int[]{harvestLevel}, new float[]{attack}, new float[]{speed});
    }

    /**
     * 附件属性合成（旧版 {@code ToolNBT.extra}，第二调用）：
     * <pre>durability += round(Σ附件附加耐久 / 附件数)</pre>
     */
    public ToolData extra(int... extraDurability) {
        int count = Math.max(1, extraDurability.length);
        int dur = 0;
        for (int d : extraDurability) {
            dur += d;
        }
        return new ToolData(durability + Math.round((float) dur / count), harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }

    /**
     * 手柄属性合成（旧版 {@code ToolNBT.handle}，最后调用）：
     * <pre>
     * modifier   = Σ手柄系数 / 手柄数
     * durability = round(durability × modifier)
     * durability += round(Σ手柄耐久 / 手柄数)
     * durability = max(1, durability)
     * </pre>
     */
    public ToolData handle(float[] modifiers, int[] handleDurability) {
        int count = Math.max(1, modifiers.length);
        float mod = 0f;
        int dur = 0;
        for (int i = 0; i < count; i++) {
            mod += modifiers[i];
            dur += handleDurability[i];
        }
        mod /= count;
        int newDur = Math.round(durability * mod);
        newDur += Math.round((float) dur / count);
        return new ToolData(Math.max(1, newDur), harvestLevel, attack, speed, attackSpeedMultiplier, this.modifiers);
    }

    /** 旧版 {@code ToolNBT.handle} 单手柄便捷版。 */
    public ToolData handle(float modifier, int durability) {
        return handle(new float[]{modifier}, new int[]{durability});
    }

    /* ---------- 便捷修改（修饰符/工具使用） ---------- */

    public ToolData withDurability(int durability) {
        return new ToolData(durability, harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }

    public ToolData withHarvestLevel(int harvestLevel) {
        return new ToolData(durability, harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }

    public ToolData withAttack(float attack) {
        return new ToolData(durability, harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }

    public ToolData withSpeed(float speed) {
        return new ToolData(durability, harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }

    public ToolData withAttackSpeedMultiplier(float attackSpeedMultiplier) {
        return new ToolData(durability, harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }

    public ToolData withModifiers(int modifiers) {
        return new ToolData(durability, harvestLevel, attack, speed, attackSpeedMultiplier, modifiers);
    }
}
