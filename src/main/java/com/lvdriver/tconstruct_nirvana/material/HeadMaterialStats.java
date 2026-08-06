package com.lvdriver.tconstruct_nirvana.material;

/**
 * 头部属性：决定工具的耐久、采掘等级、攻击力与采掘速度。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code HeadMaterialStats}。
 * 构造参数顺序与旧版一致：{@code (durability, miningspeed, attack, harvestLevel)}。</p>
 *
 * @param durability    基础耐久（通常 1~1000）
 * @param miningspeed   采掘速度（通常 1~10）
 * @param attack        攻击力加成（半心为单位，除以 2 为心的伤害）
 * @param harvestLevel  采掘等级（见 {@code HarvestLevels}：0=石 1=铁 2=钻石 3=黑曜石 4=钴）
 */
public record HeadMaterialStats(int durability, float miningspeed, float attack, int harvestLevel) implements IMaterialStats {

    @Override
    public String getIdentifier() {
        return MaterialTypes.HEAD;
    }
}
