package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
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
 * 材料展示会话按需补齐全部条目。流体名 key 为
 * {@code fluid_type.tconstruct_nirvana.<流体注册名>}（1.21.1 FluidType 默认翻译 key）。</p>
 */
public class TConLanguageProvider extends LanguageProvider {

    /** 部件名：注册名 → (英文, 中文)。 */
    private static final Map<String, String[]> PART_NAMES = new LinkedHashMap<>();

    /** 流体名：注册名 → (英文, 中文)。1:1 自旧版 lang（熔融金属/石头/血/史莱姆）。 */
    private static final Map<String, String[]> FLUID_NAMES = new LinkedHashMap<>();

    static {
        FLUID_NAMES.put("molten_iron", new String[]{"Molten Iron", "熔融铁"});
        FLUID_NAMES.put("molten_gold", new String[]{"Molten Gold", "熔融金"});
        FLUID_NAMES.put("molten_pigiron", new String[]{"Molten Pig Iron", "熔融生铁"});
        FLUID_NAMES.put("molten_cobalt", new String[]{"Molten Cobalt", "熔融钴"});
        FLUID_NAMES.put("molten_ardite", new String[]{"Molten Ardite", "熔融阿迪特"});
        FLUID_NAMES.put("molten_manyullyn", new String[]{"Molten Manyullyn", "熔融玛玉灵"});
        FLUID_NAMES.put("molten_knightslime", new String[]{"Molten Knightslime", "熔融骑士史莱姆"});
        FLUID_NAMES.put("molten_alubrass", new String[]{"Molten Alubrass", "熔融铝黄铜"});
        FLUID_NAMES.put("molten_alumite", new String[]{"Molten Alumite", "熔融铝化钢"});
        FLUID_NAMES.put("molten_brass", new String[]{"Molten Brass", "熔融黄铜"});
        FLUID_NAMES.put("molten_copper", new String[]{"Molten Copper", "熔融铜"});
        FLUID_NAMES.put("molten_tin", new String[]{"Molten Tin", "熔融锡"});
        FLUID_NAMES.put("molten_bronze", new String[]{"Molten Bronze", "熔融青铜"});
        FLUID_NAMES.put("molten_zinc", new String[]{"Molten Zinc", "熔融锌"});
        FLUID_NAMES.put("molten_lead", new String[]{"Molten Lead", "熔融铅"});
        FLUID_NAMES.put("molten_nickel", new String[]{"Molten Nickel", "熔融镍"});
        FLUID_NAMES.put("molten_silver", new String[]{"Molten Silver", "熔融银"});
        FLUID_NAMES.put("molten_electrum", new String[]{"Molten Electrum", "熔融琥珀金"});
        FLUID_NAMES.put("molten_steel", new String[]{"Molten Steel", "熔融钢"});
        FLUID_NAMES.put("molten_aluminum", new String[]{"Molten Aluminum", "熔融铝"});
        FLUID_NAMES.put("molten_stone", new String[]{"Molten Stone", "熔融石头"});
        FLUID_NAMES.put("molten_obsidian", new String[]{"Molten Obsidian", "熔融黑曜石"});
        FLUID_NAMES.put("molten_clay", new String[]{"Molten Clay", "熔融粘土"});
        FLUID_NAMES.put("molten_dirt", new String[]{"Molten Dirt", "熔融泥土"});
        FLUID_NAMES.put("blood", new String[]{"Blood", "血"});
        FLUID_NAMES.put("purpleslime", new String[]{"Purple Slime", "紫色史莱姆"});
        FLUID_NAMES.put("molten_emerald", new String[]{"Molten Emerald", "熔融绿宝石"});
        FLUID_NAMES.put("molten_diamond", new String[]{"Molten Diamond", "熔融钻石"});
        FLUID_NAMES.put("molten_glass", new String[]{"Molten Glass", "熔融玻璃"});
        FLUID_NAMES.put("notmilk", new String[]{"Calcium", "钙"});
        FLUID_NAMES.put("venom", new String[]{"Venom", "毒液"});
        FLUID_NAMES.put("milk", new String[]{"Milk", "乳"});
        FLUID_NAMES.put("greenslime", new String[]{"Green Slime", "绿色史莱姆"});
        FLUID_NAMES.put("blueslime", new String[]{"Blue Slime", "蓝色史莱姆"});
    }

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
        PART_NAMES.put("bolt_core", new String[]{"Bolt Core", "弩芯"});
        PART_NAMES.put("sharpening_kit", new String[]{"Sharpening Kit", "磨刀石"});
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
        addBlock(ModBlocks.TOOL_STATION, chinese ? "工具站" : "Tool Station");
        addBlock(ModBlocks.TOOL_FORGE, chinese ? "锻造厂" : "Tool Forge");

        // 冶炼炉（会话7）：seared 12 变体 + 玻璃/储罐/控制器
        add("block.tconstruct_nirvana.seared_stone", chinese ? "焦黑石" : "Seared Stone");
        add("block.tconstruct_nirvana.seared_cobble", chinese ? "焦黑圆石" : "Seared Cobble");
        add("block.tconstruct_nirvana.seared_paver", chinese ? "焦黑石板" : "Seared Paver");
        add("block.tconstruct_nirvana.seared_brick", chinese ? "焦黑砖" : "Seared Bricks");
        add("block.tconstruct_nirvana.seared_brick_cracked", chinese ? "裂纹焦黑砖" : "Cracked Seared Bricks");
        add("block.tconstruct_nirvana.seared_brick_fancy", chinese ? "华丽焦黑砖" : "Fancy Seared Bricks");
        add("block.tconstruct_nirvana.seared_brick_square", chinese ? "方形焦黑砖" : "Square Seared Bricks");
        add("block.tconstruct_nirvana.seared_brick_triangle", chinese ? "三角焦黑砖" : "Triangle Seared Bricks");
        add("block.tconstruct_nirvana.seared_brick_small", chinese ? "小焦黑砖" : "Small Seared Bricks");
        add("block.tconstruct_nirvana.seared_road", chinese ? "焦黑路砖" : "Seared Road");
        add("block.tconstruct_nirvana.seared_tile", chinese ? "焦黑瓦" : "Seared Tiles");
        add("block.tconstruct_nirvana.seared_creeper", chinese ? "焦黑苦力怕纹" : "Seared Creeper");
        addBlock(ModBlocks.SEARED_GLASS, chinese ? "焦黑玻璃" : "Seared Glass");
        addBlock(ModBlocks.SEARED_TANK, chinese ? "焦黑储罐" : "Seared Tank");
        addBlock(ModBlocks.SMELTERY_CONTROLLER, chinese ? "冶炼炉控制器" : "Smeltery Controller");
        add("gui.smeltery.name", chinese ? "冶炼炉" : "Smeltery");
        add("gui.tconstruct_nirvana.smeltery.empty", chinese ? "空" : "Empty");

        addItem(ModItems.COBALT_INGOT, chinese ? "钴锭" : "Cobalt Ingot");
        addItem(ModItems.ARDITE_INGOT, chinese ? "阿迪特锭" : "Ardite Ingot");
        addItem(ModItems.COBALT_NUGGET, chinese ? "钴粒" : "Cobalt Nugget");
        addItem(ModItems.ARDITE_NUGGET, chinese ? "阿迪特粒" : "Ardite Nugget");

        // 流体（1:1 旧版 fluid.*.name；key = fluid_type.<modid>.<名>，1.21.1 FluidType 默认翻译 key）
        for (Map.Entry<String, String[]> entry : FLUID_NAMES.entrySet()) {
            add("fluid_type.tconstruct_nirvana." + entry.getKey(),
                    chinese ? entry.getValue()[1] : entry.getValue()[0]);
        }
        // 桶（旧版无独立桶名，直接显示流体名）
        for (ModFluids.FluidEntry fluid : ModFluids.FLUIDS_ALL) {
            addItem(fluid.bucket(), chinese
                    ? FLUID_NAMES.get(fluid.id().getPath())[1] + "桶"
                    : FLUID_NAMES.get(fluid.id().getPath())[0] + " Bucket");
        }

        // 工具部件
        for (Map.Entry<String, String[]> entry : PART_NAMES.entrySet()) {
            add("item.tconstruct_nirvana." + entry.getKey(), chinese ? entry.getValue()[1] : entry.getValue()[0]);
        }

        // 模具（空白 + 带形状；getName 覆写只读 .blank/.name 变体键）
        add("item.tconstruct_nirvana.pattern.blank", chinese ? "空白模具" : "Blank Pattern");
        add("item.tconstruct_nirvana.pattern.name", chinese ? "%s 模具" : "%s Pattern");
        add("item.tconstruct_nirvana.cast.blank", chinese ? "空白浇铸模具" : "Blank Cast");
        add("item.tconstruct_nirvana.cast.name", chinese ? "%s 浇铸模具" : "%s Cast");
        // 铸造形状模具（1:1 旧版 cast_custom 的 5 个 meta 名）
        add("item.tconstruct_nirvana.cast.ingot", chinese ? "铸锭模具" : "Ingot Cast");
        add("item.tconstruct_nirvana.cast.nugget", chinese ? "铸粒模具" : "Nugget Cast");
        add("item.tconstruct_nirvana.cast.gem", chinese ? "铸宝石模具" : "Gem Cast");
        add("item.tconstruct_nirvana.cast.plate", chinese ? "铸板模具" : "Plate Cast");
        add("item.tconstruct_nirvana.cast.gear", chinese ? "铸齿轮模具" : "Gear Cast");
        add("tooltip.pattern.cost", chinese ? "材料消耗：%s" : "Material Cost: %s");

        // JEI 分类（client/jei 包）
        add("jei.tconstruct_nirvana.melting", chinese ? "熔炼" : "Melting");
        add("jei.tconstruct_nirvana.casting", chinese ? "浇铸" : "Casting");
        add("jei.tconstruct_nirvana.part", chinese ? "部件制作" : "Part Crafting");
        add("jei.tconstruct_nirvana.alloy", chinese ? "合金" : "Alloying");
        add("jei.tconstruct_nirvana.melting.temperature", chinese ? "所需炉温：%s" : "Temperature: %s");
        add("jei.tconstruct_nirvana.casting.consumed", chinese ? "消耗模具" : "Consumes cast");
        add("jei.tconstruct_nirvana.casting.basin", chinese ? "铸造盆（无模具）" : "Basin (no cast)");
        add("jei.tconstruct_nirvana.casting.preference", chinese ? "输出取 c: tag 首选物品" : "Output: first item of c: tag");
        add("jei.tconstruct_nirvana.part.material", chinese ? "材料消耗：%s mb（任意 craftable 材料）" : "Material: %s mb (any craftable material)");

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

        // 弹射物信息（1:1 旧版 ProjectileCore.getInformation 的弹药/精准度行）
        add("tooltip.ammo", chinese ? "弹药：%s / %s" : "Ammo: %s / %s");
        add("tooltip.accuracy", chinese ? "精准度：%s%%" : "Accuracy: %s%%");

        // 工具（1:1 旧版工具名）
        add("item.tconstruct_nirvana.pickaxe", chinese ? "镐" : "Pickaxe");
        add("item.tconstruct_nirvana.shovel", chinese ? "铲" : "Shovel");
        add("item.tconstruct_nirvana.hatchet", chinese ? "斧" : "Hatchet");
        add("item.tconstruct_nirvana.mattock", chinese ? "鹤嘴锄" : "Mattock");
        add("item.tconstruct_nirvana.kama", chinese ? "镰刀" : "Kama");
        add("item.tconstruct_nirvana.hammer", chinese ? "锤" : "Hammer");
        add("item.tconstruct_nirvana.excavator", chinese ? "挖掘机" : "Excavator");
        add("item.tconstruct_nirvana.lumberaxe", chinese ? "伐木斧" : "Lumber Axe");
        add("item.tconstruct_nirvana.scythe", chinese ? "大镰" : "Scythe");
        add("item.tconstruct_nirvana.broadsword", chinese ? "宽剑" : "Broadsword");
        add("item.tconstruct_nirvana.longsword", chinese ? "长剑" : "Longsword");
        add("item.tconstruct_nirvana.rapier", chinese ? "细剑" : "Rapier");
        add("item.tconstruct_nirvana.frypan", chinese ? "平底锅" : "Fry Pan");
        add("item.tconstruct_nirvana.battlesign", chinese ? "战牌" : "Battle Sign");
        add("item.tconstruct_nirvana.cleaver", chinese ? "斩首刀" : "Cleaver");
        add("item.tconstruct_nirvana.shortbow", chinese ? "短弓" : "Shortbow");
        add("item.tconstruct_nirvana.longbow", chinese ? "长弓" : "Longbow");
        add("item.tconstruct_nirvana.crossbow", chinese ? "弩" : "Crossbow");
        add("item.tconstruct_nirvana.arrow", chinese ? "箭" : "Arrow");
        add("item.tconstruct_nirvana.bolt", chinese ? "弩矢" : "Bolt");
        add("item.tconstruct_nirvana.shuriken", chinese ? "手里剑" : "Shuriken");
        add("tooltip.tool.broken", chinese ? "已损坏" : "Broken");

        // 工具属性显示（tooltip）
        add("stat.durability", chinese ? "耐久：%s" : "Durability: %s");
        add("stat.mininglevel", chinese ? "采掘等级：%s" : "Mining Level: %s");
        add("stat.miningspeed", chinese ? "采掘速度：%s" : "Mining Speed: %s");
        add("stat.attack", chinese ? "攻击力：%s" : "Attack: %s");
        add("stat.free_modifiers", chinese ? "强化槽：%s" : "Modifiers: %s");

        // 修饰符（1:1 旧版 identifier）
        addModifier("haste", chinese ? "急速" : "Haste");
        addModifier("sharpness", chinese ? "锋利" : "Sharpness");
        addModifier("diamond", chinese ? "钻石" : "Diamond");
        addModifier("emerald", chinese ? "绿宝石" : "Emerald");
        addModifier("reinforced", chinese ? "加固" : "Reinforced");
        addModifier("knockback", chinese ? "击退" : "Knockback");
        addModifier("necrotic", chinese ? "凋灵之骨" : "Necrotic");
        addModifier("fiery", chinese ? "烈焰" : "Fiery");
        addModifier("beheading", chinese ? "斩首" : "Beheading");
        addModifier("mending_moss", chinese ? "苔藓修复" : "Mending Moss");
        addModifier("blasting", chinese ? "爆破" : "Blasting");
        addModifier("silktouch", chinese ? "精准采集" : "Silk Touch");
        addModifier("mod_autosmelt", chinese ? "自动冶炼" : "Auto-Smelt");
        addModifier("glowing", chinese ? "发光" : "Glowing");
        addModifier("luck", chinese ? "幸运" : "Luck");
        addModifier("shulking", chinese ? "迷幻" : "Shulking");
        addModifier("webbed", chinese ? "蛛网" : "Webbed");
        addModifier("soulbound", chinese ? "灵魂绑定" : "Soulbound");
        addModifier("incognito", chinese ? "隐匿" : "Incognito");
        addModifier("creative", chinese ? "创造" : "Creative");
        addModifier("bane_of_arthopods", chinese ? "节肢杀手" : "Bane of Arthopods");
        addModifier("smite", chinese ? "亡灵杀手" : "Smite");
        addModifier("harvestwidth", chinese ? "扩展宽" : "Expanded Width");
        addModifier("harvestheight", chinese ? "扩展高" : "Expanded Height");
        addModifier("fins", chinese ? "鱼鳍" : "Fins");
        addModifier("extramodifier", chinese ? "额外强化" : "Extra Modifier");

        // 材料特质（1:1 旧版 identifier，分级 trait 共用基础名）
        addModifier("momentum", chinese ? "势头" : "Momentum");
        addModifier("stonebound", chinese ? "石缚" : "Stonebound");
        addModifier("jagged", chinese ? "锯齿" : "Jagged");
        addModifier("depthdigger", chinese ? "深挖者" : "Depth Digger");
        addModifier("unnatural", chinese ? "超凡" : "Unnatural");
        addModifier("lightweight", chinese ? "轻盈" : "Lightweight");
        addModifier("aquadynamic", chinese ? "流体动力" : "Aquadynamic");
        addModifier("aridiculous", chinese ? "干旱狂热" : "Aridiculous");
        addModifier("crumbling", chinese ? "崩解" : "Crumbling");
        addModifier("coldblooded", chinese ? "冷血" : "Cold-Blooded");
        addModifier("raging", chinese ? "狂暴" : "Raging");
        addModifier("hellish", chinese ? "地狱" : "Hellish");
        addModifier("holy", chinese ? "神圣" : "Holy");
        addModifier("insatiable", chinese ? "贪婪" : "Insatiable");
        addModifier("superheat", chinese ? "过热" : "Superheat");
        addModifier("splintering", chinese ? "裂片" : "Splintering");
        addModifier("fractured", chinese ? "碎裂" : "Fractured");
        addModifier("crude", chinese ? "粗制" : "Crude");
        addModifier("poisonous", chinese ? "剧毒" : "Poisonous");
        addModifier("sharp", chinese ? "锋锐" : "Sharp");
        addModifier("freezing", chinese ? "冻结" : "Freezing");
        addModifier("spiky", chinese ? "尖刺" : "Spiky");
        addModifier("prickly", chinese ? "多刺" : "Prickly");
        addModifier("baconlicious", chinese ? "培根" : "Baconlicious");
        addModifier("slimey_green", chinese ? "粘液（绿）" : "Slimey Green");
        addModifier("slimey_blue", chinese ? "粘液（蓝）" : "Slimey Blue");
        addModifier("enderference", chinese ? "末影干扰" : "Enderference");
        addModifier("shocking", chinese ? "电击" : "Shocking");
        addModifier("tasty", chinese ? "美味" : "Tasty");
        addModifier("duritos", chinese ? "杜瑞托斯" : "Duritos");
        addModifier("dense", chinese ? "致密" : "Dense");
        addModifier("cheap", chinese ? "廉价" : "Cheap");
        addModifier("cheapskate", chinese ? "守财奴" : "Cheapskate");
        addModifier("ecological", chinese ? "生态" : "Ecological");
        addModifier("petramor", chinese ? "石肤" : "Petramor");
        addModifier("heavy", chinese ? "沉重" : "Heavy");
        addModifier("stiff", chinese ? "僵硬" : "Stiff");
        addModifier("writable", chinese ? "可写" : "Writable");
        addModifier("magnetic", chinese ? "磁力" : "Magnetic");
        addModifier("autosmelt", chinese ? "自动冶炼" : "Auto-Smelt");
        addModifier("squeaky", chinese ? "吱吱" : "Squeaky");
        addModifier("established", chinese ? "老练" : "Established");
        addModifier("flammable", chinese ? "易燃" : "Flammable");
        addModifier("alien", chinese ? "异形" : "Alien");
        addModifier("splinters", chinese ? "倒刺" : "Splinters");
        addModifier("breakable", chinese ? "易碎" : "Breakable");
        addModifier("hovering", chinese ? "悬浮" : "Hovering");
        addModifier("endspeed", chinese ? "末影速度" : "Endspeed");
        addModifier("splitting", chinese ? "分裂" : "Splitting");
    }

    /** 修饰符/特质名称条目（modifier.<id>.name）。 */
    private void addModifier(String id, String name) {
        add("modifier." + id + ".name", name);
    }
}
