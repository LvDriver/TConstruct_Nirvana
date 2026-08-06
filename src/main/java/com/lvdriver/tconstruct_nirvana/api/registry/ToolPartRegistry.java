package com.lvdriver.tconstruct_nirvana.api.registry;

import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.List;
import java.util.Optional;

/**
 * 工具部件注册表 API（附属 mod 扩展入口）。
 *
 * <p>允许附属 mod 注册自定义部件类型，使其可用于工具组装、模具形状
 * （{@code pattern}/{@code cast} 的形状即部件注册名）与 {@code tool_parts} 查询。</p>
 *
 * <p><b>稳定 API 承诺</b>：本接口一经发布不再变更方法签名；新增能力只以 default
 * 方法或新接口形式追加。获取实现：{@code TConstructNirvanaAPI.toolParts()}。</p>
 *
 * <p>两种注册方式：<ul>
 * <li>{@link #registerPart(String, int, String...)}：委托本 mod 物品注册表注册
 * （部件物品注册名 = shapeId，须在物品注册事件前调用，如附属 Mod 构造器）；</li>
 * <li>{@link #registerPart(ResourceLocation, ToolPart)}：登记附属自行注册的
 * 部件物品（推荐：物品生命周期完全由附属自己的 DeferredRegister 管理，
 * shapeId 建议与物品注册名一致）。</li>
 * </ul></p>
 */
public interface ToolPartRegistry {

    /** 按形状标识（部件注册名）查询部件，未找到返回空。 */
    Optional<ToolPart> getPart(ResourceLocation shapeId);

    /** 全部已注册部件（含 shard），按注册顺序。 */
    List<DeferredItem<? extends ToolPart>> getAllParts();

    /**
     * 用本 mod 物品注册表注册新部件（部件物品注册名即 shapeId）。
     * 须在物品注册事件（RegisterEvent）之前调用。
     *
     * @param name      部件注册名（shapeId 的 path，如 {@code "my_head"}）
     * @param cost      材料价值（旧版 cost，1 锭 = {@link com.lvdriver.tconstruct_nirvana.material.Material#VALUE_Ingot} 144）
     * @param statTypes 该部件使用的属性类型（见 {@link com.lvdriver.tconstruct_nirvana.material.MaterialTypes}，如 "head"/"handle"/"extra"）
     */
    DeferredItem<ToolPart> registerPart(String name, int cost, String... statTypes);

    /**
     * 登记附属自行注册的部件物品（附属扩展，可随时调用）。
     * 用附属自己的 {@link DeferredRegister} 注册部件后，把返回的
     * {@link DeferredItem} 连同形状标识（建议与物品注册名一致）登记进来；
     * 模具形状即部件注册名，登记后自动可用。
     */
    void registerPart(ResourceLocation shapeId, DeferredItem<? extends ToolPart> part);
}
