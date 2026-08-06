package com.lvdriver.tconstruct_nirvana.item.tool.harvest;

import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 鹤嘴锄（1:1 移植自 Tinkers' Antique {@code Mattock}）。
 * 部件：工具杆 + 斧头 + 铲头（双头）；伤害倍率 0.9、攻速 0.9、挖掘速度 0.95、
 * 击退 1.1、攻击 +3；斧/铲采掘等级分开存储（1:1 MattockToolNBT）。
 */
public class Mattock extends TinkerToolItem {

    public Mattock() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.AXE_HEAD.get()),
                PartMaterialType.head(ModToolParts.SHOVEL_HEAD.get()));
        addCategory(Category.HARVEST);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE) || state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.95f;
    }

    @Override
    public float damagePotential() {
        return 0.90f;
    }

    @Override
    public float knockback() {
        return 1.1f;
    }

    @Override
    public double attackSpeed() {
        return 0.9f;
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1, 2};
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material handle = materials.get(0);
        Material axe = materials.get(1);
        Material shovel = materials.get(2);

        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handleStats = handle.getStatsOrUnknown(MaterialTypes.HANDLE);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats axeStats = axe.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats shovelStats = shovel.getStatsOrUnknown(MaterialTypes.HEAD);

        ToolData data = ToolData.empty()
                .head(new int[]{axeStats.durability(), shovelStats.durability()},
                        new int[]{axeStats.harvestLevel(), shovelStats.harvestLevel()},
                        new float[]{axeStats.attack(), shovelStats.attack()},
                        new float[]{axeStats.miningspeed(), shovelStats.miningspeed()})
                .handle(handleStats.modifier(), handleStats.durability());
        // 1:1 旧版：基础伤害 +3
        data = data.withAttack(data.attack() + 3f);
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = super.buildItem(materials);
        if (materials.size() >= 3) {
            com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats axe = materials.get(1).getStatsOrUnknown(MaterialTypes.HEAD);
            com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats shovel = materials.get(2).getStatsOrUnknown(MaterialTypes.HEAD);
            storeLevels(tool, axe.harvestLevel(), shovel.harvestLevel());
        }
        return tool;
    }

    /** 斧/铲采掘等级分开存储（1:1 MattockToolNBT：axeLevel/shovelLevel）。 */
    public void storeLevels(ItemStack stack, int axeLevel, int shovelLevel) {
        stack.set(ModDataComponents.MATTOCK_LEVELS, List.of(axeLevel, shovelLevel));
    }

    /** 斧头采掘等级。 */
    public int getAxeLevel(ItemStack stack) {
        List<Integer> levels = stack.getOrDefault(ModDataComponents.MATTOCK_LEVELS, List.of());
        return levels.size() > 0 ? levels.get(0) : getHarvestLevelStat(stack);
    }

    /** 铲头采掘等级。 */
    public int getShovelLevel(ItemStack stack) {
        List<Integer> levels = stack.getOrDefault(ModDataComponents.MATTOCK_LEVELS, List.of());
        return levels.size() > 1 ? levels.get(1) : getHarvestLevelStat(stack);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_AXE_ITEMS, TConTags.MINEABLE_SHOVEL_ITEMS);
    }
}
