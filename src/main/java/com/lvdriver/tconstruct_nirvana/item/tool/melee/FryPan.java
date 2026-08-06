package com.lvdriver.tconstruct_nirvana.item.tool.melee;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * 平底锅（1:1 移植自 Tinkers' Antique {@code FryPan}）。
 * 部件：工具杆 + 平底锅头（2 槽）；伤害倍率 1.0、攻速 1.4、击退 2.0；
 * 右击蓄力后击飞前方实体。
 */
public class FryPan extends TinkerToolItem {

    public FryPan() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.PAN_HEAD.get()));
        addCategory(Category.WEAPON);
    }

    @Override
    public float damagePotential() {
        return 1.0f;
    }

    @Override
    public float knockback() {
        return 2f;
    }

    @Override
    public double attackSpeed() {
        return 1.4d;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity player, int timeLeft) {
        if (world.isClientSide) {
            return;
        }
        // 蓄力强度（1:1 旧版：30 tick 满蓄，strength = 0.1 + 2.5 × progress²）
        float progress = Math.min(1f, (getUseDuration(stack, player) - timeLeft) / 30f);
        float strength = 0.1f + 2.5f * progress * progress;

        // 击飞视线前方 3.2 格内的实体（1:1 旧版简化版）
        Vec3 eye = player.getEyePosition();
        Vec3 look = player.getLookAngle();
        AABB box = player.getBoundingBox().expandTowards(look.scale(3.2)).inflate(1.0, 0.5, 1.0);
        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, box, e -> e != player && e.isAlive())) {
            Vec3 toTarget = target.position().subtract(eye);
            if (toTarget.lengthSqr() < 3.2 * 3.2 && toTarget.normalize().dot(look) > 0.3f) {
                target.hurt(player.damageSources().playerAttack((Player) player), 4f + strength * 4f);
                target.push(look.x * strength * 2.5, 0.35 + strength * 0.5, look.z * strength * 2.5);
            }
        }
        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        return buildDefaultTag(materials);
    }
}
