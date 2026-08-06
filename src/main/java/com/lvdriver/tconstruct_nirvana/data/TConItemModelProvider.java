package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品模型生成（DataGen）。
 *
 * <p>锭/粒为 generated 模型，贴图沿用旧版 {@code items/materials/ingot_* / nugget_*}。</p>
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
    }
}
