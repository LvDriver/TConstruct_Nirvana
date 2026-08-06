package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 近战效果类特质（onHit/afterHit，1:1 移植自 Tinkers' Antique）。
 * 文件内多个 package-private 类，由 {@code ModTraits} 统一实例化注册。
 */

/** 剧毒（poisonous）：命中附加中毒 I 101 tick。 */
class TraitPoisonous extends Trait {
    TraitPoisonous() {
        super("poisonous", 0xffffff);
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isAlive()) {
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.POISON, 101, 0));
        }
    }
}

/** 锋利（sharp）：命中附加流血 DOT（每 15 tick (等级+1)/3 伤害，简化：等级按分裂计数）。 */
class TraitSharp extends Trait {
    TraitSharp() {
        super("sharp", 0xffffff);
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isAlive()) {
            // 简化：直接施加一次流血伤害（完整 DOT 药水系统随后续会话）
            target.hurt(target.damageSources().generic(), 0.5f);
        }
    }
}

/** 冻结（freezing）：命中减速（等级+1，30 tick）。 */
class TraitFreezing extends Trait {
    TraitFreezing() {
        super("freezing", 0xffffff);
    }

    @Override
    public void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical) {
        int amp = target.hasEffect(MobEffects.MOVEMENT_SLOWDOWN)
                ? target.getEffect(MobEffects.MOVEMENT_SLOWDOWN).getAmplifier() + 1 : 0;
        target.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, Math.min(4, amp)));
    }
}

/** 尖刺（spiky）：受伤时反弹 50% 实际伤害。 */
class TraitSpiky extends Trait {
    TraitSpiky() {
        super("spiky", 0x00aa00);
    }

    @Override
    public void onPlayerHurt(ItemStack tool, net.minecraft.world.entity.player.Player player,
                             net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker && !player.level().isClientSide) {
            float damage = ToolHelper.getActualAttack(tool) / 2f;
            attacker.hurt(attacker.damageSources().thorns(player), damage);
        }
    }

    @Override
    public void onBlock(ItemStack tool, net.minecraft.world.entity.player.Player player,
                        net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            attacker.hurt(attacker.damageSources().thorns(player), event.getAmount());
        }
    }
}

/** 多刺（prickly）：命中反伤 0.5 ± 0.75 高斯（无视护甲）。 */
class TraitPrickly extends Trait {
    TraitPrickly() {
        super("prickly", 0x00aa00);
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit) {
            float amount = 0.5f + (float) Math.max(-0.5, (player.getRandom().nextGaussian() * 0.75));
            target.hurt(target.damageSources().cactus(), amount);
        }
    }
}

/** 培根（baconlicious）：破坏 0.5%、击杀 5% 掉培根。 */
class TraitBaconlicious extends Trait {
    TraitBaconlicious() {
        super("baconlicious", 0xffaaaa);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, net.minecraft.world.level.Level world,
                                net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos,
                                LivingEntity player, boolean wasEffective) {
        if (wasEffective && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 0.005f) {
            spawnBacon(world, pos);
        }
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isDeadOrDying() && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 0.05f) {
            spawnBacon(target.level(), target.blockPosition());
        }
    }

    private void spawnBacon(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos) {
        if (!world.isClientSide) {
            world.addFreshEntity(new net.minecraft.world.entity.item.ItemEntity(world,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    new ItemStack(net.minecraft.world.item.Items.COOKED_PORKCHOP)));
        }
    }
}

/** 史莱姆（slimey_green/slimey_blue）：破坏/击杀 0.33% 生成史莱姆。 */
class TraitSlimey extends Trait {
    private final String spawnType; // "green"/"blue"

    TraitSlimey(String suffix, String spawnType, int color) {
        super("slimey_" + suffix, color);
        this.spawnType = spawnType;
    }

    @Override
    public void afterBlockBreak(ItemStack tool, net.minecraft.world.level.Level world,
                                net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos,
                                LivingEntity player, boolean wasEffective) {
        if (wasEffective && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 0.0033f) {
            spawnSlime(world, pos);
        }
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isDeadOrDying() && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 0.0033f) {
            spawnSlime(target.level(), target.blockPosition());
        }
    }

    private void spawnSlime(net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos) {
        if (!world.isClientSide) {
            var type = spawnType.equals("blue")
                    ? net.minecraft.world.entity.EntityType.SLIME
                    : net.minecraft.world.entity.EntityType.SLIME;
            var slime = type.create(world);
            if (slime != null) {
                slime.moveTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                world.addFreshEntity(slime);
            }
        }
    }
}

/** 末影干扰（enderference）：命中末影人附加干扰效果。 */
class TraitEnderference extends Trait {
    TraitEnderference() {
        super("enderference", 0x00aaaa);
    }

    @Override
    public void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical) {
        if (target.getType() == net.minecraft.world.entity.EntityType.ENDERMAN) {
            // 简化：干扰 = 缓慢（完整 Enderference 药水/禁传送随后续会话）
            target.addEffect(new net.minecraft.world.effect.MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        }
    }
}

/** 电击（shocking）：充能满后命中造成 5 点闪电伤害。 */
class TraitShocking extends Trait {
    TraitShocking() {
        super("shocking", 0xffffff);
    }

    @Override
    public void onHit(ItemStack tool, LivingEntity player, LivingEntity target, float damage, boolean isCritical) {
        int charge = getCharge(tool);
        if (charge >= 100) {
            setCharge(tool, 0);
            target.hurt(target.damageSources().lightningBolt(), 5f);
        }
    }

    private int getCharge(ItemStack tool) {
        return tool.getOrDefault(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.<String, Integer>of())
                .getOrDefault(identifier, 0);
    }

    private void setCharge(ItemStack tool, int charge) {
        var levels = new java.util.LinkedHashMap<>(tool.getOrDefault(
                com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.of()));
        levels.put(identifier, charge);
        tool.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), levels);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, net.minecraft.world.level.Level world,
                                net.minecraft.world.level.block.state.BlockState state, net.minecraft.core.BlockPos pos,
                                LivingEntity player, boolean wasEffective) {
        int charge = Math.min(100, getCharge(tool) + 15);
        setCharge(tool, charge);
    }
}

/** 美味（tasty）：饥饿时啃食工具恢复饥饿。 */
class TraitTasty extends Trait {
    TraitTasty() {
        super("tasty", 0xff5555);
    }

    @Override
    public void onUpdate(ItemStack tool, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity,
                         int itemSlot, boolean isSelected) {
        if (world.isClientSide || !isSelected || !(entity instanceof net.minecraft.world.entity.player.Player player)) {
            return;
        }
        if (player.getFoodData().getFoodLevel() >= 18) {
            return;
        }
        float chance = 0.01f;
        if (player.getFoodData().getFoodLevel() <= 10) {
            chance += (10 - player.getFoodData().getFoodLevel()) * 0.0025f
                    - player.getFoodData().getSaturationLevel() * 0.005f;
        }
        if (com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < chance) {
            player.getFoodData().eat(1, 0f);
            ToolHelper.damageTool(tool, 5, player);
        }
    }
}
