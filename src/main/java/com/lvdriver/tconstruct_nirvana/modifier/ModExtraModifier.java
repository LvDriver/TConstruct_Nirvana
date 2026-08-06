package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 额外强化槽（1:1 移植自 Tinkers' Antique {@code ModExtraModifier}，变量名 endearment）。
 * 注意 identifier 为 {@code extramodifier}（旧版如此）；单次应用、不耗槽；
 * 效果：免费强化槽 +level（每次应用 +1）。
 */
public class ModExtraModifier extends Modifier {

    public ModExtraModifier() {
        super("extramodifier");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xA300cc));
        // 不消耗强化槽
    }

    @Override
    public ModifierData updateNBT(ModifierData data) {
        // 1:1 旧版：手动 level++
        return data.withLevel(data.level() + 1);
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        // 免费强化槽 +level
        ToolData toolData = ToolHelper.getToolData(stack);
        ToolHelper.setToolData(stack, toolData.withModifiers(toolData.modifiers() + data.level()));
    }
}
