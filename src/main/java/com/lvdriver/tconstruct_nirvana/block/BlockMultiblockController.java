package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.multiblock.TileMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 多方块控制器方块基类（1:1 移植自 Tinkers' Antique
 * {@code BlockMultiblockController}）。
 *
 * <p>控制器带 FACING（水平朝向）+ ACTIVE（结构是否成型）两个状态；
 * 放置时立即检测结构；未成型时右键不打开 GUI（1:1 旧版 openGui 前置校验）；
 * 不响应旋转（防止旋转破坏结构数据，1:1 旧版 rotateBlock=false）。</p>
 */
public abstract class BlockMultiblockController extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    /** 1.21.1 BlockStateProperties 无 ACTIVE 常量，自定义（同旧版 PropertyBool "active"）。 */
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    protected BlockMultiblockController(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE);
    }

    protected TileMultiblock getTile(BlockGetter world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof TileMultiblock tile ? tile : null;
    }

    public boolean isActive(BlockGetter world, BlockPos pos) {
        TileMultiblock te = getTile(world, pos);
        return te != null && te.isActive();
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        if (!isActive(level, pos)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            MenuProvider provider = state.getMenuProvider(level, pos);
            if (provider != null) {
                player.openMenu(provider);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        // 放置后立即检测结构（1:1 旧版 onBlockPlacedBy）
        TileMultiblock te = getTile(level, pos);
        if (te != null) {
            te.checkMultiblockStructure();
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    /** 不响应旋转：旋转会破坏已成型结构（1:1 旧版 rotateBlock=false）。 */
    @Override
    protected BlockState rotate(BlockState state, net.minecraft.world.level.block.Rotation rotation) {
        return state;
    }

    @Override
    protected BlockState mirror(BlockState state, net.minecraft.world.level.block.Mirror mirror) {
        return state;
    }
}
