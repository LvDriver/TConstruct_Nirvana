package com.lvdriver.tconstruct_nirvana.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

/**
 * 模具注册表 API（附属 mod 扩展入口）。
 *
 * <p>匠魂的模具（{@code pattern} 部件模具 / {@code cast} 浇铸模具）为
 * "单物品多形状"：形状 = 部件注册名（见 {@link com.lvdriver.tconstruct_nirvana.item.pattern.PatternItem}）。
 * 因此附属扩展模具形状的路径是 {@link ToolPartRegistry#registerPart}——
 * 登记新部件后其形状自动可用。本接口提供模具物品 accessor 与形状查询。</p>
 *
 * <p><b>稳定 API 承诺</b>：本接口一经发布不再变更方法签名；新增能力只以 default
 * 方法或新接口形式追加。获取实现：{@code TConstructNirvanaAPI.patterns()}。</p>
 *
 * <p>附属如需自定义模具物品（新材质/特殊形状物品），用附属自己的 DeferredRegister
 * 注册物品即可，无需本注册表。</p>
 */
public interface PatternRegistry {

    /** 空白部件模具物品（须在物品注册完成后调用，运行期安全）。 */
    Item getPattern();

    /** 空白浇铸模具物品（须在物品注册完成后调用，运行期安全）。 */
    Item getCast();

    /** 形状是否已知（已登记的部件形状；附属自定义模具形状可据此校验）。 */
    boolean isKnownShape(ResourceLocation shapeId);
}
