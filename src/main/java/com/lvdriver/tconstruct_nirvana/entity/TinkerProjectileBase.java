package com.lvdriver.tconstruct_nirvana.entity;

import com.lvdriver.tconstruct_nirvana.data.LauncherData;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.BowToolItem;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

import java.util.List;

/**
 * 匠魂弹射物基类（1:1 移植自 Tinkers' Antique {@code EntityProjectileBase}）。
 *
 * <p>继承 {@link AbstractArrow} 复用其物理/碰撞/拾取协议，自定义部分：</p>
 * <ul>
 *   <li>伤害结算 1:1：{@code (弹射物攻击力 + 弓基础伤害×power + bonusDamage) × damageModifier × power}，
 *       命中后 {@code setArrowCount+1}（旧版 EntityArrow/EntityBolt 行为）；</li>
 *   <li>未造成伤害时按 {@link #bounceOnNoDamage} 反弹或消失（旧版 bounceOff 逻辑）；</li>
 *   <li>空气阻力差异通过 {@link #getAirResistance()} 修正（1.21.1 默认 0.99 硬编码）；</li>
 *   <li>重力经 {@link #getDefaultGravity()} 覆写（旧版 getGravity，Bolt 0.065 / Shuriken 动态）。</li>
 * </ul>
 */
public abstract class TinkerProjectileBase extends AbstractArrow {

    /** 发射力量（旧版 ProjectileLauncherNBT.power / ProjectileCore.power，乘算伤害）。 */
    protected float power = 1f;

    /** 发射工具栈（弓/弩/手里剑自身，用于 bonusDamage 与 damageModifier 结算）。 */
    protected ItemStack launchingStack = ItemStack.EMPTY;

    /** 未造成伤害时是否反弹（旧版 EntityProjectileBase.bounceOnNoDamage，手里剑为 false）。 */
    protected boolean bounceOnNoDamage = true;

    protected TinkerProjectileBase(EntityType<? extends TinkerProjectileBase> entityType, Level level) {
        super(entityType, level);
    }

    /**
     * 玩家发射构造（1:1 旧版 {@code EntityProjectileBase(World, EntityPlayer, speed, inaccuracy, power, stack, launchingStack)}）。
     * 拾取状态：创造 CREATIVE_ONLY，否则 ALLOWED；携带弹药栈由发射侧按"一单位弹药"构造。
     */
    protected TinkerProjectileBase(EntityType<? extends TinkerProjectileBase> entityType, Level level, Player owner,
                                   float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(entityType, owner, level, stack, launchingStack);
        this.power = power;
        this.launchingStack = launchingStack;
        this.pickup = owner.getAbilities().instabuild ? Pickup.CREATIVE_ONLY : Pickup.ALLOWED;
        this.shootFromRotation(owner, owner.getXRot(), owner.getYRot(), 0.0F, speed, inaccuracy);
    }

    /* ---------- 物理参数（1:1 旧版 getGravity / getSlowdown） ---------- */

    /** 空气阻力（1.21.1 默认 0.99 硬编码，子类按旧版 getSlowdown 覆写：Bolt 0.985 / Shuriken 0.95）。 */
    protected double getAirResistance() {
        return 0.99d;
    }

    /** 重力（旧版 getGravity，默认 0.05 与原版一致；Bolt 0.065、Shuriken 动态在子类覆写）。 */
    @Override
    protected double getDefaultGravity() {
        return 0.05d;
    }

    @Override
    public void tick() {
        super.tick();
        // 1.21.1 空气阻力 0.99 无覆写点：按旧版 getSlowdown 差值在 super 后修正（仅飞行中）
        double ratio = getAirResistance() / 0.99d;
        if (!this.inGround && ratio != 1.0d) {
            this.setDeltaMovement(this.getDeltaMovement().scale(ratio));
        }
    }

    /* ---------- 命中结算（1:1 旧版 onHitEntity + ProjectileCore.dealDamageRanged） ---------- */

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity owner = this.getOwner();
        ItemStack ammo = this.getPickupItemStackOrigin();

        // 1:1 旧版 AmmoHelper.getMatchingItemstackFromInventory：用背包中实际弹药栈结算
        // （发射后弹药耐久/损坏状态可能变化），找不到则用实体携带栈
        ItemStack inventoryItem = findMatchingAmmoInInventory(ammo, owner);
        if (inventoryItem.isEmpty() || inventoryItem.getItem() != ammo.getItem()) {
            inventoryItem = ammo;
        }
        // 损坏弹药伤害 1（1:1 旧版 toggleBroken 后 broken 工具的 1 点攻击语义；
        // 本项目 getActualAttack 不随 broken 变化，直接按损坏判定）
        float attack = ToolHelper.isBroken(inventoryItem) ? 1f : ToolHelper.getActualAttack(inventoryItem);

        // 1:1 旧版属性叠加：弹射物攻击力(flat) + [弓基础伤害×power + bonusDamage](flat) × damageModifier(base_multiply) × power(multiply)
        float damage = attack;
        ItemStack launcher = this.launchingStack;
        if (!launcher.isEmpty() && launcher.getItem() instanceof BowToolItem bow) {
            LauncherData data = launcher.getOrDefault(ModDataComponents.LAUNCHER_DATA, new LauncherData(1f, 1f, 0f));
            damage += bow.baseProjectileDamage() * power + data.bonusDamage();
            damage *= bow.projectileDamageModifier();
        }
        damage *= power;

        // 着火传递（旧版：命中实体着火 5 秒，末影人除外）
        if (this.isOnFire() && !(target instanceof EnderMan)) {
            target.igniteForSeconds(5.0F);
        }

        boolean dealt = target.hurt(this.damageSources().arrow(this, owner), damage);
        if (dealt) {
            // 旧版 EntityArrow/EntityBolt.onEntityHit：命中生物箭头计数 +1
            if (!this.level().isClientSide && target instanceof LivingEntity living) {
                living.setArrowCount(living.getArrowCount() + 1);
            }
            this.discard();
        } else if (this.bounceOnNoDamage) {
            // 1:1 旧版 bounceOff：反向减速 + 旋转 180°
            this.setDeltaMovement(this.getDeltaMovement().scale(-0.1d));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
        } else {
            this.discard();
        }

        this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
    }

    /* ---------- 背包弹药查找（1:1 旧版 AmmoHelper.getMatchingItemstackFromInventory） ---------- */

    /** 主手/副手 → 背包中与携带弹药同物品同材料的实际栈。 */
    private ItemStack findMatchingAmmoInInventory(ItemStack stack, Entity owner) {
        if (stack.isEmpty() || !(owner instanceof LivingEntity living)) {
            return ItemStack.EMPTY;
        }
        ItemStack main = living.getMainHandItem();
        if (isEqualTinkersItem(main, stack)) {
            return main;
        }
        ItemStack off = living.getOffhandItem();
        if (isEqualTinkersItem(off, stack)) {
            return off;
        }
        if (owner instanceof Player p) {
            for (ItemStack s : p.getInventory().items) {
                if (isEqualTinkersItem(s, stack)) {
                    return s;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    /** 同物品且同材料（1:1 旧版 ToolCore.isEqualTinkersItem 简化：物品 + BASE_MATERIALS 相同）。 */
    private boolean isEqualTinkersItem(ItemStack a, ItemStack b) {
        if (a.isEmpty() || b.isEmpty() || a.getItem() != b.getItem()) {
            return false;
        }
        List<String> ma = a.getOrDefault(ModDataComponents.baseMaterialsType(), List.of());
        List<String> mb = b.getOrDefault(ModDataComponents.baseMaterialsType(), List.of());
        return ma.equals(mb);
    }

    /* ---------- 拾取协议 ---------- */

    @Override
    protected ItemStack getDefaultPickupItem() {
        // 弹药栈由发射侧经构造传入（getPickupItemStackOrigin），默认空
        return ItemStack.EMPTY;
    }

    /* ---------- NBT（旧版 writeEntityToNBT / readEntityFromNBT 的 power 与发射栈） ---------- */

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putFloat("power", this.power);
        if (!this.launchingStack.isEmpty()) {
            tag.put("launching", this.launchingStack.save(this.registryAccess()));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.power = tag.getFloat("power");
        if (tag.contains("launching")) {
            this.launchingStack = ItemStack.parse(this.registryAccess(), tag.getCompound("launching")).orElse(ItemStack.EMPTY);
        }
    }
}
