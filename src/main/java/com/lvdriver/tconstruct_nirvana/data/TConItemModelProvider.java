package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.item.ModItems;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import com.lvdriver.tconstruct_nirvana.item.tool.ModTools;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.loaders.DynamicFluidContainerModelBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 物品模型生成（DataGen）。
 *
 * <p>锭/粒为 generated 模型，贴图沿用旧版 {@code items/materials/ingot_* / nugget_*}；
 * 部件/模具为 generated 模型，贴图沿用旧版对应部件/模具贴图（按注册名重命名）；
 * 工具为自定义多层材质模型（loader = {@code tconstruct_nirvana:tool}，层定义
 * 1:1 提取自旧版 .tcon.json，运行期按部件材料组合渲染）。</p>
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
        basicItem(ModItems.NECROTIC_BONE.get());

        // 模具（空白模具/浇铸模具，形状由 DataComponent 决定，显示同一贴图）
        basicItem(ModPatterns.PATTERN.get());
        basicItem(ModPatterns.CAST.get());

        // 工具部件（含碎块）
        for (var part : ModToolParts.getAllParts()) {
            basicItem(part.get());
        }

        // 工具（多层材质模型：loader=tconstruct_nirvana:tool，层定义见 client.model.ToolModelData）
        for (ModTools.ToolEntry entry : ModTools.entries()) {
            getBuilder(entry.name())
                    .customLoader(ToolModelBuilder::begin)
                    .toolName(entry.name())
                    .end();
        }

        // 流体桶：neoforge:fluid_container 动态模型（桶身 + 流体层，按流体染色）
        for (ModFluids.FluidEntry entry : ModFluids.FLUIDS_ALL) {
            ItemModelBuilder builder = getBuilder(entry.bucket().getId().getPath())
                    .parent(getExistingFile(mcLoc("item/generated")))
                    .texture("layer0", mcLoc("item/bucket"));
            builder.customLoader(DynamicFluidContainerModelBuilder::begin)
                    .fluid(entry.still().get())
                    .end();
        }

        // seared 楼梯/台阶物品模型（parent = 对应方块模型；BlockStateProvider 只生成 blockstate）
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            withExistingParent(stairs.block().getId().getPath(), modLoc("block/seared_stairs_" + stairs.name()));
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            withExistingParent(slab.block().getId().getPath(), modLoc("block/seared_slab_" + slab.name()));
        }

        // 史莱姆生态（会话10）：BlockItem 模型 = 对应方块默认状态模型
        withExistingParent("slime_dirt", modLoc("block/slime_dirt_green"));
        withExistingParent("slime_grass", modLoc("block/slime_grass_green_blue"));
        withExistingParent("slime_leaves", modLoc("block/slime_leaves_blue"));
        withExistingParent("slime_grass_tall", modLoc("block/slime_grass_tall_blue"));
        withExistingParent("slime_sapling", modLoc("block/slime_sapling_blue"));
        withExistingParent("slime_vine_blue", mcLoc("block/vine"));
        withExistingParent("slime_vine_blue_mid", mcLoc("block/vine"));
        withExistingParent("slime_vine_blue_end", mcLoc("block/vine"));
        withExistingParent("slime_vine_purple", mcLoc("block/vine"));
        withExistingParent("slime_vine_purple_mid", mcLoc("block/vine"));
        withExistingParent("slime_vine_purple_end", mcLoc("block/vine"));
        withExistingParent("slime_congealed", modLoc("block/slime_congealed_green"));
    }
}
