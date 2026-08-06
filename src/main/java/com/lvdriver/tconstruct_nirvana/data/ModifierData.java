package com.lvdriver.tconstruct_nirvana.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * 修饰符/特质实例数据（1:1 移植自 Tinkers' Antique {@code ModifierNBT}，DataComponent 版）。
 *
 * <p>每个应用到工具上的修饰符对应一条记录：identifier（注册标识）、level（等级，
 * 多级修饰符/特质可能大于 1）、color（tooltip 显示色）、extraInfo（附加信息字符串，
 * 旧版用于显示额外数值）。</p>
 */
public record ModifierData(String identifier, int level, int color, String extraInfo) {

    public static final Codec<ModifierData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("identifier").forGetter(ModifierData::identifier),
            Codec.INT.fieldOf("level").forGetter(ModifierData::level),
            Codec.INT.fieldOf("color").forGetter(ModifierData::color),
            Codec.STRING.optionalFieldOf("extra_info", "").forGetter(ModifierData::extraInfo)
    ).apply(instance, ModifierData::new));

    public ModifierData(String identifier) {
        this(identifier, 1, 0xffffff, "");
    }

    public ModifierData withLevel(int level) {
        return new ModifierData(identifier, level, color, extraInfo);
    }

    public ModifierData withColor(int color) {
        return new ModifierData(identifier, level, color, extraInfo);
    }

    public ModifierData withExtraInfo(String extraInfo) {
        return new ModifierData(identifier, level, color, extraInfo);
    }
}
