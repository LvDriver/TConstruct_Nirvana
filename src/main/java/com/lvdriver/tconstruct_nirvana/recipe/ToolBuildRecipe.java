package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.item.tool.ModTools;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具组装配方（1:1 移植自 Tinkers' Antique {@code TableRecipeFactory.TableRecipe} 的组装核心）。
 *
 * <p>工作台合成：按序摆入部件（2-4 个，须连续无空洞），逐工具尝试
 * {@link TinkerToolItem#buildItemFromStacks}，首个成功者输出组装好的工具。
 * 与旧版 ToolBuilder.tryBuildTool 一致（遍历注册工具、精确槽位数、逐槽校验）。</p>
 */
public class ToolBuildRecipe extends CustomRecipe {

    public ToolBuildRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return build(input) != ItemStack.EMPTY;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return build(input);
    }

    private ItemStack build(CraftingInput input) {
        // 收集非空槽位并校验连续性（旧版 tryBuildTool 的 gap 检查）
        List<ItemStack> stacks = new ArrayList<>();
        boolean seenGap = false;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                if (!stacks.isEmpty()) {
                    seenGap = true;
                }
            } else {
                if (seenGap) {
                    return ItemStack.EMPTY;
                }
                stacks.add(stack);
            }
        }
        if (stacks.size() < 2) {
            return ItemStack.EMPTY;
        }
        for (TinkerToolItem tool : ModTools.getAllTools()) {
            ItemStack output = tool.buildItemFromStacks(stacks);
            if (!output.isEmpty()) {
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 4;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.TOOL_BUILD_SERIALIZER.get();
    }
}
