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
 * 战利品表与全局 LootModifier 生成（DataGen）。
 *
 * <p>1:1 移植自 Tinkers' Antique {@code ToolEvents.onLootTableLoad}：
 * 凋灵骷髅有 7%（每级抢夺 +5%）概率掉落坏死骨（需玩家击杀）。
 * 1.21.1 用数据驱动 GLM（{@code neoforge:add_table}）替代旧版
 * {@code LootTableLoadEvent} 代码注入：本 mod 生成实体子表
 * {@code tconstruct_nirvana:entities/wither_skeleton}，再由 GLM 在目标表
 * {@code minecraft:entities/wither_skeleton} 结算时并入掉落。</p>
 *
 * <p>说明：旧版并未向地牢/神殿/村庄宝箱注入任何战利品（全库 grep 仅此一处 +
 * 村庄建筑用独立表），故 1:1 还原只有凋灵骷髅掉落。</p>
 */
public class TConLootModifiersProvider implements DataProvider {

    private final PackOutput output;
    private final String modid;

    public TConLootModifiersProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Map<ResourceLocation, JsonElement> data = new LinkedHashMap<>();

        /* ---------- 1. 实体子表：坏死骨（1:1 旧版 LootPool：rolls=1、玩家击杀、
           7% + 每级抢夺 5% 概率） ---------- */
        JsonObject entry = new JsonObject();
        entry.addProperty("type", "minecraft:item");
        entry.addProperty("name", modid + ":necrotic_bone");
        entry.addProperty("weight", 1);

        JsonArray poolConditions = new JsonArray();
        JsonObject killedByPlayer = new JsonObject();
        killedByPlayer.addProperty("condition", "minecraft:killed_by_player");
        poolConditions.add(killedByPlayer);
        JsonObject chance = new JsonObject();
        chance.addProperty("condition", "minecraft:random_chance_with_looting");
        chance.addProperty("chance", 0.07);
        chance.addProperty("looting_multiplier", 0.05);
        poolConditions.add(chance);

        JsonObject pool = new JsonObject();
        pool.addProperty("rolls", 1);
        pool.addProperty("bonus_rolls", 0);
        JsonArray entries = new JsonArray();
        entries.add(entry);
        pool.add("entries", entries);
        pool.add("conditions", poolConditions);

        JsonObject entityTable = new JsonObject();
        entityTable.addProperty("type", "minecraft:entity");
        entityTable.addProperty("random_sequence", modid + ":entities/wither_skeleton");
        JsonArray pools = new JsonArray();
        pools.add(pool);
        entityTable.add("pools", pools);
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "entities/wither_skeleton"), entityTable);

        /* ---------- 2. GLM：目标表 = 凋灵骷髅实体表，并入子表掉落 ---------- */
        JsonObject glm = new JsonObject();
        glm.addProperty("type", "neoforge:add_table");
        JsonArray glmConditions = new JsonArray();
        JsonObject tableId = new JsonObject();
        tableId.addProperty("condition", "neoforge:loot_table_id");
        tableId.addProperty("loot_table_id", "minecraft:entities/wither_skeleton");
        glmConditions.add(tableId);
        glm.add("conditions", glmConditions);
        glm.addProperty("table", modid + ":entities/wither_skeleton");
        data.put(ResourceLocation.fromNamespaceAndPath(modid, "loot_modifiers/wither_skeleton_necrotic_bone"), glm);

        /* ---------- 3. NeoForge 启用列表（GLM 默认禁用，须在此登记） ---------- */
        JsonObject globalList = new JsonObject();
        JsonArray modifierEntries = new JsonArray();
        modifierEntries.add(modid + ":wither_skeleton_necrotic_bone");
        globalList.add("entries", modifierEntries);
        globalList.addProperty("replace", false);
        data.put(ResourceLocation.fromNamespaceAndPath("neoforge", "loot_modifiers/global_loot_modifiers"), globalList);

        // 战利品表走 data/<modid>/loot_tables/，GLM 走 data/<modid>/loot_modifiers/（子目录在 key 中已含）
        PackOutput.PathProvider pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "");
        return CompletableFuture.allOf(data.entrySet().stream()
                .map(e -> DataProvider.saveStable(cache, e.getValue(), pathProvider.json(e.getKey())))
                .toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Loot Modifiers: " + TConstructNirvana.MODID;
    }
}
