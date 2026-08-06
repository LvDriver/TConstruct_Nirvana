package com.lvdriver.tconstruct_nirvana.api.registry;

import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 流体注册表 API（附属 mod 扩展入口）。
 *
 * <p>允许附属 mod 注册可冶炼/可浇铸的金属流体，并与材料关联
 * （关联后材料可浇铸部件、流体可熔炼对应物品，冶炼炉会话接入）。</p>
 *
 * <p><b>稳定 API 承诺</b>：本接口一经发布不再变更方法签名；新增能力只以 default
 * 方法或新接口形式追加。获取实现：{@code TConstructNirvanaAPI.fluids()}。</p>
 *
 * <p>用法：<ol>
 * <li>调用 {@link #fluids()} / {@link #fluidTypes()} 向本 mod 流体注册表登记
 * {@link Fluid}/{@link FluidType} 条目（条目登记须在注册事件前，如附属 Mod 构造器）；</li>
 * <li>调用 {@link #associateFluid(String, String)} 将材料与流体关联；
 * 或材料已有流体时用 {@link #getFluidId(String)} 查询。</li>
 * </ol>
 * 附属也可完全用自己的 DeferredRegister 注册流体，仅调用关联方法即可。</p>
 */
public interface FluidRegistry {

    /** 本 mod 流体本体注册表（附属可直接 {@code .register(name, supplier)} 登记条目）。 */
    DeferredRegister<Fluid> fluids();

    /** 本 mod 流体类型注册表（属性：密度/粘度/温度等）。 */
    DeferredRegister<FluidType> fluidTypes();

    /** 查询材料的关联流体 ID，未关联返回 null。 */
    String getFluidId(String materialId);

    /**
     * 关联材料与流体（等价于 {@code material.setFluid(fluidId)}）。
     * 材料不存在时忽略并记日志；材料已有关联时覆盖。
     */
    Material associateFluid(String materialId, String fluidId);
}
