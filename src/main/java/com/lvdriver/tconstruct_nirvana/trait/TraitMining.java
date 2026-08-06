package com.lvdriver.tconstruct_nirvana.trait;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * 挖掘类特质（1:1 移植自 Tinkers' Antique 各 Trait 的 miningSpeed/afterBlockBreak 钩子）。
 * 文件内多个 package-private 类，由 {@code ModTraits} 统一实例化注册。
 */

/** 势头（momentum）：连续挖掘加速，上限 32 级 = +40% 原速。 */
class TraitMomentum extends Trait {
    public static final int MAX_LEVEL = 32;

    TraitMomentum() {
        super("momentum", 0x5555ff);
    }

    private int getLevel(ItemStack tool) {
        return tool.getOrDefault(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.<String, Integer>of())
                .getOrDefault(identifier, 0);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        int level = getLevel(tool);
        event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * (level / 80f));
    }

    @Override
    public void afterBlockBreak(ItemStack tool, Level world, BlockState state, BlockPos pos, LivingEntity player, boolean wasEffective) {
        if (wasEffective) {
            var levels = new java.util.LinkedHashMap<>(tool.getOrDefault(
                    com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), java.util.Map.of()));
            levels.put(identifier, Math.min(MAX_LEVEL, getLevel(tool) + 1));
            tool.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.traitLevelsType(), levels);
        }
    }
}

/** 石缚（stonebound）：耐久越低挖掘越快 +2×ln(1+(最大-当前)/72)。 */
class TraitStonebound extends Trait {
    TraitStonebound() {
        super("stonebound", 0x555555);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        int max = tool.getMaxDamage();
        int current = tool.getDamageValue();
        event.setNewSpeed(event.getNewSpeed() + (float) (2 * Math.log(1 + (max - current) / 72.0)));
    }
}

/** 锯齿（jagged）：耐久越低攻击越高 +2×ln(1+(最大-当前)/72)。 */
class TraitJagged extends Trait {
    TraitJagged() {
        super("jagged", 0x55ffff);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        int max = tool.getMaxDamage();
        int current = tool.getDamageValue();
        return newDamage + (float) (2 * Math.log(1 + (max - current) / 72.0));
    }
}

/** 深挖者（depthdigger）：y<72 时挖掘加速 (72-y)/30。 */
class TraitDepthdigger extends Trait {
    TraitDepthdigger() {
        super("depthdigger", 0xffffff);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        int y = event.getPosition().map(net.minecraft.core.BlockPos::getY).orElse(0);
        if (y < 72) {
            event.setNewSpeed(event.getNewSpeed() + (72 - y) / 30f);
        }
    }
}

/** 超凡（unnatural）：采掘等级高出需求每级 +1 速度。 */
class TraitUnnatural extends Trait {
    TraitUnnatural() {
        super("unnatural", 0xff55ff);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        if (event.getState() == null) {
            return;
        }
        int required = 0;
        if (event.getState().is(net.minecraft.tags.BlockTags.NEEDS_DIAMOND_TOOL)) {
            required = 2;
        } else if (event.getState().is(net.minecraft.tags.BlockTags.NEEDS_IRON_TOOL)) {
            required = 1;
        }
        int level = ToolHelper.getHarvestLevelStat(tool) - required;
        if (level > 0) {
            event.setNewSpeed(event.getNewSpeed() + level);
        }
    }
}

/** 轻盈（lightweight）：挖掘速度 ×1.1、攻速 ×1.1。 */
class TraitLightweight extends Trait {
    TraitLightweight() {
        super("lightweight", 0x00ff00);
    }

    @Override
    public void applyEffect(ItemStack stack, ModifierData data) {
        super.applyEffect(stack, data);
        com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
        ToolHelper.setToolData(stack, toolData.withAttackSpeedMultiplier(toolData.attackSpeedMultiplier() * 1.1f));
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(event.getNewSpeed() * 1.1f);
    }
}

/** 流体动力（aquadynamic）：水中挖掘 +5.5 倍原速，下雨再加。 */
class TraitAquadynamic extends Trait {
    TraitAquadynamic() {
        super("aquadynamic", 0x55ffff);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        var player = event.getEntity();
        if (player.isInWater()) {
            event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * 5.5f);
            if (player.level().isRainingAt(player.blockPosition())) {
                float strength = player.level().getRainLevel(1f);
                event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * strength / 1.6f);
            }
        }
    }
}

/** 干旱狂热（aridiculous）：炎热干燥处加速、伤害加成。 */
class TraitAridiculous extends Trait {
    TraitAridiculous() {
        super("aridiculous", 0xaa0000);
    }

    /** 干旱度（1:1 旧版 getAridity 的简化版：炎热群系（#minecraft:is_hot）按降雨量折算）。 */
    private float getAridity(LivingEntity player) {
        var biome = player.level().getBiome(player.blockPosition());
        var isHot = net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BIOME,
                net.minecraft.resources.ResourceLocation.withDefaultNamespace("is_hot"));
        if (!biome.is(isHot)) {
            return 0f;
        }
        float aridity = 1f;
        if (player.level().isRainingAt(player.blockPosition())) {
            aridity -= player.level().getRainLevel(1f) / 2f;
        }
        return Math.max(0, aridity);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        event.setNewSpeed(event.getNewSpeed() + event.getOriginalSpeed() * getAridity(event.getEntity()) / 10f);
    }

    @Override
    public float damage(ItemStack tool, LivingEntity player, LivingEntity target, float damage, float newDamage, boolean isCritical) {
        return newDamage + 2 * getAridity(player);
    }
}

/** 崩解（crumbling）：徒手可采的方块挖掘大幅变慢。 */
class TraitCrumbling extends Trait {
    TraitCrumbling() {
        super("crumbling", 0xff0000);
    }

    @Override
    public void miningSpeed(ItemStack tool, PlayerEvent.BreakSpeed event) {
        // 简化：徒手可采（硬度 0）且非树叶时速度减半（1:1 旧版 isToolNotRequired 语义）
        if (event.getState().getDestroySpeed(event.getEntity().level(), event.getEntity().blockPosition()) == 0f
                && !event.getState().is(net.minecraft.tags.BlockTags.LEAVES)) {
            event.setNewSpeed(event.getNewSpeed() * 0.5f);
        }
    }
}
