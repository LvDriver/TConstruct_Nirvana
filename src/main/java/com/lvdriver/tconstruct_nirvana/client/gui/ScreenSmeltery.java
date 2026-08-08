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
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 冶炼炉 GUI（1:1 移植自 Tinkers' Antique {@code GuiSmeltery} 完整版）。
 *
 * <p>布局 1:1 旧版：材料输入侧栏在左（槽背景 22×18 + 每槽左侧竖向进度条：
 * 融化中橙 / 温度不够或无燃料蓝 / 无配方灰 / 液体已满黄，悬浮有 tooltip），
 * 主 GUI 在右（左上液体罐 52×52 + scala 刻度贴图覆盖，右上燃料条渲染为
 * 流体外观 + 下方温度数值，左下玩家背包）。液体/燃料/容量/进度数据经
 * Menu DataSlot 同步。</p>
 */
public class ScreenSmeltery extends AbstractContainerScreen<ContainerSmeltery> {

    private static final ResourceLocation BACKGROUND =
            ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "textures/gui/smeltery.png");

    /** 液体罐区域（相对主 GUI，1:1 旧版 scala 52×52，与 tooltip/点击判定一致）。 */
    private static final int TANK_X = 8;
    private static final int TANK_Y = 16;
    private static final int TANK_W = 52;
    private static final int TANK_H = 52;
    /** 最小液位高度（px，1:1 旧版 Config.minFluidHeight=3）。 */
    private static final int MIN_FLUID_HEIGHT = 3;
    /** 燃料条区域（相对主 GUI，1:1 旧版 drawFuel(71,16,12,52)）。 */
    private static final int FUEL_X = 71;
    private static final int FUEL_Y = 16;
    private static final int FUEL_W = 12;
    private static final int FUEL_H = 52;

    /** scala 刻度贴图源（smeltery.png 中 (176,76,52,52)）。 */
    private static final int SCALA_U = 176;
    private static final int SCALA_V = 76;
    /** 侧栏槽背景贴图源（(0,166,22,18)）。 */
    private static final int SLOT_U = 0;
    private static final int SLOT_V = 166;
    /** 进度条贴图源（smeltery.png (176,150) 起 4 条 3×16）。 */
    private static final int BAR_U = 176;
    private static final int BAR_V = 150;
    private static final int BAR_W = 3;
    private static final int BAR_H = 16;

    public ScreenSmeltery(ContainerSmeltery menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = ContainerSmeltery.MAIN_X + 176;
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
            Fluid fluid = BuiltInRegistries.FLUID.byId(fluidId);
            if (fluid != null) {
                fluids.add(new FluidStack(fluid, amount));
            }
        }
        return fluids;
    }

    /** 总容量（mb，由 DataSlot 同步真实容量）。 */
    private int capacity() {
        return Math.max(1, menu.syncData.get(ContainerSmeltery.DATA_CAPACITY).get());
    }

    /** 冶炼炉槽数（DataSlot 同步）。 */
    private int slotCount() {
        return Math.max(0, menu.syncData.get(ContainerSmeltery.DATA_SLOT_COUNT).get());
    }

    /**
     * 液位高度计算（1:1 旧版 SmelteryRenderer.calcLiquidHeights）：
     * 每层至少 min 像素；总量不满时底部留 min 像素空罐；超出时从最高层逐像素削减。
     */
    private int[] calcLiquidHeights(List<FluidStack> liquids, int capacity, int height) {
        int[] heights = new int[liquids.size()];
        int total = 0;
        for (int i = 0; i < liquids.size(); i++) {
            float h = (float) liquids.get(i).getAmount() / capacity;
            total += liquids.get(i).getAmount();
            heights[i] = Math.max(MIN_FLUID_HEIGHT, (int) Math.ceil(h * height));
        }
        if (total < capacity) {
            height -= MIN_FLUID_HEIGHT;
        }
        int sum;
        do {
            sum = 0;
            int biggest = -1;
            int max = 0;
            for (int i = 0; i < heights.length; i++) {
                sum += heights[i];
                if (heights[i] > max) {
                    max = heights[i];
                    biggest = i;
                }
            }
            if (biggest < 0 || heights[biggest] == 0) {
                break;
            }
            if (sum > height) {
                heights[biggest]--;
            }
        } while (sum > height);
        return heights;
    }

    /** 主 GUI 左上角（侧栏右侧）。 */
    private int mainX() {
        return this.leftPos + ContainerSmeltery.MAIN_X;
    }

    private int mainY() {
        return this.topPos;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int mainX = mainX();
        int mainY = mainY();

        // 主 GUI 背景
        graphics.blit(BACKGROUND, mainX, mainY, 0, 0, 176, 166);

        // 侧栏槽背景（3 列 × 可见行数；超出槽数的位置补 slotEmpty 效果——直接画空槽背景）
        int rows = Math.max(1, Math.min(8, (Math.max(1, slotCount()) + 2) / 3));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < ContainerSmeltery.SIDE_COLUMNS; col++) {
                graphics.blit(BACKGROUND,
                        this.leftPos + ContainerSmeltery.SIDE_BORDER + col * ContainerSmeltery.SLOT_W,
                        this.topPos + ContainerSmeltery.SIDE_BORDER + row * ContainerSmeltery.SLOT_H,
                        SLOT_U, SLOT_V, ContainerSmeltery.SLOT_W, ContainerSmeltery.SLOT_H);
            }
        }

        // 液体罐：按层自底向上绘制（高度算法 1:1 旧版）
        List<FluidStack> fluids = fluids();
        if (!fluids.isEmpty()) {
            int cap = capacity();
            int[] heights = calcLiquidHeights(fluids, cap, TANK_H);
            int y = TANK_Y + TANK_H; // 罐底
            for (int i = 0; i < heights.length; i++) {
                int h = heights[i];
                if (h <= 0) {
                    continue;
                }
                y -= h;
                FluidStack fluid = fluids.get(i);
                int tint = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getTintColor(fluid);
                float r = (tint >> 16 & 255) / 255f;
                float g = (tint >> 8 & 255) / 255f;
                float b = (tint & 255) / 255f;
                float a = (tint >> 24 & 255) / 255f;
                ResourceLocation still = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType()).getStillTexture(fluid);
                if (still != null) {
                    var sprite = this.minecraft.getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS).apply(still);
                    graphics.blit(mainX + TANK_X, mainY + y, 0, TANK_W, h, sprite, r, g, b, a);
                }
            }
        }
        // scala 刻度贴图覆盖在罐上（1:1 旧版 scala.draw，空罐也显示）
        graphics.blit(BACKGROUND, mainX + TANK_X, mainY + TANK_Y, SCALA_U, SCALA_V, TANK_W, TANK_H);

        // 燃料条：流体外观（当前燃料 still 贴图，缺省岩浆），自底部按剩余比例上涨
        int fuel = menu.syncData.get(ContainerSmeltery.DATA_FUEL).get();
        if (fuel > 0) {
            Fluid fuelFluid = Fluids.LAVA;
            int fuelFluidId = menu.syncData.get(ContainerSmeltery.DATA_FUEL_FLUID).get();
            if (fuelFluidId >= 0) {
                Fluid synced = BuiltInRegistries.FLUID.byId(fuelFluidId);
                if (synced != null) {
                    fuelFluid = synced;
                }
            }
            int h = Math.max(1, (int) (FUEL_H * Math.min(1f, fuel / 1000f)));
            int tint = IClientFluidTypeExtensions.of(fuelFluid.getFluidType()).getTintColor(new FluidStack(fuelFluid, 1000));
            float r = (tint >> 16 & 255) / 255f;
            float g = (tint >> 8 & 255) / 255f;
            float b = (tint & 255) / 255f;
            float a = (tint >> 24 & 255) / 255f;
            ResourceLocation still = IClientFluidTypeExtensions.of(fuelFluid.getFluidType()).getStillTexture(new FluidStack(fuelFluid, 1000));
            if (still != null) {
                var sprite = this.minecraft.getTextureAtlas(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS).apply(still);
                graphics.blit(mainX + FUEL_X, mainY + FUEL_Y + FUEL_H - h, 0, FUEL_W, h, sprite, r, g, b, a);
            }
            // 温度数值（燃料条下方，heat = temperature + 300）
            int heat = menu.syncData.get(ContainerSmeltery.DATA_TEMPERATURE).get();
            graphics.drawString(this.font, String.valueOf(heat),
                    mainX + FUEL_X, mainY + FUEL_Y + FUEL_H + 2, 0xFFFFFFFF, true);
        }

        // 每槽进度条（有物品且非空状态；绘制在槽背景内左侧，1:1 旧版）
        int scrollOffset = menu.getScrollOffset();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < ContainerSmeltery.SIDE_COLUMNS; col++) {
                int viewIndex = col + row * ContainerSmeltery.SIDE_COLUMNS;
                int realIndex = scrollOffset + viewIndex;
                if (realIndex >= ContainerSmeltery.MAX_SYNC_SLOTS) {
                    continue;
                }
                int status = menu.syncData.get(ContainerSmeltery.DATA_PROGRESS_START + realIndex).get();
                if (status == -3) {
                    continue;
                }
                int barU = BAR_U;
                int height = BAR_H;
                if (status == -2) {
                    barU += 9; // noMelt（灰）
                } else if (status == -1) {
                    barU += 3; // unprogress（蓝）
                } else if (status == 101) {
                    barU += 6; // uberHeat（黄）
                } else {
                    height = 1 + Math.round(Math.min(100, Math.max(1, status)) / 100f * (BAR_H - 1));
                }
                int barX = this.leftPos + ContainerSmeltery.SIDE_BORDER + col * ContainerSmeltery.SLOT_W + 1;
                int barY = this.topPos + ContainerSmeltery.SIDE_BORDER + row * ContainerSmeltery.SLOT_H + 1;
                // 贴图自底部绘制（drawModalRectWithCustomSizedTexture 语义：从贴图底部截取）
                graphics.blit(BACKGROUND, barX, barY + BAR_H - height, barU, BAR_V + BAR_H - height, BAR_W, height);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        // 液体 tooltip（区域与渲染一致，相对主 GUI）
        if (isHovering(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX - ContainerSmeltery.MAIN_X, mouseY)) {
            FluidStack hovered = getFluidAt(mouseX - mainX(), mouseY - mainY());
            if (hovered != null) {
                graphics.renderTooltip(font, Component.literal(hovered.getHoverName().getString()
                        + ": " + hovered.getAmount() + " mb"), mouseX, mouseY);
            } else {
                graphics.renderTooltip(font, Component.translatable("gui.tconstruct_nirvana.smeltery.empty"),
                        mouseX, mouseY);
            }
        }

        // 进度条 tooltip（1:1 旧版 gui.smeltery.progress.*）
        int rows = Math.max(1, Math.min(8, (Math.max(1, slotCount()) + 2) / 3));
        int scrollOffset = menu.getScrollOffset();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < ContainerSmeltery.SIDE_COLUMNS; col++) {
                int viewIndex = col + row * ContainerSmeltery.SIDE_COLUMNS;
                int realIndex = scrollOffset + viewIndex;
                if (realIndex >= ContainerSmeltery.MAX_SYNC_SLOTS) {
                    continue;
                }
                int status = menu.syncData.get(ContainerSmeltery.DATA_PROGRESS_START + realIndex).get();
                if (status == -3 || (status >= 0 && status <= 100)) {
                    continue;
                }
                int barX = this.leftPos + ContainerSmeltery.SIDE_BORDER + col * ContainerSmeltery.SLOT_W + 1;
                int barY = this.topPos + ContainerSmeltery.SIDE_BORDER + row * ContainerSmeltery.SLOT_H + 1;
                if (mouseX >= barX && mouseX < barX + BAR_W && mouseY >= barY && mouseY < barY + BAR_H) {
                    String key = status == -2 ? "gui.tconstruct_nirvana.smeltery.progress.no_recipe"
                            : status == -1 ? "gui.tconstruct_nirvana.smeltery.progress.no_fuel"
                            : "gui.tconstruct_nirvana.smeltery.progress.no_space";
                    graphics.renderTooltip(font, Component.translatable(key), mouseX, mouseY);
                    break;
                }
            }
        }
    }

    /** 点击液体区域 → 装桶（服务端处理）。 */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isHovering(TANK_X, TANK_Y, TANK_W, TANK_H, mouseX - ContainerSmeltery.MAIN_X, mouseY)) {
            // 找点击的液体层（底层=0，1:1 旧版 GUI 从底部点击）
            int index = getFluidIndexAt((int) mouseX - mainX(), (int) mouseY - mainY());
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
        int[] heights = calcLiquidHeights(fluids, capacity(), TANK_H);
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
