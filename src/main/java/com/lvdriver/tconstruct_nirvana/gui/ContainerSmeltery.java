package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.block.TileSmeltery;
import com.lvdriver.tconstruct_nirvana.smeltery.SmelteryTank;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 冶炼炉菜单（1:1 移植自 Tinkers' Antique {@code ContainerSmeltery} 简化版）。
 *
 * <p>冶炼炉物品栏大小 = 结构内部尺寸（宽×高×深，最多 9×9×9）；GUI 显示
 * 3 列 × 9 行 = 27 个可见槽（侧栏），通过滚动按钮翻页（1:1 旧版
 * ContainerSideInventory 滚动）。流体/燃料经 DataSlot 同步（1.21.1
 * broadcastChanges 自动下发，客户端 setData 回填）。</p>
 */
public class ContainerSmeltery extends AbstractContainerMenu {

    /** 可见槽数（3 列 × 9 行，1:1 旧版 calcColumns=3）。 */
    public static final int VISIBLE_SLOTS = 27;

    /** DataSlot 索引：燃料量（剩余燃料 tick，旧版 fuelQuality）。 */
    public static final int DATA_FUEL = 0;
    /** DataSlot 索引：炉温（旧版 heat = temperature + 300）。 */
    public static final int DATA_TEMPERATURE = 1;
    /** DataSlot 索引：液体层数。 */
    public static final int DATA_LAYERS = 2;
    /** DataSlot 起始索引：每层 (流体 id, 量) 两两一组。 */
    public static final int DATA_FLUID_START = 3;
    /** 同步的液体层数上限。 */
    public static final int MAX_FLUID_LAYERS = 16;
    /** DataSlot 总数。 */
    public static final int DATA_TOTAL = DATA_FLUID_START + MAX_FLUID_LAYERS * 2;

    private final TileSmeltery tile;
    private final ContainerLevelAccess access;

    /** 侧栏滚动偏移（服务端权威）。 */
    private int scrollOffset;
    /** 可见槽视图（委托真实物品栏 + 偏移）。 */
    private final Container view;

    /** 同步数据槽（燃料/液体，服务端读 BE、客户端由 setData 回填）。 */
    public final List<DataSlot> syncData = new ArrayList<>();

    public ContainerSmeltery(int id, Inventory playerInventory, TileSmeltery tile, ContainerLevelAccess access) {
        super(com.lvdriver.tconstruct_nirvana.gui.ModMenuTypes.SMELTERY.get(), id);
        this.tile = tile;
        this.access = access;

        // 可见槽视图：读取真实物品栏 offset+index（tile 为空 = 客户端占位，返回空）
        this.view = new SimpleContainer(VISIBLE_SLOTS) {
            @Override
            public ItemStack getItem(int index) {
                if (tile == null) {
                    return ItemStack.EMPTY;
                }
                int real = scrollOffset + index;
                if (real >= tile.getSizeInventory()) {
                    return ItemStack.EMPTY;
                }
                return tile.getStackInSlot(real);
            }

            @Override
            public int getContainerSize() {
                return tile == null ? 0 : Math.min(VISIBLE_SLOTS, tile.getSizeInventory() - scrollOffset);
            }

            @Override
            public ItemStack removeItem(int index, int count) {
                int real = scrollOffset + index;
                if (tile != null && real < tile.getSizeInventory()) {
                    ItemStack removed = tile.getInventory().removeItem(real, count);
                    if (!removed.isEmpty()) {
                        tile.setChanged();
                    }
                    return removed;
                }
                return ItemStack.EMPTY;
            }

            @Override
            public ItemStack removeItemNoUpdate(int index) {
                int real = scrollOffset + index;
                if (tile != null && real < tile.getSizeInventory()) {
                    ItemStack removed = tile.getInventory().removeItemNoUpdate(real);
                    if (!removed.isEmpty()) {
                        tile.setChanged();
                    }
                    return removed;
                }
                return ItemStack.EMPTY;
            }

            @Override
            public void setItem(int index, ItemStack stack) {
                int real = scrollOffset + index;
                if (tile != null && real < tile.getSizeInventory()) {
                    tile.setInventorySlotContents(real, stack);
                }
            }

            @Override
            public boolean isEmpty() {
                return tile == null || tile.getSizeInventory() == 0;
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

        // 数据槽：服务端从 BE 读值，broadcastChanges 自动下发；客户端 setData 回填
        initDataSlots();
    }

    /** 客户端构造（MenuType 工厂）：空 BE 占位，槽内容/数据由服务端同步。 */
    public ContainerSmeltery(int id, Inventory playerInventory) {
        this(id, playerInventory, null, ContainerLevelAccess.NULL);
    }

    /** 服务端构造（方块右键打开）：真实 BE。 */
    public ContainerSmeltery(int id, Inventory playerInventory, TileSmeltery tile) {
        this(id, playerInventory, tile, ContainerLevelAccess.create(tile.getLevel(), tile.getBlockPos()));
    }

    private void initDataSlots() {
        // 服务端：get() 读 BE 实时值（broadcastChanges 经 checkAndClearUpdateFlag 自动下发）；
        // 客户端：tile==null，set() 由 setData 回填保存，get() 返回回填值供 Screen 渲染
        syncData.add(addDataSlot(new SmelteryDataSlot(() -> tile != null ? tile.fuelQuality : 0)));
        syncData.add(addDataSlot(new SmelteryDataSlot(() -> tile != null ? tile.getTemperature() + 300 : 0)));
        syncData.add(addDataSlot(new SmelteryDataSlot(() -> {
            SmelteryTank tank = tank();
            return tank == null ? 0 : Math.min(MAX_FLUID_LAYERS, tank.getFluids().size());
        })));
        for (int i = 0; i < MAX_FLUID_LAYERS; i++) {
            final int layer = i;
            syncData.add(addDataSlot(new SmelteryDataSlot(() -> {
                SmelteryTank tank = tank();
                if (tank == null || layer >= tank.getFluids().size()) {
                    return 0;
                }
                return BuiltInRegistries.FLUID.getId(tank.getFluids().get(layer).getFluid());
            })));
            syncData.add(addDataSlot(new SmelteryDataSlot(() -> {
                SmelteryTank tank = tank();
                if (tank == null || layer >= tank.getFluids().size()) {
                    return 0;
                }
                return tank.getFluids().get(layer).getAmount();
            })));
        }
    }

    /**
     * 冶炼炉数据槽：服务端从 BE 实时取值（供 broadcastChanges 推送），
     * 客户端由 {@code setData} 回填保存（供 Screen 渲染）。
     */
    private class SmelteryDataSlot extends DataSlot {

        private final java.util.function.IntSupplier serverGetter;
        private int clientValue;

        SmelteryDataSlot(java.util.function.IntSupplier serverGetter) {
            this.serverGetter = serverGetter;
        }

        @Override
        public int get() {
            // 客户端（tile==null）返回回填值；服务端读 BE
            return tile != null ? serverGetter.getAsInt() : clientValue;
        }

        @Override
        public void set(int value) {
            this.clientValue = value;
        }
    }

    private SmelteryTank tank() {
        return tile != null ? tile.getTank() : null;
    }

    public TileSmeltery getTile() {
        return tile;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    /** 滚动（1:1 旧版侧栏滚动），返回是否成功。 */
    public boolean scroll(int amount) {
        if (tile == null) {
            return false;
        }
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
        // 客户端（tile==null）：返回 true 放行发包，服务端收到后校验并执行
        if (tile == null) {
            return true;
        }
        // 0 = 上滚，1 = 下滚
        if (id == 0) {
            return scroll(-VISIBLE_SLOTS);
        }
        if (id == 1) {
            return scroll(VISIBLE_SLOTS);
        }
        // 2+ = 点击液体层装桶（1:1 旧版 handleTankClick）
        return tile.fillBucketFromTank(player, id - 2);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < VISIBLE_SLOTS) {
                // 冶炼炉槽 → 背包
                if (!this.moveItemStackTo(stack, VISIBLE_SLOTS, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 背包 → 冶炼炉槽（可熔物品；先找空槽再合并）
                if (!this.moveItemStackTo(stack, 0, VISIBLE_SLOTS, false)) {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, stack);
        }
        return itemstack;
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
