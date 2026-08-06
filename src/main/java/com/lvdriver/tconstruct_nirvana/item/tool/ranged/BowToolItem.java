package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.LauncherData;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.entity.TinkerProjectileBase;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
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
 * 弓弩基类（1:1 移植自 Tinkers' Antique {@code BowCore}）。
 *
 * <p>属性计算 1:1（弓臂/弓弦/附加/手柄合成，见 {@link #buildLauncherData}）；
 * 发射 1:1（蓄力 → power 计算 → 弹药消耗 → 自定义弹射物实体）。
 * 弹药：弓用箭、弩用弩矢（{@link #getAmmoItems}）。</p>
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

    /** 基础弹射初速（1:1 旧版 BowCore 默认 3f，子类覆写）。 */
    protected float baseProjectileSpeed() {
        return 3f;
    }

    /** 基础不准度（1:1 旧版 BowCore 默认 0f——弩因此 0 不准度，子类覆写）。 */
    protected float baseInaccuracy() {
        return 0f;
    }

    /** 弹射伤害倍率。 */
    public float projectileDamageModifier() {
        return 1f;
    }

    /** 拉满所需 tick（1:1 旧版 getDrawTime）。 */
    public abstract int getDrawTime();

    /** 弹药物品列表（1:1 旧版 getAmmoItems：弓 → 箭，弩 → 弩矢）。 */
    protected abstract List<Item> getAmmoItems();

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

    /* ---------- 使用（1:1 旧版 BowCore use / onPlayerStoppedUsing） ---------- */

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
        boolean hasAmmo = !findAmmo(stack, player).isEmpty();
        if (player.getAbilities().instabuild || hasAmmo) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
        if (world.isClientSide || !(entity instanceof Player p)) {
            return;
        }
        if (ToolHelper.isBroken(stack)) {
            return;
        }
        ItemStack ammo = findAmmo(stack, p);
        if (ammo.isEmpty() && !p.getAbilities().instabuild) {
            return;
        }

        int useTime = this.getUseDuration(stack, entity) - timeLeft;
        if (useTime < 5) {
            return;
        }

        if (ammo.isEmpty()) {
            ammo = getCreativeProjectileStack();
        }

        shootProjectile(ammo, stack, world, p, useTime);
    }

    /* ---------- 弹药（1:1 旧版 AmmoHelper / getCreativeProjectileStack） ---------- */

    /**
     * 背包中第一个匹配弹药（1:1 旧版 {@code AmmoHelper.findAmmoFromInventory}）：
     * 主手/副手 → 快捷栏 → 其余背包；TCon 弹药须弹药数 &gt; 0。
     */
    public ItemStack findAmmo(ItemStack weapon, LivingEntity player) {
        if (!(player instanceof Player p)) {
            return ItemStack.EMPTY;
        }
        ItemStack main = p.getMainHandItem();
        if (isValidAmmo(main)) {
            return main;
        }
        ItemStack off = p.getOffhandItem();
        if (isValidAmmo(off)) {
            return off;
        }
        for (int i = 0; i < p.getInventory().items.size(); i++) {
            ItemStack s = p.getInventory().items.get(i);
            if (isValidAmmo(s)) {
                return s;
            }
        }
        return ItemStack.EMPTY;
    }

    /** 弹药有效性：类型匹配且（TCon 弹药）弹药数 &gt; 0（1:1 旧版 validAmmoInRange）。 */
    private boolean isValidAmmo(ItemStack stack) {
        if (stack.isEmpty() || !getAmmoItems().contains(stack.getItem())) {
            return false;
        }
        if (stack.getItem() instanceof ProjectileToolItem projectile) {
            return projectile.getCurrentAmmo(stack) > 0;
        }
        return true;
    }

    /** 创造模式无弹药时的兜底弹药（1:1 旧版：原版箭）。 */
    protected ItemStack getCreativeProjectileStack() {
        return new ItemStack(Items.ARROW);
    }

    /** 消耗弹药（1:1 旧版 consumeAmmo：创造不消耗；TCon 弹药用 useAmmo，原版 shrink）。 */
    public boolean consumeAmmo(ItemStack ammo, Player player) {
        if (player.getAbilities().instabuild) {
            return false;
        }
        if (ammo.getItem() instanceof ProjectileToolItem projectile) {
            return projectile.useAmmo(ammo, player);
        }
        ammo.shrink(1);
        return true;
    }

    /* ---------- 发射（1:1 旧版 BowCore.shootProjectile） ---------- */

    /** 蓄力进度（1:1 旧版 getDrawbackProgress：drawSpeed × 用时 / drawTime，上限 1）。 */
    public float getDrawbackProgress(ItemStack stack, int timePassed) {
        LauncherData launcher = stack.getOrDefault(ModDataComponents.LAUNCHER_DATA, new LauncherData(1f, 1f, 0f));
        return Math.min(1f, launcher.drawSpeed() * timePassed / (float) getDrawTime());
    }

    public void shootProjectile(ItemStack ammoIn, ItemStack bow, Level world, Player player, int useTime) {
        float progress = getDrawbackProgress(bow, useTime);
        LauncherData launcher = bow.getOrDefault(ModDataComponents.LAUNCHER_DATA, new LauncherData(1f, 1f, 0f));
        // 1:1 旧版：power = ItemBow.getArrowVelocity(progress×20) × progress × baseProjectileSpeed × range
        // getArrowVelocity(f) = (f² + 2f) / 3，f = progress
        float f = progress;
        float power = (f * f + 2f * f) / 3f * progress * baseProjectileSpeed() * launcher.range();

        if (!world.isClientSide) {
            // 复制弹药栈（consumeAmmo 可能删掉原栈）
            ItemStack ammoStackToShoot = ammoIn.copy();
            boolean usedAmmo = consumeAmmo(ammoIn, player);
            float inaccuracy = baseInaccuracy();

            AbstractArrow projectile = getProjectileEntity(ammoStackToShoot, bow, world, player, power, inaccuracy, progress, usedAmmo);
            if (projectile != null) {
                if (progress >= 1f) {
                    projectile.setCritArrow(true);
                }
                if (!player.getAbilities().instabuild) {
                    ToolHelper.damageTool(bow, 1, player);
                }
                world.addFreshEntity(projectile);
            }
        }

        playShootSound(power, world, player);
    }

    /**
     * 创建弹射物实体（1:1 旧版 getProjectileEntity）：
     * TCon 弹药 → 各弹射物实体；原版箭 → 原版箭实体（创造兜底）。
     */
    public AbstractArrow getProjectileEntity(ItemStack ammo, ItemStack bow, Level world, Player player,
                                             float power, float inaccuracy, float progress, boolean usedAmmo) {
        if (ammo.getItem() instanceof ProjectileToolItem projectile) {
            return projectile.getProjectile(ammo, bow, world, player, power, inaccuracy, power, usedAmmo);
        }
        if (ammo.getItem() instanceof ArrowItem arrowItem) {
            AbstractArrow arrow = arrowItem.createArrow(world, ammo, player, bow);
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power, inaccuracy);
            if (player.getAbilities().instabuild) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            } else if (!usedAmmo) {
                arrow.pickup = AbstractArrow.Pickup.DISALLOWED;
            }
            return arrow;
        }
        return null;
    }

    /** 射击音效（1:1 旧版 playShootSound）。 */
    public void playShootSound(float power, Level world, Player player) {
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS,
                1.0F, 1.0F / (player.getRandom().nextFloat() * 0.4F + 1.2F) + power * 0.5F);
    }
}
