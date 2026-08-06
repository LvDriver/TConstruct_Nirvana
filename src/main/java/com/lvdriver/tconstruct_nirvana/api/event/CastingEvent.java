package com.lvdriver.tconstruct_nirvana.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 浇铸事件（{@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}，可取消）。
 *
 * <p>冶炼炉向模具/铸造台浇铸时触发：附属可修改产出物品（{@link #setOutput}，
 * 如换材料/换产物）或取消浇铸（{@link #setCanceled(boolean)}，流体留在槽内）。</p>
 *
 * <p><b>注意</b>：冶炼炉多方块系统为后期子系统，本事件当前尚未有触发点
 * （API 先行发布，冶炼炉会话接入触发），见 {@link SmelteryEvent}。</p>
 */
public class CastingEvent extends SmelteryEvent implements ICancellableEvent {

    /** 使用的模具（浇铸盆/模具/铸件，只读）。 */
    private final ItemStack cast;

    /** 浇铸流体（只读）。 */
    private final FluidStack fluid;

    /** 产出物品（可修改：自定义浇铸产物）。 */
    private ItemStack output;

    public CastingEvent(Level level, BlockPos pos, ItemStack cast, FluidStack fluid, ItemStack output) {
        super(level, pos);
        this.cast = cast;
        this.fluid = fluid;
        this.output = output;
    }

    /** 使用的模具（只读）。 */
    public ItemStack getCast() {
        return cast;
    }

    /** 浇铸流体（只读）。 */
    public FluidStack getFluid() {
        return fluid;
    }

    /** 产出物品（可修改）。 */
    public ItemStack getOutput() {
        return output;
    }

    /** 修改产出物品（自定义浇铸产物）。 */
    public void setOutput(ItemStack output) {
        this.output = output;
    }
}
