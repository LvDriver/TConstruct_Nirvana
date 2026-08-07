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

    /** 浇铸台方块实体（会话8，1:1 旧版 TileCastingTable）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingBlockEntity>> CASTING_TABLE =
            BLOCK_ENTITIES.register("casting_table",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new CastingBlockEntity(ModBlockEntities.CASTING_TABLE.get(), pos, state, false),
                            ModBlocks.CASTING_TABLE.get())
                            .build(null));

    /** 浇铸盆方块实体（会话8，1:1 旧版 TileCastingBasin）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CastingBlockEntity>> CASTING_BASIN =
            BLOCK_ENTITIES.register("casting_basin",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new CastingBlockEntity(ModBlockEntities.CASTING_BASIN.get(), pos, state, true),
                            ModBlocks.CASTING_BASIN.get())
                            .build(null));

    /** 龙头方块实体（会话8，1:1 旧版 TileFaucet）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileFaucet>> FAUCET =
            BLOCK_ENTITIES.register("faucet",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new TileFaucet(ModBlockEntities.FAUCET.get(), pos, state),
                            ModBlocks.FAUCET.get())
                            .build(null));

    /** 沟槽方块实体（会话8，1:1 旧版 TileChannel）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileChannel>> CHANNEL =
            BLOCK_ENTITIES.register("channel",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new TileChannel(ModBlockEntities.CHANNEL.get(), pos, state),
                            ModBlocks.CHANNEL.get())
                            .build(null));

    /** 排液口方块实体（会话8，1:1 旧版 TileDrain）。 */
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileDrain>> DRAIN =
            BLOCK_ENTITIES.register("drain",
                    () -> BlockEntityType.Builder.of(
                            (pos, state) -> new TileDrain(ModBlockEntities.DRAIN.get(), pos, state),
                            ModBlocks.DRAIN.get())
                            .build(null));

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
