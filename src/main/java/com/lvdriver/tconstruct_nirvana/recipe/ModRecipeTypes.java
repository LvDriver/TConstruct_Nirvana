package com.lvdriver.tconstruct_nirvana.recipe;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 配方类型注册中枢（DeferredRegister）。
 *
 * <p>当前为骨架：本会话只建注册表，冶炼/铸造/合金等自定义配方类型在"配方系统"会话中填充。</p>
 */
public final class ModRecipeTypes {

    /** 配方类型注册表。 */
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, TConstructNirvana.MODID);

    private ModRecipeTypes() {
    }

    public static void register(IEventBus modEventBus) {
        RECIPE_TYPES.register(modEventBus);
    }
}
