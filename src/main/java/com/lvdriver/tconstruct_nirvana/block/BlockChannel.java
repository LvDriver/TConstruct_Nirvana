package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.item.ItemChannel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 沟槽方块（1:1 移植自 Tinkers' Antique {@code BlockChannel}）。
 *
 * <p>流体分配通道：侧向连接（IN/OUT）与向下输出（DOWN）状态写入 BlockState
 * （1.21.1 无 getActualState，模型由属性驱动）；连接交互在
 * {@link TileChannel#interact}，放置自动连接在 {@link TileChannel#onPlaceBlock}
 * （经 {@code ItemChannel} 在放置后调用，1:1 旧版 ItemChannel）。</p>
 */
public class BlockChannel extends Block implements net.minecraft.world.level.block.EntityBlock {

    public static final BooleanProperty DOWN = BooleanProperty.create("down");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");

    /** 中央主体（1:1 旧版 BOUNDS_CENTER）。 */
    private static final VoxelShape SHAPE_CENTER = box(5, 4, 5, 11, 8, 11);
    /** 中央主体（无连接时的碰撞，1:1 旧版 BOUNDS_CENTER_UNCONNECTED）。 */
    private static final VoxelShape SHAPE_CENTER_UNCONNECTED = box(5, 2, 5, 11, 8, 11);
    /** 侧向支管（1:1 旧版 BOUNDS_NORTH 等）。 */
    private static final VoxelShape SHAPE_NORTH = box(5, 4, 0, 11, 8, 5);
    private static final VoxelShape SHAPE_SOUTH = box(5, 4, 11, 11, 8, 16);
    private static final VoxelShape SHAPE_WEST = box(0, 4, 5, 5, 8, 11);
    private static final VoxelShape SHAPE_EAST = box(11, 4, 5, 16, 8, 11);
    /** 向下喷口（1:1 旧版 BOUNDS 扩展：中央向下延伸到 0.125）。 */
    private static final VoxelShape SHAPE_DOWN = box(5, 0, 5, 11, 4, 11);

    public BlockChannel(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DOWN, false)
                .setValue(NORTH, false)
                .setValue(SOUTH, false)
                .setValue(WEST, false)
                .setValue(EAST, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DOWN, NORTH, SOUTH, WEST, EAST);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileChannel(ModBlockEntities.CHANNEL.get(), pos, state);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends BlockEntity> net.minecraft.world.level.block.entity.BlockEntityTicker<T> getTicker(
            Level level, BlockState state, net.minecraft.world.level.block.entity.BlockEntityType<T> type) {
        if (type == ModBlockEntities.CHANNEL.get()) {
            net.minecraft.world.level.block.entity.BlockEntityTicker<TileChannel> ticker = TileChannel::tick;
            return (net.minecraft.world.level.block.entity.BlockEntityTicker<T>)
                    (net.minecraft.world.level.block.entity.BlockEntityTicker<?>) ticker;
        }
        return null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // 手持沟槽时跳过（放第二个沟槽），点击顶部除外（1:1 旧版）
        if (player.getMainHandItem().getItem() instanceof ItemChannel && hit.getDirection() != Direction.UP) {
            return InteractionResult.PASS;
        }
        if (level.getBlockEntity(pos) instanceof TileChannel channel) {
            // 默认用点击面；顶/空 → 下（1:1 旧版）
            Direction side = hit.getDirection() == Direction.UP ? Direction.DOWN : hit.getDirection();
            return channel.interact(player, side) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean movedByPiston) {
        if (level.isClientSide) {
            return;
        }
        if (level.getBlockEntity(pos) instanceof TileChannel channel) {
            channel.handleBlockUpdate(neighborPos, level.hasNeighborSignal(pos));
        }
    }

    /* ---------- 形状（1:1 旧版 BOUNDS：中心 + 各连接支管） ---------- */

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        VoxelShape shape = state.getValue(DOWN) ? SHAPE_CENTER : SHAPE_CENTER_UNCONNECTED;
        if (state.getValue(NORTH)) {
            shape = Shapes.or(shape, SHAPE_NORTH);
        }
        if (state.getValue(SOUTH)) {
            shape = Shapes.or(shape, SHAPE_SOUTH);
        }
        if (state.getValue(WEST)) {
            shape = Shapes.or(shape, SHAPE_WEST);
        }
        if (state.getValue(EAST)) {
            shape = Shapes.or(shape, SHAPE_EAST);
        }
        if (state.getValue(DOWN)) {
            shape = Shapes.or(shape, SHAPE_DOWN);
        }
        return shape;
    }

    /** 龙头滴液深度（1:1 旧版 getFlowDepth）。 */
    public float getFlowDepth() {
        return 0.53125F;
    }

    /** 沟槽方块属性（1:1 旧版 hardness=3/resistance=20/SoundType.METAL）。 */
    public static Block.Properties channelProperties() {
        return Block.Properties.of()
                .noOcclusion()
                .strength(3.0F, 20.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL);
    }
}
