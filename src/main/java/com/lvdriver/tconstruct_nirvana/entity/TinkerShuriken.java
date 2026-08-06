package com.lvdriver.tconstruct_nirvana.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 手里剑弹射物（1:1 移植自 Tinkers' Antique {@code EntityShuriken}）。
 * 重力随时间增长（前 10 tick 无重力，之后每 tick +0.04，整数除法 1:1）、
 * 阻力 0.95、未造成伤害不反弹直接消失（bounceOnNoDamage=false）。
 */
public class TinkerShuriken extends TinkerProjectileBase {

    public TinkerShuriken(EntityType<? extends TinkerProjectileBase> entityType, Level level) {
        super(entityType, level);
        this.bounceOnNoDamage = false;
    }

    public TinkerShuriken(EntityType<? extends TinkerProjectileBase> entityType, Level level, Player owner,
                          float speed, float inaccuracy, ItemStack stack, ItemStack launchingStack) {
        super(entityType, level, owner, speed, inaccuracy, 1f, stack, launchingStack);
        this.bounceOnNoDamage = false;
    }

    @Override
    protected double getDefaultGravity() {
        // 1:1 旧版：(ticksExisted / 10) * 0.04d —— 整数除法，前 10 tick 无重力
        return (this.tickCount / 10) * 0.04d;
    }

    @Override
    protected double getAirResistance() {
        return 0.95d;
    }
}
