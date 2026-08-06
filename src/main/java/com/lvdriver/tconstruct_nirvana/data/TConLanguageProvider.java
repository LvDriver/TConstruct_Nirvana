package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地化文件生成（DataGen）。
 *
 * <p>提供 en_us 与 zh_cn 两种语言；材料名（material.*.name）在后续
 * 材料展示会话按需补齐全部条目。</p>
 */
public class TConLanguageProvider extends LanguageProvider {

    /** 部件名：注册名 → (英文, 中文)。 */
    private static final Map<String, String[]> PART_NAMES = new LinkedHashMap<>();

    static {
        PART_NAMES.put("pick_head", new String[]{"Pickaxe Head", "镐头"});
        PART_NAMES.put("shovel_head", new String[]{"Shovel Head", "铲头"});
        PART_NAMES.put("axe_head", new String[]{"Axe Head", "斧刃"});
        PART_NAMES.put("broad_axe_head", new String[]{"Broad Axe Head", "阔斧刃"});
        PART_NAMES.put("sword_blade", new String[]{"Sword Blade", "剑刃"});
        PART_NAMES.put("large_sword_blade", new String[]{"Large Sword Blade", "巨剑刃"});
        PART_NAMES.put("hammer_head", new String[]{"Hammer Head", "锤头"});
        PART_NAMES.put("excavator_head", new String[]{"Excavator Head", "开掘铲头"});
        PART_NAMES.put("kama_head", new String[]{"Kama Head", "镰刀头"});
        PART_NAMES.put("scythe_head", new String[]{"Scythe Head", "镰刃"});
        PART_NAMES.put("pan_head", new String[]{"Pan Head", "平底锅头"});
        PART_NAMES.put("sign_head", new String[]{"Sign Head", "告示牌头"});
        PART_NAMES.put("tool_rod", new String[]{"Tool Rod", "工具手柄"});
        PART_NAMES.put("tough_tool_rod", new String[]{"Tough Tool Rod", "坚固工具手柄"});
        PART_NAMES.put("binding", new String[]{"Binding", "绑定结"});
        PART_NAMES.put("tough_binding", new String[]{"Tough Binding", "坚固绑定结"});
        PART_NAMES.put("wide_guard", new String[]{"Wide Guard", "宽护手"});
        PART_NAMES.put("hand_guard", new String[]{"Hand Guard", "手护手"});
        PART_NAMES.put("cross_guard", new String[]{"Cross Guard", "十字护手"});
        PART_NAMES.put("large_plate", new String[]{"Large Plate", "大板"});
        PART_NAMES.put("knife_blade", new String[]{"Knife Blade", "小刀刃"});
        PART_NAMES.put("bow_limb", new String[]{"Bow Limb", "弓臂"});
        PART_NAMES.put("bow_string", new String[]{"Bow String", "弓弦"});
        PART_NAMES.put("arrow_head", new String[]{"Arrow Head", "箭镞"});
        PART_NAMES.put("arrow_shaft", new String[]{"Arrow Shaft", "箭杆"});
        PART_NAMES.put("fletching", new String[]{"Fletching", "箭羽"});
        PART_NAMES.put("shard", new String[]{"Shard", "碎块"});
    }

    private final boolean chinese;

    public TConLanguageProvider(PackOutput output, String modid, String locale, boolean chinese) {
        super(output, modid, locale);
        this.chinese = chinese;
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.tconstruct_nirvana", chinese ? "匠魂：涅槃" : "Tinkers' Construct: Nirvana");

        addBlock(ModBlocks.COBALT_ORE, chinese ? "钴矿" : "Cobalt Ore");
        addBlock(ModBlocks.ARDITE_ORE, chinese ? "阿迪特矿" : "Ardite Ore");
        addBlock(ModBlocks.COBALT_BLOCK, chinese ? "钴块" : "Block of Cobalt");
        addBlock(ModBlocks.ARDITE_BLOCK, chinese ? "阿迪特块" : "Block of Ardite");

        addItem(ModItems.COBALT_INGOT, chinese ? "钴锭" : "Cobalt Ingot");
        addItem(ModItems.ARDITE_INGOT, chinese ? "阿迪特锭" : "Ardite Ingot");
        addItem(ModItems.COBALT_NUGGET, chinese ? "钴粒" : "Cobalt Nugget");
        addItem(ModItems.ARDITE_NUGGET, chinese ? "阿迪特粒" : "Ardite Nugget");

        // 工具部件
        for (Map.Entry<String, String[]> entry : PART_NAMES.entrySet()) {
            add("item.tconstruct_nirvana." + entry.getKey(), chinese ? entry.getValue()[1] : entry.getValue()[0]);
        }

        // 模具（空白 + 带形状；getName 覆写只读 .blank/.name 变体键）
        add("item.tconstruct_nirvana.pattern.blank", chinese ? "空白模具" : "Blank Pattern");
        add("item.tconstruct_nirvana.pattern.name", chinese ? "%s 模具" : "%s Pattern");
        add("item.tconstruct_nirvana.cast.blank", chinese ? "空白浇铸模具" : "Blank Cast");
        add("item.tconstruct_nirvana.cast.name", chinese ? "%s 浇铸模具" : "%s Cast");
        add("tooltip.pattern.cost", chinese ? "材料消耗：%s" : "Material Cost: %s");

        // 属性显示（1:1 旧版 stat.*.name）
        add("stat.head.name", chinese ? "头部" : "Head");
        add("stat.head.durability.name", chinese ? "耐久" : "Durability");
        add("stat.head.miningspeed.name", chinese ? "采掘速度" : "Mining Speed");
        add("stat.head.harvestlevel.name", chinese ? "采掘等级" : "Mining Level");
        add("stat.head.attack.name", chinese ? "攻击" : "Attack");
        add("stat.handle.name", chinese ? "手柄" : "Handle");
        add("stat.handle.modifier.name", chinese ? "系数" : "Modifier");
        add("stat.handle.durability.name", chinese ? "耐久" : "Durability");
        add("stat.extra.name", chinese ? "附加部件" : "Extra");
        add("stat.extra.durability.name", chinese ? "耐久" : "Durability");
        add("stat.bow.name", chinese ? "弓身" : "Bow");
        add("stat.bow.drawspeed.name", chinese ? "拉弓速度" : "Drawspeed");
        add("stat.bow.range.name", chinese ? "射程倍率" : "Range Multiplier");
        add("stat.bow.damage.name", chinese ? "附加伤害" : "Bonus Damage");
        add("stat.bowstring.name", chinese ? "弓弦" : "Bowstring");
        add("stat.bowstring.modifier.name", chinese ? "系数" : "Modifier");
        add("stat.shaft.name", chinese ? "箭杆" : "Arrow Shaft");
        add("stat.shaft.modifier.name", chinese ? "系数" : "Modifier");
        add("stat.shaft.ammo.name", chinese ? "额外弹药" : "Bonus Ammo");
        add("stat.fletching.name", chinese ? "箭羽" : "Fletching");
        add("stat.fletching.accuracy.name", chinese ? "精准度" : "Accuracy");
        add("stat.fletching.modifier.name", chinese ? "系数" : "Modifier");

        // 采掘等级名称（1:1 旧版 ui.mininglevel.*）
        add("ui.mininglevel.stone", chinese ? "石" : "Stone");
        add("ui.mininglevel.iron", chinese ? "铁" : "Iron");
        add("ui.mininglevel.diamond", chinese ? "钻石" : "Diamond");
        add("ui.mininglevel.obsidian", chinese ? "黑曜石" : "Obsidian");
        add("ui.mininglevel.cobalt", chinese ? "钴" : "Cobalt");
    }
}
