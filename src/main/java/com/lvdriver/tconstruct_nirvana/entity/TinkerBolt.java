package com.lvdriver.tconstruct_nirvana.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 弩矢弹射物（1:1 移植自 Tinkers' Antique {@code EntityBolt}）。
 * 重力 0.065、阻力 0.985（旧版 getGravity/getSlowdown 数值）。
 */
public class TinkerBolt extends TinkerProjectileBase {

    public TinkerBolt(EntityType<? extends TinkerProjectileBase> entityType, Level level) {
        super(entityType, level);
    }

    public TinkerBolt(EntityType<? extends TinkerProjectileBase> entityType, Level level, Player owner,
                      float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(entityType, level, owner, speed, inaccuracy, power, stack, launchingStack);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.065d;
    }

    @Override
    protected double getAirResistance() {
        return 0.985d;
    }
}
