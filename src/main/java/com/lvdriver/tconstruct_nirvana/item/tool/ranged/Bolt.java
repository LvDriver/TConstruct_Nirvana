package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.BoltCore;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 弩矢（1:1 移植自 Tinkers' Antique {@code Bolt} 的简化版）。
 * 组装输入：弩芯 + 箭羽（2 输入 → 内部 3 材料：芯/头/羽）；
 * 伤害倍率 1.0、攻速 1.0、耐久 ×0.8。
 */
public class Bolt extends ProjectileToolItem {

    public Bolt() {
        super(new Item.Properties(),
                PartMaterialType.arrowShaft(ModToolParts.BOLT_CORE.get()),
                PartMaterialType.arrowHead(ModToolParts.BOLT_CORE.get()),
                PartMaterialType.fletching(ModToolParts.FLETCHING.get()));
    }

    @Override
    public ItemStack buildItemFromStacks(List<ItemStack> stacks) {
        List<ItemStack> input = stacks.stream().filter(s -> !s.isEmpty()).toList();
        if (input.size() != 2) {
            return ItemStack.EMPTY;
        }
        ItemStack boltCore = input.get(0);
        ItemStack fletching = input.get(1);
        if (!(boltCore.getItem() instanceof BoltCore core) || !(fletching.getItem() instanceof com.lvdriver.tconstruct_nirvana.item.part.ToolPart fletchPart)) {
            return ItemStack.EMPTY;
        }
        Material coreMat = core.getMaterial(boltCore);
        Material fletchingMat = fletchPart.getMaterial(fletching);
        // 简化：头材料复用核心材料（1:1 完整双材料留待后续）
        return buildItem(List.of(coreMat, coreMat, fletchingMat));
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public double attackSpeed() {
        return 1;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material shaft = materials.get(0);
        Material head = materials.get(1);
        Material fletching = materials.get(2);

        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown(MaterialTypes.HEAD);

        // 1:1 旧版 Bolt buildTagData（head + fletchings + shafts，耐久 ×0.8）
        ToolData data = ToolData.empty()
                .head(headStats.durability(), headStats.harvestLevel(), headStats.attack(), headStats.miningspeed());
        data = applyFletchings(data, fletching);
        data = applyShafts(data, shaft);
        data = data.withDurability(Math.round(data.durability() * 0.8f));
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = super.buildItem(materials);
        Material fletching = materials.get(2);
        com.lvdriver.tconstruct_nirvana.material.FletchingMaterialStats stats = fletching.getStatsOrUnknown(MaterialTypes.FLETCHING);
        tool.set(ModDataComponents.ACCURACY, Math.min(1f, Math.max(0, stats.accuracy())));
        return tool;
    }

    @Override
    public com.lvdriver.tconstruct_nirvana.entity.TinkerProjectileBase getProjectile(
            ItemStack stack, ItemStack launcher, net.minecraft.world.level.Level world,
            net.minecraft.world.entity.player.Player player, float speed, float inaccuracy, float power, boolean usedAmmo) {
        // 1:1 旧版 Bolt.getProjectile：精准度修正不准度（同 Arrow）
        float accuracy = stack.getOrDefault(ModDataComponents.ACCURACY, 1f);
        inaccuracy -= (1f - 1f / Math.max(0.01f, accuracy)) * speed / 2f;
        return new com.lvdriver.tconstruct_nirvana.entity.TinkerBolt(
                com.lvdriver.tconstruct_nirvana.entity.ModEntities.TINKER_BOLT.get(), world, player,
                speed, inaccuracy, power, getProjectileStack(stack, player, usedAmmo), launcher);
    }
}
