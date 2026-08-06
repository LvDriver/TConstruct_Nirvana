package com.lvdriver.tconstruct_nirvana.modifier;

import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

/**
 * 修饰符注册中枢（1:1 对应旧版 {@code TinkerModifiers#registerModifiers}）。
 *
 * <p>实例化即注册（Modifier 构造自动登记到 {@link Modifiers} 静态注册表）。
 * 应用配方（物品匹配，旧版 RecipeMatch）随 RecipeMatch→ItemTag 会话补全；
 * ModFortify / ModExtraTrait 动态修饰符随后续会话实现。</p>
 */
public final class ModModifiers {

    // 增强类（1:1 旧版构造参数）
    public static final Modifier modHaste = new ModHaste(50);
    public static final Modifier modSharpness = new ModSharpness(50);
    public static final Modifier modDiamond = new ModDiamond();
    public static final Modifier modEmerald = new ModEmerald();
    public static final Modifier modReinforced = new ModReinforced();
    public static final Modifier modKnockback = new ModKnockback();
    public static final Modifier modNecrotic = new ModNecrotic();
    public static final Modifier modFiery = new ModFiery();
    public static final Modifier modBeheading = new ModBeheading();
    public static final Modifier modMendingMoss = new ModMendingMoss();
    public static final Modifier modBlasting = new ModBlasting();
    public static final Modifier modSilktouch = new ModSilktouch();
    public static final Modifier modAutosmelt = new ModAutosmelt();
    public static final Modifier modGlowing = new ModGlowing();
    public static final Modifier modLuck = new ModLuck();
    public static final Modifier modShulking = new ModShulking();
    public static final Modifier modWebbed = new ModWebbed();
    public static final Modifier modSoulbound = new ModSoulbound();
    public static final Modifier modIncognito = new ModIncognito();
    public static final Modifier modCreative = new ModCreative();

    // 生物类型增强（1:1 旧版 ModAntiMonsterType 参数）
    public static final Modifier modBaneOfArthopods = new ModAntiMonsterType("bane_of_arthopods", 0x61ba49, 5, 24, EntityTypeTags.ARTHROPOD);
    public static final Modifier modSmite = new ModAntiMonsterType("smite", 0xe8d500, 5, 24, EntityTypeTags.UNDEAD);

    // 范围/弹射物
    public static final Modifier modHarvestWidth = new ModHarvestSize("harvestwidth", "width", 0xcaf6a2);
    public static final Modifier modHarvestHeight = new ModHarvestSize("harvestheight", "height", 0xcaf6a2);
    public static final Modifier modFins = new ModFins();

    // 额外强化槽（旧版变量名 endearment，identifier extramodifier）
    public static final Modifier modExtraModifier = new ModExtraModifier();

    private ModModifiers() {
    }

    /** 初始化入口（Mod 启动时调用一次，触发全部实例化注册）。 */
    public static void init() {
        // 引用全部静态字段确保构造注册
        Modifier[] all = {
                modHaste, modSharpness, modDiamond, modEmerald, modReinforced, modKnockback,
                modNecrotic, modFiery, modBeheading, modMendingMoss, modBlasting, modSilktouch,
                modAutosmelt, modGlowing, modLuck, modShulking, modWebbed, modSoulbound,
                modIncognito, modCreative, modBaneOfArthopods, modSmite,
                modHarvestWidth, modHarvestHeight, modFins, modExtraModifier
        };
        // 供后续会话使用（配方/ GUI 引用）
        com.lvdriver.tconstruct_nirvana.util.TConUtil.random();
    }
}
