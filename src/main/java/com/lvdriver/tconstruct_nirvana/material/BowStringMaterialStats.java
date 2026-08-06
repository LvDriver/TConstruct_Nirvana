package com.lvdriver.tconstruct_nirvana.material;

/**
 * 弓弦属性：系数。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code BowStringMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (modifier)}。</p>
 *
 * @param modifier 弓弦系数（约 1.0）
 */
public record BowStringMaterialStats(float modifier) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.BOWSTRING;
    }
}
