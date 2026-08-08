package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * 工具站槽位布局信息（1:1 移植自 Tinkers' Antique {@code ToolBuildGuiInfo}）。
 *
 * <p>记录选中工具后输入槽的屏幕位置（相对主 GUI，x 7~69 / y 18~64 区域），
 * 修复模式为 6 槽布局（工具槽 + 5 材料槽）。</p>
 */
public class ToolBuildGuiInfo {

    /** 工具（null = 修复模式）。 */
    public final TinkerToolItem tool;
    /** 按钮图标（工具成品栈，用默认材料构建；修复模式为铁砧图标）。 */
    public final ItemStack icon;
    /** 槽位坐标列表（相对主 GUI，{x, y}）。 */
    public final List<int[]> positions = new ArrayList<>();

    public ToolBuildGuiInfo(TinkerToolItem tool, ItemStack icon) {
        this.tool = tool;
        this.icon = icon;
    }

    public ToolBuildGuiInfo addSlot(int x, int y) {
        positions.add(new int[]{x, y});
        return this;
    }

    /** 激活输入槽数（1:1 旧版 activeSlots = min(槽位数, 6)）。 */
    public int slotCount() {
        return Math.min(positions.size(), 6);
    }

    public boolean isRepair() {
        return tool == null;
    }
}
