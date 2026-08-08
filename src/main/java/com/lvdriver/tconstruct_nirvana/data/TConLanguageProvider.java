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

    /** 材料名：id → (英文名, 英文前缀, 中文名, 中文前缀)。1:1 自旧版 en_us.lang（旧版仅 wood/slime/blueslime 有 prefix，其余用材料名）。 */
    private static final Map<String, String[]> MATERIAL_NAMES = new LinkedHashMap<>();

    /** 修饰符/特质描述：id → (英文, 中文)。1:1 自旧版 modifier.*.desc（去除 § 格式码；creative 旧版无 desc）。 */
    private static final Map<String, String[]> MODIFIER_DESCS = new LinkedHashMap<>();

    /** seared 变体中文名（楼梯/台阶名拼接用）。 */
    private static final Map<String, String> SEARED_ZH = new LinkedHashMap<>();

    static {
        SEARED_ZH.put("stone", "焦黑石");
        SEARED_ZH.put("cobble", "焦黑圆石");
        SEARED_ZH.put("paver", "焦黑石板");
        SEARED_ZH.put("brick", "焦黑砖");
        SEARED_ZH.put("brick_cracked", "裂纹焦黑砖");
        SEARED_ZH.put("brick_fancy", "华丽焦黑砖");
        SEARED_ZH.put("brick_square", "方形焦黑砖");
        SEARED_ZH.put("brick_triangle", "三角焦黑砖");
        SEARED_ZH.put("brick_small", "小焦黑砖");
        SEARED_ZH.put("road", "焦黑路砖");
        SEARED_ZH.put("tile", "焦黑瓦");
        SEARED_ZH.put("creeper", "焦黑苦力怕纹");
    }

    /** seared 变体英文名（楼梯/台阶名拼接用）。 */
    private static String searedEnName(String variant) {
        return switch (variant) {
            case "stone" -> "Stone";
            case "cobble" -> "Cobble";
            case "paver" -> "Paver";
            case "brick" -> "Brick";
            case "brick_cracked" -> "Cracked Brick";
            case "brick_fancy" -> "Fancy Brick";
            case "brick_square" -> "Square Brick";
            case "brick_triangle" -> "Triangle Brick";
            case "brick_small" -> "Small Brick";
            case "road" -> "Road";
            case "tile" -> "Tile";
            default -> "Creeper";
        };
    }

    private static String searedZhName(String variant) {
        return SEARED_ZH.getOrDefault(variant, variant);
    }

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

    static {
        // 材料名（1:1 旧版 en_us.lang material.*；prefix 用于工具组合名"钴 镐"）
        // 格式：{en name, en prefix, zh name, zh prefix}
        MATERIAL_NAMES.put("wood", new String[]{"Wood", "Wooden", "木", "木"});
        MATERIAL_NAMES.put("stone", new String[]{"Stone", "Stone", "石头", "石头"});
        MATERIAL_NAMES.put("flint", new String[]{"Flint", "Flint", "燧石", "燧石"});
        MATERIAL_NAMES.put("cactus", new String[]{"Cactus", "Cactus", "仙人掌", "仙人掌"});
        MATERIAL_NAMES.put("bone", new String[]{"Bone", "Bone", "骨头", "骨头"});
        MATERIAL_NAMES.put("obsidian", new String[]{"Obsidian", "Obsidian", "黑曜石", "黑曜石"});
        MATERIAL_NAMES.put("prismarine", new String[]{"Prismarine", "Prismarine", "海晶石", "海晶石"});
        MATERIAL_NAMES.put("endstone", new String[]{"End", "End", "末地石", "末地石"});
        MATERIAL_NAMES.put("paper", new String[]{"Paper", "Paper", "纸", "纸"});
        MATERIAL_NAMES.put("sponge", new String[]{"Sponge", "Sponge", "海绵", "海绵"});
        MATERIAL_NAMES.put("firewood", new String[]{"Firewood", "Firewood", "火木", "火木"});
        MATERIAL_NAMES.put("knightslime", new String[]{"Knightslime", "Knightslime", "骑士史莱姆", "骑士史莱姆"});
        MATERIAL_NAMES.put("slime", new String[]{"Green Slime", "Slime", "绿色史莱姆", "史莱姆"});
        MATERIAL_NAMES.put("blueslime", new String[]{"Blue Slime", "Slime", "蓝色史莱姆", "史莱姆"});
        MATERIAL_NAMES.put("magmaslime", new String[]{"Magma Slime", "Magma Slime", "岩浆史莱姆", "岩浆史莱姆"});
        MATERIAL_NAMES.put("iron", new String[]{"Iron", "Iron", "铁", "铁"});
        MATERIAL_NAMES.put("pigiron", new String[]{"Pig Iron", "Pig Iron", "生铁", "生铁"});
        MATERIAL_NAMES.put("netherrack", new String[]{"Netherrack", "Netherrack", "下界岩", "下界岩"});
        MATERIAL_NAMES.put("ardite", new String[]{"Ardite", "Ardite", "阿迪特", "阿迪特"});
        MATERIAL_NAMES.put("cobalt", new String[]{"Cobalt", "Cobalt", "钴", "钴"});
        MATERIAL_NAMES.put("manyullyn", new String[]{"Manyullyn", "Manyullyn", "玛玉灵", "玛玉灵"});
        MATERIAL_NAMES.put("bloodbone", new String[]{"Bloody Bone", "Bloody Bone", "血骨", "血骨"});
        MATERIAL_NAMES.put("copper", new String[]{"Copper", "Copper", "铜", "铜"});
        MATERIAL_NAMES.put("bronze", new String[]{"Bronze", "Bronze", "青铜", "青铜"});
        MATERIAL_NAMES.put("lead", new String[]{"Lead", "Lead", "铅", "铅"});
        MATERIAL_NAMES.put("silver", new String[]{"Silver", "Silver", "银", "银"});
        MATERIAL_NAMES.put("electrum", new String[]{"Electrum", "Electrum", "琥珀金", "琥珀金"});
        MATERIAL_NAMES.put("steel", new String[]{"Steel", "Steel", "钢", "钢"});
        MATERIAL_NAMES.put("alubrass", new String[]{"Aluminum Brass", "Aluminum Brass", "铝黄铜", "铝黄铜"});
        MATERIAL_NAMES.put("alumite", new String[]{"Alumite", "Alumite", "铝化钢", "铝化钢"});
        MATERIAL_NAMES.put("string", new String[]{"String", "String", "线", "线"});
        MATERIAL_NAMES.put("vine", new String[]{"Vines", "Vines", "藤蔓", "藤蔓"});
        MATERIAL_NAMES.put("slimevine_blue", new String[]{"Blue Slimevine", "Blue Slimevine", "蓝色史莱姆藤蔓", "蓝色史莱姆藤蔓"});
        MATERIAL_NAMES.put("slimevine_purple", new String[]{"Purple Slimevine", "Purple Slimevine", "紫色史莱姆藤蔓", "紫色史莱姆藤蔓"});
        MATERIAL_NAMES.put("blaze", new String[]{"Blazerod", "Blazerod", "烈焰棒", "烈焰棒"});
        MATERIAL_NAMES.put("reed", new String[]{"Reeds", "Reeds", "甘蔗", "甘蔗"});
        MATERIAL_NAMES.put("ice", new String[]{"Ice", "Ice", "冰", "冰"});
        MATERIAL_NAMES.put("endrod", new String[]{"Endrod", "Endrod", "末地棒", "末地棒"});
        MATERIAL_NAMES.put("feather", new String[]{"Feather", "Feather", "羽毛", "羽毛"});
        MATERIAL_NAMES.put("leaf", new String[]{"Leaf", "Leaf", "树叶", "树叶"});
        MATERIAL_NAMES.put("slimeleaf_blue", new String[]{"Blue Slimeleaf", "Blue Slimeleaf", "蓝色史莱姆叶", "蓝色史莱姆叶"});
        MATERIAL_NAMES.put("slimeleaf_orange", new String[]{"Magma Slimeleaf", "Magma Slimeleaf", "橙色史莱姆叶", "橙色史莱姆叶"});
        MATERIAL_NAMES.put("slimeleaf_purple", new String[]{"Purple Slimeleaf", "Purple Slimeleaf", "紫色史莱姆叶", "紫色史莱姆叶"});
        MATERIAL_NAMES.put("unstable", new String[]{"Unstable", "Unstable", "不稳定", "不稳定"});
    }

    static {
        // 修饰符/特质描述（1:1 旧版 modifier.*.desc，去除 § 格式码；creative 旧版无 desc 不生成）
        // 格式：{en, zh}
        MODIFIER_DESCS.put("haste", new String[]{"Weeeeeeee!\nThe redstone impulse when using your tool motivates your tool to move even faster.", "哇——！\n使用工具时的红石脉冲激励工具移动得更快。"});
        MODIFIER_DESCS.put("sharpness", new String[]{"Ouch!\nSo sharp, it hurts to look at it.", "哎哟！\n锋利得让人不敢直视。"});
        MODIFIER_DESCS.put("diamond", new String[]{"Shiny!\nIncreased durability!", "闪闪发光！\n增加耐久！"});
        MODIFIER_DESCS.put("emerald", new String[]{"Fancy!\nIncreases durability depending on base stats!", "华丽！\n按基础属性增加耐久！"});
        MODIFIER_DESCS.put("reinforced", new String[]{"So strong!\nEach level adds a chance to not use durability.", "坚固无比！\n每级增加不消耗耐久的机会。"});
        MODIFIER_DESCS.put("knockback", new String[]{"Homerun!\nTeach things how to fly - the cool way.", "全垒打！\n教它们如何飞翔——以酷炫的方式。"});
        MODIFIER_DESCS.put("necrotic", new String[]{"I feel so Alive!!\nHitting enemies returns health to you depending on damage dealt.", "我感觉充满活力！！\n命中敌人时按造成伤害回复生命。"});
        MODIFIER_DESCS.put("fiery", new String[]{"So hot right now!\nBonus fire damage, sets your enemies on fire.", "现在可太热了！\n附加火焰伤害并点燃敌人。"});
        MODIFIER_DESCS.put("beheading", new String[]{"Off with his head!\nEach level adds a 10% chance to get the enemies head on kill.", "砍下它的头！\n每级增加 10% 击杀掉落头颅的几率。"});
        MODIFIER_DESCS.put("mending_moss", new String[]{"It's alive!\nPicked up XP is stored in the tool. It will slowly use up the XP to regenerate durability.", "它是活的！\n拾取的经验储存在工具中，缓慢消耗以回复耐久。"});
        MODIFIER_DESCS.put("blasting", new String[]{"Ka-Boom!\nYou can break non-effective blocks like normal blocks, but they might get destroyed.", "轰隆！\n可像普通方块一样破坏非有效方块，但可能被炸毁。"});
        MODIFIER_DESCS.put("silktouch", new String[]{"Smooth as silk!\nMined blocks drop themselves instead of the usual items.", "如丝绸般顺滑！\n采掘的方块掉落自身而非常规掉落物。"});
        MODIFIER_DESCS.put("mod_autosmelt", new String[]{"A furnace in tool form!\nHarvested blocks get smelted.", "工具形态的熔炉！\n采掘的方块会被冶炼。"});
        MODIFIER_DESCS.put("glowing", new String[]{"Shine bright\nWhenever it gets too dark your tool sacrifices a part of itself to light up your way.", "闪亮登场\n太暗时工具会牺牲自身一部分来照亮你的路。"});
        MODIFIER_DESCS.put("luck", new String[]{"Shiny!\nYou're getting all the nice things. And a lot of them.", "闪闪发光！\n你会得到所有好东西，而且非常多。"});
        MODIFIER_DESCS.put("shulking", new String[]{"Up up and away!\nHitting foes causes them to float away.", "起飞咯！\n命中敌人使其飘浮起来。"});
        MODIFIER_DESCS.put("webbed", new String[]{"Yuck!\nEntangle your foes in cobwebs, slowing them.", "呕！\n用蛛网缠住敌人，使其减速。"});
        MODIFIER_DESCS.put("soulbound", new String[]{"A merry couple!\nYou love your tool. Not even death can part you.", "天作之合！\n你深爱你的工具，死亡也无法将你们分开。"});
        MODIFIER_DESCS.put("incognito", new String[]{"Washed away!\nHides modifiers for a clean look.", "洗掉了！\n隐藏修饰符，外观更干净。"});
        MODIFIER_DESCS.put("bane_of_arthopods", new String[]{"Anti-Spider!\nDeal massive damage to spiders.", "反蜘蛛！\n对蜘蛛造成巨额伤害。"});
        MODIFIER_DESCS.put("smite", new String[]{"For Justice!\nDeal massive damage to undead.", "为了正义！\n对亡灵造成巨额伤害。"});
        MODIFIER_DESCS.put("harvestwidth", new String[]{"Expand!\nIncreases the width of the area affected by your tool. The effect is tool specific.", "扩展！\n增加工具影响区域的宽度。效果因工具而异。"});
        MODIFIER_DESCS.put("harvestheight", new String[]{"Expand!\nIncreases the height of the area affected by your tool. The effect is tool specific.", "扩展！\n增加工具影响区域的高度。效果因工具而异。"});
        MODIFIER_DESCS.put("fins", new String[]{"Something's fishy...\nAttaching fins to the projectiles makes them travel like normal underwater.", "有点不对劲……\n给弹射物装上鱼鳍，使其在水下如常飞行。"});
        MODIFIER_DESCS.put("extramodifier", new String[]{"Explored the End with passion!\nEndeared with an Ender Dragon head, giving you an extra modifier.", "满怀热情探索末地！\n与末影龙之首结缘，获得一个额外强化槽。"});
        MODIFIER_DESCS.put("momentum", new String[]{"Faster, keep going!\nMining blocks increases your speed, as long as you keep going.", "更快，继续！\n持续挖掘会使速度不断提升。"});
        MODIFIER_DESCS.put("stonebound", new String[]{"Your tool absolutely loves stone!\nThe tool mines faster as it wears out, but does less damage.", "你的工具深爱石头！\n工具越磨损挖得越快，但伤害降低。"});
        MODIFIER_DESCS.put("jagged", new String[]{"Moves like jagged!\nEvery point durability lost increases damage.", "锯齿般移动！\n每损失一点耐久增加伤害。"});
        MODIFIER_DESCS.put("depthdigger", new String[]{"Miner's Friend!\nYour tool mines faster the deeper you mine.", "矿工之友！\n挖得越深，采掘越快。"});
        MODIFIER_DESCS.put("unnatural", new String[]{"Abomination!\nThe tool mines faster the higher its mining level is above the required one.", "异形！\n采掘等级高出所需越多，挖得越快。"});
        MODIFIER_DESCS.put("lightweight", new String[]{"Swift like the wind!\nIncreases the overall speed of your tool when mining and attacking.", "如风般迅捷！\n提升工具的采掘与攻击速度。"});
        MODIFIER_DESCS.put("aquadynamic", new String[]{"It's getting wet in here!\nThe tool is unhindered by water and loves rainy evenings.", "这里越来越湿了！\n工具在水中不受阻碍，还喜欢雨夜。"});
        MODIFIER_DESCS.put("aridiculous", new String[]{"Turn up the heat!\nThe tool works better in hotter environments.", "热起来吧！\n工具在更炎热的环境中表现更好。"});
        MODIFIER_DESCS.put("crumbling", new String[]{"Crumble rumble rumble\nThe tool breaks soft blocks that don't need a tool faster.", "轰隆隆\n工具更快破坏不需要工具的软方块。"});
        MODIFIER_DESCS.put("coldblooded", new String[]{"Savage!\nDeal increased damage to targets at full health.", "残暴！\n对满血目标造成额外伤害。"});
        MODIFIER_DESCS.put("raging", new String[]{"A desperate solution!\nYour tool does more damage the lower your health is.", "破釜沉舟！\n生命越低，工具伤害越高。"});
        MODIFIER_DESCS.put("hellish", new String[]{"From down below!\nDeal bonus damage to non-Nether mobs.", "来自地狱！\n对非下界生物造成额外伤害。"});
        MODIFIER_DESCS.put("holy", new String[]{"Radiant!\nDeal bonus damage to undead enemies.", "圣光闪耀！\n对亡灵敌人造成额外伤害。"});
        MODIFIER_DESCS.put("insatiable", new String[]{"Devour!\nDuring combat you deal more and more damage but also consume more and more durability.", "吞噬！\n战斗中伤害越来越高，耐久消耗也越来越大。"});
        MODIFIER_DESCS.put("superheat", new String[]{"Turn up the heat!\nDeal bonus damage to enemies on fire.", "火上浇油！\n对燃烧的敌人造成额外伤害。"});
        MODIFIER_DESCS.put("splintering", new String[]{"A souvenir for your enemies!\nHit them more to deal more damage.", "给敌人的纪念品！\n连续命中造成更多伤害。"});
        MODIFIER_DESCS.put("fractured", new String[]{"Hurt!\nYour tools damage is increased.", "受伤！\n你的工具伤害提高。"});
        MODIFIER_DESCS.put("crude", new String[]{"Armor is for cowards!\nBonus damage against unarmored targets.", "盔甲是懦夫穿的！\n对无护甲目标造成额外伤害。"});
        MODIFIER_DESCS.put("poisonous", new String[]{"Not exactly lead-free!\nPoisons enemies on hit.", "绝非无铅！\n命中使敌人中毒。"});
        MODIFIER_DESCS.put("sharp", new String[]{"Can even cut words!\nHitting an enemy leaves them bleeding for a short time.", "能斩断言语！\n命中使敌人短暂流血。"});
        MODIFIER_DESCS.put("freezing", new String[]{"Winter is Coming\nSuccessful hits slow your target more and more.", "凛冬将至\n命中会使目标越来越慢。"});
        MODIFIER_DESCS.put("spiky", new String[]{"200% cooler with spikes!\nBlocking and getting hurt deals damage to the attacker.", "带尖刺酷炫 200%！\n格挡与受伤时对攻击者造成伤害。"});
        MODIFIER_DESCS.put("prickly", new String[]{"Ouch!\nNobody is safe from those thorns, they always hurt.", "哎哟！\n没人能逃过那些尖刺，它们总会伤人。"});
        MODIFIER_DESCS.put("baconlicious", new String[]{"BACON!\nHitting things sometimes gives bacon.", "培根！\n命中时偶尔掉落培根。"});
        MODIFIER_DESCS.put("slimey_green", new String[]{"Eww, gooey!\nIt'll wash off.", "呕，黏糊糊！\n会洗掉的。"});
        MODIFIER_DESCS.put("slimey_blue", new String[]{"Eww, gooey!\nIt'll wash off.", "呕，黏糊糊！\n会洗掉的。"});
        MODIFIER_DESCS.put("enderference", new String[]{"Anti-Teleport!\nPrevents Endermen from teleporting around for a short time.", "反传送！\n短时间内阻止末影人传送。"});
        MODIFIER_DESCS.put("shocking", new String[]{"Bzzzzzt!\nRunning around, breaking blocks or hitting things charges your tool. Hitting an enemy discharges it, dealing damage and providing a speed boost. Mining a block discharges it, giving a mining speed boost.", "滋滋滋滋！\n奔跑、破坏方块或命中敌人为工具充能。命中敌人放电造成伤害并提供速度加成；采掘方块放电获得采掘速度加成。"});
        MODIFIER_DESCS.put("tasty", new String[]{"Om Nom Nom\nSmells so good.. You'd rather eat your tool than starve.", "啊呜啊呜\n闻起来太香了……你宁愿吃掉工具也不愿挨饿。"});
        MODIFIER_DESCS.put("duritos", new String[]{"Doesn't taste as good as it sounds.\nYour tool lasts longer ...most of the time.", "味道可没听起来那么好。\n你的工具更耐用……大多数时候。"});
        MODIFIER_DESCS.put("dense", new String[]{"Hard...er.\nYour tool lasts longer when it has less durability.", "更……硬。\n耐久越低时工具越耐用。"});
        MODIFIER_DESCS.put("cheap", new String[]{"More bang for your buck!\nIncreases durability gained when repairing the tool.", "物超所值！\n修复工具时获得更多耐久。"});
        MODIFIER_DESCS.put("cheapskate", new String[]{"Cheeky!\nStone is bad. Your tool has less durability.", "小气鬼！\n石头不好。你的工具耐久降低。"});
        MODIFIER_DESCS.put("ecological", new String[]{"Green Power!\nRenewable resources are so good, they regenerate by themselves!", "绿色能源！\n可再生资源太好了，它们会自我修复！"});
        MODIFIER_DESCS.put("petramor", new String[]{"So much stone, must see it all!\nYour tool loves stone, it literally wants to absorb it (for durability).", "这么多石头，必须全看完！\n你的工具热爱石头，简直想吸收它（来恢复耐久）。"});
        MODIFIER_DESCS.put("heavy", new String[]{"Do you even lift?\nPrevents knockback.", "你练过举重吗？\n免疫击退。"});
        MODIFIER_DESCS.put("stiff", new String[]{"Unmoving!\nBlocking reduces the damage taken even more.", "岿然不动！\n格挡时进一步减少所受伤害。"});
        MODIFIER_DESCS.put("writable", new String[]{"Intellectual!\nMore words. More Modifiers. It's only logical!", "学识渊博！\n更多文字，更多强化槽。完全合乎逻辑！"});
        MODIFIER_DESCS.put("magnetic", new String[]{"How do they work?!\nHitting things attracts nearby things?!", "它们是怎么做到的？！\n命中东西会吸引附近的东西？！"});
        MODIFIER_DESCS.put("autosmelt", new String[]{"A furnace in tool form!\nHarvested blocks get smelted.", "工具形态的熔炉！\n采掘的方块会被冶炼。"});
        MODIFIER_DESCS.put("squeaky", new String[]{"Cute.\nYour tool is so soft and squeaky it gained silktouch, but deals no damage.", "真可爱。\n你的工具柔软吱吱叫，获得了精准采集，但不造成伤害。"});
        MODIFIER_DESCS.put("established", new String[]{"Free bonus XP!\nYou gain additional XP.", "免费额外经验！\n获得额外经验。"});
        MODIFIER_DESCS.put("flammable", new String[]{"Can't have enough fire!\nBlocking blocks fire damage and getting hit sets the attacker on fire.", "火永远不嫌多！\n格挡免疫火焰伤害，被击中时点燃攻击者。"});
        MODIFIER_DESCS.put("alien", new String[]{"The stats feel off...\n..as if they're changing! Maybe time will tell?", "属性感觉不对劲……\n……仿佛在变化！也许时间会证明？"});
        MODIFIER_DESCS.put("splinters", new String[]{"Ouch!\nBe careful not to catch a splinter!", "哎哟！\n小心别扎到刺！"});
        MODIFIER_DESCS.put("breakable", new String[]{"This side up!\nProjectiles have a 50% chance to break on impact.", "此面朝上！\n弹射物命中时有 50% 几率碎裂。"});
        MODIFIER_DESCS.put("hovering", new String[]{"Free like the wind\nProjectiles move slower but don't mind gravity as much.", "如风般自由\n弹射物移动较慢但更不受重力影响。"});
        MODIFIER_DESCS.put("endspeed", new String[]{"Quantum speed!\nProjectiles instantly travel to their destination.", "量子速度！\n弹射物瞬间到达目的地。"});
        MODIFIER_DESCS.put("splitting", new String[]{"Two for one!\nThe sudden acceleration of releasing an arrow might cause it to split into two.", "一箭双雕！\n释放箭矢时的突然加速可能使其一分为二。"});
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

        // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽/排液口 + seared 楼梯/台阶
        addBlock(ModBlocks.CASTING_TABLE, chinese ? "浇铸台" : "Casting Table");
        addBlock(ModBlocks.CASTING_BASIN, chinese ? "浇铸盆" : "Casting Basin");
        addBlock(ModBlocks.FAUCET, chinese ? "龙头" : "Faucet");
        addBlock(ModBlocks.CHANNEL, chinese ? "沟槽" : "Channel");
        addBlock(ModBlocks.DRAIN, chinese ? "排液口" : "Drain");
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            String baseZh = searedZhName(stairs.name());
            add("block.tconstruct_nirvana.seared_stairs_" + stairs.name(),
                    chinese ? baseZh + "楼梯" : "Seared " + searedEnName(stairs.name()) + " Stairs");
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            String baseZh = searedZhName(slab.name());
            add("block.tconstruct_nirvana.seared_slab_" + slab.name(),
                    chinese ? baseZh + "台阶" : "Seared " + searedEnName(slab.name()) + " Slab");
        }
        // 沟槽连接状态消息（1:1 旧版 channel.connected.*）
        add("message.tconstruct_nirvana.channel.connected.out", chinese ? "流体输出" : "Fluid output");
        add("message.tconstruct_nirvana.channel.connected.in", chinese ? "流体输入" : "Fluid input");
        add("message.tconstruct_nirvana.channel.connected.none", chinese ? "已断开" : "Disconnected");
        add("message.tconstruct_nirvana.channel.connected_down.allow", chinese ? "允许向下输出" : "Falling fluid allowed");
        add("message.tconstruct_nirvana.channel.connected_down.disallow", chinese ? "禁止向下输出" : "Falling fluid blocked");

        addItem(ModItems.COBALT_INGOT, chinese ? "钴锭" : "Cobalt Ingot");
        addItem(ModItems.ARDITE_INGOT, chinese ? "阿迪特锭" : "Ardite Ingot");
        addItem(ModItems.COBALT_NUGGET, chinese ? "钴粒" : "Cobalt Nugget");
        addItem(ModItems.ARDITE_NUGGET, chinese ? "阿迪特粒" : "Ardite Nugget");
        // 坏死骨（凋灵骷髅掉落，血骨材料；旧版译名 1:1 "凋零之骨"）
        addItem(ModItems.NECROTIC_BONE, chinese ? "凋零之骨" : "Necrotic Bone");

        // 史莱姆生态（会话10）：主 key 名称（变体名简化合并，1:1 旧版 tile.*.name 前缀）
        addBlock(ModBlocks.SLIME_DIRT, chinese ? "史莱姆泥土" : "Slimy Dirt");
        addBlock(ModBlocks.SLIME_GRASS, chinese ? "史莱姆草皮" : "Slimy Grass");
        addBlock(ModBlocks.SLIME_LEAVES, chinese ? "史莱姆树叶" : "Slimy Leaves");
        addBlock(ModBlocks.SLIME_GRASS_TALL, chinese ? "史莱姆高草" : "Tall Slimy Grass");
        addBlock(ModBlocks.SLIME_SAPLING, chinese ? "史莱姆树苗" : "Slimy Sapling");
        addBlock(ModBlocks.SLIME_VINE_BLUE, chinese ? "蓝色史莱姆藤蔓" : "Blue Slime Vine");
        addBlock(ModBlocks.SLIME_VINE_BLUE_MID, chinese ? "蓝色史莱姆藤蔓" : "Blue Slime Vine");
        addBlock(ModBlocks.SLIME_VINE_BLUE_END, chinese ? "蓝色史莱姆藤蔓" : "Blue Slime Vine");
        addBlock(ModBlocks.SLIME_VINE_PURPLE, chinese ? "紫色史莱姆藤蔓" : "Purple Slime Vine");
        addBlock(ModBlocks.SLIME_VINE_PURPLE_MID, chinese ? "紫色史莱姆藤蔓" : "Purple Slime Vine");
        addBlock(ModBlocks.SLIME_VINE_PURPLE_END, chinese ? "紫色史莱姆藤蔓" : "Purple Slime Vine");
        addBlock(ModBlocks.SLIME_CONGEALED, chinese ? "凝结石块" : "Congealed Slime");

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

        // 材料名与组合名前缀（1:1 旧版 material.*.name/prefix；prefix 缺 key 时工具名显示 raw key）
        for (Map.Entry<String, String[]> entry : MATERIAL_NAMES.entrySet()) {
            String id = entry.getKey();
            String[] names = entry.getValue();
            add("material." + id + ".name", chinese ? names[2] : names[0]);
            add("material." + id + ".prefix", chinese ? names[3] : names[1]);
        }

        // 弹射物实体名（entity.<modid>.<id>，1.21.1 默认翻译 key）
        add("entity.tconstruct_nirvana.arrow", chinese ? "箭" : "Arrow");
        add("entity.tconstruct_nirvana.bolt", chinese ? "弩矢" : "Bolt");
        add("entity.tconstruct_nirvana.shuriken", chinese ? "手里剑" : "Shuriken");

        // 修饰符/特质描述（1:1 旧版 modifier.*.desc；当前无调用者，为未来 GUI/工具提示预留）
        for (Map.Entry<String, String[]> entry : MODIFIER_DESCS.entrySet()) {
            add("modifier." + entry.getKey() + ".desc",
                    chinese ? entry.getValue()[1] : entry.getValue()[0]);
        }
    }

    /** 修饰符/特质名称条目（modifier.<id>.name）。 */
    private void addModifier(String id, String name) {
        add("modifier." + id + ".name", name);
    }
}
