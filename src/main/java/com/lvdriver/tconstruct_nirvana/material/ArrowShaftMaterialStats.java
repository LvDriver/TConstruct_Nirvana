package com.lvdriver.tconstruct_nirvana.material;

/**
 * 箭杆属性：系数与额外弹药。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code ArrowShaftMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (modifier, bonusAmmo)}。</p>
 *
 * @param modifier  箭杆系数
 * @param bonusAmmo 额外弹药数
 */
public record ArrowShaftMaterialStats(float modifier, int bonusAmmo) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.SHAFT;
    }
}
