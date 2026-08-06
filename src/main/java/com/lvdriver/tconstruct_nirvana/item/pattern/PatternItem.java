package com.lvdriver.tconstruct_nirvana.item.pattern;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 模具（1:1 移植自 Tinkers' Antique {@code Pattern}）。
 *
 * <p>单物品承载两种形态：空白模具（无形状数据）与某部件的模具
 * （{@link ModDataComponents#PATTERN_SHAPE} 存部件注册名，1.21.1 用
 * {@link ResourceLocation}，旧版为 NBT 字符串部件 ID）。</p>
 *
 * <p>形状标识即部件形状注册表（{@link ModToolParts#getPart(ResourceLocation)}）的键，
 * 由此完成"部件 ↔ 模具"关联；同一形状 ID 也用于 {@link CastItem}（浇铸模具）。</p>
 */
public class PatternItem extends Item {

    private static final DecimalFormat DF = new DecimalFormat("#.##");

    public PatternItem(Properties properties) {
        super(properties);
    }

    /* ---------- 形状存取（DataComponent，null = 空白模具） ---------- */

    /** 写入模具形状（部件注册名）；传入 null 表示空白模具。 */
    public static ItemStack setShape(ItemStack stack, ResourceLocation shape) {
        if (shape == null) {
            stack.remove(ModDataComponents.PATTERN_SHAPE);
        } else {
            stack.set(ModDataComponents.PATTERN_SHAPE, shape);
        }
        return stack;
    }

    /** 读取模具形状；返回 null 表示空白模具。 */
    public static ResourceLocation getShape(ItemStack stack) {
        return stack.get(ModDataComponents.PATTERN_SHAPE);
    }

    /** 读取形状对应的部件；空白模具或未知形状返回空。 */
    public static java.util.Optional<ToolPart> getPart(ItemStack stack) {
        ResourceLocation shape = getShape(stack);
        return shape == null ? java.util.Optional.empty() : ModToolParts.getPart(shape);
    }

    /* ---------- 显示 ---------- */

    @Override
    public Component getName(ItemStack stack) {
        String baseKey = getDescriptionId(stack);
        ResourceLocation shape = getShape(stack);
        if (shape == null) {
            return Component.translatable(baseKey + ".blank");
        }
        // 部件形状（旧版 translateFormatted(unloc + ".name", partName)："%s Pattern"）
        java.util.Optional<ToolPart> part = getPart(stack);
        if (part.isPresent()) {
            String partName = part.get().getName(ItemStack.EMPTY).getString();
            return Component.translatable(baseKey + ".name", partName);
        }
        // 铸造形状（旧版 cast_custom 的独立物品名，如 "Ingot Cast"）：item.<modid>.cast.ingot 等
        return Component.translatable(baseKey + "." + shape.getPath());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        // 旧版 tooltip.pattern.cost：显示该模具制作对应部件的材料消耗（锭数）
        ResourceLocation shape = getShape(stack);
        java.util.Optional<ToolPart> part = getPart(stack);
        Integer cost = part.isPresent()
                ? part.get().getCost()
                : shape == null ? null : ModPatterns.getCastShapeCost(shape);
        if (cost != null) {
            float costIngots = cost / (float) Material.VALUE_Ingot;
            tooltip.add(Component.translatable("tooltip.pattern.cost", DF.format(costIngots)));
        }
    }
}
