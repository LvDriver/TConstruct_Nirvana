package com.lvdriver.tconstruct_nirvana.material;

/**
 * 附加部件属性：额外耐久。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code ExtraMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (extraDurability)}。</p>
 *
 * @param extraDurability 额外耐久（通常 0~500）
 */
public record ExtraMaterialStats(int extraDurability) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.EXTRA;
    }
}
