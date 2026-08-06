package com.lvdriver.tconstruct_nirvana.util;

/**
 * 通用小工具（1:1 对应旧版 {@code TinkerUtil} 的部分静态方法）。
 */
public final class TConUtil {

    private TConUtil() {
    }

    /** 罗马数字（1:1 旧版 TinkerUtil.getRomanNumeral，等级显示用）。 */
    public static String toRoman(int number) {
        if (number < 1) {
            return "";
        }
        if (number >= 4000) {
            return "MAX";
        }
        StringBuilder sb = new StringBuilder();
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        for (int i = 0; i < values.length && number > 0; i++) {
            while (number >= values[i]) {
                sb.append(symbols[i]);
                number -= values[i];
            }
        }
        return sb.toString();
    }

    /** 本地化文本（1:1 旧版 Util.translate）。 */
    public static String translate(String key, Object... args) {
        return net.minecraft.network.chat.Component.translatable(key, args).getString();
    }

    /** 全局随机源（旧版 Modifier.random）。 */
    public static java.util.Random random() {
        return RANDOM;
    }

    private static final java.util.Random RANDOM = new java.util.Random();
}
