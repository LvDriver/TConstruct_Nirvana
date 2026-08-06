package com.lvdriver.tconstruct_nirvana.item.tool;

import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

/**
 * 范围挖掘工具基类（1:1 移植自 Tinkers' Antique {@code AoeToolCore}）。
 *
 * <p>破坏主方块时同时破坏范围内的额外方块（锤/挖掘机 3×3×1、大镰 3×3×3、
 * 伐木斧整棵树等），每块额外消耗 1-2 耐久并触发 trait 钩子。
 * 额外方块破坏前做采掘等级校验并派发 BreakEvent（尊重领地保护）；
 * {@link #aoeInProgress} 为防重入标志：AOE 扩展过程中派发的 BreakEvent
 * 会被 {@code TinkerToolEvents} 识别并跳过再次扩展，避免无限递归。</p>
 */
public abstract class AoeToolItem extends TinkerToolItem {

    /** AOE 扩展进行中标志（服务端单线程，防 BreakEvent 递归）。 */
    private boolean aoeInProgress = false;

    protected AoeToolItem(Item.Properties properties, PartMaterialType... requiredComponents) {
        super(properties, requiredComponents);
        addCategory(Category.AOE);
    }

    /** 是否处于 AOE 扩展流程（供事件分发器防重入）。 */
    public boolean isAoeInProgress() {
        return aoeInProgress;
    }

    /** 设置 AOE 扩展标志（事件分发器调用）。 */
    public void setAoeInProgress(boolean inProgress) {
        this.aoeInProgress = inProgress;
    }

    /** 本次破坏的额外方块列表（子类实现范围与形状）。 */
    public abstract List<BlockPos> getAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin);

    public boolean isAoeHarvestTool() {
        return true;
    }

    /** 破坏额外方块（breakExtraBlock：采掘等级校验 + BreakEvent 派发 + 破坏 + trait 钩子）。 */
    public void breakExtraBlock(ItemStack tool, Level world, Player player, BlockPos pos, BlockPos refPos) {
        if (world.isEmptyBlock(pos) || pos.equals(refPos)) {
            return;
        }
        BlockState state = world.getBlockState(pos);

        // 安全校验：AOE 额外方块同样须满足采掘等级（防止低等级锤绕过等级挖高级矿）
        if (!player.getAbilities().instabuild
                && ToolHelper.getHarvestLevelStat(tool) < ToolHelper.requiredHarvestLevel(state)) {
            return;
        }
        // 1:1 旧版 canBreakExtraBlock 过滤：工具须对该方块有效
        if (!player.getAbilities().instabuild && !isEffective(state) && !ToolHelper.isToolEffective(tool, state)) {
            return;
        }
        // 1:1 旧版强度比过滤：额外方块硬度/主方块硬度 > 10 不破坏（钴锤不能瞬破黑曜石）
        float refStrength = world.getBlockState(refPos).getDestroySpeed(world, refPos);
        float strength = state.getDestroySpeed(world, pos);
        if (!player.getAbilities().instabuild && refStrength > 0 && strength / refStrength > 10f) {
            return;
        }
        // 派发 BreakEvent：尊重领地/保护 mod 的拦截（主方块走原版挖掘流程，额外方块须手动派发）
        if (world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            net.neoforged.neoforge.event.level.BlockEvent.BreakEvent breakEvent =
                    new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(
                            serverLevel, pos, state, player);
            if (net.neoforged.neoforge.common.NeoForge.EVENT_BUS.post(breakEvent).isCanceled()) {
                return;
            }
        }

        boolean effective = isEffective(state) || ToolHelper.isToolEffective(tool, state);
        if (player.getAbilities().instabuild) {
            world.destroyBlock(pos, false, player);
            return;
        }
        world.destroyBlock(pos, true, player);
        // 耐久损耗与 trait 钩子由 afterBlockBreak 统一处理（勿在此重复 damageTool，会双耗）
        afterBlockBreak(tool, world, state, pos, player, effective ? 1 : 2, effective);
    }

    /**
     * 计算范围方块（1:1 旧版 ToolHelper.calcAOEBlocks：按命中面分轴，简化版）：
     * 玩家俯角 >45°（挖顶/底面）→ 水平面 width×depth；否则沿水平朝向竖面。
     * 剔除空方块与自身。
     */
    public static List<BlockPos> calcAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin,
                                               int width, int height, int depth) {
        boolean horizontalFace = Math.abs(player.getXRot()) > 45f;
        Direction face = player.getDirection();
        Direction right = face.getClockWise();
        List<BlockPos> result = new ArrayList<>();
        if (horizontalFace) {
            // 挖顶/底面：水平面 = width×height 沿 x/z，Y 方向取 depth 层
            // （1:1 旧版 sideHit UP/DOWN 分支：俯视向下、仰视向上）
            int w = (width - 1) / 2;
            int h = (height - 1) / 2;
            int dySign = player.getXRot() > 0 ? -1 : 1;
            for (int dx = -w; dx <= w; dx++) {
                for (int dz = -h; dz <= h; dz++) {
                    for (int dy = 0; dy < depth; dy++) {
                        BlockPos pos = origin.offset(dx, dySign * dy, dz);
                        if (!pos.equals(origin) && !world.isEmptyBlock(pos)
                                && world.getBlockState(pos).getDestroySpeed(world, pos) >= 0) {
                            result.add(pos);
                        }
                    }
                }
            }
        } else {
            int w = (width - 1) / 2;
            int h = (height - 1) / 2;
            for (int dx = -w; dx <= w; dx++) {
                for (int dy = -h; dy <= h; dy++) {
                    for (int dz = 0; dz < depth; dz++) {
                        BlockPos pos = origin.relative(right, dx).offset(0, dy, 0).relative(face, dz);
                        if (!pos.equals(origin) && !world.isEmptyBlock(pos)
                                && world.getBlockState(pos).getDestroySpeed(world, pos) >= 0) {
                            result.add(pos);
                        }
                    }
                }
            }
        }
        return result;
    }
}
