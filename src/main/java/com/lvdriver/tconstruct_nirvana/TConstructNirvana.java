package com.lvdriver.tconstruct_nirvana;

import com.lvdriver.tconstruct_nirvana.block.ModBlockEntities;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.config.TConConfig;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.item.ModCreativeTabs;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import com.lvdriver.tconstruct_nirvana.recipe.ModRecipeTypes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tinkers' Construct: Nirvana
 *
 * <p>基于匠魂怀古（Tinkers' Antique，1.12.2 Forge）移植到 NeoForge 1.21.1。
 * 本类是 Mod 入口，只负责注册各子系统与配置，业务逻辑见各子包。</p>
 */
@Mod(TConstructNirvana.MODID)
public class TConstructNirvana {

    public static final String MODID = "tconstruct_nirvana";
    public static final Logger LOGGER = LoggerFactory.getLogger(TConstructNirvana.class);

    public TConstructNirvana(IEventBus modEventBus, ModContainer modContainer) {
        // 注册表一律使用 DeferredRegister，集中注册
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModFluids.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        // 材料系统：静态注册全部材料与属性数据（1:1 自 Tinkers' Antique）
        ModMaterials.init();

        // 配置文件（矿物生成开关等）
        modContainer.registerConfig(ModConfig.Type.COMMON, TConConfig.SPEC);

        LOGGER.info("{} initialized", MODID);
    }
}
