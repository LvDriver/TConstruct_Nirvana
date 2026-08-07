package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.multiblock.MultiblockDetection;
import com.lvdriver.tconstruct_nirvana.multiblock.TileMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Arrays;

/**
 * 加热结构基类（1:1 移植自 Tinkers' Antique {@code TileHeatingStructure}）。
 *
 * <p>冶炼炉等"有物品栏 + 加热"结构共用：物品槽内的可熔物品按需加热，
 * 温度达到所需值后触发 {@link #onItemFinishedHeating}。燃料由子类
 * （{@link TileHeatingStructureFuelTank}）消费。</p>
 */
public abstract class TileHeatingStructure extends TileMultiblock {

    public static final String TAG_FUEL = "fuel";
    public static final String TAG_TEMPERATURE = "temperature";
    public static final String TAG_NEEDS_FUEL = "needsFuel";
    public static final String TAG_ITEM_TEMPERATURES = "itemTemperatures";
    public static final String TAG_ITEM_TEMP_REQUIRED = "itemTempRequired";
    public static final String TAG_IS_HEATING = "isHeating";
    /** 物品栏大小（1:1 旧版通过 resize 动态变化，NBT 需保存才能恢复槽位）。 */
    public static final String TAG_INVENTORY_SIZE = "inventorySize";

    /** 加热精度倍率（1:1 旧版 TIME_FACTOR=8）。 */
    protected static final int TIME_FACTOR = 8;

    /** 物品栏。 */
    protected SimpleContainer inventory;

    /** 当前燃料剩余 tick 数。 */
    protected int fuel;
    /** 内部温度（加热速度 = temperature/100 per tick）。 */
    protected int temperature;
    /** 上一 tick 是否执行了需要燃料的操作。 */
    protected boolean needsFuel;

    /** 各槽当前温度（加热进度）。 */
    protected int[] itemTemperatures;
    /** 各槽所需温度。 */
    protected int[] itemTempRequired;

    /** 是否有物品正在加热（客户端音效/渲染用）。 */
    protected boolean isHeating;

    public TileHeatingStructure(BlockEntityType<?> type, BlockPos pos, BlockState state, int inventorySize) {
        super(type, pos, state);
        this.inventory = new SimpleContainer(inventorySize);
        this.itemTemperatures = new int[inventorySize];
        this.itemTempRequired = new int[inventorySize];
    }

    /* 物品栏访问（1.21.1 用 SimpleContainer 组合，供 Menu 使用） */

    public SimpleContainer getInventory() {
        return inventory;
    }

    public int getSizeInventory() {
        return inventory.getContainerSize();
    }

    public ItemStack getStackInSlot(int index) {
        return inventory.getItem(index);
    }

    public boolean isStackInSlot(int index) {
        return !inventory.getItem(index).isEmpty();
    }

    public void setInventorySlotContents(int index, ItemStack stack) {
        // 物品变化 → 重置加热进度并重新计算所需温度（1:1 旧版）
        ItemStack old = inventory.getItem(index);
        if (stack.isEmpty() || (!old.isEmpty() && !ItemStack.isSameItemSameComponents(old, stack))) {
            itemTemperatures[index] = 0;
        }
        inventory.setItem(index, stack);
        updateHeatRequired(index);
        setChanged();
    }

    /** 调整物品栏大小（结构成型/失效时调用），保留已有物品。尺寸 clamp 防篡改 NBT。 */
    public void resizeInventory(int size) {
        // 上限 = 最大内部尺寸 9×9×9=729（1:1 旧版 MAX_SIZE=9），下限 0
        size = Math.max(0, Math.min(729, size));
        SimpleContainer old = inventory;
        SimpleContainer fresh = new SimpleContainer(size);
        for (int i = 0; i < Math.min(old.getContainerSize(), size); i++) {
            fresh.setItem(i, old.getItem(i));
        }
        inventory = fresh;
        itemTemperatures = Arrays.copyOf(itemTemperatures, size);
        itemTempRequired = Arrays.copyOf(itemTempRequired, size);
    }

    /* 加热逻辑 */

    /** 槽内物品是否可加热（炉温达标）。 */
    public boolean canHeat(int index) {
        return temperature >= getHeatRequiredForSlot(index);
    }

    /** 加热进度 0~1。 */
    public float getProgress(int index) {
        if (index >= itemTemperatures.length || itemTempRequired[index] == 0) {
            return 0f;
        }
        return (float) itemTemperatures[index] / (float) itemTempRequired[index];
    }

    protected void setHeatRequiredForSlot(int index, int heat) {
        if (index < itemTempRequired.length) {
            itemTempRequired[index] = heat * TIME_FACTOR;
        }
    }

    protected int getHeatRequiredForSlot(int index) {
        if (index >= itemTempRequired.length) {
            return 0;
        }
        return itemTempRequired[index] / TIME_FACTOR;
    }

    /** 计算槽位所需温度（子类查熔炼配方）。 */
    protected abstract void updateHeatRequired(int index);

    /** 物品加热完成（子类执行熔炼并返回是否成功）。 */
    protected abstract boolean onItemFinishedHeating(ItemStack stack, int slot);

    /** 加热所有槽位（1:1 旧版 heatItems）。 */
    protected void heatItems() {
        boolean heatedItem = false;
        for (int i = 0; i < getSizeInventory(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {
                // 有目标温度才加热
                if (itemTempRequired[i] > 0) {
                    // 有燃料
                    if (hasFuel()) {
                        // 炉温足够
                        if (canHeat(i)) {
                            // 完成？
                            if (itemTemperatures[i] >= itemTempRequired[i]) {
                                if (onItemFinishedHeating(stack, i)) {
                                    itemTemperatures[i] = 0;
                                    itemTempRequired[i] = 0;
                                }
                            } else {
                                itemTemperatures[i] += heatSlot(i);
                                heatedItem = true;
                            }
                        }
                    } else {
                        // 无燃料 → 标记需要燃料，本 tick 停止
                        this.needsFuel = true;
                        break;
                    }
                }
            } else {
                itemTemperatures[i] = 0;
            }
        }

        // 加热了物品才消耗燃料（1:1 旧版：只有实际加热才扣）
        if (heatedItem) {
            fuel--;
        }
        updateIfChanged(heatedItem);
    }

    /** 加热速度：炉温每 100 度每 tick 加热 1 单位（1:1 旧版 heatSlot）。 */
    protected int heatSlot(int i) {
        return temperature / 100;
    }

    private void updateIfChanged(boolean heatedItem) {
        if (heatedItem != isHeating) {
            isHeating = heatedItem;
            setChanged();
        }
    }

    public int getTemperature(int i) {
        if (i < 0 || i >= itemTemperatures.length) {
            return 0;
        }
        return itemTemperatures[i];
    }

    public int getTempRequired(int i) {
        if (i < 0 || i >= itemTempRequired.length) {
            return 0;
        }
        return itemTempRequired[i];
    }

    public int getTemperature() {
        return temperature;
    }

    /* 燃料 */

    public int getFuel() {
        return fuel;
    }

    public boolean hasFuel() {
        return fuel > 0;
    }

    /** 添加燃料并设定炉温（1:1 旧版 addFuel，温度 = 流体温度 - 300）。 */
    protected void addFuel(int fuel, int newTemperature) {
        this.fuel += fuel;
        this.needsFuel = false;
        this.temperature = newTemperature;
    }

    /** 消耗燃料（子类从燃料罐取液）。 */
    protected abstract void consumeFuel();

    /* NBT */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt(TAG_FUEL, fuel);
        tag.putInt(TAG_TEMPERATURE, temperature);
        tag.putBoolean(TAG_NEEDS_FUEL, needsFuel);
        tag.putIntArray(TAG_ITEM_TEMPERATURES, itemTemperatures);
        tag.putIntArray(TAG_ITEM_TEMP_REQUIRED, itemTempRequired);
        tag.putBoolean(TAG_IS_HEATING, isHeating);
        // 先保存大小再存物品（加载时按大小 resize 后物品才能恢复到对应槽位）
        tag.putInt(TAG_INVENTORY_SIZE, getSizeInventory());
        // 物品栏（ContainerHelper 需要 registries）
        net.minecraft.world.ContainerHelper.saveAllItems(tag, inventory.getItems(), registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        fuel = tag.getInt(TAG_FUEL);
        temperature = tag.getInt(TAG_TEMPERATURE);
        needsFuel = tag.getBoolean(TAG_NEEDS_FUEL);
        itemTemperatures = tag.getIntArray(TAG_ITEM_TEMPERATURES);
        itemTempRequired = tag.getIntArray(TAG_ITEM_TEMP_REQUIRED);
        isHeating = tag.getBoolean(TAG_IS_HEATING);
        // 恢复物品栏大小（结构成型前构造器为 0 槽，加载时按保存值重建）
        int size = tag.getInt(TAG_INVENTORY_SIZE);
        if (size != getSizeInventory()) {
            resizeInventory(size);
        }
        // 温度数组长度与物品栏对齐（防篡改 NBT 两数组不等长导致 getProgress 越界）
        int tempLen = Math.min(itemTemperatures.length, itemTempRequired.length);
        if (itemTemperatures.length != itemTempRequired.length) {
            itemTemperatures = Arrays.copyOf(itemTemperatures, tempLen);
            itemTempRequired = Arrays.copyOf(itemTempRequired, tempLen);
        }
        net.minecraft.world.ContainerHelper.loadAllItems(tag, inventory.getItems(), registries);
    }
}
