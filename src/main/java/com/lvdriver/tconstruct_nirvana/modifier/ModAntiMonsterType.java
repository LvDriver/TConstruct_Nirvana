package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 针对特定生物类型的增强伤害（1:1 移植自 Tinkers' Antique {@code ModAntiMonsterType}）。
 * 每级 +7 点伤害（对目标类型）；bane_of_arthopods=节肢动物、smite=亡灵。
 */
public class ModAntiMonsterType extends Modifier {

    /** 目标生物类型 tag。 */
    private final TagKey<EntityType<?>> targetTag;
    /** 每级所需材料数（决定 current 值）。 */
    private final int countPerLevel;

    public ModAntiMonsterType(String identifier, int color, int maxLevel, int countPerLevel,
                              TagKey<EntityType<?>> targetTag) {
        super(identifier);
        this.targetTag = targetTag;
        this.countPerLevel = countPerLevel;
        aspects.add(new ModifierAspect.MultiAspect(identifier, color, maxLevel, countPerLevel, 1));
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage,
                        float newDamage, boolean isCritical) {
        if (target.getType().is(targetTag)) {
            ModifierData data = ToolHelper.getModifierData(tool, identifier);
            int level = data != null ? data.level() : 0;
            // 1:1 旧版：current × (7/countPerLevel)，current = level × countPerLevel → 每级 +7
            return newDamage + level * 7f;
        }
        return newDamage;
    }
}
