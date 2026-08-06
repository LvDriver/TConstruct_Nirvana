package com.lvdriver.tconstruct_nirvana.item.part;

import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;

/**
 * 磨刀石（1:1 移植自 Tinkers' Antique {@code SharpeningKit}）。
 *
 * <p>修复材料载体：cost = 4 碎块（{@link Material#VALUE_Shard} × 4 = 288），
 * 可用材料 = 具备 HEAD 属性的材料。工作台修复配方
 * （{@link com.lvdriver.tconstruct_nirvana.recipe.RepairRecipe}）用
 * 磨刀石携带的材料修复工具的对应部件（{@link com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem#repair}）。</p>
 */
public class SharpeningKit extends ToolPart {

    public SharpeningKit(Item.Properties properties) {
        super(properties, Material.VALUE_Shard * 4);
    }

    @Override
    public boolean canUseMaterial(Material material) {
        return material.hasStats(MaterialTypes.HEAD);
    }
}
