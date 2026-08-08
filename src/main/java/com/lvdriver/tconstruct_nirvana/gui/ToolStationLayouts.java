package com.lvdriver.tconstruct_nirvana.gui;

import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import com.lvdriver.tconstruct_nirvana.item.tool.ModTools;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具站工具选择布局（1:1 移植自 Tinkers' Antique
 * {@code HarvestClientProxy / MeleeClientProxy / RangedClientProxy /
 * GuiButtonRepair} 的槽位定义）。
 *
 * <p>每个工具定义一组部件槽位坐标（相对主 GUI），左侧按钮列按此列表渲染；
 * 修复模式使用 6 槽环形布局。</p>
 */
public final class ToolStationLayouts {

    /** 修复模式布局（1:1 旧版 GuiButtonRepair.info）。 */
    public static final ToolBuildGuiInfo REPAIR = new ToolBuildGuiInfo(null, ItemStack.EMPTY)
            .addSlot(33, 42)
            .addSlot(15, 62)
            .addSlot(11, 37)
            .addSlot(33, 19)
            .addSlot(55, 37)
            .addSlot(51, 62);

    /** 全部布局（修复 + 21 工具，索引 = 按钮 id，1:1 旧版按钮列顺序）。 */
    private static final List<ToolBuildGuiInfo> ALL = new ArrayList<>();

    static {
        ALL.add(REPAIR);
        ALL.addAll(toolList());
    }

    private ToolStationLayouts() {
    }

    /** 工具布局列表（按 ModTools 注册顺序）。 */
    private static List<ToolBuildGuiInfo> toolList() {
        List<ToolBuildGuiInfo> list = new ArrayList<>();
        for (TinkerToolItem tool : ModTools.getAllTools()) {
            list.add(build(tool, positionsOf(tool)));
        }
        return list;
    }

    private static ToolBuildGuiInfo build(TinkerToolItem tool, int[][] positions) {
        ToolBuildGuiInfo info = new ToolBuildGuiInfo(tool, buildIcon(tool));
        for (int[] p : positions) {
            info.addSlot(p[0], p[1]);
        }
        return info;
    }

    /** 全部按钮（修复 + 工具），索引即按钮 id。 */
    public static List<ToolBuildGuiInfo> all() {
        return ALL;
    }

    /** 修复模式 + 全部工具布局（按钮 id → 布局）。 */
    public static ToolBuildGuiInfo byId(int id) {
        if (id >= 0 && id < ALL.size()) {
            return ALL.get(id);
        }
        return REPAIR;
    }

    /** 工具图标：默认材料构建的工具成品（1:1 旧版 buildItemForRenderingInGui）。 */
    private static ItemStack buildIcon(TinkerToolItem tool) {
        List<Material> mats = new ArrayList<>();
        for (PartMaterialType pmt : tool.getRequiredComponents()) {
            Material chosen = Material.UNKNOWN;
            outer:
            for (ToolPart part : pmt.getPossibleParts()) {
                for (Material candidate : ModMaterials.getAllMaterials()) {
                    if (part.canUseMaterial(candidate)) {
                        chosen = candidate;
                        break outer;
                    }
                }
            }
            mats.add(chosen);
        }
        return tool.buildItem(mats);
    }

    /** 各工具槽位（1:1 旧版三个 ClientProxy 的 addSlotPosition 数据）。 */
    private static int[][] positionsOf(TinkerToolItem tool) {
        Map<TinkerToolItem, int[][]> map = new LinkedHashMap<>();
        // 采掘（HarvestClientProxy）
        map.put(ModTools.PICKAXE.get(), new int[][]{{15, 60}, {53, 22}, {33, 42}});
        map.put(ModTools.SHOVEL.get(), new int[][]{{33, 42}, {51, 24}, {13, 62}});
        map.put(ModTools.HATCHET.get(), new int[][]{{22, 53}, {31, 22}, {51, 34}});
        map.put(ModTools.MATTOCK.get(), new int[][]{{22, 53}, {31, 22}, {51, 34}});
        map.put(ModTools.KAMA.get(), new int[][]{{22, 53}, {31, 22}, {51, 34}});
        map.put(ModTools.HAMMER.get(), new int[][]{{21, 52}, {44, 29}, {57, 48}, {25, 16}});
        map.put(ModTools.EXCAVATOR.get(), new int[][]{{25, 46}, {45, 26}, {25, 26}, {7, 62}});
        map.put(ModTools.LUMBER_AXE.get(), new int[][]{{32, 46}, {33, 22}, {53, 38}, {13, 62}});
        map.put(ModTools.SCYTHE.get(), new int[][]{{17, 54}, {36, 19}, {56, 29}, {37, 47}});
        // 近战（MeleeClientProxy）
        map.put(ModTools.BROAD_SWORD.get(), new int[][]{{12, 62}, {48, 26}, {30, 44}});
        map.put(ModTools.LONG_SWORD.get(), new int[][]{{12, 62}, {48, 26}, {30, 44}});
        map.put(ModTools.RAPIER.get(), new int[][]{{52, 62}, {18, 26}, {32, 44}});
        map.put(ModTools.FRY_PAN.get(), new int[][]{{12, 62}, {34, 36}});
        map.put(ModTools.BATTLE_SIGN.get(), new int[][]{{27, 60}, {27, 34}});
        map.put(ModTools.CLEAVER.get(), new int[][]{{9, 64}, {25, 36}, {47, 30}, {33, 58}});
        // 远程（RangedClientProxy）
        map.put(ModTools.SHURIKEN.get(), new int[][]{{20, 29}, {44, 29}, {44, 53}, {20, 53}});
        map.put(ModTools.SHORT_BOW.get(), new int[][]{{36, 23}, {14, 45}, {38, 47}});
        map.put(ModTools.LONG_BOW.get(), new int[][]{{44, 19}, {10, 53}, {17, 26}, {38, 47}});
        map.put(ModTools.ARROW.get(), new int[][]{{32, 41}, {50, 23}, {14, 59}});
        map.put(ModTools.CROSS_BOW.get(), new int[][]{{38, 47}, {44, 19}, {14, 23}, {18, 51}});
        map.put(ModTools.BOLT.get(), new int[][]{{40, 37}, {20, 53}});
        int[][] positions = map.get(tool);
        return positions != null ? positions : new int[][]{{33, 42}, {15, 62}, {11, 37}};
    }
}
