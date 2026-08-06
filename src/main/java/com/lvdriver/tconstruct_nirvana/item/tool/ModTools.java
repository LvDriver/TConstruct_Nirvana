package com.lvdriver.tconstruct_nirvana.item.tool;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Excavator;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Hammer;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Hatchet;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Kama;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.LumberAxe;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Mattock;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Pickaxe;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Scythe;
import com.lvdriver.tconstruct_nirvana.item.tool.harvest.Shovel;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.BattleSign;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.BroadSword;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.Cleaver;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.FryPan;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.LongSword;
import com.lvdriver.tconstruct_nirvana.item.tool.melee.Rapier;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.Arrow;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.Bolt;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.CrossBow;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.LongBow;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.ShortBow;
import com.lvdriver.tconstruct_nirvana.item.tool.ranged.Shuriken;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.Collections;
import java.util.List;

/**
 * 工具注册中枢（1:1 对应旧版 TinkerHarvestTools / TinkerMeleeWeapons / TinkerRangedWeapons 的 registerTools）。
 *
 * <p>全部注册在 {@link ModItems#ITEMS} 统一物品注册表；工具物品构造时引用部件
 * （{@code ModToolParts}），故本类须在部件类加载后加载（主类保证顺序）。</p>
 */
public final class ModTools {

    // 采集工具（harvest，1:1 旧版 TinkerHarvestTools）
    public static final DeferredItem<Pickaxe> PICKAXE = ModItems.ITEMS.register("pickaxe", () -> new Pickaxe());
    public static final DeferredItem<Shovel> SHOVEL = ModItems.ITEMS.register("shovel", () -> new Shovel());
    public static final DeferredItem<Hatchet> HATCHET = ModItems.ITEMS.register("hatchet", () -> new Hatchet());
    public static final DeferredItem<Mattock> MATTOCK = ModItems.ITEMS.register("mattock", () -> new Mattock());
    public static final DeferredItem<Kama> KAMA = ModItems.ITEMS.register("kama", () -> new Kama());
    public static final DeferredItem<Hammer> HAMMER = ModItems.ITEMS.register("hammer", () -> new Hammer());
    public static final DeferredItem<Excavator> EXCAVATOR = ModItems.ITEMS.register("excavator", () -> new Excavator());
    public static final DeferredItem<LumberAxe> LUMBER_AXE = ModItems.ITEMS.register("lumberaxe", () -> new LumberAxe());
    public static final DeferredItem<Scythe> SCYTHE = ModItems.ITEMS.register("scythe", () -> new Scythe());

    // 近战武器（melee，1:1 旧版 TinkerMeleeWeapons）
    public static final DeferredItem<BroadSword> BROAD_SWORD = ModItems.ITEMS.register("broadsword", () -> new BroadSword());
    public static final DeferredItem<LongSword> LONG_SWORD = ModItems.ITEMS.register("longsword", () -> new LongSword());
    public static final DeferredItem<Rapier> RAPIER = ModItems.ITEMS.register("rapier", () -> new Rapier());
    public static final DeferredItem<FryPan> FRY_PAN = ModItems.ITEMS.register("frypan", () -> new FryPan());
    public static final DeferredItem<BattleSign> BATTLE_SIGN = ModItems.ITEMS.register("battlesign", () -> new BattleSign());
    public static final DeferredItem<Cleaver> CLEAVER = ModItems.ITEMS.register("cleaver", () -> new Cleaver());

    // 远程武器（ranged，1:1 旧版 TinkerRangedWeapons）
    public static final DeferredItem<ShortBow> SHORT_BOW = ModItems.ITEMS.register("shortbow", () -> new ShortBow());
    public static final DeferredItem<LongBow> LONG_BOW = ModItems.ITEMS.register("longbow", () -> new LongBow());
    public static final DeferredItem<CrossBow> CROSS_BOW = ModItems.ITEMS.register("crossbow", () -> new CrossBow());
    public static final DeferredItem<Arrow> ARROW = ModItems.ITEMS.register("arrow", () -> new Arrow());
    public static final DeferredItem<Bolt> BOLT = ModItems.ITEMS.register("bolt", () -> new Bolt());
    public static final DeferredItem<Shuriken> SHURIKEN = ModItems.ITEMS.register("shuriken", () -> new Shuriken());

    private static List<TinkerToolItem> toolsCache;

    private ModTools() {
    }

    private static List<DeferredItem<? extends TinkerToolItem>> allDeferred() {
        return List.of(PICKAXE, SHOVEL, HATCHET, MATTOCK, KAMA, HAMMER, EXCAVATOR, LUMBER_AXE, SCYTHE,
                BROAD_SWORD, LONG_SWORD, RAPIER, FRY_PAN, BATTLE_SIGN, CLEAVER,
                SHORT_BOW, LONG_BOW, CROSS_BOW, ARROW, BOLT, SHURIKEN);
    }

    /**
     * 全部工具（供组装配方/事件遍历），只读。
     * 惰性解析：须在物品注册完成后调用（DeferredItem.get() 注册前会抛异常）。
     */
    public static List<TinkerToolItem> getAllTools() {
        if (toolsCache == null) {
            toolsCache = allDeferred().stream()
                    .map(d -> (TinkerToolItem) d.get())
                    .collect(java.util.stream.Collectors.toList());
        }
        return Collections.unmodifiableList(toolsCache);
    }

    /** 工具注册条目（注册名 + 句柄，供 DataGen 使用）。 */
    public record ToolEntry(String name, DeferredItem<? extends TinkerToolItem> item) {
    }

    /** 全部工具条目（注册顺序 = 注册名）。 */
    public static List<ToolEntry> entries() {
        return List.of(
                new ToolEntry("pickaxe", PICKAXE), new ToolEntry("shovel", SHOVEL),
                new ToolEntry("hatchet", HATCHET), new ToolEntry("mattock", MATTOCK),
                new ToolEntry("kama", KAMA), new ToolEntry("hammer", HAMMER),
                new ToolEntry("excavator", EXCAVATOR), new ToolEntry("lumberaxe", LUMBER_AXE),
                new ToolEntry("scythe", SCYTHE),
                new ToolEntry("broadsword", BROAD_SWORD), new ToolEntry("longsword", LONG_SWORD),
                new ToolEntry("rapier", RAPIER), new ToolEntry("frypan", FRY_PAN),
                new ToolEntry("battlesign", BATTLE_SIGN), new ToolEntry("cleaver", CLEAVER),
                new ToolEntry("shortbow", SHORT_BOW), new ToolEntry("longbow", LONG_BOW),
                new ToolEntry("crossbow", CROSS_BOW), new ToolEntry("arrow", ARROW),
                new ToolEntry("bolt", BOLT), new ToolEntry("shuriken", SHURIKEN));
    }
}
