package com.lvdriver.tconstruct_nirvana.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 弓弩投射数据（1:1 移植自 Tinkers' Antique {@code ProjectileLauncherNBT} 的弓身部分）。
 *
 * <p>由弓臂材料（{@code BowMaterialStats}）计算：drawSpeed（拉弓速度系数）、
 * range（射程系数）、bonusDamage（基础弹射伤害加成）。存储于独立组件
 * {@code LAUNCHER_DATA}（ToolData 不承载投射字段）。</p>
 */
public record LauncherData(float drawSpeed, float range, float bonusDamage) {

    public static final Codec<LauncherData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("draw_speed").forGetter(LauncherData::drawSpeed),
            Codec.FLOAT.fieldOf("range").forGetter(LauncherData::range),
            Codec.FLOAT.fieldOf("bonus_damage").forGetter(LauncherData::bonusDamage)
    ).apply(instance, LauncherData::new));

    /** 弓臂属性合成（1:1 旧版 {@code ProjectileLauncherNBT.limb}：平均，最小值 0.001）。 */
    public static LauncherData limb(float[] drawSpeed, float[] range, float[] bonusDamage) {
        int count = Math.max(1, drawSpeed.length);
        float ds = 0, r = 0, bd = 0;
        for (int i = 0; i < count; i++) {
            ds += drawSpeed[i];
            r += range[i];
            bd += bonusDamage[i];
        }
        return new LauncherData(Math.max(0.001f, ds / count), Math.max(0.001f, r / count), bd / count);
    }
}
