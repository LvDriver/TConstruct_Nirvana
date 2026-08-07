package com.lvdriver.tconstruct_nirvana.client.renderer;

import com.lvdriver.tconstruct_nirvana.block.TileFaucet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * 龙头渲染器：浇注时在龙头下方渲染一段滴液柱（1:1 旧版 faucet 液体渲染简化版）。
 */
public class TileFaucetRenderer implements BlockEntityRenderer<TileFaucet> {

    public TileFaucetRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileFaucet be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffers, int light, int overlay) {
        FluidStack drained = be.drained;
        if (drained.isEmpty() || !be.isPouring) {
            return;
        }
        // 龙头嘴（y 0.25~0.625）到下方方块顶面之间的滴液柱
        FluidBoxRenderer.renderFluidBox(drained,
                0.375F, -0.625F, 0.375F,
                0.625F, -0.25F, 0.625F,
                poseStack, buffers, light);
    }
}
