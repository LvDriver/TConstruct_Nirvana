package com.lvdriver.tconstruct_nirvana.client.renderer;

import com.lvdriver.tconstruct_nirvana.block.CastingBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 浇铸台/盆渲染器（1:1 移植自 Tinkers' Antique 的 TileCasting 渲染逻辑简化版）。
 *
 * <p>槽内物品（模具/产物）渲染在台面/盆内；内部液体按罐内量/容量比例渲染
 * （液体盒区域 1:1 旧版：台面凹槽 0.625 起、盆体 0.25 起）。</p>
 */
public class TileCastingRenderer implements BlockEntityRenderer<CastingBlockEntity> {

    public TileCastingRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CastingBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        // 槽内物品（0=模具，1=产物；1:1 旧版 setInventoryDisplay 位置）
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        for (int slot = 0; slot < 2; slot++) {
            ItemStack stack = be.getItem(slot);
            if (stack.isEmpty()) {
                continue;
            }
            poseStack.pushPose();
            poseStack.translate(0.5D, be.isBasin() ? 0.34375D : 0.90625D, 0.5D);
            poseStack.scale(be.isBasin() ? 0.75F : 0.875F, be.isBasin() ? 0.75F : 0.875F, be.isBasin() ? 0.75F : 0.875F);
            itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffers, be.getLevel(), 0);
            poseStack.popPose();
        }

        // 内部液体（按比例，1:1 旧版渲染区域）
        FluidStack fluid = be.getTank().getFluid();
        if (!fluid.isEmpty() && be.getTank().getCapacity() > 0) {
            float ratio = Math.min(1.0F, (float) be.getTank().getFluidAmount() / be.getTank().getCapacity());
            if (be.isBasin()) {
                // 盆体：0.25 ~ 1.0 内腔
                FluidBoxRenderer.renderFluidBox(fluid,
                        0.125F, 0.25F, 0.125F,
                        0.875F, 0.25F + 0.75F * ratio, 0.875F,
                        poseStack, buffers, light);
            } else {
                // 台面凹槽：0.625 ~ 1.0
                FluidBoxRenderer.renderFluidBox(fluid,
                        0.125F, 0.625F, 0.125F,
                        0.875F, 0.625F + 0.375F * ratio, 0.875F,
                        poseStack, buffers, light);
            }
        }
    }
}
