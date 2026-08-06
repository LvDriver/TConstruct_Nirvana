package com.lvdriver.tconstruct_nirvana.item.part;

import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * 碎块（1:1 移植自 Tinkers' Antique {@code Shard}）。
 *
 * <p>任何具备 head 属性且可制作/可浇铸的材料都能做碎块；
 * 碎块用于部件加工台制作部件的原料（1 碎块 + 1 锭）。无属性 tooltip。</p>
 */
public class Shard extends ToolPart {

    public Shard(Properties properties) {
        super(properties, Material.VALUE_Shard, MaterialTypes.HEAD);
    }

    @Override
    public boolean canUseMaterial(Material mat) {
        return mat.hasStats(MaterialTypes.HEAD) && (mat.isCraftable() || mat.isCastable());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // 旧版 Shard 无 stats tooltip
    }
}
