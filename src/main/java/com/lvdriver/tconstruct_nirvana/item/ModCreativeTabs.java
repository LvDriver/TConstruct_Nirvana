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
            })
            .build());

    /** 部件注册名即模具形状 ID。 */
    private static ResourceLocation shapeId(DeferredItem<? extends ToolPart> part) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, part.getId().getPath());
    }

    private ModCreativeTabs() {
    }

    public static void register(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
