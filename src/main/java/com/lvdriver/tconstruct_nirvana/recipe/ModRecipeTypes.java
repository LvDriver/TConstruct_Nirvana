package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 配方类型/序列化器注册中枢（DeferredRegister）。
 *
 * <p>工具组装配方（ToolBuildRecipeSerializer）、工具修复（RepairRecipeSerializer）、
 * 合金配方（AlloyRecipeSerializer：输入流体 → 输出流体，冶炼炉会话接入触发点）。</p>
 */
public final class ModRecipeTypes {

    /** 配方类型注册表。 */
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TConstructNirvana.MODID);

    /** 配方序列化器注册表。 */
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, TConstructNirvana.MODID);

    /** 工具组装配方序列化器（工作台摆部件 → 出工具）。 */
    public static final DeferredHolder<RecipeSerializer<?>, ToolBuildRecipeSerializer> TOOL_BUILD_SERIALIZER =
            RECIPE_SERIALIZERS.register("tool_build", () -> ToolBuildRecipeSerializer.INSTANCE);

    /** 工具修复配方序列化器（工具 + 磨刀石 → 修复，会话4.5b）。 */
    public static final DeferredHolder<RecipeSerializer<?>, RepairRecipeSerializer> REPAIR_SERIALIZER =
            RECIPE_SERIALIZERS.register("repair", () -> RepairRecipeSerializer.INSTANCE);

    /** 合金配方类型（输入流体 → 输出流体）。 */
    public static final DeferredHolder<RecipeType<?>, RecipeType<AlloyRecipe>> ALLOY_TYPE =
            RECIPE_TYPES.register("alloy", () -> RecipeType.simple(
                    ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "alloy")));

    /** 合金配方序列化器。 */
    public static final DeferredHolder<RecipeSerializer<?>, AlloyRecipeSerializer> ALLOY_SERIALIZER =
            RECIPE_SERIALIZERS.register("alloy", () -> AlloyRecipeSerializer.INSTANCE);

    private ModRecipeTypes() {
    }

    public static void register(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
