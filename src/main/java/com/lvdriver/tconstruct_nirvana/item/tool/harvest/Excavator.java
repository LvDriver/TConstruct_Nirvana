package com.lvdriver.tconstruct_nirvana.item.tool.harvest;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.AoeToolItem;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
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
 * 挖掘机（1:1 移植自 Tinkers' Antique {@code Excavator}）。
 * 部件：坚韧工具杆 + 挖掘机头 + 大板(HEAD) + 坚韧绑定结(EXTRA)；
 * 双头平均 + 绑定结 + 手柄、耐久 ×1.75、挖掘速度 0.28、伤害倍率 1.25、
 * 攻速 0.7、3×3×1 范围挖掘。
 */
public class Excavator extends AoeToolItem {

    public static final float DURABILITY_MODIFIER = 1.75f;

    public Excavator() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOUGH_TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.EXCAVATOR_HEAD.get()),
                PartMaterialType.head(ModToolParts.LARGE_PLATE.get()),
                PartMaterialType.extra(ModToolParts.TOUGH_BINDING.get()));
        addCategory(Category.HARVEST);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.28f;
    }

    @Override
    public float damagePotential() {
        return 1.25f;
    }

    @Override
    public double attackSpeed() {
        return 0.7f;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin) {
        return calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1, 2};
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.75f;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material handle = materials.get(0);
        Material head = materials.get(1);
        Material plate = materials.get(2);
        Material binding = materials.get(3);

        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handleStats = handle.getStatsOrUnknown(MaterialTypes.HANDLE);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats plateStats = plate.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats bindingStats = binding.getStatsOrUnknown(MaterialTypes.EXTRA);

        ToolData data = ToolData.empty()
                .head(new int[]{headStats.durability(), plateStats.durability()},
                        new int[]{headStats.harvestLevel(), plateStats.harvestLevel()},
                        new float[]{headStats.attack(), plateStats.attack()},
                        new float[]{headStats.miningspeed(), plateStats.miningspeed()})
                .extra(bindingStats.extraDurability())
                .handle(handleStats.modifier(), handleStats.durability());
        // 耐久 ×1.75（1:1 旧版）
        data = data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_SHOVEL_ITEMS);
    }
}
