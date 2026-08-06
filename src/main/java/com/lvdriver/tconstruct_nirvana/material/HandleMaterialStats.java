package com.lvdriver.tconstruct_nirvana.material;

/**
 * 手柄属性：耐久系数与耐久修正。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code HandleMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (modifier, durability)}。</p>
 *
 * @param modifier   耐久系数（0.0~1.0+，越高手柄越好）
 * @param durability 耐久修正（通常 -500~500，可为负）
 */
public record HandleMaterialStats(float modifier, int durability) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.HANDLE;
    }
}
