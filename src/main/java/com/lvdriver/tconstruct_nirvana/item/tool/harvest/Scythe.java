package com.lvdriver.tconstruct_nirvana.item.tool.harvest;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 大镰（1:1 移植自 Tinkers' Antique {@code Scythe}）。
 * 部件：坚韧工具杆 + 大镰头 + 坚韧绑定结 + 坚韧工具杆（双手柄）；
 * 双手柄平均、耐久 ×2.2、伤害倍率 0.75、攻速 0.9、3×3×3 范围收割。
 */
public class Scythe extends Kama {

    public static final float DURABILITY_MODIFIER = 2.2f;

    public Scythe() {
        super(PartMaterialType.handle(ModToolParts.TOUGH_TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.SCYTHE_HEAD.get()),
                PartMaterialType.extra(ModToolParts.TOUGH_BINDING.get()),
                PartMaterialType.handle(ModToolParts.TOUGH_TOOL_ROD.get()));
    }

    @Override
    public float damagePotential() {
        return 0.75f;
    }

    @Override
    public double attackSpeed() {
        return 0.9f;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin) {
        return calcAOEBlocks(stack, world, player, origin, 3, 3, 3);
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1, 2};
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material handle1 = materials.get(0);
        Material head = materials.get(1);
        Material extra = materials.get(2);
        Material handle2 = materials.get(3);

        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handle1Stats = handle1.getStatsOrUnknown(MaterialTypes.HANDLE);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats extraStats = extra.getStatsOrUnknown(MaterialTypes.EXTRA);
        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handle2Stats = handle2.getStatsOrUnknown(MaterialTypes.HANDLE);

        // 双手柄平均（1:1 旧版 data.handle(handle, handle2)）
        ToolData data = ToolData.empty()
                .head(headStats.durability(), headStats.harvestLevel(), headStats.attack(), headStats.miningspeed())
                .extra(extraStats.extraDurability())
                .handle(new float[]{handle1Stats.modifier(), handle2Stats.modifier()},
                        new int[]{handle1Stats.durability(), handle2Stats.durability()});
        // 耐久 ×2.2（1:1 旧版）
        data = data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of();
    }
}
