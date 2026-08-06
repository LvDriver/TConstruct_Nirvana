package com.lvdriver.tconstruct_nirvana.material;

/**
 * 材料属性数据接口。
 *
 * <p>每种属性类型由 {@link MaterialTypes} 中的标识唯一确定，材料通过
 * {@link Material#addStats(IMaterialStats)} 挂载。属性数据为纯数据 record，
 * 仅承载数值；tooltip 显示逻辑在 GUI 会话落地。</p>
 */
public interface IMaterialStats {

    /**
     * 返回属性类型的唯一标识（见 {@link MaterialTypes}）。
     */
    String getIdentifier();
}
