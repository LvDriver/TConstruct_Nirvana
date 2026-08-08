package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.SimpleContainer;

/**
 * 工具站方块实体（1:1 移植自 Tinkers' Antique {@code TileToolStation} 简化版）。
 *
 * <p>持有 6 个输入槽（{@link #INPUT_SLOTS}：工具槽 + 5 个材料槽），实现
 * {@link MenuProvider} 打开组装菜单；无 ticker。破坏掉落与容器网络同步留待后续会话。</p>
 */
public class ToolTableBlockEntity extends BlockEntity implements MenuProvider {

    /** 输入槽数（1:1 旧版工具站：工具槽 + 5 材料槽；锻造厂旧版 5×5 简化同 5 材料槽）。 */
    public static final int INPUT_SLOTS = 6;

    private final SimpleContainer parts = new SimpleContainer(INPUT_SLOTS);

    public ToolTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TOOL_TABLE.get(), pos, state);
    }

    public SimpleContainer getParts() {
        return parts;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(this.getBlockState().getBlock().getDescriptionId());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new com.lvdriver.tconstruct_nirvana.gui.TinkerStationMenu(
                id, playerInventory, parts, net.minecraft.world.inventory.ContainerLevelAccess.create(this.level, this.worldPosition), this);
    }

    /* ---------- NBT（1:1 旧版：部件槽随方块实体保存） ---------- */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        net.minecraft.world.ContainerHelper.saveAllItems(tag, parts.getItems(), registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        net.minecraft.world.ContainerHelper.loadAllItems(tag, parts.getItems(), registries);
    }
}
