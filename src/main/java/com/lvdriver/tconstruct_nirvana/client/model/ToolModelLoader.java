package com.lvdriver.tconstruct_nirvana.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

/**
 * 工具多层模型 JSON 加载器（loader = {@code tconstruct_nirvana:tool}）。
 *
 * <p>模型 JSON 形如 {@code {"loader": "tconstruct_nirvana:tool", "tool": "pickaxe"}}，
 * 层定义静态存于 {@link ToolModelData}（1:1 旧版 .tcon.json）。</p>
 */
public class ToolModelLoader implements IGeometryLoader<ToolUnbakedGeometry> {

    @Override
    public ToolUnbakedGeometry read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        if (!jsonObject.has("tool")) {
            throw new JsonParseException("Tool model requires a \"tool\" field");
        }
        String tool = jsonObject.get("tool").getAsString();
        if (!ToolModelData.LAYERS.containsKey(tool)) {
            throw new JsonParseException("Unknown tool model: " + tool);
        }
        return new ToolUnbakedGeometry(tool);
    }
}
