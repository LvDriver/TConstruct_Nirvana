package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.block.BlockChannel;
import com.lvdriver.tconstruct_nirvana.block.BlockDrain;
import com.lvdriver.tconstruct_nirvana.block.BlockFaucet;
import com.lvdriver.tconstruct_nirvana.block.BlockSmelteryController;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.block.slime.BlockSlimeVine;
import com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * 方块状态与模型生成（DataGen）。
 *
 * <p>贴图沿用旧版资源（1:1）：下界矿石用 {@code nether_ore_cobalt/ardite}，
 * 钴块 cube_all 用 {@code block_cobalt}，阿迪特块 cube_bottom_top 用
 * {@code block_ardite + block_ardite_top}（旧版 metal.json 布局）。</p>
 */
public class TConBlockStateProvider extends BlockStateProvider {

    public TConBlockStateProvider(PackOutput output, String modid, ExistingFileHelper exFileHelper) {
        super(output, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // 下界矿石（旧版 ore.json：cube_all + nether_ore_*）
        simpleBlockWithItem(ModBlocks.COBALT_ORE.get(),
                models().cubeAll("cobalt_ore", modLoc("block/nether_ore_cobalt")));
        simpleBlockWithItem(ModBlocks.ARDITE_ORE.get(),
                models().cubeAll("ardite_ore", modLoc("block/nether_ore_ardite")));

        // 金属块（旧版 metal.json：cobalt cube_all，ardite cube_bottom_top）
        simpleBlockWithItem(ModBlocks.COBALT_BLOCK.get(),
                models().cubeAll("cobalt_block", modLoc("block/block_cobalt")));
        simpleBlockWithItem(ModBlocks.ARDITE_BLOCK.get(),
                models().cubeBottomTop("ardite_block",
                        modLoc("block/block_ardite"),
                        modLoc("block/block_ardite_top"),
                        modLoc("block/block_ardite_top")));

        // 工具站/锻造厂（会话4.5b GUI；锻造厂沿用钴块贴图占位）
        simpleBlockWithItem(ModBlocks.TOOL_STATION.get(),
                models().cubeAll("tool_station", modLoc("block/tool_station")));
        simpleBlockWithItem(ModBlocks.TOOL_FORGE.get(),
                models().cubeAll("tool_forge", modLoc("block/block_cobalt")));

        // 冶炼炉（会话7）：seared 12 变体 + 玻璃 + 储罐 + 控制器
        for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
            simpleBlockWithItem(variant.block().get(),
                    models().cubeAll("seared_" + variant.name(), modLoc("block/smeltery/seared_" + variant.name())));
        }
        simpleBlockWithItem(ModBlocks.SEARED_GLASS.get(),
                models().cubeAll("seared_glass", modLoc("block/smeltery/seared_window_side")));
        simpleBlockWithItem(ModBlocks.SEARED_TANK.get(),
                models().cubeBottomTop("seared_tank",
                        modLoc("block/smeltery/seared_tank_side"),
                        modLoc("block/smeltery/seared_tank_top"),
                        modLoc("block/smeltery/seared_tank_top")));

        // 控制器：facing × active 两态（active 用发光贴图，1:1 旧版 smeltery_active/inactive）
        ModelFile inactive = models().cubeAll("smeltery_controller",
                modLoc("block/smeltery/smeltery_inactive"));
        ModelFile active = models().cubeAll("smeltery_controller_active",
                modLoc("block/smeltery/smeltery_active"));
        getVariantBuilder(ModBlocks.SMELTERY_CONTROLLER.get())
                .partialState().with(BlockSmelteryController.FACING, Direction.NORTH)
                .with(BlockSmelteryController.ACTIVE, false).modelForState().modelFile(inactive).rotationY(0).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.SOUTH)
                .with(BlockSmelteryController.ACTIVE, false).modelForState().modelFile(inactive).rotationY(180).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.WEST)
                .with(BlockSmelteryController.ACTIVE, false).modelForState().modelFile(inactive).rotationY(270).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.EAST)
                .with(BlockSmelteryController.ACTIVE, false).modelForState().modelFile(inactive).rotationY(90).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.NORTH)
                .with(BlockSmelteryController.ACTIVE, true).modelForState().modelFile(active).rotationY(0).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.SOUTH)
                .with(BlockSmelteryController.ACTIVE, true).modelForState().modelFile(active).rotationY(180).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.WEST)
                .with(BlockSmelteryController.ACTIVE, true).modelForState().modelFile(active).rotationY(270).addModel()
                .partialState().with(BlockSmelteryController.FACING, Direction.EAST)
                .with(BlockSmelteryController.ACTIVE, true).modelForState().modelFile(active).rotationY(90).addModel();
        simpleBlockItem(ModBlocks.SMELTERY_CONTROLLER.get(), inactive);

        // 流体方块：占位模型（无 elements，particle = 类别贴图；实际表面由
        // LiquidBlockRenderer 按 IClientFluidTypeExtensions 的 still/flow 贴图渲染）
        for (ModFluids.FluidEntry entry : ModFluids.FLUIDS_ALL) {
            String name = entry.id().getPath();
            ModelFile model = models().getBuilder("block/fluid/" + name)
                    .texture("particle", entry.stillTexture());
            simpleBlock(entry.block().get(), model);
        }

        // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽/排液口 + seared 楼梯/台阶
        castingSystem();

        // 史莱姆生态（会话10）：泥土/草皮/树叶/高草/树苗/藤蔓/凝结石块
        slimeSystem();
    }

    /** 浇铸系统方块状态与模型（会话8）。 */
    private void castingSystem() {
        // 浇铸台/盆（模型文件复制自旧版 casting_table/casting_basin）
        ModelFile castingTable = models().getExistingFile(modLoc("block/casting_table"));
        ModelFile castingBasin = models().getExistingFile(modLoc("block/casting_basin"));
        getVariantBuilder(ModBlocks.CASTING_TABLE.get())
                .partialState().modelForState().modelFile(castingTable).addModel();
        getVariantBuilder(ModBlocks.CASTING_BASIN.get())
                .partialState().modelForState().modelFile(castingBasin).addModel();
        simpleBlockItem(ModBlocks.CASTING_TABLE.get(), castingTable);
        simpleBlockItem(ModBlocks.CASTING_BASIN.get(), castingBasin);

        // 龙头：5 朝向（UP 用顶部模型，其他 y 旋转；1:1 旧版 faucet.json）
        ModelFile faucet = models().getExistingFile(modLoc("block/faucet"));
        ModelFile faucetTop = models().getExistingFile(modLoc("block/faucet_top"));
        getVariantBuilder(ModBlocks.FAUCET.get())
                .partialState().with(BlockFaucet.FACING, Direction.UP).modelForState().modelFile(faucetTop).addModel()
                .partialState().with(BlockFaucet.FACING, Direction.SOUTH).modelForState().modelFile(faucet).addModel()
                .partialState().with(BlockFaucet.FACING, Direction.NORTH).modelForState().modelFile(faucet).rotationY(180).addModel()
                .partialState().with(BlockFaucet.FACING, Direction.WEST).modelForState().modelFile(faucet).rotationY(90).addModel()
                .partialState().with(BlockFaucet.FACING, Direction.EAST).modelForState().modelFile(faucet).rotationY(270).addModel();
        simpleBlockItem(ModBlocks.FAUCET.get(), faucet);

        // 排液口：cube 模型（前/后贴图）+ 朝向旋转（1:1 旧版 smeltery_io.json）
        ModelFile drain = models().getBuilder("drain")
                .parent(models().getExistingFile(mcLoc("block/cube")))
                .texture("down", modLoc("block/smeltery/drain_front"))
                .texture("up", modLoc("block/smeltery/drain_front"))
                .texture("north", modLoc("block/smeltery/drain_back"))
                .texture("south", modLoc("block/smeltery/drain_front"))
                .texture("west", modLoc("block/smeltery/drain_front"))
                .texture("east", modLoc("block/smeltery/drain_front"))
                .texture("particle", modLoc("block/smeltery/drain_front"));
        getVariantBuilder(ModBlocks.DRAIN.get())
                .partialState().with(BlockDrain.FACING, Direction.SOUTH).modelForState().modelFile(drain).addModel()
                .partialState().with(BlockDrain.FACING, Direction.NORTH).modelForState().modelFile(drain).rotationY(180).addModel()
                .partialState().with(BlockDrain.FACING, Direction.WEST).modelForState().modelFile(drain).rotationY(90).addModel()
                .partialState().with(BlockDrain.FACING, Direction.EAST).modelForState().modelFile(drain).rotationY(270).addModel();
        simpleBlockItem(ModBlocks.DRAIN.get(), drain);

        // 沟槽：multipart（中心 + down 喷口 + 4 侧连接，1:1 旧版 channel.json）
        ModelFile channelCenter = models().getExistingFile(modLoc("block/channel/center"));
        ModelFile channelCenterOut = models().getExistingFile(modLoc("block/channel/center_out"));
        ModelFile channelSideOut = models().getExistingFile(modLoc("block/channel/side_out"));
        MultiPartBlockStateBuilder builder = getMultipartBuilder(ModBlocks.CHANNEL.get());
        builder.part().modelFile(channelCenter).addModel().condition(BlockChannel.DOWN, false).end();
        builder.part().modelFile(channelCenterOut).addModel().condition(BlockChannel.DOWN, true).end();
        builder.part().modelFile(channelSideOut).addModel().condition(BlockChannel.NORTH, true).end();
        builder.part().modelFile(channelSideOut).rotationY(90).addModel().condition(BlockChannel.EAST, true).end();
        builder.part().modelFile(channelSideOut).rotationY(180).addModel().condition(BlockChannel.SOUTH, true).end();
        builder.part().modelFile(channelSideOut).rotationY(270).addModel().condition(BlockChannel.WEST, true).end();
        simpleBlockItem(ModBlocks.CHANNEL.get(), channelCenter);

        // seared 楼梯/台阶（12 + 12，贴图 = 对应 seared 变体；双台阶状态引用对应完整方块模型）
        for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
            stairsBlock(stairs.block().get(), modLoc("block/smeltery/seared_" + stairs.name()));
        }
        for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
            slabBlock(slab.block().get(),
                    modLoc("block/seared_" + slab.name()),
                    modLoc("block/smeltery/seared_" + slab.name()));
        }
    }

    /** 史莱姆生态方块状态与模型（会话10）。 */
    private void slimeSystem() {
        // slime_dirt：4 变体 cube_all（旧版 slimedirt_* 贴图）
        for (SlimeTypes.DirtType type : SlimeTypes.DirtType.values()) {
            ModelFile model = models().cubeAll("slime_dirt_" + type,
                    modLoc("block/slime/slimedirt_" + type));
            ConfiguredModel[] models = ConfiguredModel.builder().modelFile(model).build();
            getVariantBuilder(ModBlocks.SLIME_DIRT.get())
                    .partialState().with(SlimeTypes.DIRT_TYPE, type).setModels(models);
        }
        simpleBlockItem(ModBlocks.SLIME_DIRT.get(), models().getExistingFile(modLoc("block/slime_dirt_green")));

        // slime_grass：type × foliage 12 组合，复用原版 grass_block 模型（overlay tintindex）
        for (SlimeTypes.DirtType type : SlimeTypes.DirtType.values()) {
            for (SlimeTypes.FoliageType foliage : SlimeTypes.FoliageType.values()) {
                ModelFile model = models().withExistingParent("slime_grass_" + type + "_" + foliage,
                                mcLoc("block/grass_block"))
                        .texture("bottom", modLoc("block/slime/slimedirt_" + type))
                        .texture("top", modLoc("block/slime/slimegrass_top"))
                        .texture("side", modLoc("block/slime/slimedirt_" + type))
                        .texture("overlay", modLoc("block/slime/slimegrass_overlay"))
                        .texture("particle", modLoc("block/slime/slimedirt_" + type));
                ConfiguredModel[] models = ConfiguredModel.builder().modelFile(model).build();
                getVariantBuilder(ModBlocks.SLIME_GRASS.get())
                        .partialState().with(SlimeTypes.DIRT_TYPE, type)
                        .with(SlimeTypes.FOLIAGE_TYPE, foliage).setModels(models);
            }
        }
        simpleBlockItem(ModBlocks.SLIME_GRASS.get(), models().getExistingFile(modLoc("block/slime_grass_green_blue")));

        // slime_leaves：3 变体，复用原版 leaves 模型（tintindex，foliage 染色）
        for (SlimeTypes.FoliageType foliage : SlimeTypes.FoliageType.values()) {
            ModelFile model = models().withExistingParent("slime_leaves_" + foliage, mcLoc("block/leaves"))
                    .texture("all", modLoc("block/slime/slimeleaves"));
            ConfiguredModel[] models = ConfiguredModel.builder().modelFile(model).build();
            getVariantBuilder(ModBlocks.SLIME_LEAVES.get())
                    .partialState().with(SlimeTypes.FOLIAGE_TYPE, foliage).setModels(models);
        }
        simpleBlockItem(ModBlocks.SLIME_LEAVES.get(), models().getExistingFile(modLoc("block/slime_leaves_blue")));

        // slime_grass_tall：3 变体 cross（旧版 slimegrass_tall 贴图）
        for (SlimeTypes.FoliageType foliage : SlimeTypes.FoliageType.values()) {
            ModelFile model = models().cross("slime_grass_tall_" + foliage,
                    modLoc("block/slime/slimegrass_tall"));
            ConfiguredModel[] models = ConfiguredModel.builder().modelFile(model).build();
            getVariantBuilder(ModBlocks.SLIME_GRASS_TALL.get())
                    .partialState().with(SlimeTypes.FOLIAGE_TYPE, foliage).setModels(models);
        }
        simpleBlockItem(ModBlocks.SLIME_GRASS_TALL.get(), models().getExistingFile(modLoc("block/slime_grass_tall_blue")));

        // slime_sapling：3 变体 cross（旧版 slimesapling_* 贴图）
        for (SlimeTypes.FoliageType foliage : SlimeTypes.FoliageType.values()) {
            ModelFile model = models().cross("slime_sapling_" + foliage,
                    modLoc("block/slime/slimesapling_" + foliage));
            ConfiguredModel[] models = ConfiguredModel.builder().modelFile(model).build();
            getVariantBuilder(ModBlocks.SLIME_SAPLING.get())
                    .partialState().with(SlimeTypes.FOLIAGE_TYPE, foliage).setModels(models);
        }
        simpleBlockItem(ModBlocks.SLIME_SAPLING.get(), models().getExistingFile(modLoc("block/slime_sapling_blue")));

        // slime_vine：6 个方块 multipart（4 方向，1:1 原版 vine 布局；贴图按段）
        vineMultipart(ModBlocks.SLIME_VINE_BLUE.get(), "slime_vine_blue", "slimevine");
        vineMultipart(ModBlocks.SLIME_VINE_BLUE_MID.get(), "slime_vine_blue_mid", "slimevine_mid");
        vineMultipart(ModBlocks.SLIME_VINE_BLUE_END.get(), "slime_vine_blue_end", "slimevine_end");
        vineMultipart(ModBlocks.SLIME_VINE_PURPLE.get(), "slime_vine_purple", "slimevine");
        vineMultipart(ModBlocks.SLIME_VINE_PURPLE_MID.get(), "slime_vine_purple_mid", "slimevine_mid");
        vineMultipart(ModBlocks.SLIME_VINE_PURPLE_END.get(), "slime_vine_purple_end", "slimevine_end");
        simpleBlockItem(ModBlocks.SLIME_VINE_BLUE.get(), models().getExistingFile(mcLoc("block/vine")));

        // slime_congealed：5 变体 cube_all（旧版 slimeblock_* 贴图）
        for (SlimeTypes.SlimeType type : SlimeTypes.SlimeType.values()) {
            ModelFile model = models().cubeAll("slime_congealed_" + type,
                    modLoc("block/slime/slimeblock_" + type));
            ConfiguredModel[] models = ConfiguredModel.builder().modelFile(model).build();
            getVariantBuilder(ModBlocks.SLIME_CONGEALED.get())
                    .partialState().with(SlimeTypes.SLIME_TYPE, type).setModels(models);
        }
        simpleBlockItem(ModBlocks.SLIME_CONGEALED.get(), models().getExistingFile(modLoc("block/slime_congealed_green")));
    }

    /** 藤蔓 multipart：4 方向 when → 原版 vine_1 单面模型 + 段贴图。 */
    private void vineMultipart(Block vine, String modelName, String texture) {
        ModelFile model = models().withExistingParent(modelName, mcLoc("block/vine"))
                .texture("vine", modLoc("block/slime/" + texture));
        MultiPartBlockStateBuilder builder = getMultipartBuilder(vine);
        builder.part().modelFile(model).addModel().condition(net.minecraft.world.level.block.VineBlock.NORTH, true).end();
        builder.part().modelFile(model).rotationY(90).addModel().condition(net.minecraft.world.level.block.VineBlock.EAST, true).end();
        builder.part().modelFile(model).rotationY(180).addModel().condition(net.minecraft.world.level.block.VineBlock.SOUTH, true).end();
        builder.part().modelFile(model).rotationY(270).addModel().condition(net.minecraft.world.level.block.VineBlock.WEST, true).end();
    }
}
