package com.lvdriver.tconstruct_nirvana.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 浇铸台/浇铸盆方块（1:1 移植自 Tinkers' Antique {@code BlockCasting}）。
 *
 * <p>旧版单方块 + TYPE（TABLE/BASIN）两态；1.21.1 无 meta，注册为两个独立方块
 * （casting_table / casting_basin）共用本类，以 {@link #basin} 构造标志区分。
 * 多 AABB 形状（桌面/盆体 + 4 腿，1:1 旧版 BOUNDS_Table/BOUNDS_Basin）；
 * 输出槽有物品时输出红石比较器信号 15。</p>
 *
 * <p>注：{@code codec()} 用于方块状态序列化（/setblock 等走注册表 ID，不依赖
 * codec 重建标志），故返回默认构造（basin=false）。</p>
 */
public class BlockCasting extends BaseEntityBlock {

    /** 浇铸台形状（1:1 旧版 BOUNDS_Table：桌面 0.625~1 + 4 腿）。 */
    private static final VoxelShape SHAPE_TABLE = Shapes.or(
            box(0, 10, 0, 16, 16, 16),
            box(0, 0, 0, 4, 10, 4),
            box(12, 0, 0, 16, 10, 4),
            box(12, 0, 12, 16, 10, 16),
            box(0, 0, 12, 4, 10, 16));

    /** 浇铸盆形状（1:1 旧版 BOUNDS_Basin：盆体 0.25~1 + 4 腿）。 */
    private static final VoxelShape SHAPE_BASIN = Shapes.or(
            box(0, 4, 0, 16, 16, 16),
            box(0, 0, 0, 5, 4, 5),
            box(11, 0, 0, 16, 4, 5),
            box(11, 0, 11, 16, 4, 16),
            box(0, 0, 11, 5, 4, 16));

    private final boolean basin;

    public BlockCasting(Properties properties, boolean basin) {
        super(properties);
        this.basin = basin;
    }

    /** 默认构造（codec/属性占位，实际注册用双参构造）。 */
    public BlockCasting(Properties properties) {
        this(properties, false);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(BlockCasting::new);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CastingBlockEntity(
                basin ? ModBlockEntities.CASTING_BASIN.get() : ModBlockEntities.CASTING_TABLE.get(),
                pos, state, basin);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type,
                basin ? ModBlockEntities.CASTING_BASIN.get() : ModBlockEntities.CASTING_TABLE.get(),
                CastingBlockEntity::tick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        // 潜行不拦截（1:1 旧版）
        if (player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }
        if (level.getBlockEntity(pos) instanceof CastingBlockEntity casting) {
            casting.interact(player);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    /**
     * 桶/流体容器交互（倒入/取出）：旧版 1.12 由 ItemBucket.onItemUse 检查方块
     * capability 自动处理；1.21.1 桶交互移到方块侧（useItemOn），缺失会导致
     * 手持熔融金属桶右键只把桶当物品放入槽内、液体进不来。非流体容器回退
     * 默认（useWithoutItem → interact 放/取物品，1:1 旧版）。
     */
    @Override
    protected net.minecraft.world.ItemInteractionResult useItemOn(
            net.minecraft.world.item.ItemStack stack, BlockState state, Level level, BlockPos pos,
            Player player, net.minecraft.world.InteractionHand hand, net.minecraft.world.phys.BlockHitResult hitResult) {
        net.neoforged.neoforge.fluids.capability.IFluidHandler handler =
                level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                        pos, hitResult.getDirection());
        if (handler != null && net.neoforged.neoforge.fluids.FluidUtil.interactWithFluidHandler(player, hand, handler)) {
            return net.minecraft.world.ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    /** 红石比较器支持（1:1 旧版 hasComparatorInputOverride）。 */
    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof CastingBlockEntity casting ? casting.comparatorStrength() : 0;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, net.minecraft.world.phys.shapes.CollisionContext context) {
        return basin ? SHAPE_BASIN : SHAPE_TABLE;
    }

    /** 龙头滴液深度（1:1 旧版 getFlowDepth，客户端龙头渲染用）。 */
    public float getFlowDepth() {
        return basin ? CastingBlockEntity.FLOW_DEPTH_BASIN : CastingBlockEntity.FLOW_DEPTH_TABLE;
    }

    /** 浇铸台/盆方块属性（1:1 旧版 hardness=3/resistance=20）。 */
    public static net.minecraft.world.level.block.state.BlockBehaviour.Properties castingProperties() {
        return net.minecraft.world.level.block.state.BlockBehaviour.Properties.of()
                .noOcclusion()
                .strength(3.0F, 20.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }
}
