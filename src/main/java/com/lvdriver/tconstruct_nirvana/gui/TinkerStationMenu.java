package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
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
 * 工具站/锻造厂菜单（1:1 移植自 Tinkers' Antique {@code ContainerToolStation}，完整版）。
 *
 * <p>输入槽：槽 0 = 工具槽（放入已有工具进行修复/部件替换），槽 1-5 = 材料槽
 * （部件/磨刀石）；结果槽实时预览。合成链（1:1 旧版 onCraftMatrixChanged 顺序）：
 * 修复 → 部件替换 → 组装；结果槽可放入未损坏工具进入拆解模式（1:1 旧版
 * SlotToolStationOut 的 isToolForDeconstruction），取走即拆解为部件。
 * 客户端与服务端运行同一确定性逻辑，无需网络包（重命名文本框留待后续会话）。</p>
 */
public class TinkerStationMenu extends AbstractContainerMenu {

    /** 工具槽索引。 */
    public static final int TOOL_SLOT = 0;
    /** 材料槽数。 */
    public static final int PART_SLOTS = 5;
    /** 输入槽总数（工具槽 + 材料槽）。 */
    public static final int INPUT_SLOTS = TOOL_SLOT + PART_SLOTS + 1;
    /** 结果槽索引。 */
    public static final int RESULT_SLOT = INPUT_SLOTS;

    private final SimpleContainer parts;
    private final ResultContainer result = new ResultContainer();
    private final ContainerLevelAccess access;
    private final net.minecraft.world.ContainerListener partListener;
    /** 拆解模式标志：结果槽工具由玩家放入（mayPlace 置位，合成链/取走时清除）。 */
    private boolean deconstructMode;
    /** Shift 取走标志：quickMoveStack 调用 onTake 前置位（区分点击路径——点击时物品已在光标）。 */
    private boolean shiftTake;

    /** 服务端构造：直接持有方块实体容器。 */
    public TinkerStationMenu(int id, Inventory playerInventory, SimpleContainer parts, ContainerLevelAccess access, BlockEntity blockEntity) {
        super(ModMenuTypes.TOOL_STATION.get(), id);
        this.parts = parts;
        this.access = access;
        // 部件变化 → 重算结果 + 标记方块实体脏（服务器保存时部件槽不丢失）
        this.partListener = container -> {
            this.slotsChanged(container);
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        };
        parts.addListener(this.partListener);

        // 工具槽 + 5 材料槽（横排）
        addSlot(new Slot(parts, TOOL_SLOT, 26, 34));
        for (int i = 0; i < PART_SLOTS; i++) {
            addSlot(new Slot(parts, TOOL_SLOT + 1 + i, 44 + i * 18, 34));
        }
        // 结果槽：可放入未损坏工具（拆解模式，1:1 旧版 SlotToolStationOut.isItemValid），取走按合成类型消耗
        addSlot(new Slot(result, 0, 134, 34) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                if (canDeconstruct(stack)) {
                    deconstructMode = true;
                    return true;
                }
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                boolean wasDeconstruct = deconstructMode;
                boolean wasShift = shiftTake;
                deconstructMode = false;
                shiftTake = false;
                takeResult(player, stack, wasDeconstruct, wasShift);
            }
        });

        // 玩家背包（3×9）+ 快捷栏
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 92 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 150));
        }
    }

    /** 客户端构造（MenuType 工厂）：空容器占位，槽内容由服务端同步。 */
    public TinkerStationMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, new SimpleContainer(INPUT_SLOTS), ContainerLevelAccess.NULL, null);
    }

    /* ---------- 合成链（1:1 旧版 onCraftMatrixChanged） ---------- */

    /** 部件/工具变化 → 重算结果（修复 → 替换 → 组装）。 */
    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        // 拆解模式：结果槽工具由玩家放入 → 保留（1:1 旧版 out.isToolForDeconstruction，
        // 防止部件槽变化吞掉结果槽工具；onTake 时按材料槽状态决定拆解或返还）
        ItemStack currentResult = result.getItem(0);
        if (deconstructMode && !currentResult.isEmpty() && isDeconstructing(currentResult)) {
            return;
        }
        deconstructMode = false;
        ItemStack resultStack = ItemStack.EMPTY;
        // 1. 修复（工具 + 磨刀石）
        resultStack = repairTool();
        // 2. 部件替换（工具 + 部件）
        if (resultStack.isEmpty()) {
            resultStack = replaceToolParts();
        }
        // 3. 组装（仅部件）
        if (resultStack.isEmpty()) {
            resultStack = buildTool();
        }
        result.setItem(0, resultStack);
    }

    /** 修复预览（1:1 旧版 repairTool：槽 0 工具 + 材料槽磨刀石 → 修复后工具）。 */
    private ItemStack repairTool() {
        ItemStack tool = parts.getItem(TOOL_SLOT);
        if (tool.isEmpty() || !(tool.getItem() instanceof TinkerToolItem tinkerTool)) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> inputs = materialSlotContents();
        if (inputs.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return tinkerTool.repair(tool, inputs);
    }

    /** 部件替换预览（1:1 旧版 replaceToolParts：新材料部件替换工具对应槽位并重算属性）。 */
    private ItemStack replaceToolParts() {
        ItemStack tool = parts.getItem(TOOL_SLOT);
        if (tool.isEmpty() || !(tool.getItem() instanceof TinkerToolItem tinkerTool)) {
            return ItemStack.EMPTY;
        }
        List<String> materials = tool.getOrDefault(ModDataComponents.BASE_MATERIALS, List.of());
        if (materials.isEmpty()) {
            return ItemStack.EMPTY;
        }
        List<Material> newMaterials = new ArrayList<>(materials.size());
        boolean changed = false;
        for (int i = 0; i < materials.size(); i++) {
            ItemStack part = parts.getItem(TOOL_SLOT + 1 + i);
            String current = materials.get(i);
            if (!part.isEmpty() && tinkerTool.validComponent(i, part)) {
                String partMaterial = part.getOrDefault(ModDataComponents.PART_MATERIAL, null);
                if (partMaterial != null && !partMaterial.equals(current)) {
                    newMaterials.add(Material.getByIdentifier(partMaterial));
                    changed = true;
                    continue;
                }
            }
            newMaterials.add(Material.getByIdentifier(current));
        }
        if (!changed) {
            return ItemStack.EMPTY;
        }
        ItemStack result = tinkerTool.buildItem(newMaterials);
        // 保留修饰符与已消耗耐久（简化：修饰符复制，耐久按比例换算）
        result.set(ModDataComponents.MODIFIERS, tool.getOrDefault(ModDataComponents.MODIFIERS, List.of()));
        result.set(ModDataComponents.BASE_MODIFIERS, tool.getOrDefault(ModDataComponents.BASE_MODIFIERS, List.of()));
        result.set(ModDataComponents.TRAITS, tool.getOrDefault(ModDataComponents.TRAITS, List.of()));
        int maxDur = tool.getMaxDamage();
        if (maxDur > 0) {
            int resultMax = result.getMaxDamage();
            float ratio = 1f - (float) tool.getDamageValue() / maxDur;
            result.setDamageValue(resultMax - Math.round(resultMax * ratio));
        }
        return result;
    }

    /** 组装预览（1:1 旧版 buildTool：材料槽连续非空部件 → 工具）。 */
    private ItemStack buildTool() {
        if (!parts.getItem(TOOL_SLOT).isEmpty()) {
            return ItemStack.EMPTY;
        }
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < PART_SLOTS; i++) {
            ItemStack stack = parts.getItem(TOOL_SLOT + 1 + i);
            if (!stack.isEmpty()) {
                stacks.add(stack);
            }
        }
        if (stacks.size() < 2) {
            return ItemStack.EMPTY;
        }
        for (TinkerToolItem tool : com.lvdriver.tconstruct_nirvana.item.tool.ModTools.getAllTools()) {
            ItemStack output = tool.buildItemFromStacks(stacks);
            if (!output.isEmpty()) {
                return output;
            }
        }
        return ItemStack.EMPTY;
    }

    /* ---------- 结果取走（1:1 旧版 onResultTaken） ---------- */

    private void takeResult(Player player, ItemStack stack, boolean deconstruct, boolean viaShift) {
        // 拆解模式（仅玩家放入的工具）：
        // - shift 取走 + 材料槽空 → 拆解（工具消耗，部件填入材料槽）
        // - 点击取走 → 物品已在光标（vanilla tryRemove 先移走）= 取消拆解拿回工具，不产生部件
        // - shift 取走 + 材料槽非空 → 放回结果槽（不吞不复制）
        if (deconstruct && isDeconstructing(stack)) {
            if (viaShift && materialSlotsEmpty()) {
                List<ItemStack> partsList = getDeconstructedParts(stack);
                for (int i = 0; i < PART_SLOTS; i++) {
                    parts.setItem(TOOL_SLOT + 1 + i, i < partsList.size() ? partsList.get(i) : ItemStack.EMPTY);
                }
                result.clearContent();
            } else if (viaShift) {
                // 材料槽非空：工具放回结果槽（点击路径工具已在光标，无需处理）
                result.setItem(0, stack);
                deconstructMode = true; // 工具仍在结果槽 → 拆解模式继续（关闭菜单时归还）
            }
            return;
        }
        ItemStack tool = parts.getItem(TOOL_SLOT);
        // 修复取走：消耗磨刀石，返回修复后工具
        if (!tool.isEmpty() && tool.getItem() instanceof TinkerToolItem tinkerTool) {
            ItemStack repaired = tinkerTool.repair(tool, materialSlotContents());
            if (!repaired.isEmpty()) {
                clearInputSlots();
                result.clearContent();
                giveToPlayer(player, repaired);
                return;
            }
            // 部件替换取走：消耗材料槽部件，返回替换后工具
            ItemStack replaced = replaceToolParts();
            if (!replaced.isEmpty()) {
                clearInputSlots();
                result.clearContent();
                giveToPlayer(player, replaced);
                return;
            }
            // 无有效合成：保留槽位内容，不吞工具
            return;
        }
        // 组装取走：消耗全部材料槽，工具进玩家背包
        ItemStack built = buildTool();
        if (built.isEmpty()) {
            return;
        }
        for (int i = 0; i < INPUT_SLOTS; i++) {
            parts.setItem(i, ItemStack.EMPTY);
        }
        result.clearContent();
        giveToPlayer(player, built);
    }

    private static void giveToPlayer(Player player, ItemStack stack) {
        if (!player.addItem(stack)) {
            player.drop(stack, false);
        }
    }

    /* ---------- 拆解（1:1 旧版 SlotToolStationOut / deconstructTool / getDeconstructedParts） ---------- */

    /** 拆解资格：未损坏工具 + 材料槽全空（1:1 旧版 isItemValid 简化，无 XP/配置门槛）。 */
    private boolean canDeconstruct(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof TinkerToolItem)) {
            return false;
        }
        if (stack.isDamaged() || ToolHelper.isBroken(stack)) {
            return false;
        }
        for (int i = 0; i < PART_SLOTS; i++) {
            if (!parts.getItem(TOOL_SLOT + 1 + i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private boolean isDeconstructing(ItemStack stack) {
        return stack.getItem() instanceof TinkerToolItem && !stack.isDamaged() && !ToolHelper.isBroken(stack);
    }

    /** 工具拆解为部件栈（1:1 旧版 getDeconstructedParts：BASE_MATERIALS × 对应槽位部件）。 */
    private List<ItemStack> getDeconstructedParts(ItemStack tool) {
        List<ItemStack> partsList = new ArrayList<>();
        if (!(tool.getItem() instanceof TinkerToolItem tinkerTool)) {
            return partsList;
        }
        List<String> materials = tool.getOrDefault(ModDataComponents.BASE_MATERIALS, List.of());
        List<com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType> required = tinkerTool.getRequiredComponents();
        for (int i = 0; i < materials.size() && i < required.size(); i++) {
            String materialId = materials.get(i);
            Material material = Material.getByIdentifier(materialId);
            for (var part : required.get(i).getPossibleParts()) {
                ItemStack partStack = new ItemStack(part);
                if (partStack.getItem() instanceof com.lvdriver.tconstruct_nirvana.item.part.ToolPart toolPart
                        && toolPart.canUseMaterial(material)) {
                    partStack.set(ModDataComponents.PART_MATERIAL, materialId);
                    partsList.add(partStack);
                    break;
                }
            }
        }
        return partsList;
    }

    /* ---------- Shift 快速移动（1:1 旧版 transferStackInSlot） ---------- */

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index == RESULT_SLOT) {
                // 结果槽：取走即消耗（onTake 内处理：点击路径物品已在光标，shift 路径由此分发）
                shiftTake = true;
                slot.onTake(player, stack);
                return ItemStack.EMPTY;
            } else if (index < INPUT_SLOTS) {
                // 输入槽 → 背包
                if (!this.moveItemStackTo(stack, INPUT_SLOTS + 1, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 背包 → 输入槽（工具进工具槽；可拆解工具进结果槽；其余进第一个空材料槽）
                if (canDeconstruct(stack)) {
                    if (!this.moveItemStackTo(stack, RESULT_SLOT, RESULT_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (stack.getItem() instanceof TinkerToolItem) {
                    if (!this.moveItemStackTo(stack, TOOL_SLOT, TOOL_SLOT + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (!this.moveItemStackTo(stack, TOOL_SLOT + 1, INPUT_SLOTS, false)) {
                        return ItemStack.EMPTY;
                    }
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

    /* ---------- 工具方法 ---------- */

    private List<ItemStack> materialSlotContents() {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < PART_SLOTS; i++) {
            ItemStack stack = parts.getItem(TOOL_SLOT + 1 + i);
            if (!stack.isEmpty()) {
                list.add(stack);
            }
        }
        return list;
    }

    private boolean materialSlotsEmpty() {
        for (int i = 0; i < PART_SLOTS; i++) {
            if (!parts.getItem(TOOL_SLOT + 1 + i).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private void clearInputSlots() {
        for (int i = 0; i < INPUT_SLOTS; i++) {
            parts.setItem(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return access.evaluate((level, pos) -> level.getBlockState(pos).getBlock() instanceof com.lvdriver.tconstruct_nirvana.block.BlockToolTable, true);
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // 菜单关闭后移除容器监听，防止挂在长生命周期 BE 容器上累积泄漏
        this.parts.removeListener(this.partListener);
        // 仅归还拆解模式（玩家放入结果槽）的工具；合成预览直接丢弃
        // （预览未消耗输入，若归还=免费复制；deconstructMode 仅玩家放入时置位）
        if (!player.level().isClientSide && deconstructMode) {
            ItemStack leftover = result.getItem(0);
            if (!leftover.isEmpty()) {
                result.clearContent();
                if (!player.addItem(leftover)) {
                    player.drop(leftover, false);
                }
            }
        }
    }
}
