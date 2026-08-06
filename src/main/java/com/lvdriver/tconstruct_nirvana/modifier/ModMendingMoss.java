package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * 苔藓修复（1:1 移植自 Tinkers' Antique {@code ModMendingMoss} 的简化版）。
 * 3 级；吸收 XP（容量 100×3^(level-1)），每 150 tick 耗 1 XP 修复 2+level 耐久。
 * XP 吸收的完整事件链（PlayerXpEvent.PickupXp）在修饰符 GUI 会话接线，
 * 本实现以组件存储 XP 并支持 onUpdate 自动修复。
 */
public class ModMendingMoss extends Modifier {

    public static final String XP_COMPONENT = "mending_moss_xp";
    private static final int REPAIR_INTERVAL = 150;

    public ModMendingMoss() {
        super("mending_moss");
        aspects.add(new ModifierAspect.LevelAspect(identifier, 3));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0x43ab32));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
    }

    /** XP 容量（1:1 旧版 100×3^(level-1)）。 */
    public int getMaxXp(ItemStack tool) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        return level > 0 ? 100 * (int) Math.pow(3, level - 1) : 0;
    }

    /** 存储 XP 到工具（返回实际存入量，1:1 旧版 storeXp）。 */
    public int storeXp(int amount, ItemStack tool) {
        int stored = tool.getOrDefault(ModDataComponents.REPAIR_COUNT, 0);
        int max = getMaxXp(tool);
        int newStored = Math.min(max, stored + amount);
        int delta = newStored - stored;
        tool.set(ModDataComponents.REPAIR_COUNT, newStored);
        return delta;
    }

    /** 每 XP 修复的耐久（1:1 旧版：2+level）。 */
    public int getDurabilityPerXp(ItemStack tool) {
        ModifierData data = ToolHelper.getModifierData(tool, identifier);
        int level = data != null ? data.level() : 0;
        return 2 + level;
    }

    @Override
    public void onUpdate(ItemStack tool, net.minecraft.world.level.Level world, net.minecraft.world.entity.Entity entity,
                         int itemSlot, boolean isSelected) {
        if (world.isClientSide || !(entity instanceof LivingEntity)) {
            return;
        }
        if (!isSelected && !(entity instanceof net.minecraft.world.entity.player.Player p
                && p.getOffhandItem() == tool)) {
            return;
        }
        // 每 150 tick 修复一次
        if (world.getGameTime() % REPAIR_INTERVAL != 0) {
            return;
        }
        if (tool.getDamageValue() > 0) {
            int stored = tool.getOrDefault(ModDataComponents.REPAIR_COUNT, 0);
            if (stored > 0) {
                tool.set(ModDataComponents.REPAIR_COUNT, stored - 1);
                ToolHelper.repairTool(tool, getDurabilityPerXp(tool), (LivingEntity) entity);
            }
        }
    }
}
