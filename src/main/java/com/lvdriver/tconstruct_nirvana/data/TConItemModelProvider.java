package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品模型生成（DataGen）。
 *
 * <p>锭/粒为 generated 模型，贴图沿用旧版 {@code items/materials/ingot_* / nugget_*}；
 * 部件/模具为 generated 模型，贴图沿用旧版对应部件/模具贴图（按注册名重命名）。</p>
 */
public class TConItemModelProvider extends ItemModelProvider {

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
    }
}
