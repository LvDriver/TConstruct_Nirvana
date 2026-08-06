package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.part.ToolPart;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 手里剑（1:1 移植自 Tinkers' Antique {@code Shuriken}）。
 * 4×匕首刃（须具备 HEAD+EXTRA+PROJECTILE 属性）；伤害倍率 0.7、攻击 +1、精准度 1。
 */
public class Shuriken extends ProjectileToolItem {

    private static PartMaterialType shurikenPMT() {
        return new PartMaterialType(ModToolParts.KNIFE_BLADE.get(), MaterialTypes.HEAD, MaterialTypes.EXTRA, MaterialTypes.PROJECTILE);
    }

    public Shuriken() {
        super(new Item.Properties(),
                shurikenPMT(), shurikenPMT(), shurikenPMT(), shurikenPMT());
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{0, 1, 2, 3};
    }

    @Override
    public float damagePotential() {
        return 0.7f;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        // 1:1 旧版 Shuriken buildTagData（head×4 平均 + extra×4 平均，攻击 +1）
        int[] durability = new int[4];
        int[] harvestLevels = new int[4];
        float[] attack = new float[4];
        float[] speed = new float[4];
        int[] extraDur = new int[4];
        for (int i = 0; i < 4; i++) {
            Material material = materials.get(i);
            com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats head = material.getStatsOrUnknown(MaterialTypes.HEAD);
            com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats extra = material.getStatsOrUnknown(MaterialTypes.EXTRA);
            durability[i] = head.durability();
            harvestLevels[i] = head.harvestLevel();
            attack[i] = head.attack();
            speed[i] = head.miningspeed();
            extraDur[i] = extra.extraDurability();
        }
        ToolData data = ToolData.empty()
                .head(durability, harvestLevels, attack, speed)
                .extra(extraDur);
        data = data.withAttack(data.attack() + 1f);
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    /* ---------- 投掷（1:1 旧版 Shuriken.onItemRightClick） ---------- */

    @Override
    public net.minecraft.world.InteractionResultHolder<ItemStack> use(
            net.minecraft.world.level.Level world, net.minecraft.world.entity.player.Player player,
            net.minecraft.world.InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (com.lvdriver.tconstruct_nirvana.util.ToolHelper.isBroken(stack)) {
            return net.minecraft.world.InteractionResultHolder.fail(stack);
        }
        player.getCooldowns().addCooldown(this, 4);

        if (!world.isClientSide) {
            boolean usedAmmo = useAmmo(stack, player);
            com.lvdriver.tconstruct_nirvana.entity.TinkerProjectileBase projectile =
                    getProjectile(stack, stack, world, player, 2.1f, 0f, 1f, usedAmmo);
            world.addFreshEntity(projectile);
        }
        return net.minecraft.world.InteractionResultHolder.success(stack);
    }

    @Override
    public com.lvdriver.tconstruct_nirvana.entity.TinkerProjectileBase getProjectile(
            ItemStack stack, ItemStack launcher, net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player player, float speed, float inaccuracy, float power, boolean usedAmmo) {
        // 1:1 旧版 Shuriken.getProjectile：不准度 × 精准度
        float accuracy = stack.getOrDefault(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.ACCURACY, 1f);
        inaccuracy *= accuracy;
        return new com.lvdriver.tconstruct_nirvana.entity.TinkerShuriken(
                com.lvdriver.tconstruct_nirvana.entity.ModEntities.TINKER_SHURIKEN.get(), world, player,
                speed, inaccuracy, getProjectileStack(stack, player, usedAmmo), launcher);
    }
}
