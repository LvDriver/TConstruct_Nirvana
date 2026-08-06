package com.lvdriver.tconstruct_nirvana.item.part;

import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 弩芯（1:1 移植自 Tinkers' Antique {@code BoltCore} 的简化版）。
 *
 * <p>旧版 BoltCore 为双材料部件（核心材料 + 头材料，各占一半价值）。
 * 简化实现：单一材料（弩矢主体），弩矢组装时头部复用该材料
 * （head 属性取同材料的 HEAD stats）。完整双材料逻辑留待后续。</p>
 */
public class BoltCore extends ToolPart {

    public BoltCore(Item.Properties properties, int cost) {
        super(properties, cost, new String[]{com.lvdriver.tconstruct_nirvana.material.MaterialTypes.SHAFT});
    }

    /** 头材料（简化：与主体同材料）。 */
    public Material getHeadMaterial(ItemStack stack) {
        return getMaterial(stack);
    }
}
