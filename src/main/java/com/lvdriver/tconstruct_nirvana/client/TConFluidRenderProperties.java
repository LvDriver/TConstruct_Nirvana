package com.lvdriver.tconstruct_nirvana.client;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;

/**
 * 流体客户端渲染属性（贴图 + 染色）。
 *
 * <p>1:1 旧版：贴图按流体类别（熔融金属 molten_metal / 石头 liquid_stone /
 * 经典 liquid / 史莱姆 liquid_slime），染色用旧版 {@code FluidColored.color}
 * （ARGB，alpha 缺失已补 0xFF）。由 {@link ModClientEvents} 经
 * {@code RegisterClientExtensionsEvent} 注册到各 FluidType。</p>
 */
public class TConFluidRenderProperties implements IClientFluidTypeExtensions {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private final int tintColor;

    public TConFluidRenderProperties(ResourceLocation stillTexture, ResourceLocation flowingTexture, int tintColor) {
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.tintColor = tintColor;
    }

    @Override
    public ResourceLocation getStillTexture() {
        return stillTexture;
    }

    @Override
    public ResourceLocation getFlowingTexture() {
        return flowingTexture;
    }

    @Override
    public int getTintColor() {
        return tintColor;
    }
}
