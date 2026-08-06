package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.network.chat.Component;

/**
 * 分级特质基类（1:1 移植自 Tinkers' Antique {@code AbstractTraitLeveled}）。
 *
 * <p>identifier = 基础名 + 等级后缀（如 {@code crude1}/ {@code crude2}、
 * {@code magnetic1}/ {@code magnetic2}、{@code writable1}/ {@code writable2}），
 * 显示名用基础名 + 罗马数字。每级一次应用，等级上限 maxLevel。</p>
 */
public abstract class TraitLeveled extends Trait {

    /** 基础名（不带等级后缀）。 */
    protected final String name;
    /** 等级（1/2）。 */
    protected final int levels;
    /** 等级上限。 */
    protected final int maxLevel;

    public TraitLeveled(String name, int levels, int color, int maxLevel) {
        super(name + levels, color);
        this.name = name;
        this.levels = levels;
        this.maxLevel = maxLevel;
        this.aspects.clear();
        this.aspects.add(new com.lvdriver.tconstruct_nirvana.modifier.ModifierAspect.LevelAspect(name + levels, maxLevel));
        this.aspects.add(new com.lvdriver.tconstruct_nirvana.modifier.ModifierAspect.DataAspect(name + levels, color));
    }

    /** 本实例对应的等级（用于效果计算）。 */
    public int getLevel() {
        return levels;
    }

    @Override
    public String getLocalizedName() {
        String locName = Component.translatable(LOC_NAME.formatted(name)).getString();
        if (levels > 1) {
            locName += " " + com.lvdriver.tconstruct_nirvana.util.TConUtil.toRoman(levels);
        }
        return locName;
    }

    @Override
    public String getLocalizedDesc() {
        return Component.translatable(LOC_DESC.formatted(name)).getString();
    }
}
