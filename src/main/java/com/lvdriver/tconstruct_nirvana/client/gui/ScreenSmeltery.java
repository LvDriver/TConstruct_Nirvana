package com.lvdriver.tconstruct_nirvana.client.gui;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.gui.ContainerSmeltery;
import com.lvdriver.tconstruct_nirvana.smeltery.SmelteryTank;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
/**
 * 冶炼炉 GUI（1:1 移植自 Tinkers' Antique {@code GuiSmeltery} 最小版）。
 *
 * <p>左侧液体罐区域（60×68）：按层绘制流体（tint 色块占位，后续会话换
 * 贴图渲染）+ 悬浮 tooltip（名称/量）；点击某层液体 → 服务端装桶（消耗
 * 背包空桶）。右上燃料指示（fuel>0 亮起）。滚动按钮翻页物品侧栏。</p>
 */
public class ScreenSmeltery extends AbstractContainerScreen<ContainerSmeltery> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "textures/gui/smeltery.png");

    /** 液体罐区域（1:1 旧版 GuiSmeltery：x=8,y=16,w=60,h=68）。 */
    private static final int TANK_X = 8;
    private static final int TANK_Y = 16;
    private static final int TANK_W = 60;
    private static final int TANK_H = 68;

    public ScreenSmeltery(ContainerSmeltery menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.titleLabelX = 8;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // 液体罐：按层绘制（每层按量占高）
        SmelteryTank tank = menu.getTile() != null ? menu.getTile().getTank() : null;
        if (tank != null && !tank.getFluids().isEmpty()) {
            int capacity = Math.max(1, tank.getCapacity());
            int y = TANK_Y;
            List<FluidStack> fluids = tank.getFluids();
            for (int i = 0; i < fluids.size(); i++) {
                FluidStack fluid = fluids.get(i);
                int h = Math.max(1, TANK_H * fluid.getAmount() / capacity);
                if (i == fluids.size() - 1) {
                    h = TANK_Y + TANK_H - y; // 最后一层补满
                }
                if (h <= 0) {
                    continue;
                }
                int color = net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions.of(
                        fluid.getFluid().getFluidType()).getTintColor(fluid);
                graphics.fill(leftPos + TANK_X, topPos + y, leftPos + TANK_X + TANK_W, topPos + y + h, color);
                y += h;
            }
        }

        // 燃料指示（右上）
        if (menu.getTile() != null && menu.getTile().hasFuel()) {
            graphics.fill(leftPos + 71, topPos + 16, leftPos + 83, topPos + 68, 0xFFFF6600);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        // 液体 tooltip
        SmelteryTank tank = menu.getTile() != null ? menu.getTile().getTank() : null;
        if (tank != null && isHovering(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX, mouseY)) {
            FluidStack hovered = getFluidAt(mouseX - leftPos, mouseY - topPos);
            if (hovered != null) {
                graphics.renderTooltip(font, Component.literal(hovered.getHoverName().getString()
                        + ": " + hovered.getAmount() + " mb"), mouseX, mouseY);
            } else {
                graphics.renderTooltip(font, Component.translatable("gui.tconstruct_nirvana.smeltery.empty"),
                        mouseX, mouseY);
            }
        }
    }

    /** 点击液体区域 → 装桶（服务端处理）。 */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX, mouseY)) {
            // 找点击的液体层（底层=0，1:1 旧版 GUI 从底部点击）
            int index = getFluidIndexAt((int) mouseX - leftPos, (int) mouseY - topPos);
            if (index >= 0) {
                // 2 + index 编码液体层（0/1 为滚动）
                if (this.menu.clickMenuButton(this.minecraft.player, 2 + index)) {
                    this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 2 + index);
                }
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /** 滚动（上/下滚轮，1:1 旧版侧栏滚动）。 */
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (verticalAmount == 0) {
            return false;
        }
        int id = verticalAmount > 0 ? 0 : 1;
        if (this.menu.clickMenuButton(this.minecraft.player, id)) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
        }
        return true;
    }

    private FluidStack getFluidAt(int x, int y) {
        int index = getFluidIndexAt(x, y);
        SmelteryTank tank = menu.getTile() != null ? menu.getTile().getTank() : null;
        if (tank == null || index < 0 || index >= tank.getFluids().size()) {
            return null;
        }
        return tank.getFluids().get(index);
    }

    /** 由 y 坐标反推液体层（自下而上，底层=0）。 */
    private int getFluidIndexAt(int x, int y) {
        SmelteryTank tank = menu.getTile() != null ? menu.getTile().getTank() : null;
        if (tank == null || x < TANK_X || x >= TANK_X + TANK_W || y < TANK_Y || y >= TANK_Y + TANK_H) {
            return -1;
        }
        List<FluidStack> fluids = tank.getFluids();
        if (fluids.isEmpty()) {
            return -1;
        }
        int capacity = Math.max(1, tank.getCapacity());
        // 自底向上累计高度
        int fromBottom = TANK_Y + TANK_H - y; // 底部起的高度
        int accumulated = 0;
        for (int i = fluids.size() - 1; i >= 0; i--) {
            accumulated += Math.max(1, TANK_H * fluids.get(i).getAmount() / capacity);
            if (fromBottom <= accumulated) {
                return i;
            }
        }
        return -1;
    }
}
