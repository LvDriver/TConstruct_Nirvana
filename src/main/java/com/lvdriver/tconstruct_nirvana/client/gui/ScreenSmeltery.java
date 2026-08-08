package com.lvdriver.tconstruct_nirvana.client.gui;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.gui.ContainerSmeltery;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 冶炼炉 GUI（1:1 移植自 Tinkers' Antique {@code GuiSmeltery} 最小版）。
 *
 * <p>左侧液体罐区域（60×68）：按层绘制流体（tint 色块占位，后续会话换
 * 贴图渲染）+ 悬浮 tooltip（名称/量）；点击某层液体 → 服务端装桶（消耗
 * 背包空桶）。右上燃料指示（fuel>0 亮起）。滚动按钮翻页物品侧栏。
 * 液体/燃料数据经菜单 DataSlot 同步（服务端 broadcastChanges 下发）。</p>
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

    /** 当前液体列表（由 DataSlot 同步数据组装；每层 = (流体, 量)，层序与服务端一致）。 */
    private List<FluidStack> fluids() {
        List<FluidStack> fluids = new ArrayList<>();
        int layers = menu.syncData.get(ContainerSmeltery.DATA_LAYERS).get();
        for (int i = 0; i < layers; i++) {
            int fluidId = menu.syncData.get(ContainerSmeltery.DATA_FLUID_START + i * 2).get();
            int amount = menu.syncData.get(ContainerSmeltery.DATA_FLUID_START + i * 2 + 1).get();
            // 保留 0 量层（同步窗口可能短暂存在），保证点击索引与服务端层号一致
            Fluid fluid = BuiltInRegistries.FLUID.byId(fluidId);
            if (fluid != null) {
                fluids.add(new FluidStack(fluid, amount));
            }
        }
        return fluids;
    }

    /** 总容量（同步数据缺省 729 槽 × 8 锭；仅用于比例显示，非精确）。 */
    private int capacity() {
        return 729 * 144 * 8;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        // 液体罐：按层绘制（自顶向下：i=0 画在最上层；点击命中从底部反推，两者自洽）
        List<FluidStack> fluids = fluids();
        if (!fluids.isEmpty()) {
            int cap = Math.max(1, capacity());
            int y = TANK_Y;
            for (int i = 0; i < fluids.size(); i++) {
                FluidStack fluid = fluids.get(i);
                int h = Math.max(1, TANK_H * fluid.getAmount() / cap);
                if (i == fluids.size() - 1) {
                    h = TANK_Y + TANK_H - y; // 最后一层补满
                }
                if (h <= 0) {
                    continue;
                }
                // 流体贴图渲染（1:1 旧版 GuiUtil.drawGuiTank：still 贴图拉伸 + 染色）
                int tint = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getTintColor(fluid);
                float r = (tint >> 16 & 255) / 255f;
                float g = (tint >> 8 & 255) / 255f;
                float b = (tint & 255) / 255f;
                float a = (tint >> 24 & 255) / 255f;
                ResourceLocation still = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getStillTexture(fluid);
                if (still != null) {
                    var sprite = this.minecraft.getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS).apply(still);
                    graphics.blit(leftPos + TANK_X, topPos + y, 0, TANK_W, h, sprite, r, g, b, a);
                }
                y += h;
            }
        }

        // 燃料条（右上，1:1 旧版 drawFuel(71, 16, 12, 52)：按剩余燃料比例绘制）
        int fuel = menu.syncData.get(ContainerSmeltery.DATA_FUEL).get();
        if (fuel > 0) {
            int h = Math.max(1, (int) (52 * Math.min(1f, fuel / 1000f)));
            graphics.fill(leftPos + 71, topPos + 68 - h, leftPos + 83, topPos + 68, 0xFFFF6600);
        }
        // 温度显示（燃料条上方，旧版 heat = temperature + 300）
        int heat = menu.syncData.get(ContainerSmeltery.DATA_TEMPERATURE).get();
        if (heat > 0) {
            graphics.drawString(this.font, String.valueOf(heat),
                    leftPos + 71, topPos + 70, 0xFFFFFFFF);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        // 液体 tooltip
        if (isHovering(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX, mouseY)) {
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
        List<FluidStack> fluids = fluids();
        if (index < 0 || index >= fluids.size()) {
            return null;
        }
        return fluids.get(index);
    }

    /** 由 y 坐标反推液体层（自下而上，底层=0；与 renderBg 高度计算完全一致）。 */
    private int getFluidIndexAt(int x, int y) {
        List<FluidStack> fluids = fluids();
        if (x < TANK_X || x >= TANK_X + TANK_W || y < TANK_Y || y >= TANK_Y + TANK_H || fluids.isEmpty()) {
            return -1;
        }
        int cap = Math.max(1, capacity());
        // 与 renderBg 同规则计算各层渲染高度（自顶向下：末层补满到罐底）
        int[] heights = new int[fluids.size()];
        int yOff = TANK_Y;
        for (int i = 0; i < fluids.size(); i++) {
            int h = Math.max(1, TANK_H * fluids.get(i).getAmount() / cap);
            if (i == fluids.size() - 1) {
                h = TANK_Y + TANK_H - yOff; // 末层补满
            }
            if (h <= 0) {
                h = 0;
            }
            heights[i] = h;
            yOff += h;
        }
        // 自底向上累计命中
        int fromBottom = TANK_Y + TANK_H - y;
        int accumulated = 0;
        for (int i = fluids.size() - 1; i >= 0; i--) {
            accumulated += heights[i];
            if (fromBottom <= accumulated) {
                return i;
            }
        }
        return -1;
    }
}
