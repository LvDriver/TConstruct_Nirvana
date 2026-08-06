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
 * 类型 JSON：下界生物群系（#minecraft:is_nether）在 underground_ores 阶段
 * 生成钴/阿迪特矿 placed feature，1:1 还原旧版 {@code NetherOreGenerator}。</p>
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

        JsonObject ores = new JsonObject();
        ores.addProperty("type", "neoforge:add_features");
        ores.addProperty("biomes", "#minecraft:is_nether");
        JsonArray features = new JsonArray();
        features.add(modid + ":ore_cobalt");
        features.add(modid + ":ore_ardite");
        ores.add("features", features);
        ores.addProperty("step", "underground_ores");
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "ores"), ores);

        PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "neoforge/biome_modifier");
        return CompletableFuture.allOf(data.entrySet().stream()
                .map(entry -> DataProvider.saveStable(cache, entry.getValue(), pathProvider.json(entry.getKey())))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Biome Modifiers: " + TConstructNirvana.MODID;
    }
}
