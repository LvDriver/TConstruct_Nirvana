package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 方块实体注册中枢（DeferredRegister）。
 *
 * <p>当前为骨架：本会话只建注册表，具体方块实体（冶炼炉等）在后续会话中填充。</p>
 */
public final class ModBlockEntities {

    /** 方块实体注册表。 */
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TConstructNirvana.MODID);

    private ModBlockEntities() {
    }

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
