package com.lvdriver.tconstruct_nirvana.item.part;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.material.ArrowShaftMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.BowMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.BowStringMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.FletchingMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.IMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import com.lvdriver.tconstruct_nirvana.util.HarvestLevels;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 工具部件物品（1:1 移植自 Tinkers' Antique {@code ToolPart} + {@code MaterialItem}）。
 *
 * <p>部件 = 形状（Item 注册名）+ 材料（{@link ModDataComponents#PART_MATERIAL} DataComponent）。
 * 每个部件有固定 cost（材料价值，见 {@link Material#VALUE_Ingot} 体系）与所需属性类型
 * （head/handle/extra/bow/...，见 {@link MaterialTypes}）；可用的材料须具备全部所需属性。</p>
 *
 * <p>1.21.1 迁移说明：材料信息用 DataComponent 存储（旧版 NBT {@code Tags.PART_MATERIAL}）；
 * 属性显示 1:1 旧版 {@code ToolPart#getTooltipStatsInfo} 的格式与顺序。</p>
 */
public class ToolPart extends Item {

    /** 属性数值格式化（旧版 {@code Util.df}，去尾零）。 */
    private static final DecimalFormat DF = new DecimalFormat("#.##");

    /** 制作该部件的材料价值（旧版 cost：144=1 锭、72=1 碎块等）。 */
    protected final int cost;

    /** 该部件使用的材料属性类型（见 {@link MaterialTypes}），材料须全部具备。 */
    protected final List<String> statTypes;

    public ToolPart(Properties properties, int cost, String... statTypes) {
        super(properties);
        this.cost = cost;
        this.statTypes = List.of(statTypes);
    }

    /* ---------- 部件属性 ---------- */

    /** 制作该部件的材料价值（144=1 锭，见 {@link Material#VALUE_Ingot}）。 */
    public int getCost() {
        return cost;
    }

    /** 该部件使用的材料属性类型（head/handle/extra/bow/bowstring/projectile/shaft/fletching）。 */
    public List<String> getStatTypes() {
        return statTypes;
    }

    /** 该材料是否可用于此部件（须具备部件所需的全部属性类型）。 */
    public boolean canUseMaterial(Material material) {
        for (String type : statTypes) {
            if (!material.hasStats(type)) {
                return false;
            }
        }
        return true;
    }

    /** 该部件是否使用指定属性类型（tooltip 过滤用）。 */
    public boolean hasUseForStat(String stat) {
        return statTypes.contains(stat);
    }

    /* ---------- 材料存取（DataComponent） ---------- */

    /** 读取部件物品上的材料；无材料数据时返回 {@link Material#UNKNOWN}。 */
    public Material getMaterial(ItemStack stack) {
        String id = stack.getOrDefault(ModDataComponents.PART_MATERIAL, "");
        Material material = ModMaterials.getMaterial(id);
        return material != null ? material : Material.UNKNOWN;
    }

    /** 生成指定材料的部件物品（写入材料 DataComponent）。 */
    public ItemStack getItemstackWithMaterial(Material material) {
        ItemStack stack = new ItemStack(this);
        stack.set(ModDataComponents.PART_MATERIAL, material.identifier);
        return stack;
    }

    /* ---------- 显示 ---------- */

    @Override
    public Component getName(ItemStack stack) {
        Material material = getMaterial(stack);
        Component itemName = super.getName(stack);
        if (material == Material.UNKNOWN) {
            return itemName;
        }
        // 旧版 getLocalizedItemName：material.<id>.prefix 优先，其次 material.<id>.name，最后 identifier 原文
        String prefixKey = "material." + material.identifier + ".prefix";
        String prefix = Component.translatable(prefixKey).getString();
        if (!prefix.equals(prefixKey)) {
            return Component.translatable(prefixKey).append(" ").append(itemName);
        }
        String nameKey = "material." + material.identifier + ".name";
        String name = Component.translatable(nameKey).getString();
        if (!name.equals(nameKey)) {
            return Component.literal(name + " ").append(itemName);
        }
        return Component.literal(material.identifier + " ").append(itemName);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        Material material = getMaterial(stack);
        if (material == Material.UNKNOWN) {
            return; // 无材料数据时无属性可显示（旧版显示 missing 提示，简化跳过）
        }
        // 1:1 旧版 getTooltipStatsInfo：按部件所需属性类型分组显示材料属性
        for (String type : statTypes) {
            List<Component> lines = getStatInfo(material, type);
            if (!lines.isEmpty()) {
                tooltip.add(Component.empty());
                tooltip.add(Component.translatable("stat." + type + ".name")
                        .withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE));
                tooltip.addAll(lines);
            }
        }
    }

    /* ---------- 属性格式化（1:1 旧版各 MaterialStats#getLocalizedInfo） ---------- */

    /**
     * 生成材料在指定属性类型上的显示信息（顺序与格式 1:1 旧版）。
     *
     * @return 信息行；无信息返回空列表
     */
    public static List<Component> getStatInfo(Material material, String statType) {
        IMaterialStats stat = material.getStats(statType);
        if (stat == null) {
            return List.of();
        }
        return switch (statType) {
            case MaterialTypes.HEAD -> {
                HeadMaterialStats head = (HeadMaterialStats) stat;
                List<Component> lines = new java.util.ArrayList<>(4);
                if (head.durability() != 0) {
                    lines.add(line("stat.head.durability.name", String.valueOf(head.durability()), ChatFormatting.WHITE));
                }
                lines.add(Component.translatable("stat.head.harvestlevel.name")
                        .append(": ").append(Component.translatable(harvestLevelName(head.harvestLevel())))
                        .withStyle(ChatFormatting.WHITE));
                if (head.miningspeed() != 0) {
                    lines.add(line("stat.head.miningspeed.name", DF.format(head.miningspeed()), ChatFormatting.AQUA));
                }
                if (head.attack() != 0) {
                    lines.add(line("stat.head.attack.name", DF.format(head.attack()), ChatFormatting.RED));
                }
                yield lines;
            }
            case MaterialTypes.HANDLE -> {
                HandleMaterialStats handle = (HandleMaterialStats) stat;
                List<Component> lines = new java.util.ArrayList<>(2);
                if (handle.modifier() != 0) {
                    lines.add(line("stat.handle.modifier.name", DF.format(handle.modifier()), ChatFormatting.YELLOW));
                }
                if (handle.durability() != 0) {
                    lines.add(line("stat.handle.durability.name", String.valueOf(handle.durability()), ChatFormatting.WHITE));
                }
                yield lines;
            }
            case MaterialTypes.EXTRA -> {
                ExtraMaterialStats extra = (ExtraMaterialStats) stat;
                if (extra.extraDurability() == 0) {
                    yield List.of();
                }
                yield List.of(line("stat.extra.durability.name", String.valueOf(extra.extraDurability()), ChatFormatting.WHITE));
            }
            case MaterialTypes.BOW -> {
                BowMaterialStats bow = (BowMaterialStats) stat;
                List<Component> lines = new java.util.ArrayList<>(3);
                if (bow.drawspeed() != 0 && 1f / bow.drawspeed() != 0) {
                    lines.add(line("stat.bow.drawspeed.name", DF.format(1f / bow.drawspeed()), ChatFormatting.WHITE));
                }
                if (bow.range() != 0) {
                    lines.add(line("stat.bow.range.name", DF.format(bow.range()), ChatFormatting.WHITE));
                }
                if (bow.bonusDamage() != 0) {
                    lines.add(line("stat.bow.damage.name", DF.format(bow.bonusDamage()), ChatFormatting.RED));
                }
                yield lines;
            }
            case MaterialTypes.BOWSTRING -> {
                BowStringMaterialStats bowstring = (BowStringMaterialStats) stat;
                if (bowstring.modifier() == 0) {
                    yield List.of();
                }
                yield List.of(line("stat.bowstring.modifier.name", DF.format(bowstring.modifier()), ChatFormatting.YELLOW));
            }
            case MaterialTypes.SHAFT -> {
                ArrowShaftMaterialStats shaft = (ArrowShaftMaterialStats) stat;
                List<Component> lines = new java.util.ArrayList<>(2);
                if (shaft.modifier() != 0) {
                    lines.add(line("stat.shaft.modifier.name", DF.format(shaft.modifier()), ChatFormatting.YELLOW));
                }
                if (shaft.bonusAmmo() != 0) {
                    lines.add(line("stat.shaft.ammo.name", String.valueOf(shaft.bonusAmmo()), ChatFormatting.WHITE));
                }
                yield lines;
            }
            case MaterialTypes.FLETCHING -> {
                FletchingMaterialStats fletching = (FletchingMaterialStats) stat;
                List<Component> lines = new java.util.ArrayList<>(2);
                if (fletching.accuracy() != 0) {
                    lines.add(line("stat.fletching.accuracy.name", DF.format(fletching.accuracy()), ChatFormatting.WHITE));
                }
                if (fletching.modifier() != 0) {
                    lines.add(line("stat.fletching.modifier.name", DF.format(fletching.modifier()), ChatFormatting.YELLOW));
                }
                yield lines;
            }
            default -> List.of();
        };
    }

    /** 组装 "{名称}: {数值}" 行（1:1 旧版 formatNumber）。 */
    private static Component line(String nameKey, String value, ChatFormatting color) {
        return Component.translatable(nameKey).append(": ").append(value).withStyle(color);
    }

    /** 采掘等级名称 lang key（1:1 旧版 HarvestLevels#getHarvestLevelName）。 */
    public static String harvestLevelName(int level) {
        return switch (level) {
            case HarvestLevels.STONE -> "ui.mininglevel.stone";
            case HarvestLevels.IRON -> "ui.mininglevel.iron";
            case HarvestLevels.DIAMOND -> "ui.mininglevel.diamond";
            case HarvestLevels.OBSIDIAN -> "ui.mininglevel.obsidian";
            case HarvestLevels.COBALT -> "ui.mininglevel.cobalt";
            default -> String.valueOf(level);
        };
    }
}
