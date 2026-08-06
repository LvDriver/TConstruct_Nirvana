package com.lvdriver.tconstruct_nirvana.material;

/**
 * 箭羽属性：精准度与系数。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code FletchingMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (accuracy, modifier)}。</p>
 *
 * @param accuracy 精准度系数
 * @param modifier 箭羽系数
 */
public record FletchingMaterialStats(float accuracy, float modifier) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.FLETCHING;
    }
}
