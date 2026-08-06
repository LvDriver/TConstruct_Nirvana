package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import net.minecraft.world.item.ItemStack;

/**
 * 收获范围扩展（1:1 移植自 Tinkers' Antique {@code ModHarvestSize}）。
 * harvestwidth / harvestheight 共用；单次应用；仅限范围工具（AOE）。
 * 实际范围扩展由 AOE 逻辑（TinkerToolItem 子类）读取本修饰符决定。
 */
public class ModHarvestSize extends Modifier {

    private final String direction;

    public ModHarvestSize(String identifier, String direction, int color) {
        super(identifier);
        this.direction = direction;
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, color));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    public String getDirection() {
        return direction;
    }
}
