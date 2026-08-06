package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.util.ItemTagMatch;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 熔炼配方（物品 → 流体 + 熔点/耗时），匹配逻辑与温度公式 1:1 自旧版
 * {@code TinkerRegistry.registerMelting / MeltingRecipe}。
 *
 * <p>输入一律用 {@code c:} Tag（{@link ItemTagMatch}），不绑定具体物品：
 * 任何 mod 的铁矿石/铁锭携带 {@code c:ores/iron}/{@code c:ingots/iron} 即可被
 * 冶炼炉熔化（万物皆可熔，500mod 兼容核心设计）。数据以数据驱动
 * （DataGen 生成 {@code recipe/melting/*.json}），供冶炼炉会话调用。</p>
 *
 * <p>温度语义（1:1 旧版）：{@link #temperature} 是配方的"熔点/耗时"（炉温须
 * 不低于它才可熔），缺省按 {@link #calcTemperature} 由流体温度与物品价值自动
 * 推算（同一流体价值越大所需炉温越高，2^log9 曲线）。</p>
 */
public class MeltingRecipe implements Recipe<MeltingRecipeInput> {

    /** 旧版 {@code MeltingRecipe.LOG9_2}（价值 9 倍 → 温度指数 +1）。 */
    private static final double LOG9_2 = 0.31546487678;

    private final ItemTagMatch input;
    private final FluidStack result;
    private final int temperature;

    public MeltingRecipe(ItemTagMatch input, FluidStack result) {
        this(input, result, calcTemperature(result, input.amount()));
    }

    public MeltingRecipe(ItemTagMatch input, FluidStack result, int temperature) {
        this.input = input;
        this.result = result.copy();
        this.temperature = temperature;
    }

    public ItemTagMatch getInput() {
        return input;
    }

    /** 输出流体（副本，量 = 输入价值）。 */
    public FluidStack getResult() {
        return result.copy();
    }

    /** 配方熔点/所需炉温（旧版 getTemperature）。 */
    public int getTemperature() {
        return temperature;
    }

    /** 可用温度（相对室温 300K 的增量，1:1 旧版 getUsableTemperature）。 */
    public int getUsableTemperature() {
        return Math.max(1, temperature - 300);
    }

    @Override
    public boolean matches(MeltingRecipeInput recipeInput, Level level) {
        return input.matches(recipeInput.stack());
    }

    @Override
    public ItemStack assemble(MeltingRecipeInput recipeInput, HolderLookup.Provider registries) {
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
        return ModRecipeTypes.MELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.MELTING_TYPE.get();
    }

    /** 特殊配方：输出非物品，不进配方书（同 {@link AlloyRecipe}）。 */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /**
     * 温度自动推算（1:1 旧版 {@code calcTemperature}）：
     * 以整块价值（{@link Material#VALUE_Block}）为基准，价值 f 倍 → 温度指数
     * {@code f^log9(2)}（1=1、9=2、81=4、1/9=1/2），再映射到流体熔点之上的区间。
     */
    public static int calcTemperature(FluidStack result, int amount) {
        return calcTemperature(result.getFluid().getFluidType().getTemperature(), amount);
    }

    /** 温度自动推算（旧版同签名：流体温度 + 物品价值）。 */
    public static int calcTemperature(int fluidTemperature, int timeAmount) {
        int base = Material.VALUE_Block;
        int maxTmp = Math.max(0, fluidTemperature - 300);
        double f = (double) timeAmount / (double) base;
        f = Math.pow(f, LOG9_2);
        return 300 + (int) (f * (double) maxTmp);
    }

    @Override
    public String toString() {
        return "melting(" + input + " -> " + result + " @ " + temperature + ')';
    }
}
