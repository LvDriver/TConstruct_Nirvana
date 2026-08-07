package com.lvdriver.tconstruct_nirvana.smeltery;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 冶炼炉液体容器（1:1 移植自 Tinkers' Antique {@code SmelteryTank}）。
 *
 * <p>多液体共存（每个流体一层），容量由结构大小决定
 * （槽数 × 锭价值 × 8）。1.21.1 适配：FluidStack 不可变，修改量用
 * {@code copyWithAmount} 重建；NBT 存取用 {@code FluidStack.CODEC} 等价结构
 * （save/load 自实现，与 DataComponent 无关）。</p>
 */
public class SmelteryTank implements IFluidHandler {

    /** 容器变化回调（通知 BE 同步/保存）。 */
    public interface TankChangeListener {
        void onTankChanged(List<FluidStack> fluids, FluidStack changed);
    }

    protected final TankChangeListener parent;
    protected List<FluidStack> liquids;
    protected int maxCapacity;

    public SmelteryTank(TankChangeListener parent) {
        liquids = new ArrayList<>();
        maxCapacity = 0;
        this.parent = parent;
    }

    public void setCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public List<FluidStack> getFluids() {
        return liquids;
    }

    public void setFluids(List<FluidStack> fluids) {
        this.liquids = fluids;
        parent.onTankChanged(liquids, null);
    }

    /** 总液体量。 */
    public int getFluidAmount() {
        int cap = 0;
        for (FluidStack liquid : liquids) {
            cap += liquid.getAmount();
        }
        return cap;
    }

    /** 总容量（结构尺寸 × 每槽容量）。 */
    public int getCapacity() {
        return maxCapacity;
    }

    /* IFluidHandler：视为单槽（多液体层对外只暴露底部层，1:1 旧版 getFluid） */

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) {
        return true;
    }

    @Override
    public int getTanks() {
        return 1;
    }

    @Override
    public int getTankCapacity(int tank) {
        return maxCapacity;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return liquids.isEmpty() ? FluidStack.EMPTY : liquids.get(0);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return 0;
        }

        // 剩余空间
        int used = getFluidAmount();
        int usable = Math.min(maxCapacity - used, resource.getAmount());
        // 容量可能为负（结构尺寸变化瞬间）
        if (usable <= 0) {
            return 0;
        }
        if (action.simulate()) {
            return usable;
        }

        // 已有同种液体 → 合并
        for (int i = 0; i < liquids.size(); i++) {
            FluidStack liquid = liquids.get(i);
            if (liquid.is(resource.getFluid())) {
                FluidStack merged = liquid.copyWithAmount(liquid.getAmount() + usable);
                liquids.set(i, merged);
                parent.onTankChanged(liquids, merged);
                return usable;
            }
        }

        // 新液体 → 追加
        FluidStack added = resource.copyWithAmount(usable);
        liquids.add(added);
        parent.onTankChanged(liquids, added);
        return usable;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (resource.isEmpty()) {
            return FluidStack.EMPTY;
        }
        for (int i = 0; i < liquids.size(); i++) {
            FluidStack liquid = liquids.get(i);
            if (liquid.is(resource.getFluid())) {
                int drainable = Math.min(resource.getAmount(), liquid.getAmount());
                if (action.execute()) {
                    if (drainable >= liquid.getAmount()) {
                        liquids.remove(i);
                    } else {
                        liquids.set(i, liquid.copyWithAmount(liquid.getAmount() - drainable));
                    }
                    parent.onTankChanged(liquids, liquid);
                }
                return resource.copyWithAmount(drainable);
            }
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (liquids.isEmpty()) {
            return FluidStack.EMPTY;
        }
        return drain(liquids.get(0).copyWithAmount(maxDrain), action);
    }

    /* 保存/加载（1.21.1 FluidStack.save/parseOptional 需要 HolderLookup.Provider） */

    public void writeToNBT(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        ListTag taglist = new ListTag();
        for (FluidStack liquid : liquids) {
            if (!liquid.isEmpty()) {
                taglist.add(liquid.save(registries));
            }
        }
        tag.put("Liquids", taglist);
        tag.putInt("LiquidCapacity", maxCapacity);
    }

    public void readFromNBT(CompoundTag tag, net.minecraft.core.HolderLookup.Provider registries) {
        maxCapacity = tag.getInt("LiquidCapacity");
        if (liquids == null) {
            liquids = new ArrayList<>();
        }
        liquids.clear();
        ListTag taglist = tag.getList("Liquids", Tag.TAG_COMPOUND);
        for (int i = 0; i < taglist.size(); i++) {
            CompoundTag fluidTag = taglist.getCompound(i);
            FluidStack liquid = FluidStack.parseOptional(registries, fluidTag);
            if (!liquid.isEmpty()) {
                liquids.add(liquid);
            }
        }
    }

    /** 将指定下标的液体移到最底层（1:1 旧版 moveFluidToBottom，GUI 点击层用）。 */
    public void moveFluidToBottom(int index) {
        if (index < liquids.size()) {
            FluidStack fluid = liquids.remove(index);
            liquids.add(0, fluid);
            parent.onTankChanged(liquids, fluid);
        }
    }
}
