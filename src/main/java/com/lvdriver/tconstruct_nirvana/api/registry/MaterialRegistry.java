package com.lvdriver.tconstruct_nirvana.api.registry;

import com.lvdriver.tconstruct_nirvana.material.Material;

import java.util.List;

/**
 * 材料注册表 API（附属 mod 扩展入口）。
 *
 * <p>允许附属 mod 注册自定义材料，使其可用于工具部件、模具与冶炼（关联流体后）。
 * 材料是纯静态数据定义（{@link com.lvdriver.tconstruct_nirvana.material.ModMaterials}），
 * 无需 NeoForge Registry，注册即生效（工具组装/部件匹配按 identifier 查询）。</p>
 *
 * <p><b>稳定 API 承诺</b>：本接口一经发布不再变更方法签名；新增能力只以 default
 * 方法或新接口形式追加。获取实现：{@code TConstructNirvanaAPI.materials()}。</p>
 *
 * <p>调用时机：任意阶段均可（Mod 构造器 / FMLCommonSetupEvent 均可）；
 * 属性与特质挂载在注册后随时可调（{@code material.addStats(...)} /
 * {@code material.addTrait(...)}）。</p>
 */
public interface MaterialRegistry {

    /**
     * 登记新材料。identifier 已存在时替换原条目（保持注册顺序，返回新材料），
     * 与旧版 {@code safeAdd} 语义一致。
     */
    Material register(Material material);

    /** 按标识查询材料，未找到返回 null。 */
    Material get(String identifier);

    /** 按标识查询材料，未找到返回 {@link Material#UNKNOWN}（兜底属性可用）。 */
    Material getOrUnknown(String identifier);

    /** 全部已注册材料（含隐藏），只读视图，按注册顺序。 */
    List<Material> getAll();

    /** 便捷工厂：创建普通可见材料（未登记，需再调用 {@link #register}）。 */
    default Material create(String identifier, int color) {
        return new Material(identifier, color);
    }
}
