package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.item.part.SharpeningKit;
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
 * 工具修复配方（1:1 移植自 Tinkers' Antique {@code RepairRecipe}）。
 *
 * <p>工作台合成：1 个 TConstruct 工具 + 若干同材料磨刀石（SharpeningKit），
 * 其余物品 → 不匹配。修复逻辑见 {@link TinkerToolItem#repair}。</p>
 */
public class RepairRecipe extends CustomRecipe {

    public RepairRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return !getRepairedTool(input).isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registryAccess) {
        return getRepairedTool(input);
    }

    /** 收集输入并尝试修复（1:1 旧版 getRepairedTool）。 */
    private ItemStack getRepairedTool(CraftingInput inv) {
        ItemStack tool = null;
        List<ItemStack> input = new ArrayList<>();
        for (int i = 0; i < inv.size(); i++) {
            ItemStack slot = inv.getItem(i);
            if (slot.isEmpty()) {
                continue;
            }
            slot = slot.copy();
            slot.setCount(1);

            if (slot.getItem() instanceof TinkerToolItem) {
                // 只允许一个工具
                if (tool != null) {
                    return ItemStack.EMPTY;
                }
                tool = slot;
            } else if (slot.getItem() instanceof SharpeningKit) {
                // 堆叠磨刀石摊平为单发条目（copy 前读原 count，与分散多槽摆法行为一致）
                int count = inv.getItem(i).getCount();
                for (int n = 0; n < count; n++) {
                    input.add(slot.copy());
                }
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (tool == null) {
            return ItemStack.EMPTY;
        }
        return ((TinkerToolItem) tool.getItem()).repair(tool.copy(), input);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.REPAIR_SERIALIZER.get();
    }
}
