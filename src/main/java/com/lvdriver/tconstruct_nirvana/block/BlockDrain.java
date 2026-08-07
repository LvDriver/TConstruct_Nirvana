package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.fluids.FluidUtil;
import org.jetbrains.annotations.Nullable;

/**
 * 排液口方块（1:1 移植自 Tinkers' Antique {@code BlockSmelteryIO}）。
 *
 * <p>冶炼炉墙体上的流体接口：朝向为水平 4 向（玩家放置时面向玩家）。
 * 手持桶右键交互经 {@code FluidUtil}；交互方向为 null（1:1 旧版
 * getCapability(facing=null)）命中 {@link TileDrain} 的只读包装——
 * 与旧版一致：玩家桶只能抽取装桶，倒液请经侧向管道（side != null 走完整主罐）。</p>
 */
public class BlockDrain extends Block implements net.minecraft.world.level.block.EntityBlock {

    /** 朝向（1:1 旧版 FACING，仅水平）。 */
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockDrain(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // 面向玩家（1:1 旧版：玩家朝向反向）
        Direction facing = context.getPlayer() == null ? Direction.NORTH : context.getPlayer().getDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileDrain(ModBlockEntities.DRAIN.get(), pos, state);
    }

    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                                   Player player, InteractionHand hand, BlockHitResult hit) {
        // 桶/流体容器交互（1:1 旧版 FluidUtil.tryEmptyContainerAndStow；true=交互已处理）
        boolean handled = FluidUtil.interactWithFluidHandler(player, hand, level, pos, null);
        return handled ? net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide)
                : net.minecraft.world.ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /** 排液口方块属性（1:1 旧版 BlockEnumSmeltery：硬度 3 / 抗爆 20 / 金属音）。 */
    public static Block.Properties drainProperties() {
        return Block.Properties.of()
                .strength(3.0F, 20.0F)
                .sound(SoundType.METAL);
    }
}
