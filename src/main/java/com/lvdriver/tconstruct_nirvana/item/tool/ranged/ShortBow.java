package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.LauncherData;
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
 * 短弓（1:1 移植自 Tinkers' Antique {@code ShortBow}）。
 * 部件：弓臂 + 弓臂 + 弓弦；伤害倍率 0.7、攻速 1.5、拉弓 12 tick、
 * 弹射伤害倍率 0.8、初速 3.5、不准度 1.0；移动时减速减半（简化：不实现）。
 */
public class ShortBow extends BowToolItem {

    public ShortBow() {
        super(new Item.Properties(),
                PartMaterialType.bow(ModToolParts.BOW_LIMB.get()),
                PartMaterialType.bow(ModToolParts.BOW_LIMB.get()),
                PartMaterialType.bowstring(ModToolParts.BOW_STRING.get()));
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{0, 1};
    }

    @Override
    public float damagePotential() {
        return 0.7f;
    }

    @Override
    public double attackSpeed() {
        return 1.5;
    }

    @Override
    public float projectileDamageModifier() {
        return 0.8f;
    }

    @Override
    public int getDrawTime() {
        return 12;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material limb1 = materials.get(0);
        Material limb2 = materials.get(1);
        Material bowstring = materials.get(2);

        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats head1 = limb1.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats head2 = limb2.getStatsOrUnknown(MaterialTypes.HEAD);

        // 1:1 旧版 ShortBow buildTagData（head 平均 + bowstring 修正，无手柄）
        ToolData data = ToolData.empty()
                .head(new int[]{head1.durability(), head2.durability()},
                        new int[]{head1.harvestLevel(), head2.harvestLevel()},
                        new float[]{head1.attack(), head2.attack()},
                        new float[]{head1.miningspeed(), head2.miningspeed()});
        return applyBowstring(data, bowstring).withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }
}
