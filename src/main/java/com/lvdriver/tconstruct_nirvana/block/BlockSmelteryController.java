package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 冶炼炉控制器方块（1:1 移植自 Tinkers' Antique {@code BlockSmelteryController}）。
 *
 * <p>BE = {@link TileSmeltery}；成型后随机喷火粒子（1:1 旧版 randomDisplayTick）。
 * 属性：硬度 3 / 抗爆 20 / 金属音。</p>
 */
public class BlockSmelteryController extends BlockMultiblockController {

    public BlockSmelteryController(Properties properties) {
        super(properties);
    }

    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties controllerProperties() {
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                .strength(3.0F, 20.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileSmeltery(pos, state);
    }

    @Override
    protected com.mojang.serialization.MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BlockSmelteryController::new);
    }

    /** 服务端 tick（1:1 旧版 ITickable.update）。 */
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            net.minecraft.world.level.Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        if (level.isClientSide) {
            return null;
        }
        return (lvl, pos, st, be) -> {
            if (be instanceof TileSmeltery smeltery) {
                smeltery.tickServer();
            }
        };
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return (MenuProvider) level.getBlockEntity(pos);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        if (isActive(level, pos)) {
            Direction facing = state.getValue(FACING);
            double d0 = pos.getX() + 0.5D;
            double d1 = pos.getY() + 0.5D + (rand.nextFloat() * 6F) / 16F;
            double d2 = pos.getZ() + 0.5D;
            double d3 = 0.52D;
            double d4 = rand.nextDouble() * 0.6D - 0.3D;
            switch (facing) {
                case WEST -> {
                    level.addParticle(ParticleTypes.SMOKE, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    level.addParticle(ParticleTypes.FLAME, d0 - d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                }
                case EAST -> {
                    level.addParticle(ParticleTypes.SMOKE, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                    level.addParticle(ParticleTypes.FLAME, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
                }
                case NORTH -> {
                    level.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
                    level.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 - d3, 0.0D, 0.0D, 0.0D);
                }
                case SOUTH -> {
                    level.addParticle(ParticleTypes.SMOKE, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
                    level.addParticle(ParticleTypes.FLAME, d0 + d4, d1, d2 + d3, 0.0D, 0.0D, 0.0D);
                }
                default -> {
                }
            }
        }
    }
}
