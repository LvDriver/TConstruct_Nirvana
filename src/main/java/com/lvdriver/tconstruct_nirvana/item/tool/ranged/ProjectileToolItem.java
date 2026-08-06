package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 弹射物基类（1:1 移植自 Tinkers' Antique {@code ProjectileCore} 的简化版）。
 *
 * <p>箭/弩矢/手里剑：属性计算 1:1（头部/箭杆/箭羽合成 + 精准度），
 * 物品注册与属性展示可用；自定义弹射物实体与投掷行为留待后续会话。</p>
 */
public abstract class ProjectileToolItem extends TinkerToolItem {

    protected ProjectileToolItem(Item.Properties properties, PartMaterialType... requiredComponents) {
        super(properties, requiredComponents);
        addCategory(Category.PROJECTILE);
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public double attackSpeed() {
        return 1;
    }

    /** 每个弹药单位对应的耐久（1:1 旧版 ProjectileCore.getDurabilityPerAmmo，默认 1）。 */
    public int getDurabilityPerAmmo() {
        return 1;
    }

    /**
     * 箭杆属性合成（1:1 旧版 ProjectileNBT.shafts）：
     * 耐久 = round(耐久 × 平均系数) + round(平均 bonusAmmo × 每弹药耐久)。
     */
    protected ToolData applyShafts(ToolData data, Material shaftMat) {
        com.lvdriver.tconstruct_nirvana.material.ArrowShaftMaterialStats shaft = shaftMat.getStatsOrUnknown(MaterialTypes.SHAFT);
        int dur = shaft.bonusAmmo() * getDurabilityPerAmmo();
        int newDur = Math.round(data.durability() * shaft.modifier());
        newDur += Math.round((float) dur);
        return data.withDurability(Math.max(1, newDur));
    }

    /**
     * 箭羽属性合成（1:1 旧版 ProjectileNBT.fletchings）：
     * 精准度 = clamp(平均 accuracy, 0, 1)；耐久 × 平均系数。
     */
    protected ToolData applyFletchings(ToolData data, Material fletchingMat) {
        com.lvdriver.tconstruct_nirvana.material.FletchingMaterialStats fletching = fletchingMat.getStatsOrUnknown(MaterialTypes.FLETCHING);
        float accuracy = Math.min(1f, Math.max(0, fletching.accuracy()));
        int newDur = Math.round(data.durability() * fletching.modifier());
        return data.withDurability(Math.max(1, newDur));
    }

    /** 组装时写入精准度组件。 */
    @Override
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = super.buildItem(materials);
        tool.set(ModDataComponents.ACCURACY, 1f);
        return tool;
    }
}
