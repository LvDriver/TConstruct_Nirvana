package com.lvdriver.tconstruct_nirvana.material;

/**
 * 弓身属性：拉弓速度、射程与附加伤害。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code BowMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (drawspeed, range, bonusDamage)}。</p>
 *
 * @param drawspeed   拉弓速度系数（旧版显示时取倒数）
 * @param range       射程系数
 * @param bonusDamage 附加伤害（对慢速柔性材料的平伤奖励）
 */
public record BowMaterialStats(float drawspeed, float range, float bonusDamage) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.BOW;
    }
}
