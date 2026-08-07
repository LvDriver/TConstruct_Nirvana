package com.lvdriver.tconstruct_nirvana.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

/**
 * 流体盒渲染工具（浇铸台/龙头/沟槽内部液体渲染用）。
 *
 * <p>按 FluidType 的 still 贴图 + 染色绘制一个轴对齐盒的 6 个面
 * （半透明，1:1 旧版 FluidRenderer 的简化实现）。</p>
 */
public final class FluidBoxRenderer {

    private FluidBoxRenderer() {
    }

    /** 渲染一个轴对齐流体盒（世界坐标相对方块原点）。 */
    public static void renderFluidBox(FluidStack fluid,
                                      float minX, float minY, float minZ,
                                      float maxX, float maxY, float maxZ,
                                      PoseStack poseStack, MultiBufferSource buffers, int light) {
        if (fluid.isEmpty() || maxX <= minX || maxY <= minY || maxZ <= minZ) {
            return;
        }
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid.getFluid().getFluidType());
        ResourceLocation still = ext.getStillTexture(fluid);
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getModelManager().getAtlas(TextureAtlas.LOCATION_BLOCKS).getSprite(still);

        int tint = ext.getTintColor();
        float a = ((tint >> 24) & 0xFF) / 255.0F;
        float r = ((tint >> 16) & 0xFF) / 255.0F;
        float g = ((tint >> 8) & 0xFF) / 255.0F;
        float b = (tint & 0xFF) / 255.0F;

        Matrix4f pose = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        VertexConsumer buffer = buffers.getBuffer(RenderType.translucent());

        // 下
        quad(buffer, pose, normal, poseStack.last(), sprite, r, g, b, a, light,
                minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ);
        // 上
        quad(buffer, pose, normal, poseStack.last(), sprite, r, g, b, a, light,
                minX, maxY, minZ, minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ);
        // 北
        quad(buffer, pose, normal, poseStack.last(), sprite, r, g, b, a, light,
                minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ, maxX, minY, minZ);
        // 南
        quad(buffer, pose, normal, poseStack.last(), sprite, r, g, b, a, light,
                maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ, minX, minY, maxZ);
        // 西
        quad(buffer, pose, normal, poseStack.last(), sprite, r, g, b, a, light,
                minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ, minX, minY, minZ);
        // 东
        quad(buffer, pose, normal, poseStack.last(), sprite, r, g, b, a, light,
                maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, maxX, minY, maxZ);
    }

    /** 绘制一个四边面（顶点逆时针，朝向由顶点顺序的右手定则决定）。 */
    private static void quad(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, com.mojang.blaze3d.vertex.PoseStack.Pose poseStack,
                             TextureAtlasSprite sprite,
                             float r, float g, float b, float a, int light,
                             float x0, float y0, float z0,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3) {
        // 面法线（前三个顶点叉积，近似）
        float nx = (y1 - y0) * (z2 - z0) - (z1 - z0) * (y2 - y0);
        float ny = (z1 - z0) * (x2 - x0) - (x1 - x0) * (z2 - z0);
        float nz = (x1 - x0) * (y2 - y0) - (y1 - y0) * (x2 - x0);
        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 1.0E-4F) {
            nx /= len;
            ny /= len;
            nz /= len;
        }

        vertex(buffer, pose, normal, poseStack, sprite, r, g, b, a, light, nx, ny, nz, x0, y0, z0, sprite.getU0(), sprite.getV0());
        vertex(buffer, pose, normal, poseStack, sprite, r, g, b, a, light, nx, ny, nz, x1, y1, z1, sprite.getU0(), sprite.getV1());
        vertex(buffer, pose, normal, poseStack, sprite, r, g, b, a, light, nx, ny, nz, x2, y2, z2, sprite.getU1(), sprite.getV1());
        vertex(buffer, pose, normal, poseStack, sprite, r, g, b, a, light, nx, ny, nz, x3, y3, z3, sprite.getU1(), sprite.getV0());
    }

    private static void vertex(VertexConsumer buffer, Matrix4f pose, Matrix3f normal, com.mojang.blaze3d.vertex.PoseStack.Pose poseStack,
                               TextureAtlasSprite sprite,
                               float r, float g, float b, float a, int light,
                               float nx, float ny, float nz,
                               float x, float y, float z, float u, float v) {
        buffer.addVertex(pose, x, y, z)
                .setColor(r, g, b, a)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(poseStack, nx, ny, nz);
    }
}
