package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

/**
 * 部件制作配方（模具 + 材料 → 部件 + 余料），匹配逻辑 1:1 自旧版
 * {@code ToolBuilder.tryBuildToolPart}（部件加工台 GUI 槽位合成，本会话
 * 抽象为数据驱动配方）。
 *
 * <p>每条配方对应一个模具形状（JSON {@code "pattern"} = 部件注册名），
 * cost 即该部件消耗的材料价值（mb）。材料维度运行时展开：遍历全部
 * craftable 材料，用材料-物品 Tag 关联（会话3.5）计算输入价值，首个
 * 匹配且部件允许（{@link ToolPart#canUseMaterial}）者出料。</p>
 *
 * <p>余料（1:1 旧版）：材料投入价值超过 cost 的部分按
 * {@link Material#VALUE_Shard}（72）折算为碎块副产物（不足 72 丢弃）。
 * 匹配/消耗动作由冶炼炉/部件加工台会话的调用方按 {@link #assemble} 与
 * {@link #getLeftover} 执行。</p>
 */
public class PartRecipe implements Recipe<PartRecipeInput> {

    private final ResourceLocation pattern;
    private final int cost;

    public PartRecipe(ResourceLocation pattern, int cost) {
        this.pattern = pattern;
        this.cost = cost;
    }

    public ResourceLocation getPattern() {
        return pattern;
    }

    public int getCost() {
        return cost;
    }

    /** 形状对应的部件（未注册形状 → 空）。 */
    public java.util.Optional<ToolPart> getPart() {
        return ModToolParts.getPart(pattern);
    }

    /** 材料总价值：各材料对输入栈的匹配价值合计（1:1 旧版 matches 多槽求和）。 */
    public int getTotalValue(Material material, PartRecipeInput input) {
        int total = 0;
        for (ItemStack stack : input.materials()) {
            total += material.getMatchValue(stack);
        }
        return total;
    }

    /**
     * 首个满足条件（craftable + 部件可用 + 价值 ≥ cost）的材料；无则空。
     * 遍历顺序 = 材料注册顺序（1:1 旧版 TinkerRegistry.getAllMaterials()）。
     */
    public java.util.Optional<Material> findMaterial(PartRecipeInput input) {
        java.util.Optional<ToolPart> partOpt = getPart();
        if (partOpt.isEmpty()) {
            return java.util.Optional.empty();
        }
        ToolPart part = partOpt.get();
        for (Material material : ModMaterials.getAllMaterials()) {
            if (!material.isCraftable()) {
                continue;
            }
            if (!part.canUseMaterial(material)) {
                continue;
            }
            if (getTotalValue(material, input) >= cost) {
                return java.util.Optional.of(material);
            }
        }
        return java.util.Optional.empty();
    }

    /** 余料碎块数（1:1 旧版 leftover = (匹配价值 - cost) / VALUE_Shard）。 */
    public int getLeftover(Material material, PartRecipeInput input) {
        return (getTotalValue(material, input) - cost) / Material.VALUE_Shard;
    }

    @Override
    public boolean matches(PartRecipeInput input, Level level) {
        // 模具形状校验（1:1 旧版 Pattern.getPartFromTag）
        if (input.pattern().isEmpty() || !pattern.equals(input.pattern().get(ModDataComponents.PATTERN_SHAPE))) {
            return false;
        }
        return findMaterial(input).isPresent();
    }

    @Override
    public ItemStack assemble(PartRecipeInput input, HolderLookup.Provider registries) {
        java.util.Optional<Material> material = findMaterial(input);
        java.util.Optional<ToolPart> part = getPart();
        if (material.isEmpty() || part.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return part.get().getItemstackWithMaterial(material.get());
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.PART_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.PART_TYPE.get();
    }

    /** 特殊配方：不进配方书（部件加工台 GUI 展示）。 */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String toString() {
        return "part(" + pattern + ", cost=" + cost + ')';
    }
}
