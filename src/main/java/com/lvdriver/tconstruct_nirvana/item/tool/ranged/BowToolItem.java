package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.LauncherData;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * 弓弩基类（1:1 移植自 Tinkers' Antique {@code BowCore} 的简化版）。
 *
 * <p>属性计算 1:1（弓臂/弓弦/附加/手柄合成，见 {@link #buildLauncherData}）；
 * 发射行为为简化版：右击蓄力、松开射出原版箭实体（完整自定义弹射物系统
 * 留待后续会话）。弹药消耗原版箭。</p>
 */
public abstract class BowToolItem extends TinkerToolItem {

    protected BowToolItem(Item.Properties properties, PartMaterialType... requiredComponents) {
        super(properties, requiredComponents);
        addCategory(Category.LAUNCHER);
    }

    /* ---------- 弓特有系数（1:1 旧版 BowCore 抽象方法） ---------- */

    /** 基础弹射伤害。 */
    public float baseProjectileDamage() {
        return 0f;
    }

    /** 基础弹射初速。 */
    protected float baseProjectileSpeed() {
        return 3.5f;
    }

    /** 基础不准度。 */
    protected float baseInaccuracy() {
        return 1f;
    }

    /** 弹射伤害倍率。 */
    public float projectileDamageModifier() {
        return 1f;
    }

    /** 拉满所需 tick（1:1 旧版 getDrawTime）。 */
    public abstract int getDrawTime();

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public abstract double attackSpeed();

    /* ---------- 属性合成 ---------- */

    /** 弓身属性合成（1:1 旧版 ProjectileLauncherNBT.limb，子类在 buildItem 链路中使用）。 */
    protected LauncherData buildLauncherData(Material limb1, Material limb2) {
        com.lvdriver.tconstruct_nirvana.material.BowMaterialStats bow1 = limb1.getStatsOrUnknown(MaterialTypes.BOW);
        com.lvdriver.tconstruct_nirvana.material.BowMaterialStats bow2 = limb2.getStatsOrUnknown(MaterialTypes.BOW);
        return LauncherData.limb(
                new float[]{bow1.drawspeed(), bow2.drawspeed()},
                new float[]{bow1.range(), bow2.range()},
                new float[]{bow1.bonusDamage(), bow2.bonusDamage()});
    }

    /** 弓弦耐久修正（1:1 旧版 ProjectileLauncherNBT.bowstring）。 */
    protected ToolData applyBowstring(ToolData data, Material bowstringMat) {
        com.lvdriver.tconstruct_nirvana.material.BowStringMaterialStats bowstring = bowstringMat.getStatsOrUnknown(MaterialTypes.BOWSTRING);
        return data.withDurability(Math.max(1, Math.round(data.durability() * bowstring.modifier())));
    }

    @Override
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = super.buildItem(materials);
        LauncherData launcher = buildLauncherData(materials.get(0), materials.get(1));
        tool.set(ModDataComponents.LAUNCHER_DATA, launcher);
        return tool;
    }

    /* ---------- 发射（简化版） ---------- */

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (ToolHelper.isBroken(stack)) {
            return InteractionResultHolder.fail(stack);
        }
        // 背包中须有箭
        boolean hasAmmo = player.getAbilities().instabuild
                || player.getInventory().items.stream().anyMatch(s -> !s.isEmpty() && s.getItem() == Items.ARROW);
        if (!hasAmmo) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity player, int timeLeft) {
        if (world.isClientSide || !(player instanceof Player p)) {
            return;
        }
        if (ToolHelper.isBroken(stack)) {
            return;
        }
        float progress = Math.min(1f, (float) (getUseDuration(stack, player) - timeLeft) / getDrawTime());
        if (progress < 0.1f) {
            return;
        }

        // 消耗弹药
        if (!p.getAbilities().instabuild) {
            boolean found = false;
            for (ItemStack s : p.getInventory().items) {
                if (!s.isEmpty() && s.getItem() == Items.ARROW) {
                    s.shrink(1);
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }
        }

        // 简化发射：原版箭实体（1:1 完整弹射物系统留待后续会话）
        LauncherData launcher = stack.getOrDefault(ModDataComponents.LAUNCHER_DATA, new LauncherData(1f, 1f, 0f));
        float velocity = baseProjectileSpeed() * (0.5f + progress * 0.5f) * launcher.drawSpeed();
        ItemStack arrowStack = new ItemStack(Items.ARROW);
        AbstractArrow arrow = ((ArrowItem) Items.ARROW).createArrow(world, arrowStack, p, arrowStack);
        arrow.shootFromRotation(p, p.getXRot(), p.getYRot(), 0.0F, velocity, baseInaccuracy());
        arrow.setBaseDamage(baseProjectileDamage() + ToolHelper.getAttackStat(stack) * projectileDamageModifier());
        world.addFreshEntity(arrow);
        world.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);

        // 耐久消耗（1:1 旧版：拉满 1，未拉满 2）
        ToolHelper.damageTool(stack, progress > 0.99f ? 1 : 2, p);
    }
}
