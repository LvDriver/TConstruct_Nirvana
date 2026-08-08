package com.lvdriver.tconstruct_nirvana.client.model;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.function.Function;

/**
 * 工具多层模型几何体（1.21.1 IUnbakedGeometry，替代旧版 ToolModel/MaterialModel）。
 *
 * <p>bake 时按层定义预取全部候选贴图（基础贴图 + 材料后缀变体 + 损坏贴图），
 * 实际渲染按 ItemStack 材料在 {@link ToolItemOverrides#resolve} 中组合。</p>
 */
public class ToolUnbakedGeometry implements IUnbakedGeometry<ToolUnbakedGeometry> {

    private final String toolName;

    public ToolUnbakedGeometry(String toolName) {
        this.toolName = toolName;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker,
                           Function<Material, TextureAtlasSprite> spriteGetter,
                           ModelState modelState, ItemOverrides overrides) {
        return ToolBakedModel.create(toolName, context.getTransforms(), spriteGetter, modelState);
    }

    /** 贴图定位辅助：{@code <modid>:item/<path>}。 */
    static ResourceLocation modTexture(String path) {
        return ResourceLocation.fromNamespaceAndPath(TConstructNirvana.MODID, "item/" + path);
    }
}
