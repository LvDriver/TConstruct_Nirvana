package com.lvdriver.tconstruct_nirvana.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * BiomeModifier 生成（DataGen）。
 *
 * <p>NeoForge 21.1 无 BiomeModifiersBuilder，直接输出 {@code neoforge:add_features}
 * 类型 JSON：
 * <ul>
 *   <li>下界钴/阿迪特矿（underground_ores，1:1 旧版 NetherOreGenerator）</li>
 *   <li>主世界史莱姆浮岛（vegetal_decoration，1/730）与地下矿池（underground_decoration，默认关）</li>
 *   <li>下界岩浆岛（top_layer_modification，1/100）与岩浆池（underground_decoration，默认关）</li>
 * </ul>
 * 每个 modifier 挂 {@code neoforge:conditions}（{@code tconstruct_nirvana:config_enabled}
 * 自定义条件），1:1 还原旧版 Config 的世界生成开关（改 config 后 /reload 生效）。</p>
 */
public class TConBiomeModifiersProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;

    public TConBiomeModifiersProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, JsonElement> data = new LinkedHashMap<>();

        data.put(ResourceLocation.fromNamespaceAndPath(modid, "ores_cobalt"),
                addFeatures("#minecraft:is_nether", "underground_ores",
                        new String[]{modid + ":ore_cobalt"}, configEnabled("generateCobaltOre")));
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "ores_ardite"),
                addFeatures("#minecraft:is_nether", "underground_ores",
                        new String[]{modid + ":ore_ardite"}, configEnabled("generateArditeOre")));

        // 史莱姆岛（旧版 genSlimeIslands 同时控制主世界岛与下界岩浆岛）
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "slime_islands"),
                addFeatures("#minecraft:is_overworld", "vegetal_decoration",
                        new String[]{modid + ":slime_island"}, configEnabled("generateSlimeIslands")));
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "magma_slime_islands"),
                addFeatures("#minecraft:is_nether", "top_layer_modification",
                        new String[]{modid + ":magma_slime_island"}, configEnabled("generateSlimeIslands")));

        // 史莱姆矿池（旧版 genSlimePools，默认关）
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "slime_pools"),
                addFeatures("#minecraft:is_overworld", "underground_decoration",
                        new String[]{modid + ":slime_pool"}, configEnabled("generateSlimePools")));
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "magma_slime_pools"),
                addFeatures("#minecraft:is_nether", "underground_decoration",
                        new String[]{modid + ":magma_slime_pool"}, configEnabled("generateSlimePools")));

        PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "neoforge/biome_modifier");
        return CompletableFuture.allOf(data.entrySet().stream()
                .map(e -> DataProvider.saveStable(cache, e.getValue(), pathProvider.json(e.getKey())))
                .toArray(CompletableFuture[]::new));
    }

    /** 生成 neoforge:add_features modifier JSON（含 neoforge:conditions）。 */
    private static JsonObject addFeatures(String biomes, String step, String[] features, JsonArray conditions) {
        JsonObject json = new JsonObject();
        json.addProperty("type", "neoforge:add_features");
        json.addProperty("biomes", biomes);
        JsonArray featureArray = new JsonArray();
        for (String feature : features) {
            featureArray.add(feature);
        }
        json.add("features", featureArray);
        json.addProperty("step", step);
        json.add("neoforge:conditions", conditions);
        return json;
    }

    /** config 开关条件（tconstruct_nirvana:config_enabled + key）。 */
    private static JsonArray configEnabled(String key) {
        JsonObject condition = new JsonObject();
        condition.addProperty("type", modid() + ":config_enabled");
        condition.addProperty("key", key);
        JsonArray array = new JsonArray();
        array.add(condition);
        return array;
    }

    private static String modid() {
        return TConstructNirvana.MODID;
    }

    @Override
    public String getName() {
        return "Biome Modifiers: " + TConstructNirvana.MODID;
    }
}
