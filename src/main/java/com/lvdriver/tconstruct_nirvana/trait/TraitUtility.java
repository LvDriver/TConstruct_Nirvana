package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

/**
 * 功能类特质（1:1 移植自 Tinkers' Antique）。
 * 文件内多个 package-private 类，由 {@code ModTraits} 统一实例化注册。
 */

/** 可写（writable1/writable2）：自由强化槽 +level。 */
class TraitWritable extends TraitLeveled {
    TraitWritable(int levels) {
        super("writable", levels, 0xffffff, 3);
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        super.applyEffect(stack, data);
        com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
        ToolHelper.setToolData(stack, toolData.withModifiers(toolData.modifiers() + getLevel()));
    }
}

/** 磁力（magnetic1/magnetic2）：破坏/命中后吸引周围物品。 */
class TraitMagnetic extends TraitLeveled {
    TraitMagnetic(int levels) {
        super("magnetic", levels, 0xdddddd, 3);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, Level world, BlockState state, BlockPos pos, LivingEntity player, boolean wasEffective) {
        if (wasEffective) {
            attractItems(tool, player);
        }
    }

    @Override
    public void afterHit(ItemStack tool, LivingEntity player, LivingEntity target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit) {
            attractItems(tool, player);
        }
    }

    private void attractItems(ItemStack tool, LivingEntity player) {
        double range = 1.8 + 0.3 * getLevel();
        var items = player.level().getEntitiesOfClass(net.minecraft.world.entity.item.ItemEntity.class,
                player.getBoundingBox().inflate(range), e -> e.isAlive() && !e.hasPickUpDelay());
        int count = 0;
        for (var item : items) {
            if (count++ >= 200) {
                break;
            }
            var pos = player.position();
            var itemPos = item.position();
            double dx = pos.x - itemPos.x;
            double dy = pos.y + 0.5 - itemPos.y;
            double dz = pos.z - itemPos.z;
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
            if (dist > 0.1) {
                item.setDeltaMovement(item.getDeltaMovement().add(dx / dist * 0.07, dy / dist * 0.07, dz / dist * 0.07));
            }
        }
    }
}

/** 自动冶炼（autosmelt）：方块掉落改为熔炼产物。 */
class TraitAutosmelt extends Trait {
    TraitAutosmelt() {
        super("autosmelt", 0xff5500);
        // 1:1 旧版 TraitAutosmelt.canApplyTogether：拒 squeaky、silktouch
        aspects.add(new com.lvdriver.tconstruct_nirvana.modifier.ModifierAspect.ExclusiveAspect("squeaky", "silktouch"));
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockDropsEvent event) {
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        var smeltingRecipes = serverLevel.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        for (net.minecraft.world.entity.item.ItemEntity entity : java.util.List.copyOf(event.getDrops())) {
            ItemStack stack = entity.getItem();
            for (var holder : smeltingRecipes) {
                if (holder.value() instanceof net.minecraft.world.item.crafting.SmeltingRecipe smelting
                        && smelting.getIngredients().stream().anyMatch(ing -> ing.test(stack))) {
                    ItemStack result = smelting.getResultItem(serverLevel.registryAccess()).copy();
                    result.setCount(stack.getCount());
                    entity.setItem(result);
                    break;
                }
            }
        }
    }
}

/** 吱吱（squeaky）：附加丝触附魔（简化：不附加，仅注册）。 */
class TraitSqueaky extends Trait {
    TraitSqueaky() {
        super("squeaky", 0xffff55);
    }
}

/** 老练（established）：击杀与破坏额外经验（简化：+1 经验）。 */
class TraitEstablished extends Trait {
    TraitEstablished() {
        super("established", 0xffffff);
    }

    @Override
    public void afterBlockBreak(ItemStack tool, Level world, BlockState state, BlockPos pos, LivingEntity player, boolean wasEffective) {
        if (wasEffective && com.lvdriver.tconstruct_nirvana.util.TConUtil.random().nextFloat() < 0.33f
                && player instanceof net.minecraft.world.entity.player.Player p) {
            p.giveExperiencePoints(1);
        }
    }
}

/** 易燃（flammable）：受伤时攻击者着火 3 秒。 */
class TraitFlammable extends Trait {
    TraitFlammable() {
        super("flammable", 0xffffff);
    }

    @Override
    public void onPlayerHurt(ItemStack tool, net.minecraft.world.entity.player.Player player,
                             net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getSource().getEntity() instanceof LivingEntity attacker) {
            attacker.setRemainingFireTicks(60);
        }
    }

    @Override
    public void onBlock(ItemStack tool, net.minecraft.world.entity.player.Player player,
                        net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent event) {
        if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FIRE)) {
            event.setAmount(0f);
            ToolHelper.damageTool(tool, 3, player);
            if (event.getSource().getEntity() instanceof LivingEntity attacker) {
                attacker.setRemainingFireTicks(60);
            }
        }
    }
}

/** 异形（alien）：缓慢成长（每 72 tick +1 耐久，攻击/速度按比例）。 */
class TraitAlien extends Trait {
    TraitAlien() {
        super("alien", 0xffff55);
    }

    @Override
    public void onUpdate(ItemStack tool, Level world, net.minecraft.world.entity.Entity entity, int itemSlot, boolean isSelected) {
        if (world.isClientSide || world.getGameTime() % 72 != 0) {
            return;
        }
        com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(tool);
        ToolHelper.setToolData(tool, toolData.withDurability(toolData.durability() + 1));
    }
}

/** 倒刺（splinters）：破坏/命中 1/150 概率自伤 0.1（简化：不实现自伤）。 */
class TraitSplinters extends Trait {
    TraitSplinters() {
        super("splinters", 0x55ff55);
    }
}
