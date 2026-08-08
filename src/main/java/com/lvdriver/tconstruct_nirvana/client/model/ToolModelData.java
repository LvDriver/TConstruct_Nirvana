package com.lvdriver.tconstruct_nirvana.client.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 工具模型层定义与材料渲染后缀（1:1 提取自旧版资源，静态数据）。
 *
 * <p>层定义来自旧版 {@code models/item/tools/*.tcon.json} 的 textures 条目
 * （layerN = 第 N 层贴图，brokenN = 损坏时替换第 N 层）；贴图路径相对
 * {@code textures/item/}。未注册工具的旧版条目（battleaxe）已跳过。</p>
 *
 * <p>材料后缀来自旧版 {@code assets/tconstruct/materials/*.json} 的 suffix 字段：
 * 渲染某层时，若存在 {@code <part>_<suffix>} 贴图则直接使用（原色），
 * 否则用基础贴图 + 材料颜色顶点着色（简化 1:1，未移植运行时贴图生成）。</p>
 */
public final class ToolModelData {

    /** 单层定义（partIndex, texture, brokenTexture?）。 */
    public record Layer(int partIndex, String texture, String broken) {
    }

    /** 全部已注册工具 → 层定义（注册顺序与 {@code ModTools.entries()} 一致）。 */
    public static final Map<String, List<Layer>> LAYERS = new LinkedHashMap<>();

    /** 材料 identifier → 贴图后缀（1:1 旧版 materials JSON suffix；metal_base 无变体贴图时落回纯色）。 */
    public static final Map<String, String> MATERIAL_SUFFIX = new LinkedHashMap<>();

    /** 全部可用后缀（bake 时预取变体贴图用）。 */
    public static final List<String> SUFFIXES = List.of("metal_base", "bone_base", "slime_base", "contrast", "feather");

    static {
        layer("pickaxe", 0, "pickaxe/handle", null, 1, "pickaxe/head", "pickaxe/broken_head", 2, "pickaxe/binding", null);
        layer("shovel", 0, "shovel/handle", null, 1, "shovel/head", "shovel/broken_head", 2, "shovel/binding", null);
        layer("hatchet", 0, "pickaxe/handle", null, 1, "hatchet/head", "hatchet/broken_head", 2, "hatchet/binding", null);
        layer("mattock", 0, "mattock/handle", null, 1, "mattock/head", "mattock/broken_head", 2, "mattock/back", null);
        layer("kama", 0, "pickaxe/handle", null, 1, "kama/head", "kama/broken_head", 2, "kama/binding", null);
        layer("hammer", 0, "hammer/handle", "hammer/broken_handle", 1, "hammer/head", null, 2, "hammer/back", null, 3, "hammer/front", null);
        layer("excavator", 0, "excavator/handle", null, 1, "excavator/head", "excavator/broken_head", 2, "excavator/binding", null, 3, "excavator/grip", null);
        layer("lumberaxe", 0, "lumberaxe/handle", null, 1, "lumberaxe/head", "lumberaxe/broken_head", 2, "lumberaxe/shield", null, 3, "lumberaxe/binding", null);
        layer("scythe", 0, "scythe/handle", null, 1, "scythe/head", "scythe/broken_head", 2, "scythe/binding", null, 3, "scythe/accessory", null);
        layer("broadsword", 0, "broadsword/handle", null, 1, "broadsword/blade", "broadsword/broken_blade", 2, "broadsword/guard", null);
        layer("longsword", 0, "broadsword/handle", null, 1, "longsword/blade", "longsword/broken_blade", 2, "longsword/guard", null);
        layer("rapier", 0, "broadsword/handle", null, 1, "rapier/blade", "rapier/broken_blade", 2, "rapier/guard", null);
        layer("frypan", 0, "broadsword/handle", null, 1, "frypan/head", "frypan/broken_head");
        layer("battlesign", 0, "battlesign/handle", null, 1, "battlesign/head", "battlesign/broken_head");
        layer("cleaver", 0, "broadsword/handle", null, 1, "cleaver/head", "cleaver/broken_head", 2, "cleaver/shield", null, 3, "cleaver/guard", null);
        layer("shortbow", 0, "shortbow/limb_top", null, 1, "shortbow/limb_bottom", null, 2, "shortbow/bowstring", "shortbow/bowstring_broken");
        layer("longbow", 0, "longbow/limb_top", null, 1, "longbow/limb_bottom", null, 2, "longbow/grip", null, 3, "longbow/bowstring", "longbow/bowstring_broken");
        layer("crossbow", 0, "crossbow/body", null, 1, "crossbow/limb", null, 2, "crossbow/binding", null, 3, "crossbow/bowstring", "crossbow/bowstring_broken");
        layer("arrow", 0, "arrow/shaft", "arrow/shaft_broken", 1, "arrow/head", null, 2, "arrow/fletching", null);
        layer("bolt", 0, "bolt/shaft", null, 1, "bolt/head", null, 2, "bolt/fletching", null);
        // shuriken：单材料 4 层贴图（旋转角度），材料越界时兜底复用最后一个
        layer("shuriken", 0, "shuriken/shuriken", null, 1, "shuriken/shuriken2", null, 2, "shuriken/shuriken3", null, 3, "shuriken/shuriken4", null);

        suffix("metal_base", "alubrass", "alumite", "ardite", "bronze", "cobalt", "copper", "electrum", "iron", "lead", "manyullyn", "pigiron", "silver", "steel");
        suffix("bone_base", "bone", "bloodbone");
        suffix("slime_base", "blueslime", "knightslime", "magmaslime", "slime");
        suffix("contrast", "flint", "obsidian");
        suffix("feather", "feather", "leaf");
    }

    private static void layer(String tool, Object... defs) {
        var list = new java.util.ArrayList<Layer>();
        for (int i = 0; i < defs.length; i += 3) {
            list.add(new Layer((Integer) defs[i], (String) defs[i + 1], (String) defs[i + 2]));
        }
        LAYERS.put(tool, List.copyOf(list));
    }

    private static void suffix(String suffix, String... materials) {
        for (String m : materials) {
            MATERIAL_SUFFIX.put(m, suffix);
        }
    }

    private ToolModelData() {
    }
}
