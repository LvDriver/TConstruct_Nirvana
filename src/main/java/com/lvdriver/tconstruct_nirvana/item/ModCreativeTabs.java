package com.lvdriver.tconstruct_nirvana.item;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.block.ModBlocks;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import com.lvdriver.tconstruct_nirvana.item.pattern.ModPatterns;
import com.lvdriver.tconstruct_nirvana.item.pattern.PatternItem;
import com.lvdriver.tconstruct_nirvana.item.tool.ModTools;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.ModMaterials;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 创造模式标签页注册中枢（DeferredRegister）。
 *
 * <p>TCon 标签页：矿石/金属 + 模具（空白 + 各部件形状）+ 部件（每个部件一个
 * 代表性材料变体，1:1 旧版 listAllPartMaterials=false 时创造页只显示第一个
 * 可用材料变体）。</p>
 */
public final class ModCreativeTabs {

    /** 创造模式标签页注册表。 */
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TConstructNirvana.MODID);

    /** TCon 主标签页。 */
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TCON_TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tconstruct_nirvana"))
            .icon(() -> new ItemStack(ModItems.COBALT_INGOT.get()))
            .displayItems((parameters, output) -> {
                // 矿石
                output.accept(ModBlocks.COBALT_ORE.get());
                output.accept(ModBlocks.ARDITE_ORE.get());
                // 金属块
                output.accept(ModBlocks.COBALT_BLOCK.get());
                output.accept(ModBlocks.ARDITE_BLOCK.get());
                // 锭
                output.accept(ModItems.COBALT_INGOT.get());
                output.accept(ModItems.ARDITE_INGOT.get());
                // 粒
                output.accept(ModItems.COBALT_NUGGET.get());
                output.accept(ModItems.ARDITE_NUGGET.get());
                // 坏死骨（凋灵骷髅掉落，血骨材料来源）
                output.accept(ModItems.NECROTIC_BONE.get());

                // 工具站/锻造厂（会话4.5b GUI）
                output.accept(ModBlocks.TOOL_STATION.get());
                output.accept(ModBlocks.TOOL_FORGE.get());

                // 冶炼炉（会话7）：seared 变体 + 玻璃 + 储罐 + 控制器
                for (ModBlocks.SearedVariant variant : ModBlocks.SEARED_VARIANTS) {
                    output.accept(variant.block().get());
                }
                output.accept(ModBlocks.SEARED_GLASS.get());
                output.accept(ModBlocks.SEARED_TANK.get());
                output.accept(ModBlocks.SMELTERY_CONTROLLER.get());

                // 浇铸系统（会话8）：浇铸台/盆/龙头/沟槽/排液口 + seared 楼梯/台阶
                output.accept(ModBlocks.CASTING_TABLE.get());
                output.accept(ModBlocks.CASTING_BASIN.get());
                output.accept(ModBlocks.FAUCET.get());
                output.accept(ModBlocks.CHANNEL.get());
                output.accept(ModBlocks.DRAIN.get());
                for (ModBlocks.SearedStairsEntry stairs : ModBlocks.SEARED_STAIRS) {
                    output.accept(stairs.block().get());
                }
                for (ModBlocks.SearedSlabEntry slab : ModBlocks.SEARED_SLABS) {
                    output.accept(slab.block().get());
                }

                // 流体桶（1:1 旧版 FluidRegistry.addBucketForFluid 入创造页行为）
                for (ModFluids.FluidEntry fluid : ModFluids.FLUIDS_ALL) {
                    output.accept(fluid.bucket().get());
                }

                // 模具：空白 + 各部件形状（图案模具）
                output.accept(new ItemStack(ModPatterns.PATTERN.get()));
                for (DeferredItem<? extends ToolPart> part : ModToolParts.getAllParts()) {
                    output.accept(PatternItem.setShape(
                            new ItemStack(ModPatterns.PATTERN.get()),
                            shapeId(part)));
                }
                // 浇铸模具：空白 + 各部件形状
                output.accept(new ItemStack(ModPatterns.CAST.get()));
                for (DeferredItem<? extends ToolPart> part : ModToolParts.getAllParts()) {
                    output.accept(PatternItem.setShape(
                            new ItemStack(ModPatterns.CAST.get()),
                            shapeId(part)));
                }
                // 铸造形状模具（1:1 旧版 cast_custom：铸锭/粒/宝石/板/齿轮）
                for (ResourceLocation shape : ModPatterns.CAST_SHAPES) {
                    output.accept(PatternItem.setShape(
                            new ItemStack(ModPatterns.CAST.get()), shape));
                }

                // 部件：每个部件第一个可用材料变体（1:1 旧版默认创造页行为）
                for (DeferredItem<? extends ToolPart> part : ModToolParts.getAllParts()) {
                    ToolPart toolPart = part.get();
                    for (Material material : ModMaterials.getAllMaterials()) {
                        if (toolPart.canUseMaterial(material)) {
                            output.accept(toolPart.getItemstackWithMaterial(material));
                            break;
                        }
                    }
                }

                // 工具：每工具第一个可用材料变体（1:1 旧版 addDefaultSubItems 默认行为）
                for (com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem tool : ModTools.getAllTools()) {
                    for (Material material : ModMaterials.getAllMaterials()) {
                        ItemStack built = tool.buildItem(
                                java.util.Collections.nCopies(tool.getRequiredComponents().size(), material));
                        if (tool.hasValidMaterials(built)) {
                            output.accept(built);
                            break;
                        }
                    }
                }

                // 史莱姆生态（会话10）：变体由 BLOCK_STATE 组件表达（1:1 旧版 getSubBlocks 全变体）
                for (com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.DirtType type
                        : com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.DirtType.values()) {
                    output.accept(withState(ModBlocks.SLIME_DIRT.get(),
                            ModBlocks.SLIME_DIRT.get().defaultBlockState()
                                    .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.DIRT_TYPE, type)));
                }
                for (com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.DirtType type
                        : com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.DirtType.values()) {
                    for (com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType foliage
                            : com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType.values()) {
                        output.accept(withState(ModBlocks.SLIME_GRASS.get(),
                                ModBlocks.SLIME_GRASS.get().defaultBlockState()
                                        .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.DIRT_TYPE, type)
                                        .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FOLIAGE_TYPE, foliage)));
                    }
                }
                for (com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType foliage
                        : com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FoliageType.values()) {
                    output.accept(withState(ModBlocks.SLIME_LEAVES.get(),
                            ModBlocks.SLIME_LEAVES.get().defaultBlockState()
                                    .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FOLIAGE_TYPE, foliage)));
                    output.accept(withState(ModBlocks.SLIME_GRASS_TALL.get(),
                            ModBlocks.SLIME_GRASS_TALL.get().defaultBlockState()
                                    .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FOLIAGE_TYPE, foliage)));
                    output.accept(withState(ModBlocks.SLIME_SAPLING.get(),
                            ModBlocks.SLIME_SAPLING.get().defaultBlockState()
                                    .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.FOLIAGE_TYPE, foliage)));
                }
                output.accept(ModBlocks.SLIME_VINE_BLUE.get());
                output.accept(ModBlocks.SLIME_VINE_BLUE_MID.get());
                output.accept(ModBlocks.SLIME_VINE_BLUE_END.get());
                output.accept(ModBlocks.SLIME_VINE_PURPLE.get());
                output.accept(ModBlocks.SLIME_VINE_PURPLE_MID.get());
                output.accept(ModBlocks.SLIME_VINE_PURPLE_END.get());
                for (com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.SlimeType type
                        : com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.SlimeType.values()) {
                    output.accept(withState(ModBlocks.SLIME_CONGEALED.get(),
                            ModBlocks.SLIME_CONGEALED.get().defaultBlockState()
                                    .setValue(com.lvdriver.tconstruct_nirvana.block.slime.SlimeTypes.SLIME_TYPE, type)));
                }
            })
            .build());

    /** 部件注册名即模具形状 ID。 */
    private static ResourceLocation shapeId(DeferredItem<? extends ToolPart> part) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, part.getId().getPath());
    }

    /** 变体方块物品（BLOCK_STATE 组件表达变体，1:1 旧版 ItemBlockMeta 行为）。 */
    private static ItemStack withState(net.minecraft.world.level.block.Block block, net.minecraft.world.level.block.state.BlockState state) {
        ItemStack stack = new ItemStack(block);
        net.minecraft.world.item.component.BlockItemStateProperties props =
                net.minecraft.world.item.component.BlockItemStateProperties.EMPTY;
        for (net.minecraft.world.level.block.state.properties.Property<?> property : state.getProperties()) {
            props = props.with(property, state);
        }
        stack.set(net.minecraft.core.component.DataComponents.BLOCK_STATE, props);
        return stack;
    }

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
