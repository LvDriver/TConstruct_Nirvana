package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体注册中枢（DeferredRegister）。
 *
 * <p>会话4.5b：工具站/锻造厂方块实体（组装 GUI 容器持有者）。</p>
 */
public final class ModBlockEntities {

    /** 方块实体注册表。 */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TConstructNirvana.MODID);

    /** 工具站/锻造厂方块实体（共用，1:1 旧版 TileToolStation/TileToolForge 简化）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ToolTableBlockEntity>> TOOL_TABLE =
            BLOCK_ENTITIES.register("tool_table",
                    () -> BlockEntityType.Builder.of(ToolTableBlockEntity::new,
                                    ModBlocks.TOOL_STATION.get(), ModBlocks.TOOL_FORGE.get())
                            .build(null));

    /** 冶炼炉控制器方块实体（多方块主机，会话7）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileSmeltery>> SMELTERY =
            BLOCK_ENTITIES.register("smeltery",
                    () -> BlockEntityType.Builder.of(TileSmeltery::new, ModBlocks.SMELTERY_CONTROLLER.get())
                            .build(null));

    /** seared 储罐方块实体（会话7）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTank>> TANK =
            BLOCK_ENTITIES.register("tank",
                    () -> BlockEntityType.Builder.of(TileTank::new, ModBlocks.SEARED_TANK.get())
                            .build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
