package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.block.TileSmeltery;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * 冶炼炉菜单（1:1 移植自 Tinkers' Antique {@code ContainerSmeltery} 简化版）。
 *
 * <p>冶炼炉物品栏大小 = 结构内部尺寸（宽×高×深，最多 9×9×9）；GUI 显示
 * 3 列 × 9 行 = 27 个可见槽（侧栏），通过滚动按钮翻页（1:1 旧版
 * ContainerSideInventory 滚动）。流体/燃料经菜单数据同步。</p>
 */
public class ContainerSmeltery extends AbstractContainerMenu {

    /** 可见槽数（3 列 × 9 行，1:1 旧版 calcColumns=3）。 */
    public static final int VISIBLE_SLOTS = 27;

    private final TileSmeltery tile;
    private final ContainerLevelAccess access;

    /** 侧栏滚动偏移（服务端权威）。 */
    private int scrollOffset;
    /** 可见槽视图（委托真实物品栏 + 偏移）。 */
    private final Container view;

    public ContainerSmeltery(int id, Inventory playerInventory, TileSmeltery tile, ContainerLevelAccess access) {
        super(com.lvdriver.tconstruct_nirvana.gui.ModMenuTypes.SMELTERY.get(), id);
        this.tile = tile;
        this.access = access;

        // 可见槽视图：读取真实物品栏 offset+index
        this.view = new SimpleContainer(VISIBLE_SLOTS) {
            @Override
            public ItemStack getItem(int index) {
                int real = scrollOffset + index;
                if (real >= tile.getSizeInventory()) {
                    return ItemStack.EMPTY;
                }
                return tile.getStackInSlot(real);
            }

            @Override
            public int getContainerSize() {
                return Math.min(VISIBLE_SLOTS, tile.getSizeInventory() - scrollOffset);
            }

            @Override
            public ItemStack removeItem(int index, int count) {
                int real = scrollOffset + index;
                return real < tile.getSizeInventory() ? tile.getInventory().removeItem(real, count) : ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItemNoUpdate(int index) {
                int real = scrollOffset + index;
                return real < tile.getSizeInventory() ? tile.getInventory().removeItemNoUpdate(real) : ItemStack.EMPTY;
            }

            @Override
            public void setItem(int index, ItemStack stack) {
                int real = scrollOffset + index;
                if (real < tile.getSizeInventory()) {
                    tile.setInventorySlotContents(real, stack);
                }
            }

            @Override
            public boolean isEmpty() {
                return tile.getSizeInventory() == 0;
            }
        };

        // 侧栏可见槽（3 列 × 9 行）
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 3; col++) {
                addSlot(new Slot(view, col + row * 3, 8 + col * 18, 16 + row * 18));
            }
        }

        // 玩家背包（3×9）+ 快捷栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 84 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 84 + col * 18, 142));
        }
    }

    /** 客户端构造（MenuType 工厂）：空 BE，槽内容由服务端广播同步。 */
    public ContainerSmeltery(int id, Inventory playerInventory) {
        this(id, playerInventory, null, ContainerLevelAccess.NULL);
    }

    /** 服务端构造（方块右键打开）：真实 BE。 */
    public ContainerSmeltery(int id, Inventory playerInventory, TileSmeltery tile) {
        this(id, playerInventory, tile, ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()));
    }

    public TileSmeltery getTile() {
        return tile;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    /** 滚动（1:1 旧版侧栏滚动），返回是否成功。 */
    public boolean scroll(int amount) {
        int max = Math.max(0, tile.getSizeInventory() - VISIBLE_SLOTS);
        int newOffset = Math.max(0, Math.min(max, scrollOffset + amount));
        if (newOffset != scrollOffset) {
            scrollOffset = newOffset;
            return true;
        }
        return false;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        // 0 = 上滚，1 = 下滚
        if (id == 0) {
            return scroll(-VISIBLE_SLOTS);
        }
        if (id == 1) {
            return scroll(VISIBLE_SLOTS);
        }
        // 2+ = 点击液体层装桶（1:1 旧版 handleTankClick）
        if (tile != null) {
            return tile.fillBucketFromTank(player, id - 2);
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        // 简化：不实现 Shift 快速移动（同工具站）
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player player) {
        if (tile == null) {
            return false;
        }
        return access.evaluate((level, pos) ->
                level.getBlockEntity(pos) instanceof com.lvdriver.tconstruct_nirvana.block.TileSmeltery, true);
    }
}
