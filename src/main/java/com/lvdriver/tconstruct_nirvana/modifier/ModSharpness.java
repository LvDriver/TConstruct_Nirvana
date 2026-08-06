package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 锋利（1:1 移植自 Tinkers' Antique {@code ModSharpness}）。
 * 5 级（每级 50 石英）；基于原始攻击的分段加成 + 每级平坦 +0.25。
 */
public class ModSharpness extends Modifier {

    private final int max;

    public ModSharpness(int max) {
        super("sharpness");
        this.max = max;
        aspects.add(new ModifierAspect.MultiAspect(identifier, 0xfff6f6, 5, max, 1));
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        int level = data.level();
        // 基于原始攻击（1:1 旧版：用 TOOL_DATA_ORIG 的 attack 迭代，再叠加到当前值）
        ToolData base = ToolHelper.getOriginalToolData(stack);
        float attack = base.attack();
        for (int count = level; count > 0; count--) {
            if (attack <= 10f) {
                attack += 0.05f - 0.025f * attack / 10f;
            } else if (attack <= 20f) {
                attack += 0.025f - 0.01f * attack / 20f;
            } else {
                attack += 0.015f;
            }
        }
        attack += level * 0.25f;

        // 叠加到当前攻击（1:1 旧版：attack = 当前值 + 加成差值）
        ToolData toolData = ToolHelper.getToolData(stack);
        float bonus = attack - base.attack();
        ToolHelper.setToolData(stack, toolData.withAttack(toolData.attack() + bonus));
    }
}
