package com.lvdriver.tconstruct_nirvana.api.event;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 工具组装事件（{@link NeoForge#EVENT_BUS}，可取消）。
 *
 * <p>触发时机：工具组装（工作台组装配方 / 创造标签页生成）计算基础属性之后、
 * 写入物品组件之前，每次组装触发一次。附属可在此修改基础属性
 * （{@link #setToolData}，如调整耐久/采掘等级/速度/攻击），或取消组装
 * （{@link #setCanceled(boolean)}，取消后组装不产出工具）。</p>
 *
 * <p>事件内修改 {@link ItemStack} 组件（如追加修饰符/特质）同样生效。
 * 监听示例：{@code NeoForge.EVENT_BUS.addListener(EventPriority.LOW, ToolBuildEvent.class, e -> ...)}。</p>
 *
 * <p><b>稳定 API 承诺</b>：字段与 getter 一经发布不变；新增数据只以新 getter 追加。</p>
 */
public class ToolBuildEvent extends Event implements ICancellableEvent {

    /** 正在组装的工具（物品类型）。 */
    private final TinkerToolItem tool;

    /** 组装中的工具栈（已写 BASE_MATERIALS，未写 TOOL_DATA/组件，可直接修改）。 */
    private final ItemStack stack;

    /** 槽位材料列表（只读视图，顺序即槽位顺序）。 */
    private final List<Material> materials;

    /** 基础属性（可修改，组装结果以修改后为准）。 */
    private ToolData toolData;

    public ToolBuildEvent(TinkerToolItem tool, ItemStack stack, List<Material> materials, ToolData toolData) {
        this.tool = tool;
        this.stack = stack;
        this.materials = List.copyOf(materials);
        this.toolData = toolData;
    }

    /** 正在组装的工具（物品类型）。 */
    public TinkerToolItem getTool() {
        return tool;
    }

    /** 组装中的工具栈（可直接修改组件）。 */
    public ItemStack getStack() {
        return stack;
    }

    /** 槽位材料列表（只读，顺序即槽位顺序）。 */
    public List<Material> getMaterials() {
        return materials;
    }

    /** 当前基础属性（默认 = 工具公式计算结果，见 {@link ToolData}）。 */
    public ToolData getToolData() {
        return toolData;
    }

    /** 修改基础属性（组装结果以修改后为准）。 */
    public void setToolData(ToolData toolData) {
        this.toolData = toolData;
    }
}
