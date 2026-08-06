package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.item.pattern.PatternItem;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Optional;

/**
 * 浇铸配方（模具 + 流体 → 物品 + 冷却时间），匹配与冷却公式 1:1 自旧版
 * {@code TinkerRegistry.registerTableCasting / registerBasinCasting + CastingRecipe}。
 *
 * <p>两种输出模式（1:1 旧版 CastingRecipe / PreferenceCastingRecipe）：
 * <ul>
 *   <li>静态输出：{@code result={"item":..}}，输出固定物品；</li>
 *   <li>动态输出：{@code result={"tag":"c:ingots/iron"}}，运行时取 tag 内首选
 *   物品（1:1 旧版 PreferenceCastingRecipe 的 oredict 首选——任何 mod 的
 *   铁锭都能被铸出，配合"万物皆可铸"）。</li>
 * </ul>
 * 模具形状 = {@code ResourceLocation}（部件注册名或铸造形状 ingot/nugget/gem/plate/gear），
 * {@code null} 表示无模具（铸造盆浇铸块）。</p>
 */
public class CastingRecipe implements Recipe<CastingRecipeInput> {

    /** 模具：左=形状 ID（部件注册名或铸造形状），右=物品 tag（如染色陶瓦/沙）；null=无模具（铸造盆）。 */
    private final Either<ResourceLocation, TagKey<Item>> cast;
    private final FluidStack fluid;
    private final Either<ItemStack, TagKey<Item>> output;
    private final int time;
    private final boolean consumesCast;
    private final boolean switchOutputs;

    public CastingRecipe(Either<ResourceLocation, TagKey<Item>> cast, FluidStack fluid,
                         Either<ItemStack, TagKey<Item>> output, int time,
                         boolean consumesCast, boolean switchOutputs) {
        this.cast = cast;
        this.fluid = fluid;
        this.output = output;
        this.time = time;
        this.consumesCast = consumesCast;
        this.switchOutputs = switchOutputs;
    }

    /** 形状模具构造（旧版 CastingRecipe）。 */
    public static CastingRecipe ofShape(ResourceLocation castShape, FluidStack fluid, ItemStack output,
                                        boolean consumesCast, boolean switchOutputs) {
        return new CastingRecipe(castShape == null ? null : Either.left(castShape), fluid, Either.left(output),
                calcCooldownTime(fluid), consumesCast, switchOutputs);
    }

    /** tag 模具构造（旧版 RecipeMatch.of(物品) 作模具，如染色陶瓦/沙，须消耗）。 */
    public static CastingRecipe ofTag(TagKey<Item> castTag, FluidStack fluid, ItemStack output,
                                      int time, boolean consumesCast, boolean switchOutputs) {
        return new CastingRecipe(Either.right(castTag), fluid, Either.left(output),
                time, consumesCast, switchOutputs);
    }

    /** 动态输出构造（旧版 PreferenceCastingRecipe：输出 = tag 首选物品）。 */
    public static CastingRecipe ofTagOutput(ResourceLocation castShape, FluidStack fluid, TagKey<Item> outputTag,
                                            boolean consumesCast, boolean switchOutputs) {
        return new CastingRecipe(castShape == null ? null : Either.left(castShape), fluid, Either.right(outputTag),
                calcCooldownTime(fluid), consumesCast, switchOutputs);
    }

    /** 模具（形状 or tag）；null = 无模具。 */
    public Either<ResourceLocation, TagKey<Item>> getCast() {
        return cast;
    }

    /** 形状模具 ID；tag 模具或无模具返回 null。 */
    public ResourceLocation getCastShape() {
        return cast == null ? null : cast.left().orElse(null);
    }

    /** 模具 tag；形状模具或无模具返回 null。 */
    public TagKey<Item> getCastTag() {
        return cast == null ? null : cast.right().orElse(null);
    }

    public FluidStack getFluid() {
        return fluid;
    }

    public Either<ItemStack, TagKey<Item>> getOutput() {
        return output;
    }

    public int getTime() {
        return time;
    }

    /** 覆盖冷却时间（DataGen 显式时间用，1:1 旧版显式 time 构造）。 */
    public CastingRecipe withTime(int time) {
        return new CastingRecipe(cast, fluid, output, time, consumesCast, switchOutputs);
    }

    public boolean consumesCast() {
        return consumesCast;
    }

    public boolean switchOutputs() {
        return switchOutputs;
    }

    public int getFluidAmount() {
        return fluid.getAmount();
    }

    /** 输出物品（动态输出取 tag 首选物品，tag 空 → 空栈；1:1 旧版 getResult）。 */
    public ItemStack getResult() {
        return output.map(ItemStack::copy, tag -> {
            Registry<Item> registry = BuiltInRegistries.ITEM;
            Optional<net.minecraft.core.HolderSet.Named<Item>> holders = registry.getTag(tag);
            if (holders.isEmpty()) {
                return ItemStack.EMPTY;
            }
            return holders.get().stream()
                    .findFirst()
                    .map(holder -> new ItemStack(holder.value()))
                    .orElse(ItemStack.EMPTY);
        });
    }

    @Override
    public boolean matches(CastingRecipeInput recipeInput, Level level) {
        // 模具匹配：无模具配方要求空模具（铸造盆）；形状模具校验 PATTERN_SHAPE；tag 模具校验物品 tag
        ItemStack castStack = recipeInput.cast();
        if (cast == null) {
            if (!castStack.isEmpty()) {
                return false;
            }
        } else if (cast.left().isPresent()) {
            ResourceLocation shape = cast.left().get();
            if (castStack.isEmpty() || !(castStack.getItem() instanceof PatternItem)
                    || !shape.equals(castStack.get(ModDataComponents.PATTERN_SHAPE))) {
                return false;
            }
        } else {
            if (!castStack.is(cast.right().get())) {
                return false;
            }
        }
        // 流体匹配：类型一致即可（量由冶炼炉按 getFluidAmount 校验，1:1 旧版）
        return fluid.getFluid() == recipeInput.fluid().getFluid();
    }

    @Override
    public ItemStack assemble(CastingRecipeInput recipeInput, HolderLookup.Provider registries) {
        return getResult();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return getResult();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.CASTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.CASTING_TYPE.get();
    }

    /** 特殊配方：不进配方书（冶炼炉 GUI 展示，1:1 旧版）。 */
    @Override
    public boolean isSpecial() {
        return true;
    }

    /** 冷却时间（1:1 旧版 calcCooldownTime：最低 24 tick + 温升 × 量 / 1600）。 */
    public static int calcCooldownTime(FluidStack fluid) {
        int time = 24;
        int temperature = fluid.getFluid().getFluidType().getTemperature() - 300;
        return time + (temperature * fluid.getAmount()) / 1600;
    }

    @Override
    public String toString() {
        return "casting(" + (cast == null ? "basin" : cast) + " + " + fluid + " -> " + output + ')';
    }
}
