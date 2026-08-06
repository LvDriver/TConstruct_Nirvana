package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * 合金配方（输入流体 → 输出流体），匹配逻辑 1:1 自旧版
 * {@code TinkerRegistry.registerAlloy / AlloyRecipe}（ratio 匹配，可多次应用）。
 *
 * <p>配方以数据驱动（DataGen 生成 {@code recipe/alloy/*.json}），供冶炼炉
 * （后续会话）调用 {@link #matchesAmount} 按"可应用次数"扣输入、加输出。
 * 数据 1:1 自旧版 {@code TinkerSmeltery.registerAlloys} 的 10 条合金。</p>
 */
public class AlloyRecipe implements Recipe<AlloyRecipeInput> {

    private final List<SizedFluidIngredient> inputs;
    private final FluidStack result;

    public AlloyRecipe(List<SizedFluidIngredient> inputs, FluidStack result) {
        this.inputs = new ArrayList<>(inputs);
        this.result = result;
    }

    public List<SizedFluidIngredient> getInputs() {
        return inputs;
    }

    public FluidStack getResult() {
        return result;
    }

    /**
     * 计算本配方对给定流体槽可应用的次数（1:1 旧版 {@code AlloyRecipe.matches}）：
     * 每个输入必须被槽中某流体匹配（同类型且量足够），可应用次数 = 各输入
     * {@code 槽内量 / 需要量} 的最小值；任一输入缺失返回 0。
     *
     * <p>契约：每个输入条目独立匹配一次，<b>同流体必须合并为单条</b>（拆条时
     * 同一槽栈只能命中一条，另一条匹配失败返回 0，与旧版行为一致）；输入
     * {@code amount <= 0} 的条目视为无效（返回 0，防除零）。</p>
     */
    public int matchesAmount(AlloyRecipeInput tank) {
        int times = Integer.MAX_VALUE;
        List<SizedFluidIngredient> needed = new LinkedList<>(inputs);
        for (FluidStack fluid : tank.fluids()) {
            for (Iterator<SizedFluidIngredient> it = needed.iterator(); it.hasNext(); ) {
                SizedFluidIngredient need = it.next();
                int needAmount = need.amount();
                if (needAmount > 0 && need.test(fluid)) {
                    it.remove();
                    times = Math.min(times, fluid.getAmount() / needAmount);
                    break;
                }
            }
        }
        return needed.isEmpty() ? times : 0;
    }

    @Override
    public boolean matches(AlloyRecipeInput input, Level level) {
        return matchesAmount(input) > 0;
    }

    @Override
    public ItemStack assemble(AlloyRecipeInput input, HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
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
        return ModRecipeTypes.ALLOY_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.ALLOY_TYPE.get();
    }

    /** 特殊配方：不进入配方书（输出非物品，避免配方书渲染崩溃）。 */
    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public String toString() {
        return TConstructNirvana.MODID + ":alloy(" + result + " <- " + inputs + ")";
    }
}
