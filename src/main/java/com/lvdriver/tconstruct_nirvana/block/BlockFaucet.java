package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * 龙头方块（1:1 移植自 Tinkers' Antique {@code BlockFaucet}）。
 *
 * <p>从朝向面（{@code facing}）的流体容器抽液，向下方的浇铸台/盆/沟槽浇注。
 * 右键触发一次浇铸（再点停止）；红石上升沿触发（延迟 2 tick，1:1 旧版
 * scheduleUpdate）。形状为嘴部朝向各面的细管（1:1 旧版 BOUNDS）。</p>
 */
public class BlockFaucet extends Block implements net.minecraft.world.level.block.EntityBlock {

    /** 输入方向（除 DOWN 外任意方向；DOWN 恒为输出方向）。 */
    public static final DirectionProperty FACING = DirectionProperty.create("facing",
            Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);

    /** 嘴部朝向形状（1:1 旧版 BOUNDS）。 */
    private static final VoxelShape SHAPE_UP = box(4, 10, 4, 12, 16, 12);
    private static final VoxelShape SHAPE_NORTH = box(4, 4, 0, 12, 10, 6);
    private static final VoxelShape SHAPE_SOUTH = box(4, 4, 10, 12, 10, 16);
    private static final VoxelShape SHAPE_EAST = box(10, 4, 4, 16, 10, 12);
    private static final VoxelShape SHAPE_WEST = box(0, 4, 4, 6, 10, 12);

    public BlockFaucet(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getClickedFace().getOpposite();
        // 朝下放置 → 改为玩家朝向反向（1:1 旧版）
        if (facing == Direction.DOWN) {
            facing = context.getPlayer() == null ? Direction.UP : context.getPlayer().getDirection().getOpposite();
        }
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.getBlockEntity(pos) instanceof TileFaucet faucet) {
            faucet.activate();
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileFaucet(ModBlockEntities.FAUCET.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        if (type == ModBlockEntities.FAUCET.get()) {
            BlockEntityTicker<TileFaucet> ticker = TileFaucet::tick;
            return (BlockEntityTicker<T>) (BlockEntityTicker<?>) ticker;
        }
        return null;
    }

    /* ---------- 红石（1:1 旧版） ---------- */

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
        return true;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof TileFaucet faucet) {
            faucet.handleRedstone(level.hasNeighborSignal(pos));
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getBlockEntity(pos) instanceof TileFaucet faucet) {
            faucet.activate();
        }
    }

    /* ---------- 形状 ---------- */

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case UP -> SHAPE_UP;
            case NORTH -> SHAPE_NORTH;
            case SOUTH -> SHAPE_SOUTH;
            case EAST -> SHAPE_EAST;
            default -> SHAPE_WEST;
        };
    }

    /** 龙头方块属性（1:1 旧版 hardness=3/resistance=20/SoundType.METAL）。 */
    public static Block.Properties faucetProperties() {
        return Block.Properties.of()
                .noOcclusion()
                .strength(3.0F, 20.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL);
    }
}
