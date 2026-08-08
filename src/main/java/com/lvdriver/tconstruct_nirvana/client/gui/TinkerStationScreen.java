package com.lvdriver.tconstruct_nirvana.client.gui;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.gui.TinkerStationMenu;
import com.lvdriver.tconstruct_nirvana.gui.ToolBuildGuiInfo;
import com.lvdriver.tconstruct_nirvana.gui.ToolStationLayouts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具站/锻造厂 GUI（1:1 移植自 Tinkers' Antique {@code GuiToolStation} 完整版）。
 *
 * <p>布局：左侧工具选择按钮列（修复 + 全部工具，点击切换槽位布局并同步服务端，
 * 1:1 旧版 GuiButtonsToolStation）；中间主 GUI（背景 toolstation.png，槽位背景 +
 * 边框绘制，1:1 旧版 SlotBackground/SlotBorder）；右侧两个信息面板（工具信息 +
 * 特质/部件列表，1:1 旧版 GuiInfoPanel ×2）。重命名文本框留待后续会话。</p>
 */
public class TinkerStationScreen extends AbstractContainerScreen<TinkerStationMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "textures/gui/toolstation.png");
    private static final ResourceLocation ICONS =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "textures/gui/icons.png");
    private static final ResourceLocation PANEL =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "textures/gui/panel.png");

    /** 槽背景/边框贴图源（toolstation.png (176,0) 起，18×18）。 */
    private static final int SLOT_BG_U = 176;
    private static final int SLOT_BG_V = 0;
    private static final int SLOT_BORDER_U = 194;
    private static final int SLOT_BORDER_V = 0;

    /** 按钮贴图源（icons.png (144/180/216,216)，18×18）。 */
    private static final int BUTTON_U = 180;
    private static final int BUTTON_V = 216;
    private static final int BUTTON_PRESSED_U = 144;
    private static final int BUTTON_HOVER_U = 216;
    private static final int BUTTON_SIZE = 18;
    private static final int BUTTON_SPACING = 4;
    /** 修复按钮图标（icons.png ICON_Anvil (54,0)）。 */
    private static final int ANVIL_U = 54;
    private static final int ANVIL_V = 0;
    /** 修复模式槽位图标（icons.png ICON_Pickaxe/Dust/Lapis/Ingot/Gem/Quartz (0..90,234)）。 */
    private static final int REPAIR_ICON_U = 0;
    private static final int REPAIR_ICON_V = 234;

    /** 信息面板（panel.png 9 宫格，wood 样式 = x+126 偏移；中心 (4,4,118,75)）。 */
    private static final int PANEL_W = 126;
    private static final int PANEL_H = 83;
    private static final int PANEL_BORDER = 4;
    private static final int PANEL_CENTER_U = 4;
    private static final int PANEL_CENTER_V = 4;
    private static final int PANEL_RES_W = 118;
    private static final int PANEL_RES_H = 75;
    private static final int PANEL_WOOD_OFFSET = PANEL_W;

    /** 按钮列起始（主 GUI 左侧 20px 处，按钮 18 宽）。 */
    private static final int BUTTONS_X = 2;
    private static final int BUTTONS_Y = 7;
    /** 信息面板位置（主 GUI 右侧 +2）。 */
    private static final int PANEL_X = TinkerStationMenu.MAIN_X + 176 + 2;
    private static final int PANEL1_Y = 5;
    private static final int PANEL2_Y = 92;

    public TinkerStationScreen(TinkerStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = TinkerStationMenu.MAIN_X + 176 + 2 + PANEL_W;
        this.imageHeight = 178;
    }

    private int mainX() {
        return this.leftPos + TinkerStationMenu.MAIN_X;
    }

    /**
     * 输入槽当前布局位置（相对 leftPos 的绝对坐标；停用槽返回 null）。
     * 1.21.1 Slot.x/y 为 final 不可改，渲染/点击/悬停全部由此驱动。
     */
    private int[] slotPos(int index) {
        ToolBuildGuiInfo info = this.menu.currentInfo();
        if (index < this.menu.getActiveSlots() && index < info.positions.size()) {
            int[] p = info.positions.get(index);
            return new int[]{p[0] + TinkerStationMenu.MAIN_X, p[1]};
        }
        return null;
    }

    /* ---------- 背景层 ---------- */

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int mainX = mainX();

        // 主 GUI 背景
        graphics.blit(BACKGROUND, mainX, this.topPos, 0, 0, 176, 174);

        // 槽背景 + 边框（1:1 旧版 SlotBackground alpha 0.28/0.58 + SlotBorder）
        for (int i = 0; i < TinkerStationMenu.INPUT_SLOTS; i++) {
            int[] p = slotPos(i);
            if (p == null) {
                continue; // 停用槽隐藏
            }
            // 拆解模式下空槽半透明更深（1:1 旧版）
            graphics.setColor(1f, 1f, 1f, 0.28f);
            graphics.blit(BACKGROUND, this.leftPos + p[0] - 1, this.topPos + p[1] - 1,
                    SLOT_BG_U, SLOT_BG_V, 18, 18);
            graphics.setColor(1f, 1f, 1f, 1f);
            graphics.blit(BACKGROUND, this.leftPos + p[0] - 1, this.topPos + p[1] - 1,
                    SLOT_BORDER_U, SLOT_BORDER_V, 18, 18);
        }

        // 左侧工具选择按钮列（修复 + 21 工具）
        List<ToolBuildGuiInfo> infos = ToolStationLayouts.all();
        for (int i = 0; i < infos.size(); i++) {
            ToolBuildGuiInfo info = infos.get(i);
            int bx = this.leftPos + BUTTONS_X;
            int by = this.topPos + BUTTONS_Y + i * (BUTTON_SIZE + BUTTON_SPACING);
            boolean selected = info.isRepair() ? menu.getSelectedTool() == null
                    : menu.getSelectedTool() == info.tool;
            boolean hovered = mouseX >= bx && mouseX < bx + BUTTON_SIZE && mouseY >= by && mouseY < by + BUTTON_SIZE;
            int u = selected ? BUTTON_PRESSED_U : hovered ? BUTTON_HOVER_U : BUTTON_U;
            graphics.blit(ICONS, bx, by, u, BUTTON_V, BUTTON_SIZE, BUTTON_SIZE);
            if (info.isRepair()) {
                graphics.blit(ICONS, bx, by, ANVIL_U, ANVIL_V, BUTTON_SIZE, BUTTON_SIZE);
            } else if (!info.icon.isEmpty()) {
                graphics.renderItem(info.icon, bx, by);
            }
        }

        // 右侧信息面板 ×2（1:1 旧版 GuiInfoPanel，wood 边框）
        drawPanel(graphics, PANEL_X, PANEL1_Y);
        drawPanel(graphics, PANEL_X, PANEL2_Y);

        // 修复模式槽位图标（1:1 旧版 drawRepairSlotIcons：空槽显示镐/粉/青金石/锭/宝石/石英）
        if (menu.getSelectedTool() == null) {
            for (int i = 0; i < TinkerStationMenu.INPUT_SLOTS; i++) {
                int[] p = slotPos(i);
                if (p == null || this.menu.slots.get(i).hasItem()) {
                    continue;
                }
                int iconIndex = i < 6 ? i : 0;
                graphics.blit(ICONS, this.leftPos + p[0], this.topPos + p[1],
                        REPAIR_ICON_U + iconIndex * 18, REPAIR_ICON_V, 18, 18);
            }
        }

        // 面板文本
        drawInfoText(graphics, PANEL_X, PANEL1_Y, true);
        drawInfoText(graphics, PANEL_X, PANEL2_Y, false);
    }

    /** 面板背景（9 宫格：中心 + wood 边框）。 */
    private void drawPanel(GuiGraphics graphics, int x, int y) {
        int px = this.leftPos + x;
        int py = this.topPos + y;
        // 中心
        graphics.blit(PANEL, px + PANEL_BORDER, py + PANEL_BORDER, PANEL_CENTER_U, PANEL_CENTER_V, PANEL_RES_W, PANEL_RES_H);
        // 四边
        graphics.blit(PANEL, px + PANEL_BORDER, py, PANEL_CENTER_U + PANEL_WOOD_OFFSET, 0, PANEL_RES_W, PANEL_BORDER);
        graphics.blit(PANEL, px + PANEL_BORDER, py + PANEL_BORDER + PANEL_RES_H, PANEL_CENTER_U + PANEL_WOOD_OFFSET, PANEL_RES_H + PANEL_BORDER, PANEL_RES_W, PANEL_BORDER);
        graphics.blit(PANEL, px, py + PANEL_BORDER, PANEL_WOOD_OFFSET, PANEL_CENTER_V, PANEL_BORDER, PANEL_RES_H);
        graphics.blit(PANEL, px + PANEL_BORDER + PANEL_RES_W, py + PANEL_BORDER, PANEL_WOOD_OFFSET + PANEL_RES_W + PANEL_BORDER, PANEL_CENTER_V, PANEL_BORDER, PANEL_RES_H);
        // 四角
        graphics.blit(PANEL, px, py, PANEL_WOOD_OFFSET, 0, PANEL_BORDER, PANEL_BORDER);
        graphics.blit(PANEL, px + PANEL_BORDER + PANEL_RES_W, py, PANEL_WOOD_OFFSET + PANEL_RES_W + PANEL_BORDER, 0, PANEL_BORDER, PANEL_BORDER);
        graphics.blit(PANEL, px, py + PANEL_BORDER + PANEL_RES_H, PANEL_WOOD_OFFSET, PANEL_RES_H + PANEL_BORDER, PANEL_BORDER, PANEL_BORDER);
        graphics.blit(PANEL, px + PANEL_BORDER + PANEL_RES_W, py + PANEL_BORDER + PANEL_RES_H, PANEL_WOOD_OFFSET + PANEL_RES_W + PANEL_BORDER, PANEL_RES_H + PANEL_BORDER, PANEL_BORDER, PANEL_BORDER);
    }

    /* ---------- 信息内容（1:1 旧版 updateDisplay 简化） ---------- */

    private List<String> toolInfoLines() {
        ItemStack toolStack = currentToolStack();
        if (toolStack.isEmpty()) {
            return List.of(Component.translatable("gui.tconstruct_nirvana.toolstation.info").getString());
        }
        List<String> lines = new ArrayList<>();
        var data = ToolHelper.getToolData(toolStack);
        lines.add(" " + Component.translatable("gui.tconstruct_nirvana.toolstation.durability",
                data.durability()).getString());
        lines.add(" " + Component.translatable("gui.tconstruct_nirvana.toolstation.attack",
                String.format("%.2f", data.attack())).getString());
        lines.add(" " + Component.translatable("gui.tconstruct_nirvana.toolstation.miningspeed",
                String.format("%.2f", data.speed())).getString());
        lines.add(" " + Component.translatable("gui.tconstruct_nirvana.toolstation.harvestlevel",
                data.harvestLevel()).getString());
        lines.add(" " + Component.translatable("gui.tconstruct_nirvana.toolstation.modifiers",
                data.modifiers()).getString());
        return lines;
    }

    private List<String> traitInfoLines() {
        ItemStack toolStack = currentToolStack();
        // 已有工具/组装预览：部件列表（1:1 旧版 components）
        if (toolStack.isEmpty()) {
            TinkerToolItem selected = menu.getSelectedTool();
            if (selected != null) {
                return componentLines(selected);
            }
            return List.of(Component.translatable("gui.tconstruct_nirvana.toolstation.deconstruct.info").getString());
        }
        // 修饰符/特质列表（简化：显示数量与标识符）
        List<String> lines = new ArrayList<>();
        List<com.lvdriver.tconstruct_nirvana.data.ModifierData> modifiers =
                toolStack.getOrDefault(ModDataComponents.MODIFIERS, List.of());
        if (modifiers.isEmpty()) {
            lines.add(Component.translatable("gui.tconstruct_nirvana.toolstation.noTraits").getString());
        } else {
            for (com.lvdriver.tconstruct_nirvana.data.ModifierData m : modifiers) {
                lines.add(" * " + m.identifier());
            }
        }
        return lines;
    }

    private List<String> componentLines(TinkerToolItem tool) {
        List<String> lines = new ArrayList<>();
        for (PartMaterialType pmt : tool.getRequiredComponents()) {
            StringBuilder sb = new StringBuilder(" * ");
            boolean first = true;
            for (var part : pmt.getPossibleParts()) {
                if (!first) {
                    sb.append("/");
                }
                sb.append(new ItemStack(part).getHoverName().getString());
                first = false;
            }
            lines.add(sb.toString());
        }
        return lines;
    }

    /** 当前展示的工具栈（结果槽优先，其次工具槽）。 */
    private ItemStack currentToolStack() {
        ItemStack result = menu.getResult();
        if (!result.isEmpty() && result.getItem() instanceof TinkerToolItem) {
            return result;
        }
        ItemStack tool = menu.getToolSlot();
        return tool.getItem() instanceof TinkerToolItem ? tool : ItemStack.EMPTY;
    }

    private void drawInfoText(GuiGraphics graphics, int x, int y, boolean toolPanel) {
        int px = this.leftPos + x;
        int py = this.topPos + y;
        int tx = px + 6;
        int ty = py + 6;
        List<String> lines = toolPanel ? toolInfoLines() : traitInfoLines();
        String caption;
        if (toolPanel) {
            ItemStack toolStack = currentToolStack();
            caption = !toolStack.isEmpty() ? toolStack.getHoverName().getString()
                    : Component.translatable("gui.tconstruct_nirvana.toolstation.repair").getString();
        } else {
            if (currentToolStack().isEmpty() && menu.getSelectedTool() == null) {
                caption = Component.translatable("gui.tconstruct_nirvana.toolstation.deconstruct").getString();
            } else if (currentToolStack().isEmpty()) {
                caption = Component.translatable("gui.tconstruct_nirvana.toolstation.components").getString();
            } else {
                caption = Component.translatable("gui.tconstruct_nirvana.toolstation.traits").getString();
            }
        }
        // 标题（居中，1:1 旧版带下划线阴影）
        int cx = px + PANEL_W / 2 - this.font.width(caption) / 2;
        graphics.drawString(this.font, caption, cx, ty, 0xfff0f0f0, true);
        ty += this.font.lineHeight + 4;
        for (String line : lines) {
            if (ty > py + PANEL_H - 6) {
                break;
            }
            graphics.drawString(this.font, line, tx, ty, 0xfff0f0f0, true);
            ty += this.font.lineHeight;
        }
    }

    /* ---------- 交互 ---------- */

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            List<ToolBuildGuiInfo> infos = ToolStationLayouts.all();
            for (int i = 0; i < infos.size(); i++) {
                int bx = this.leftPos + BUTTONS_X;
                int by = this.topPos + BUTTONS_Y + i * (BUTTON_SIZE + BUTTON_SPACING);
                if (mouseX >= bx && mouseX < bx + BUTTON_SIZE && mouseY >= by && mouseY < by + BUTTON_SIZE) {
                    if (this.menu.clickMenuButton(this.minecraft.player, i)) {
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
                    }
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        // 输入槽位置由布局驱动（Slot.x/y 为 final），悬停判定需自定义；其余槽走 vanilla
        this.hoveredSlot = findSlotCustom(mouseX, mouseY);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    /** 输入槽渲染（1.21.1 Slot.x/y final，位置由布局驱动；其余槽走 vanilla）。 */
    @Override
    protected void renderSlot(GuiGraphics graphics, Slot slot) {
        if (slot.index >= TinkerStationMenu.INPUT_SLOTS) {
            super.renderSlot(graphics, slot);
            return;
        }
        int[] p = slotPos(slot.index);
        if (p == null) {
            return; // 停用槽不渲染
        }
        int i = this.leftPos + p[0];
        int j = this.topPos + p[1];
        ItemStack itemstack = slot.getItem();
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 100.0F);
        if (itemstack.isEmpty() && slot.isActive() && slot.getNoItemIcon() != null) {
            var pair = slot.getNoItemIcon();
            var sprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
            graphics.blit(i, j, 0, 16, 16, sprite);
        }
        this.renderSlotContents(graphics, itemstack, slot, null);
        graphics.pose().popPose();
    }

    /** 悬停判定（布局位置版 vanilla findSlot）。 */
    private Slot findSlotCustom(double mouseX, double mouseY) {
        for (int i = 0; i < this.menu.slots.size(); i++) {
            Slot slot = this.menu.slots.get(i);
            if (slot.isActive() && isHoveredSlotCustom(slot, mouseX, mouseY) && slot.mayPickup(this.minecraft.player)) {
                return slot;
            }
        }
        return null;
    }

    private boolean isHoveredSlotCustom(Slot slot, double mouseX, double mouseY) {
        int[] p = slot.index < TinkerStationMenu.INPUT_SLOTS ? slotPos(slot.index) : new int[]{slot.x, slot.y};
        return p != null && isHovering(p[0], p[1], 16, 16, mouseX, mouseY);
    }
}
