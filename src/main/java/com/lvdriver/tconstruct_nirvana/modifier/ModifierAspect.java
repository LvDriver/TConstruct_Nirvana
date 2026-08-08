package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.item.ItemStack;

/**
 * 修饰符约束（1:1 移植自 Tinkers' Antique {@code ModifierAspect}）。
 *
 * <p>每个方面约束修饰符的一个行为维度：可应用次数（{@link SingleAspect}）、
 * 等级上限（{@link LevelAspect}）、强化槽消耗（{@link FreeModifierAspect} 系列）、
 * 数据维护（{@link DataAspect}）与多级成长（{@link MultiAspect}）。</p>
 */
public abstract class ModifierAspect {

    /** 是否可以应用（默认 true）。 */
    public boolean canApply(ItemStack stack, ItemStack original) {
        return true;
    }

    /** 应用时更新修饰符数据（默认原样返回）。 */
    public ModifierData updateNBT(ItemStack stack, ModifierData data) {
        return data;
    }

    /** 只能应用一次。 */
    public static class SingleAspect extends ModifierAspect {
        private final String identifier;

        public SingleAspect(String identifier) {
            this.identifier = identifier;
        }

        @Override
        public boolean canApply(ItemStack stack, ItemStack original) {
            return !ToolHelper.hasModifier(stack, identifier);
        }
    }

    /** 等级上限：maxLevel 为 0 时表示只能应用一次；否则当前等级须低于上限。 */
    public static class LevelAspect extends ModifierAspect {
        private final String identifier;
        private final int maxLevel;

        public LevelAspect(String identifier, int maxLevel) {
            this.identifier = identifier;
            this.maxLevel = maxLevel;
        }

        @Override
        public boolean canApply(ItemStack stack, ItemStack original) {
            int level = 0;
            for (ModifierData data : ToolHelper.getModifierData(stack)) {
                if (data.identifier().equals(identifier)) {
                    level = data.level();
                    break;
                }
            }
            return maxLevel <= 0 || level < maxLevel;
        }

        @Override
        public ModifierData updateNBT(ItemStack stack, ModifierData data) {
            return data.withLevel(Math.min(maxLevel <= 0 ? 1 : maxLevel, data.level() + 1));
        }
    }

    /** 保证修饰符数据存在并携带正确颜色。 */
    public static class DataAspect extends ModifierAspect {
        private final String identifier;
        private final int color;

        public DataAspect(String identifier, int color) {
            this.identifier = identifier;
            this.color = color;
        }

        @Override
        public ModifierData updateNBT(ItemStack stack, ModifierData data) {
            return data.withColor(color);
        }
    }

    /** 消耗指定数量的强化槽（每应用扣一次）。 */
    public static class FreeModifierAspect extends ModifierAspect {
        private final int amount;

        public FreeModifierAspect(int amount) {
            this.amount = amount;
        }

        @Override
        public boolean canApply(ItemStack stack, ItemStack original) {
            return ToolHelper.getFreeModifiers(stack) >= amount;
        }

        @Override
        public ModifierData updateNBT(ItemStack stack, ModifierData data) {
            int free = ToolHelper.getFreeModifiers(stack) - amount;
            com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
            stack.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.TOOL_DATA,
                    toolData.withModifiers(Math.max(0, free)));
            return data;
        }
    }

    /** 首次免费，之后每次消耗指定槽数（旧版 blasting/luck 用）。 */
    public static class FreeFirstModifierAspect extends ModifierAspect {
        private final String identifier;
        private final int amount;

        public FreeFirstModifierAspect(String identifier, int amount) {
            this.identifier = identifier;
            this.amount = amount;
        }

        @Override
        public boolean canApply(ItemStack stack, ItemStack original) {
            int free = ToolHelper.getFreeModifiers(stack);
            boolean alreadyApplied = ToolHelper.hasModifier(stack, identifier);
            return free >= (alreadyApplied ? amount : 0);
        }

        @Override
        public ModifierData updateNBT(ItemStack stack, ModifierData data) {
            boolean alreadyApplied = ToolHelper.hasModifier(stack, identifier);
            int free = ToolHelper.getFreeModifiers(stack) - (alreadyApplied ? amount : 0);
            com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
            stack.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.TOOL_DATA,
                    toolData.withModifiers(Math.max(0, free)));
            return data;
        }
    }

    /** 免费槽消耗 + 等级成长（旧版 MultiAspect：每应用 1 槽、升 1 级，上限 maxLevel）。 */
    public static class MultiAspect extends ModifierAspect {
        private final String identifier;
        private final int color;
        private final int maxLevel;
        private final int countPerLevel;
        private final int freeAmount;

        public MultiAspect(String identifier, int color, int maxLevel, int countPerLevel, int freeAmount) {
            this.identifier = identifier;
            this.color = color;
            this.maxLevel = maxLevel;
            this.countPerLevel = countPerLevel;
            this.freeAmount = freeAmount;
        }

        @Override
        public boolean canApply(ItemStack stack, ItemStack original) {
            int level = 0;
            for (ModifierData data : ToolHelper.getModifierData(stack)) {
                if (data.identifier().equals(identifier)) {
                    level = data.level();
                    break;
                }
            }
            return level < maxLevel && ToolHelper.getFreeModifiers(stack) >= freeAmount;
        }

        @Override
        public ModifierData updateNBT(ItemStack stack, ModifierData data) {
            int free = ToolHelper.getFreeModifiers(stack) - freeAmount;
            com.lvdriver.tconstruct_nirvana.data.ToolData toolData = ToolHelper.getToolData(stack);
            stack.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.TOOL_DATA,
                    toolData.withModifiers(Math.max(0, free)));
            return data.withLevel(Math.min(maxLevel, data.level() + 1)).withColor(color);
        }
    }

    /**
     * 互斥（1:1 旧版 canApplyTogether 拒绝规则）：工具上已存在任一互斥修饰符/特质时拒绝应用。
     * 旧版为单向声明 + 双向检查（A 拒 B 或 B 拒 A 均拒绝），挂载时按旧版声明成对配置即可等效。
     */
    public static class ExclusiveAspect extends ModifierAspect {
        private final java.util.List<String> exclusiveWith;

        public ExclusiveAspect(String... exclusiveWith) {
            this.exclusiveWith = java.util.List.of(exclusiveWith);
        }

        @Override
        public boolean canApply(ItemStack stack, ItemStack original) {
            for (com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
                if (exclusiveWith.contains(modifier.getIdentifier())) {
                    return false;
                }
            }
            return true;
        }
    }
}
