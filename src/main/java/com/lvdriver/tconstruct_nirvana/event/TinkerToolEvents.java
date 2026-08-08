package com.lvdriver.tconstruct_nirvana.event;

import com.lvdriver.tconstruct_nirvana.item.tool.AoeToolItem;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Mattock;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.BattleSign;
import com.lvdriver.tconstruct_nirvana.modifier.Modifier;
import com.lvdriver.tconstruct_nirvana.util.HarvestLevels;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

import java.util.List;

/**
 * 工具运行期事件分发（EVENT_BUS）。
 *
 * <p>1:1 移植旧版 ToolEvents / TraitEvents 的触发时机，全部挂 NeoForge 事件：
 * 挖掘速度（BreakSpeed）、采掘判定（HarvestCheck）、方块破坏与 AOE 扩展
 * （BreakEvent）、掉落结算（BlockDropsEvent，替代旧版 HarvestDropsEvent）、
 * 伤害修正与格挡（LivingIncomingDamageEvent，替代旧版 LivingHurtEvent）。
 * 钩子统一遍历工具的活跃修饰符（{@link ToolHelper#getActiveModifiers}）。</p>
 */
public final class TinkerToolEvents {

    private TinkerToolEvents() {
    }

    /** 注册到运行期事件总线（NeoForge.EVENT_BUS）。 */
    public static void register() {
        NeoForge.EVENT_BUS.register(TinkerToolEvents.class);
    }

    /* ---------- 挖掘速度 ---------- */

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        ItemStack stack = event.getEntity().getMainHandItem();
        if (stack.getItem() instanceof TinkerToolItem) {
            event.setNewSpeed(ToolHelper.calcDigSpeed(stack, event.getState()));
            for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                modifier.miningSpeed(stack, event);
            }
        }
    }

    /* ---------- 采掘判定（1:1 旧版 harvestLevel 体系 → 1.21.1 needs_* tag 映射） ---------- */

    @SubscribeEvent
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        ItemStack stack = event.getEntity().getMainHandItem();
        if (stack.getItem() instanceof TinkerToolItem tool && !ToolHelper.isBroken(stack)) {
            BlockState state = event.getTargetBlock();
            if (tool.isEffective(state) || ToolHelper.isToolEffective(stack, state)) {
                int required = requiredHarvestLevel(state);
                int level = toolHarvestLevel(tool, stack, state);
                if (level >= required) {
                    event.setCanHarvest(true);
                } else {
                    // 显式拦截：工具已加入 minecraft:pickaxes 等原版工具 tag 后，
                    // 原版 isCorrectToolForDrops 默认可能放行（如石镐挖钻石矿），
                    // 必须显式拒绝等级不足，保证 mod 采掘等级体系不被绕过
                    event.setCanHarvest(false);
                }
            }
        }
    }

    /** 方块所需采掘等级（复用 ToolHelper，供 HarvestCheck 判定）。 */
    private static int requiredHarvestLevel(BlockState state) {
        return ToolHelper.requiredHarvestLevel(state);
    }

    /** 工具在该方块上的采掘等级（Mattock 按斧/铲分别取）。 */
    private static int toolHarvestLevel(TinkerToolItem tool, ItemStack stack, BlockState state) {
        if (tool instanceof Mattock mattock) {
            if (state.is(BlockTags.MINEABLE_WITH_AXE)) {
                return mattock.getAxeLevel(stack);
            }
            if (state.is(BlockTags.MINEABLE_WITH_SHOVEL)) {
                return mattock.getShovelLevel(stack);
            }
        }
        return tool.getHarvestLevelStat(stack);
    }

    /* ---------- 方块破坏（BreakEvent：钩子 + AOE 扩展，带防重入） ---------- */

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof TinkerToolItem tool)) {
            return;
        }
        if (tool instanceof AoeToolItem aoe) {
            // 防重入：AOE 扩展过程中额外方块派发的 BreakEvent 直接返回
            // （额外方块已在 breakExtraBlock 中处理，避免无限递归）
            if (aoe.isAoeInProgress()) {
                return;
            }
            aoe.setAoeInProgress(true);
            try {
                // beforeBlockBreak（1:1 旧版：仅主方块触发）
                for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                    modifier.beforeBlockBreak(stack, event);
                }
                // AOE 扩展：破坏范围额外方块（1:1 旧版 onBlockStartBreak 的 AOE 逻辑）
                if (!ToolHelper.isBroken(stack) && aoe.isAoeHarvestTool()) {
                    net.minecraft.world.level.Level level = (net.minecraft.world.level.Level) event.getLevel();
                    for (BlockPos extraPos : aoe.getAOEBlocks(stack, level, player, event.getPos())) {
                        aoe.breakExtraBlock(stack, level, player, extraPos, event.getPos());
                    }
                }
            } finally {
                aoe.setAoeInProgress(false);
            }
            return;
        }
        // 非 AOE 工具：仅 beforeBlockBreak 钩子
        for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
            modifier.beforeBlockBreak(stack, event);
        }
    }

    /* ---------- 掉落结算（1.21.1 BlockDropsEvent 替代旧版 HarvestDropsEvent） ---------- */

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        if (event.getBreaker() instanceof Player player) {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() instanceof TinkerToolItem) {
                for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                    modifier.blockHarvestDrops(stack, event);
                }
            }
        }
    }

    /* ---------- 命中后钩子（LivingDamageEvent.Post：可拿到实际结算伤害，necrotic 吸血依赖） ---------- */

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        if (event.getSource().getDirectEntity() instanceof Player attacker) {
            ItemStack stack = attacker.getMainHandItem();
            if (stack.getItem() instanceof TinkerToolItem && !ToolHelper.isBroken(stack)
                    && event.getEntity() instanceof net.minecraft.world.entity.LivingEntity target) {
                float damageDealt = Math.max(0f, event.getNewDamage() - event.getBlockedDamage());
                for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                    modifier.afterHit(stack, attacker, target, damageDealt, false, true);
                }
            }
        }
    }

    /* ---------- 伤害修正与格挡（1.21.1 LivingIncomingDamageEvent 替代旧版 LivingHurtEvent） ---------- */

    @SubscribeEvent
    public static void onLivingIncomingDamage(LivingIncomingDamageEvent event) {
        // 攻击伤害修正：仅近战直击（1:1 旧版 attackEntity 的 damage 链 + 伤害削减；
        // 间接来源如箭/爆炸不应进入近战修正链）
        if (event.getSource().getDirectEntity() instanceof Player attacker) {
            ItemStack stack = attacker.getMainHandItem();
            if (stack.getItem() instanceof TinkerToolItem tool && !ToolHelper.isBroken(stack)) {
                float baseDamage = event.getOriginalAmount();
                float damage = event.getAmount();
                if (event.getEntity() instanceof net.minecraft.world.entity.LivingEntity target) {
                    for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                        damage = modifier.damage(stack, attacker, target, baseDamage, damage, false);
                    }
                }
                damage = tool.modifyDamage(stack, attacker, event.getEntity(), damage);
                damage = ToolHelper.calcCutoffDamage(damage, tool.damageCutoff());
                event.setAmount(damage);
            }
        }
        // 玩家受伤（工具在任意栏位时的 onPlayerHurt 钩子）
        if (event.getEntity() instanceof Player player) {
            // 战牌格挡：减伤 50% + onBlock（1:1 旧版格挡逻辑简化版）
            ItemStack active = player.getUseItem();
            if (active.getItem() instanceof BattleSign && !ToolHelper.isBroken(active)) {
                event.setAmount(event.getAmount() * 0.5f);
                for (Modifier modifier : ToolHelper.getActiveModifiers(active)) {
                    modifier.onBlock(active, player, event);
                }
            }
            for (ItemStack stack : player.getInventory().items) {
                if (stack.getItem() instanceof TinkerToolItem) {
                    for (Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                        modifier.onPlayerHurt(stack, player, event);
                    }
                }
            }
        }
    }
}
