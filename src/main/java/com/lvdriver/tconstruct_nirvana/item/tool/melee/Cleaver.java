package com.lvdriver.tconstruct_nirvana.item.tool.melee;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 斩首刀（1:1 移植自 Tinkers' Antique {@code Cleaver}）。
 * 部件：坚韧工具杆 + 大剑刃 + 大板(HEAD) + 坚韧工具杆(EXTRA)；
 * 双头平均 + 附加 + 手柄；攻击 ×1.3 再 +3、耐久 ×2.0、伤害倍率 1.2、
 * 攻速 0.7、伤害削减阈值 25。
 */
public class Cleaver extends SwordToolItem {

    public static final float DURABILITY_MODIFIER = 2f;

    public Cleaver() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOUGH_TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.LARGE_SWORD_BLADE.get()),
                PartMaterialType.head(ModToolParts.LARGE_PLATE.get()),
                PartMaterialType.extra(ModToolParts.TOUGH_TOOL_ROD.get()));
        addCategory(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 1.2f;
    }

    @Override
    public double attackSpeed() {
        return 0.7d;
    }

    @Override
    public float damageCutoff() {
        return 25f;
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1, 2};
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material handle = materials.get(0);
        Material head = materials.get(1);
        Material shield = materials.get(2);
        Material guard = materials.get(3);

        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handleStats = handle.getStatsOrUnknown(MaterialTypes.HANDLE);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats shieldStats = shield.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats guardStats = guard.getStatsOrUnknown(MaterialTypes.EXTRA);

        ToolData data = ToolData.empty()
                .head(new int[]{headStats.durability(), shieldStats.durability()},
                        new int[]{headStats.harvestLevel(), shieldStats.harvestLevel()},
                        new float[]{headStats.attack(), shieldStats.attack()},
                        new float[]{headStats.miningspeed(), shieldStats.miningspeed()})
                .extra(guardStats.extraDurability())
                .handle(handleStats.modifier(), handleStats.durability());
        // 攻击 ×1.3 再 +3、耐久 ×2.0（1:1 旧版）
        data = data.withAttack(data.attack() * 1.3f + 3f);
        data = data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
        return data.withModifiers(com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem.DEFAULT_MODIFIERS);
    }
}
