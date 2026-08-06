package com.lvdriver.tconstruct_nirvana.material;

/**
 * 弹射物部件占位属性：无数值字段，仅用于将弹射工具部件与普通部件区分开。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code ProjectileMaterialStats}。</p>
 */
public record ProjectileMaterialStats() implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.PROJECTILE;
    }
}
