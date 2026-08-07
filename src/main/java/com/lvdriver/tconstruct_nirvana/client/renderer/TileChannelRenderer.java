package com.lvdriver.tconstruct_nirvana.client.renderer;

import com.lvdriver.tconstruct_nirvana.block.BlockChannel;
import com.lvdriver.tconstruct_nirvana.block.TileChannel;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 沟槽渲染器：渲染通道内液体（按液量比例）+ 向下流动时的滴液柱
 * （1:1 旧版 channel 液体渲染简化版，流动方向细节省略）。
 */
public class TileChannelRenderer implements BlockEntityRenderer<TileChannel> {

    public TileChannelRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileChannel be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        FluidStack fluid = be.getTank().getFluid();
        if (fluid.isEmpty()) {
            return;
        }
        float ratio = Math.min(1.0F, (float) be.getTank().getFluidAmount() / TileChannel.CAPACITY);
        // 中心通道内液体（通道槽 y 0.25~0.5）
        FluidBoxRenderer.renderFluidBox(fluid,
                0.375F, 0.25F, 0.375F,
                0.625F, 0.25F + 0.25F * ratio, 0.625F,
                poseStack, buffers, light);
        // 向下输出时渲染下方滴液
        if (be.getBlockState().getValue(BlockChannel.DOWN)) {
            FluidBoxRenderer.renderFluidBox(fluid,
                    0.375F, -0.75F, 0.375F,
                    0.625F, 0.0F, 0.625F,
                    poseStack, buffers, light);
        }
    }
}
