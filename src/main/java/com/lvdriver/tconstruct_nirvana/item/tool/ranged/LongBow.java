package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

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
 * 长弓（1:1 移植自 Tinkers' Antique {@code LongBow}）。
 * 部件：弓臂 + 弓臂 + 大板(EXTRA) + 弓弦；攻速 1.3、拉弓 30 tick、
 * 基础弹射伤害 2.5、初速 5.5、不准度 1.2、弹射伤害倍率 1.25、耐久 ×1.4。
 */
public class LongBow extends BowToolItem {

    public static final float DURABILITY_MODIFIER = 1.4f;

    public LongBow() {
        super(new Item.Properties(),
                PartMaterialType.bow(ModToolParts.BOW_LIMB.get()),
                PartMaterialType.bow(ModToolParts.BOW_LIMB.get()),
                PartMaterialType.extra(ModToolParts.LARGE_PLATE.get()),
                PartMaterialType.bowstring(ModToolParts.BOW_STRING.get()));
    }

    @Override
    public double attackSpeed() {
        return 1.3;
    }

    @Override
    public float baseProjectileDamage() {
        return 2.5f;
    }

    @Override
    protected float baseProjectileSpeed() {
        return 5.5f;
    }

    @Override
    protected float baseInaccuracy() {
        return 1.2f;
    }

    @Override
    public float projectileDamageModifier() {
        return 1.25f;
    }

    @Override
    public int getDrawTime() {
        return 30;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material limb1 = materials.get(0);
        Material limb2 = materials.get(1);
        Material grip = materials.get(2);
        Material bowstring = materials.get(3);

        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats head1 = limb1.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats head2 = limb2.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats gripStats = grip.getStatsOrUnknown(MaterialTypes.EXTRA);

        // 1:1 旧版 LongBow buildTagData（head + limb + extra + bowstring，耐久 ×1.4）
        ToolData data = ToolData.empty()
                .head(new int[]{head1.durability(), head2.durability()},
                        new int[]{head1.harvestLevel(), head2.harvestLevel()},
                        new float[]{head1.attack(), head2.attack()},
                        new float[]{head1.miningspeed(), head2.miningspeed()})
                .extra(gripStats.extraDurability());
        data = applyBowstring(data, bowstring);
        data = data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }
}
