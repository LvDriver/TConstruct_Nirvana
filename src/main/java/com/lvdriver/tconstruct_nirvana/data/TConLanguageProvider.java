package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

/**
 * 本地化文件生成（DataGen）。
 *
 * <p>当前提供 en_us 与 zh_cn 两种语言；材料名（material.*.name）在后续
 * 材料展示会话按需补齐全部条目。</p>
 */
public class TConLanguageProvider extends LanguageProvider {

    private final boolean chinese;

    public TConLanguageProvider(PackOutput output, String modid, String locale, boolean chinese) {
        super(output, modid, locale);
        this.chinese = chinese;
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.tconstruct_nirvana", chinese ? "匠魂：涅槃" : "Tinkers' Construct: Nirvana");

        addBlock(ModBlocks.COBALT_ORE, chinese ? "钴矿" : "Cobalt Ore");
        addBlock(ModBlocks.ARDITE_ORE, chinese ? "阿迪特矿" : "Ardite Ore");
        addBlock(ModBlocks.COBALT_BLOCK, chinese ? "钴块" : "Block of Cobalt");
        addBlock(ModBlocks.ARDITE_BLOCK, chinese ? "阿迪特块" : "Block of Ardite");

        addItem(ModItems.COBALT_INGOT, chinese ? "钴锭" : "Cobalt Ingot");
        addItem(ModItems.ARDITE_INGOT, chinese ? "阿迪特锭" : "Ardite Ingot");
        addItem(ModItems.COBALT_NUGGET, chinese ? "钴粒" : "Cobalt Nugget");
        addItem(ModItems.ARDITE_NUGGET, chinese ? "阿迪特粒" : "Ardite Nugget");
    }
}
