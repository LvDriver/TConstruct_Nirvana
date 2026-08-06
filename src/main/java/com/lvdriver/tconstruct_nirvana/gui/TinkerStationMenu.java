package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具站/锻造厂菜单（1:1 移植自 Tinkers' Antique {@code ContainerToolStation} 简化版）。
 *
 * <p>5 个部件槽 + 1 个结果槽（实时预览）；拿走结果即消耗部件（1:1 旧版
 * {@code SlotToolStationOut} 行为）。组装逻辑复用
 * {@link TinkerToolItem#buildItemFromStacks}（工作台组装配方同源）。
 * 客户端由 MenuType 工厂以空容器重建，槽内容经服务端广播同步。</p>
 */
public class TinkerStationMenu extends AbstractContainerMenu {

    /** 部件槽数。 */
    public static final int PART_SLOTS = 5;

    private final SimpleContainer parts;
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;

    /** 服务端构造：直接持有方块实体容器。 */
    public TinkerStationMenu(int id, Inventory playerInventory, SimpleContainer parts, ContainerLevelAccess access, BlockEntity blockEntity) {
        super(ModMenuTypes.TOOL_STATION.get(), id);
        this.parts = parts;
        this.access = access;
        // 部件变化 → 重算结果 + 标记方块实体脏（服务器保存时部件槽不丢失）
        parts.addListener(container -> {
            this.slotsChanged(container);
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        });

        // 5 个部件槽（横排）
        for (int i = 0; i < PART_SLOTS; i++) {
            addSlot(new Slot(parts, i, 26 + i * 18, 34));
        }
        // 结果槽：不可放入，取走即消耗部件（1:1 旧版 SlotToolStationOut）
        addSlot(new Slot(result, 0, 134, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                for (int i = 0; i < PART_SLOTS; i++) {
                    parts.setItem(i, ItemStack.EMPTY);
                }
                result.clearContent();
                super.onTake(player, stack);
            }
        });

        // 玩家背包（3×9）+ 快捷栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    /** 客户端构造（MenuType 工厂）：空容器占位，槽内容由服务端同步。 */
    public TinkerStationMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(PART_SLOTS), ContainerLevelAccess.NULL, null);
    }

    /** 部件变化 → 重算结果（1:1 旧版 SlotToolStationIn 的 onSlotChanged + updateResult）。 */
    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        // 从槽 0 起收集连续非空部件（1:1 旧版 tryBuildTool 的连续性校验）
        List<ItemStack> stacks = new ArrayList<>();
        boolean seenGap = false;
        for (int i = 0; i < PART_SLOTS; i++) {
            ItemStack stack = parts.getItem(i);
            if (stack.isEmpty()) {
                if (!stacks.isEmpty()) {
                    seenGap = true;
                }
            } else {
                if (seenGap) {
                    result.setItem(0, ItemStack.EMPTY);
                    return;
                }
                stacks.add(stack);
            }
        }
        if (stacks.size() < 2) {
            result.setItem(0, ItemStack.EMPTY);
            return;
        }
        for (TinkerToolItem tool : com.lvdriver.tconstruct_nirvana.item.tool.ModTools.getAllTools()) {
            ItemStack output = tool.buildItemFromStacks(stacks);
            if (!output.isEmpty()) {
                result.setItem(0, output);
                return;
            }
        }
        result.setItem(0, ItemStack.EMPTY);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // 简化：不实现 Shift 快速移动（1:1 旧版有，后续会话补）
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.BlockToolTable, true);
    }
}
