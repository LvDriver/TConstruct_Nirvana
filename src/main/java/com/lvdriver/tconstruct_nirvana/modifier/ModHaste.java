package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 急速（1:1 移植自 Tinkers' Antique {@code ModHaste}）。
 * 5 级（每级 50 红石）；挖掘速度分段加成 + 每级平坦 +0.5；
 * 武器攻速 +0.2×level；弓拉弓速度 +0.1×level；弹射物不可用。
 */
public class ModHaste extends Modifier {

    private final int max;

    public ModHaste(int max) {
        super("haste");
        this.max = max;
        aspects.add(new ModifierAspect.MultiAspect(identifier, 0x910000, 5, max, 1));
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        int level = data.level();
        boolean harvest = false;
        boolean weapon = false;
        boolean launcherCategory = false;
        if (stack.getItem() instanceof com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem tool) {
            harvest = tool.hasCategory(com.lvdriver.tconstruct_nirvana.item.tool.Category.HARVEST);
            weapon = tool.hasCategory(com.lvdriver.tconstruct_nirvana.item.tool.Category.WEAPON);
            launcherCategory = tool.hasCategory(com.lvdriver.tconstruct_nirvana.item.tool.Category.LAUNCHER);
        }

        ToolData toolData = ToolHelper.getToolData(stack);

        if (harvest) {
            float speed = toolData.speed();
            // 每级逐次分段加成（1:1 旧版 applyHarvestBoost）
            for (int count = level; count > 0; count--) {
                if (speed <= 15f) {
                    speed += 0.15f - 0.05f * speed / 15f;
                } else if (speed <= 25f) {
                    speed += 0.1f - 0.05f * (speed - 15f) / (25f - 15f);
                } else {
                    speed += 0.05f;
                }
            }
            // 每完整级平坦 +0.5
            speed += level * 0.5f;
            toolData = toolData.withSpeed(speed);
        }

        if (weapon) {
            // 攻速：每级 +0.2
            toolData = toolData.withAttackSpeedMultiplier(toolData.attackSpeedMultiplier() + getSpeedBonus(level));
        }

        ToolHelper.setToolData(stack, toolData);

        // 弓拉弓速度：每级 +10%（1:1 旧版 drawSpeed += drawSpeed × 0.1 × level）
        if (launcherCategory) {
            com.lvdriver.tconstruct_nirvana.data.LauncherData launcherData = stack.getOrDefault(
                    com.lvdriver.tconstruct_nirvana.data.ModDataComponents.LAUNCHER_DATA,
                    new com.lvdriver.tconstruct_nirvana.data.LauncherData(1f, 1f, 0f));
            float drawSpeed = launcherData.drawSpeed() * (1f + getDrawspeedBonus(level));
            stack.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.LAUNCHER_DATA,
                    new com.lvdriver.tconstruct_nirvana.data.LauncherData(drawSpeed, launcherData.range(), launcherData.bonusDamage()));
        }
    }

    protected float getSpeedBonus(int level) {
        return 0.2f * level;
    }

    protected float getDrawspeedBonus(int level) {
        return 0.1f * level;
    }

    @Override
    protected boolean canApplyCustom(ItemStack stack) {
        // 弹射物不可用（1:1 旧版 NO_MELEE 检查）
        return !(stack.getItem() instanceof com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem tool
                && tool.hasCategory(com.lvdriver.tconstruct_nirvana.item.tool.Category.PROJECTILE));
    }
}
