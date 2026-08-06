package com.lvdriver.tconstruct_nirvana.item.tool.ranged;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

/**
 * 弩（1:1 移植自 Tinkers' Antique {@code CrossBow}）。
 * 部件：坚韧工具杆(十字弩) + 弓臂 + 坚韧绑定结(EXTRA) + 弓弦；
 * 伤害倍率 0.8、攻速 2、拉弓 45 tick、基础弹射伤害 3、初速 7、
 * 弹射伤害倍率 1.3；附加伤害 ×1.5。装填状态（loaded）简化：不实现。
 */
public class CrossBow extends BowToolItem {

    public CrossBow() {
        super(new Item.Properties(),
                PartMaterialType.crossbow(ModToolParts.TOUGH_TOOL_ROD.get()),
                PartMaterialType.bow(ModToolParts.BOW_LIMB.get()),
                PartMaterialType.extra(ModToolParts.TOUGH_BINDING.get()),
                PartMaterialType.bowstring(ModToolParts.BOW_STRING.get()));
    }

    @Override
    public float damagePotential() {
        return 0.8f;
    }

    @Override
    public double attackSpeed() {
        return 2;
    }

    @Override
    public float baseProjectileDamage() {
        return 3f;
    }

    @Override
    protected float baseProjectileSpeed() {
        return 7f;
    }

    @Override
    public float projectileDamageModifier() {
        return 1.3f;
    }

    @Override
    public int getDrawTime() {
        return 45;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material body = materials.get(0);
        Material limb = materials.get(1);
        Material binding = materials.get(2);
        Material bowstring = materials.get(3);

        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats bodyStats = body.getStatsOrUnknown(MaterialTypes.HANDLE);
        com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats bodyExtraStats = body.getStatsOrUnknown(MaterialTypes.EXTRA);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = limb.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats bindingStats = binding.getStatsOrUnknown(MaterialTypes.EXTRA);

        // 1:1 旧版 CrossBow buildTagData（head + limb + extra(binding+body) + handle + bowstring）
        ToolData data = ToolData.empty()
                .head(headStats.durability(), headStats.harvestLevel(), headStats.attack(), headStats.miningspeed())
                .extra(bindingStats.extraDurability(), bodyExtraStats.extraDurability())
                .handle(bodyStats.modifier(), bodyStats.durability());
        return applyBowstring(data, bowstring).withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = super.buildItem(materials);
        // 附加伤害 ×1.5（1:1 旧版 data.bonusDamage *= 1.5f）
        com.lvdriver.tconstruct_nirvana.data.LauncherData launcher = tool.getOrDefault(
                com.lvdriver.tconstruct_nirvana.data.ModDataComponents.LAUNCHER_DATA,
                new com.lvdriver.tconstruct_nirvana.data.LauncherData(1f, 1f, 0f));
        tool.set(com.lvdriver.tconstruct_nirvana.data.ModDataComponents.LAUNCHER_DATA,
                new com.lvdriver.tconstruct_nirvana.data.LauncherData(launcher.drawSpeed(), launcher.range(), launcher.bonusDamage() * 1.5f));
        return tool;
    }
}
