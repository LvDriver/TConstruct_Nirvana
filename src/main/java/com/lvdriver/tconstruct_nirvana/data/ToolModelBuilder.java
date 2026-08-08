package com.lvdriver.tconstruct_nirvana.data;

import com.google.gson.JsonObject;
import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.CustomLoaderBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 工具多层模型 JSON 生成器（DataGen）。
 *
 * <p>生成 {@code {"loader": "tconstruct_nirvana:tool", "tool": "<name>"}}，
 * 层定义在运行期由 {@code client.model.ToolModelData} 提供（1:1 旧版 .tcon.json）。</p>
 */
public class ToolModelBuilder extends CustomLoaderBuilder<ItemModelBuilder> {

    private String toolName;

    private ToolModelBuilder(ItemModelBuilder parent, ExistingFileHelper existingFileHelper) {
        super(ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "tool"), parent, existingFileHelper, false);
    }

    public static ToolModelBuilder begin(ItemModelBuilder parent, ExistingFileHelper existingFileHelper) {
        return new ToolModelBuilder(parent, existingFileHelper);
    }

    public ToolModelBuilder toolName(String toolName) {
        this.toolName = toolName;
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        json.addProperty("tool", toolName);
        return json;
    }
}
