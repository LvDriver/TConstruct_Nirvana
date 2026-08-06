package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import com.lvdriver.tconstruct_nirvana.item.tool.ModTools;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 物品模型生成（DataGen）。
 *
 * <p>锭/粒为 generated 模型，贴图沿用旧版 {@code items/materials/ingot_* / nugget_*}；
 * 部件/模具为 generated 模型，贴图沿用旧版对应部件/模具贴图（按注册名重命名）；
 * 工具暂用代表部件贴图占位（完整部件组合渲染模型在后续会话实现）。</p>
 */
public class TConItemModelProvider extends ItemModelProvider {

    /** 工具 → 占位贴图（代表部件）映射。 */
    private static final Map<String, String> TOOL_TEXTURES = new LinkedHashMap<>();

    static {
        TOOL_TEXTURES.put("pickaxe", "item/pick_head");
        TOOL_TEXTURES.put("shovel", "item/shovel_head");
        TOOL_TEXTURES.put("hatchet", "item/axe_head");
        TOOL_TEXTURES.put("mattock", "item/axe_head");
        TOOL_TEXTURES.put("kama", "item/kama_head");
        TOOL_TEXTURES.put("hammer", "item/hammer_head");
        TOOL_TEXTURES.put("excavator", "item/excavator_head");
        TOOL_TEXTURES.put("lumberaxe", "item/broad_axe_head");
        TOOL_TEXTURES.put("scythe", "item/scythe_head");
        TOOL_TEXTURES.put("broadsword", "item/sword_blade");
        TOOL_TEXTURES.put("longsword", "item/sword_blade");
        TOOL_TEXTURES.put("rapier", "item/sword_blade");
        TOOL_TEXTURES.put("frypan", "item/pan_head");
        TOOL_TEXTURES.put("battlesign", "item/sign_head");
        TOOL_TEXTURES.put("cleaver", "item/large_sword_blade");
        TOOL_TEXTURES.put("shortbow", "item/bow_limb");
        TOOL_TEXTURES.put("longbow", "item/bow_limb");
        TOOL_TEXTURES.put("crossbow", "item/bow_limb");
        TOOL_TEXTURES.put("arrow", "item/arrow_head");
        TOOL_TEXTURES.put("bolt", "item/arrow_shaft");
        TOOL_TEXTURES.put("shuriken", "item/knife_blade");
    }

    public TConItemModelProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.COBALT_INGOT.get());
        basicItem(ModItems.ARDITE_INGOT.get());
        basicItem(ModItems.COBALT_NUGGET.get());
        basicItem(ModItems.ARDITE_NUGGET.get());

        // 模具（空白模具/浇铸模具，形状由 DataComponent 决定，显示同一贴图）
        basicItem(ModPatterns.PATTERN.get());
        basicItem(ModPatterns.CAST.get());

        // 工具部件（含碎块）
        for (var part : ModToolParts.getAllParts()) {
            basicItem(part.get());
        }

        // 工具（占位贴图 = 代表部件）
        for (ModTools.ToolEntry entry : ModTools.entries()) {
            String texture = TOOL_TEXTURES.get(entry.name());
            if (texture != null) {
                singleTexture(entry.name(), mcLoc("item/generated"), "layer0",
                        ResourceLocation.fromNamespaceAndPath(modid, texture));
            }
        }
    }
}
