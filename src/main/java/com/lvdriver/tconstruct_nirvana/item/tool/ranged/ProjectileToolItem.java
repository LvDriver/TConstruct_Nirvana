package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.entity.TinkerProjectileBase;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

import javax.annotation.Nullable;

/**
 * 弹射物基类（1:1 移植自 Tinkers' Antique {@code ProjectileCore}）。
 *
 * <p>弹射物是"有弹药的工具"：耐久抽象为弹药，每发消耗
 * {@value #DURABILITY_PER_AMMO} 耐久（1:1 旧版 durabilityPerAmmo=10）。
 * 属性计算 1:1（头部/箭杆/箭羽合成 + 精准度）；发射实体由子类
 * {@link #getProjectile} 创建（1:1 旧版 IProjectile.getProjectile）。</p>
 */
public abstract class ProjectileToolItem extends TinkerToolItem {

    /** 每个弹药单位对应的耐久（1:1 旧版 ProjectileCore.durabilityPerAmmo）。 */
    public static final int DURABILITY_PER_AMMO = 10;

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

    /** 每个弹药单位对应的耐久（1:1 旧版 ProjectileCore.getDurabilityPerAmmo）。 */
    public int getDurabilityPerAmmo() {
        return DURABILITY_PER_AMMO;
    }

    /* ---------- 弹药模型（1:1 旧版 ProjectileCore：耐久 / durabilityPerAmmo = 弹药） ---------- */

    /** 当前弹药数（损坏为 0）。 */
    public int getCurrentAmmo(ItemStack stack) {
        if (ToolHelper.isBroken(stack)) {
            return 0;
        }
        return (stack.getMaxDamage() - stack.getDamageValue()) / DURABILITY_PER_AMMO;
    }

    /** 最大弹药数。 */
    public int getMaxAmmo(ItemStack stack) {
        return stack.getMaxDamage() / DURABILITY_PER_AMMO;
    }

    /** 设置弹药数（1:1 旧版 setAmmo：损伤 0 = 满弹药）。 */
    public void setAmmo(int count, ItemStack stack) {
        stack.setDamageValue((getMaxAmmo(stack) - count) * DURABILITY_PER_AMMO);
    }

    /** 补充一发弹药（1:1 旧版 addAmmo：healTool durabilityPerAmmo）。 */
    public boolean addAmmo(ItemStack stack, LivingEntity player) {
        int ammo = getCurrentAmmo(stack);
        if (ammo < getMaxAmmo(stack)) {
            ToolHelper.repairTool(stack, DURABILITY_PER_AMMO, player);
            return true;
        }
        return false;
    }

    /**
     * 消耗一发弹药（1:1 旧版 useAmmo）。
     *
     * @return 是否实际消耗（创造/黑曜石类防损特质可能不消耗）
     */
    public boolean useAmmo(ItemStack stack, @Nullable LivingEntity player) {
        int ammo = getCurrentAmmo(stack);
        if (ammo > 0) {
            ToolHelper.damageTool(stack, DURABILITY_PER_AMMO, player);
            int newAmmo = getCurrentAmmo(stack);
            if (newAmmo <= 0) {
                ToolHelper.breakTool(stack, player);
            }
            return newAmmo < ammo;
        }
        return false;
    }

    /**
     * 构造发射用弹药栈（1:1 旧版 getProjectileStack）：
     * 复制 1 个、设为一发弹药（耐久 = 最大-10）；非创造且实际消耗了弹药 → 满耐久（拾取不白嫖耐久）；
     * 永不 broken（防止拾取时正反馈循环）。
     */
    public ItemStack getProjectileStack(ItemStack itemStack, Player player, boolean usedAmmo) {
        ItemStack reference = itemStack.copy();
        reference.setCount(1);
        setAmmo(1, reference);

        if (!player.getAbilities().instabuild && !player.level().isClientSide && !usedAmmo) {
            setAmmo(0, reference);
        }

        // 永不 broken（1:1 旧版 ToolHelper.unbreakTool）
        reference.remove(ModDataComponents.BROKEN);
        return reference;
    }

    /** 耐久条按弹药显示（1:1 旧版 showDurabilityBar/getDurabilityForDisplay 反向）。 */
    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getMaxAmmo(stack) != getCurrentAmmo(stack);
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int max = getMaxAmmo(stack);
        return max == 0 ? 0 : Math.round(13f * getCurrentAmmo(stack) / max);
    }

    /* ---------- 弹射物实体工厂（1:1 旧版 IProjectile.getProjectile） ---------- */

    /**
     * 创建发射实体（子类实现：箭/弩矢/手里剑各自实体类型 + 精准度修正）。
     */
    public abstract TinkerProjectileBase getProjectile(ItemStack stack, ItemStack launcher, Level world, Player player,
                                                       float speed, float inaccuracy, float power, boolean usedAmmo);

    /* ---------- 属性合成（1:1 旧版 ProjectileNBT.shafts / fletchings） ---------- */

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

    /* ---------- 信息显示（1:1 旧版 ProjectileCore.getInformation：弹药 + 精准度） ---------- */

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<net.minecraft.network.chat.Component> tooltip, net.minecraft.world.item.TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.ammo", getCurrentAmmo(stack), getMaxAmmo(stack)));
        float accuracy = stack.getOrDefault(ModDataComponents.ACCURACY, 1f);
        tooltip.add(net.minecraft.network.chat.Component.translatable("tooltip.accuracy", Math.round(accuracy * 100)));
    }
}
