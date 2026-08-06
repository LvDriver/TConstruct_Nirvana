package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 箭（1:1 移植自 Tinkers' Antique {@code Arrow}）。
 * 部件：箭杆 + 箭头 + 箭羽；攻击 +2；伤害倍率 1.0、攻速 1.0。
 */
public class Arrow extends ProjectileToolItem {

    public Arrow() {
        super(new Item.Properties(),
                PartMaterialType.arrowShaft(ModToolParts.ARROW_SHAFT.get()),
                PartMaterialType.arrowHead(ModToolParts.ARROW_HEAD.get()),
                PartMaterialType.fletching(ModToolParts.FLETCHING.get()));
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public double attackSpeed() {
        return 1;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material shaft = materials.get(0);
        Material head = materials.get(1);
        Material fletching = materials.get(2);

        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown(MaterialTypes.HEAD);

        // 1:1 旧版 Arrow buildTagData（head + fletchings + shafts，攻击 +2）
        ToolData data = ToolData.empty()
                .head(headStats.durability(), headStats.harvestLevel(), headStats.attack(), headStats.miningspeed());
        data = applyFletchings(data, fletching);
        data = applyShafts(data, shaft);
        data = data.withAttack(data.attack() + 2f);
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = super.buildItem(materials);
        // 精准度由箭羽决定（1:1 旧版 fletchings 合成）
        Material fletching = materials.get(2);
        com.lvdriver.tconstruct_nirvana.material.FletchingMaterialStats stats = fletching.getStatsOrUnknown(MaterialTypes.FLETCHING);
        tool.set(ModDataComponents.ACCURACY, Math.min(1f, Math.max(0, stats.accuracy())));
        return tool;
    }
}
