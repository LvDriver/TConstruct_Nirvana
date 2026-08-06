package com.lvdriver.tconstruct_nirvana.trait;

/**
 * Trait 注册中枢（1:1 对应旧版 {@code TinkerTraits} 静态实例）。
 *
 * <p>实例化即注册（Trait 构造自动登记到 {@link Traits} 静态注册表）。
 * identifier 与材料挂载字符串一致（分级 trait 带等级后缀）。</p>
 */
public final class ModTraits {

    // 挖掘类
    public static final Trait momentum = new TraitMomentum();
    public static final Trait stonebound = new TraitStonebound();
    public static final Trait jagged = new TraitJagged();
    public static final Trait depthdigger = new TraitDepthdigger();
    public static final Trait unnatural = new TraitUnnatural();
    public static final Trait lightweight = new TraitLightweight();
    public static final Trait aquadynamic = new TraitAquadynamic();
    public static final Trait aridiculous = new TraitAridiculous();
    public static final Trait crumbling = new TraitCrumbling();

    // 近战伤害类
    public static final Trait coldblooded = new TraitColdblooded();
    public static final TraitLeveled raging1 = new TraitRaging(1);
    public static final TraitLeveled raging2 = new TraitRaging(2);
    public static final Trait hellish = new TraitHellish();
    public static final Trait holy = new TraitHoly();
    public static final Trait insatiable = new TraitInsatiable();
    public static final Trait superheat = new TraitSuperheat();
    public static final Trait splintering = new TraitSplintering();
    public static final Trait fractured = new TraitFractured();
    public static final TraitLeveled crude1 = new TraitCrude(1);
    public static final TraitLeveled crude2 = new TraitCrude(2);

    // 近战效果类
    public static final Trait poisonous = new TraitPoisonous();
    public static final Trait sharp = new TraitSharp();
    public static final Trait freezing = new TraitFreezing();
    public static final Trait spiky = new TraitSpiky();
    public static final Trait prickly = new TraitPrickly();
    public static final Trait baconlicious = new TraitBaconlicious();
    public static final Trait slimeyGreen = new TraitSlimey("green", "green", 0x55ff55);
    public static final Trait slimeyBlue = new TraitSlimey("blue", "blue", 0x74c8c7);
    public static final Trait enderference = new TraitEnderference();
    public static final Trait shocking = new TraitShocking();
    public static final Trait tasty = new TraitTasty();

    // 耐久类
    public static final Trait duritos = new TraitDuritos();
    public static final Trait dense = new TraitDense();
    public static final Trait cheap = new TraitCheap();
    public static final Trait cheapskate = new TraitCheapskate();
    public static final Trait ecological = new TraitEcological();
    public static final Trait petramor = new TraitPetramor();
    public static final Trait heavy = new TraitHeavy();
    public static final Trait stiff = new TraitStiff();

    // 功能类
    public static final TraitLeveled writable1 = new TraitWritable(1);
    public static final TraitLeveled writable2 = new TraitWritable(2);
    public static final TraitLeveled magnetic1 = new TraitMagnetic(1);
    public static final TraitLeveled magnetic2 = new TraitMagnetic(2);
    public static final Trait autosmelt = new TraitAutosmelt();
    public static final Trait squeaky = new TraitSqueaky();
    public static final Trait established = new TraitEstablished();
    public static final Trait flammable = new TraitFlammable();
    public static final Trait alien = new TraitAlien();
    public static final Trait splinters = new TraitSplinters();

    // 弹射物类（投影系统后续会话接线，先注册）
    public static final Trait breakable = new TraitSimple("breakable", 0xffffff);
    public static final Trait hovering = new TraitSimple("hovering", 0xffffff);
    public static final Trait endspeed = new TraitSimple("endspeed", 0xffffff);
    public static final Trait splitting = new TraitSimple("splitting", 0xffffff);

    private ModTraits() {
    }

    /** 初始化入口（Mod 启动时调用一次，触发全部实例化注册）。 */
    public static void init() {
        Trait[] all = {
                momentum, stonebound, jagged, depthdigger, unnatural, lightweight, aquadynamic, aridiculous, crumbling,
                coldblooded, raging1, raging2, hellish, holy, insatiable, superheat, splintering, fractured, crude1, crude2,
                poisonous, sharp, freezing, spiky, prickly, baconlicious, slimeyGreen, slimeyBlue, enderference, shocking, tasty,
                duritos, dense, cheap, cheapskate, ecological, petramor, heavy, stiff,
                writable1, writable2, magnetic1, magnetic2, autosmelt, squeaky, established, flammable, alien, splinters,
                breakable, hovering, endspeed, splitting
        };
        for (Trait trait : all) {
            if (trait == null) {
                throw new IllegalStateException("Null trait in ModTraits registration");
            }
        }
    }

    /** 无效果的简化注册 trait（弹射物等后续会话完善）。 */
    private static class TraitSimple extends Trait {
        TraitSimple(String identifier, int color) {
            super(identifier, color);
        }
    }
}
