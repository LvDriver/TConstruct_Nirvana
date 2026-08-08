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
 * 冶炼炉菜单（1:1 移植自 Tinkers' Antique {@code ContainerSmeltery} 完整版）。
 *
 * <p>布局 1:1 旧版：材料输入侧栏在 GUI 左侧（3 列 × 8 行，滚动翻页，槽位
 * 背景 22×18），主 GUI（176×166：液体罐 + 燃料 + 玩家背包）在右侧。
 * 液体层/容量/燃料/逐槽加热状态经 DataSlot 同步（1.21.1 broadcastChanges
 * 自动下发，客户端 setData 回填）。</p>
 */
public class ContainerSmeltery extends AbstractContainerMenu {

    /** 可见槽数（3 列 × 8 行，1:1 旧版 GuiSmelterySideInventory 显示 8 行）。 */
    public static final int VISIBLE_SLOTS = 24;
    /** 侧栏列数（1:1 旧版 calcColumns=3）。 */
    public static final int SIDE_COLUMNS = 3;
    /** 侧栏边框宽（px）。 */
    public static final int SIDE_BORDER = 4;
    /** 侧栏槽位步进（槽背景 22 宽 × 18 高，含 2px 边距）。 */
    public static final int SLOT_W = 22;
    public static final int SLOT_H = 18;
    /** 主 GUI 左侧偏移（侧栏宽 = 边框 + 3×22 + 边框）。 */
    public static final int MAIN_X = SIDE_BORDER + SIDE_COLUMNS * SLOT_W + SIDE_BORDER;

    /** DataSlot 索引：燃料量（剩余燃料 tick，旧版 fuelQuality）。 */
    public static final int DATA_FUEL = 0;
    /** DataSlot 索引：炉温（旧版 heat = temperature + 300）。 */
    public static final int DATA_TEMPERATURE = 1;
    /** DataSlot 索引：液体层数。 */
    public static final int DATA_LAYERS = 2;
    /** DataSlot 索引：冶炼炉槽数（结构内部尺寸）。 */
    public static final int DATA_SLOT_COUNT = 3;
    /** DataSlot 索引：液体总容量（mb）。 */
    public static final int DATA_CAPACITY = 4;
    /** DataSlot 索引：当前燃料流体 id（-1 = 无）。 */
    public static final int DATA_FUEL_FLUID = 5;
    /** DataSlot 起始索引：每层 (流体 id, 量) 两两一组。 */
    public static final int DATA_FLUID_START = 6;
    /** 同步的液体层数上限。 */
    public static final int MAX_FLUID_LAYERS = 16;
    /** DataSlot 起始索引：逐槽加热状态（编码见 {@link TileSmeltery#getProgressStatus}）。 */
    public static final int DATA_PROGRESS_START = DATA_FLUID_START + MAX_FLUID_LAYERS * 2;
    /** 同步进度状态的槽数上限（5×5×5=125 已覆盖，729 上限的超大炉部分槽无进度显示）。 */
    public static final int MAX_SYNC_SLOTS = 128;
    /** DataSlot 总数。 */
    public static final int DATA_TOTAL = DATA_PROGRESS_START + MAX_SYNC_SLOTS;

    private final TileSmeltery tile;
    private final ContainerLevelAccess access;

    /** 侧栏滚动偏移（服务端权威）。 */
    private int scrollOffset;
    /** 可见槽视图（委托真实物品栏 + 偏移）。 */
    private final Container view;

    /** 同步数据槽（燃料/液体/进度，服务端读 BE、客户端由 setData 回填）。 */
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

        // 侧栏可见槽（3 列 × 8 行，槽背景 22×18；不显示的行置 GUI 外）
        int rows = visibleRows();
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < SIDE_COLUMNS; col++) {
                int index = col + row * SIDE_COLUMNS;
                boolean visible = row < rows;
                addSlot(new Slot(view, index,
                        visible ? SIDE_BORDER + col * SLOT_W + 1 : -100,
                        visible ? SIDE_BORDER + row * SLOT_H + 1 : -100));
            }
        }

        // 玩家背包（3×9）+ 快捷栏（主 GUI 内，1:1 旧版 addPlayerInventory(8,84)）
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, MAIN_X + 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, MAIN_X + 8 + col * 18, 142));
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

    /** 可见侧栏行数（最多 8 行，1:1 旧版 getDisplayedRows；客户端缺省按 27 槽）。 */
    public int visibleRows() {
        int slots = tile != null ? tile.getSizeInventory() : 27;
        return Math.max(1, Math.min(8, (slots + SIDE_COLUMNS - 1) / SIDE_COLUMNS));
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
        syncData.add(addDataSlot(new SmelteryDataSlot(() -> tile != null ? tile.getSizeInventory() : 0)));
        syncData.add(addDataSlot(new SmelteryDataSlot(() -> {
            SmelteryTank tank = tank();
            return tank != null ? tank.getCapacity() : 0;
        })));
        syncData.add(addDataSlot(new SmelteryDataSlot(() -> {
            if (tile == null || tile.currentFuel == null || tile.currentFuel.isEmpty()) {
                return -1;
            }
            return BuiltInRegistries.FLUID.getId(tile.currentFuel.getFluid());
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
        // 逐槽加热状态（编码 int，1:1 旧版 getHeatingProgress 语义）
        for (int i = 0; i < MAX_SYNC_SLOTS; i++) {
            final int slot = i;
            syncData.add(addDataSlot(new SmelteryDataSlot(() -> tile != null ? tile.getProgressStatus(slot) : -3)));
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

    /** 滚动（1:1 旧版侧栏滚动，步长 = 可见槽数），返回是否成功。 */
    public boolean scroll(int amount) {
        if (tile == null) {
            return false;
        }
        int max = Math.max(0, tile.getSizeInventory() - visibleRows() * SIDE_COLUMNS);
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
            return scroll(-visibleRows() * SIDE_COLUMNS);
        }
        if (id == 1) {
            return scroll(visibleRows() * SIDE_COLUMNS);
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
