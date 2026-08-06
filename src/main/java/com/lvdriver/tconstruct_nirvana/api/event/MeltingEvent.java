package com.lvdriver.tconstruct_nirvana.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 熔化事件（{@link net.neoforged.neoforge.common.NeoForge#EVENT_BUS}，可取消）。
 *
 * <p>冶炼炉熔化物品时触发：附属可修改输出流体（{@link #setFluid}）、温度
 * （{@link #setTemperature}）与产液量（{@link #setAmount}），或取消熔化
 * （{@link #setCanceled(boolean)}，物品保留在输入槽）。</p>
 *
 * <p><b>注意</b>：冶炼炉多方块系统为后期子系统，本事件当前尚未有触发点
 * （API 先行发布，冶炼炉会话接入触发），见 {@link SmelteryEvent}。</p>
 */
public class MeltingEvent extends SmelteryEvent implements ICancellableEvent {

    /** 被熔化的物品（只读）。 */
    private final ItemStack input;

    /** 输出流体（可修改：换流体 = 自定义熔化产物）。 */
    private FluidStack fluid;

    /** 熔化温度（可修改，须 ≥ 炉温才熔化）。 */
    private int temperature;

    /** 产液量（mb，可修改）。 */
    private int amount;

    public MeltingEvent(Level level, BlockPos pos, ItemStack input, FluidStack fluid, int temperature, int amount) {
        super(level, pos);
        this.input = input;
        this.fluid = fluid;
        this.temperature = temperature;
        this.amount = amount;
    }

    /** 被熔化的物品（只读）。 */
    public ItemStack getInput() {
        return input;
    }

    /** 输出流体（可修改）。 */
    public FluidStack getFluid() {
        return fluid;
    }

    /** 修改输出流体（自定义熔化产物）。 */
    public void setFluid(FluidStack fluid) {
        this.fluid = fluid;
    }

    /** 熔化温度（须 ≥ 炉温才熔化）。 */
    public int getTemperature() {
        return temperature;
    }

    /** 修改熔化温度。 */
    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    /** 产液量（mb）。 */
    public int getAmount() {
        return amount;
    }

    /** 修改产液量。 */
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
