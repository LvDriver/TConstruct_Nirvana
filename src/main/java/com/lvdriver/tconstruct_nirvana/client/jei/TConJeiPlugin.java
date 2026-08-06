package com.lvdriver.tconstruct_nirvana.client.jei;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.recipe.AlloyRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.CastingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.MeltingRecipe;
import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import com.lvdriver.tconstruct_nirvana.recipe.PartRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * JEI 集成插件（软依赖：compileOnly 引用 JEI API，运行时缺失 JEI 不影响加载）。
 *
 * <p>注册 4 个配方分类：熔炼（物品 → 流体）、浇铸（模具 + 流体 → 物品）、
 * 部件制作（模具 + 材料 → 部件）、合金（多流体 → 流体）。配方数据直接读
 * 数据包（DataGen 生成的 recipe JSON），客户端 JEI 打开时展示。</p>
 */
@JeiPlugin
public class TConJeiPlugin implements IModPlugin {

    public static final ResourceLocation UID =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "jei");

    public static final RecipeType<RecipeHolder<MeltingRecipe>> MELTING =
            RecipeType.createFromVanilla(ModRecipeTypes.MELTING_TYPE.get());
    public static final RecipeType<RecipeHolder<CastingRecipe>> CASTING =
            RecipeType.createFromVanilla(ModRecipeTypes.CASTING_TYPE.get());
    public static final RecipeType<RecipeHolder<PartRecipe>> PART =
            RecipeType.createFromVanilla(ModRecipeTypes.PART_TYPE.get());
    public static final RecipeType<RecipeHolder<AlloyRecipe>> ALLOY =
            RecipeType.createFromVanilla(ModRecipeTypes.ALLOY_TYPE.get());

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new MeltingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new CastingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PartRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new AlloyRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Level level = Minecraft.getInstance().level;
        if (level == null) {
            return;
        }
        var recipeManager = level.getRecipeManager();
        registration.addRecipes(MELTING, recipeManager.getAllRecipesFor(ModRecipeTypes.MELTING_TYPE.get()));
        registration.addRecipes(CASTING, recipeManager.getAllRecipesFor(ModRecipeTypes.CASTING_TYPE.get()));
        registration.addRecipes(PART, recipeManager.getAllRecipesFor(ModRecipeTypes.PART_TYPE.get()));
        registration.addRecipes(ALLOY, recipeManager.getAllRecipesFor(ModRecipeTypes.ALLOY_TYPE.get()));
    }
}
