package com.lvdriver.tconstruct_nirvana.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * 箭弹射物（1:1 移植自 Tinkers' Antique {@code EntityArrow}）。
 * 物理参数同原版（重力 0.05、阻力 0.99）；旋转动画由客户端渲染器处理。
 */
public class TinkerArrow extends TinkerProjectileBase {

    public TinkerArrow(EntityType<? extends TinkerProjectileBase> entityType, Level level) {
        super(entityType, level);
    }

    public TinkerArrow(EntityType<? extends TinkerProjectileBase> entityType, Level level, Player owner,
                       float speed, float inaccuracy, float power, ItemStack stack, ItemStack launchingStack) {
        super(entityType, level, owner, speed, inaccuracy, power, stack, launchingStack);
    }
}
