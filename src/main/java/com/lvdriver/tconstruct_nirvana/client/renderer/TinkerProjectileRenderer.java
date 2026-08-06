package com.lvdriver.tconstruct_nirvana.client.renderer;

import com.lvdriver.tconstruct_nirvana.entity.TinkerProjectileBase;
import com.lvdriver.tconstruct_nirvana.entity.TinkerShuriken;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * 弹射物渲染器：用弹射物物品自身的模型（pickupItemStack）渲染，
 * 朝向沿飞行方向（1:1 原版 {@code ArrowRenderer} 的旋转方式）。
 *
 * <p>占位实现：物品模型为 2D 纸片（generated），完整 3D 弹射物模型
 * 留待后续会话（同工具模型占位策略）。</p>
 */
public class TinkerProjectileRenderer extends EntityRenderer<TinkerProjectileBase> {

    /** 占位纹理（实际渲染走物品模型，不绑定此纹理）。 */
    private static final ResourceLocation PLACEHOLDER_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public TinkerProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(TinkerProjectileBase entity, float yaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTick, poseStack, buffer, packedLight);

        ItemStack stack = entity.getPickupItemStackOrigin();
        if (stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();
        // 1:1 原版 ArrowRenderer：先绕 Y 用 yaw，再绕 Z 用 pitch
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));

        if (entity instanceof TinkerShuriken) {
            // 手里剑：平躺 + 匀速自旋（旧版 spin/rollAngle 动画的简化）
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees((entity.tickCount * 30.0F) % 360.0F));
        }

        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED, packedLight, OverlayTexture.NO_OVERLAY,
                poseStack, buffer, entity.level(), entity.getId());
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(TinkerProjectileBase entity) {
        // 实际渲染走物品模型，此纹理仅为满足 EntityRenderer 抽象方法
        return PLACEHOLDER_TEXTURE;
    }
}
