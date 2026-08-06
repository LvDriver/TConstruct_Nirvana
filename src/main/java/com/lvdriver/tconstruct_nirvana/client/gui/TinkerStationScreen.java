package com.lvdriver.tconstruct_nirvana.client.gui;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.gui.TinkerStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * 工具站/锻造厂 GUI（1:1 移植自 Tinkers' Antique {@code GuiToolStation} 简化版）。
 *
 * <p>仅绘制背景与槽位（旧版 generic.png 布局）；构建/修饰符/修复按钮
 * 留待后续会话。</p>
 */
public class TinkerStationScreen extends AbstractContainerScreen<TinkerStationMenu> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "textures/gui/tool_station.png");

    public TinkerStationScreen(TinkerStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
