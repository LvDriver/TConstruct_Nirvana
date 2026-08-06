package com.lvdriver.tconstruct_nirvana.item.tool.melee;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;

import java.util.List;

/**
 * 长剑（1:1 移植自 Tinkers' Antique {@code LongSword}）。
 * 部件：工具杆 + 剑刃 + 手护手；伤害倍率 1.1、攻速 1.4、伤害削减阈值 18、耐久 ×1.05。
 * 右击蓄力冲刺在本会话以简化版实现（无冲刺位移）。
 */
public class LongSword extends SwordToolItem {

    public static final float DURABILITY_MODIFIER = 1.05f;

    public LongSword() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.SWORD_BLADE.get()),
                PartMaterialType.extra(ModToolParts.HAND_GUARD.get()));
        addCategory(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 1.1f;
    }

    @Override
    public double attackSpeed() {
        return 1.4;
    }

    @Override
    public float damageCutoff() {
        return 18f;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
        return 200;
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return DURABILITY_MODIFIER;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        ToolData data = buildDefaultTag(materials);
        // 攻击 +0.5（1:1 旧版）
        data = data.withAttack(data.attack() + 0.5f);
        return data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
    }
}
