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

import java.util.ArrayList;
import java.util.List;

/**
 * 伐木斧（1:1 移植自 Tinkers' Antique {@code LumberAxe}）。
 * 部件：坚韧工具杆 + 宽斧头 + 大板(HEAD) + 坚韧绑定结(EXTRA)；
 * 耐久 ×2.0、挖掘速度 0.35、伤害倍率 1.2、攻速 0.8；整棵树范围砍伐（向上扩展）。
 */
public class LumberAxe extends AoeToolItem {

    public static final float DURABILITY_MODIFIER = 2f;

    public LumberAxe() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOUGH_TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.BROAD_AXE_HEAD.get()),
                PartMaterialType.head(ModToolParts.LARGE_PLATE.get()),
                PartMaterialType.extra(ModToolParts.TOUGH_BINDING.get()));
        addCategory(Category.HARVEST);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.35f;
    }

    @Override
    public float damagePotential() {
        return 1.2f;
    }

    @Override
    public double attackSpeed() {
        return 0.8f;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin) {
        // 整棵树：以破坏点为底向上扩展（1:1 旧版 LumberAxe 逻辑的简化版）
        List<BlockPos> result = new ArrayList<>();
        for (int dy = 1; dy <= 32; dy++) {
            BlockPos pos = origin.offset(0, dy, 0);
            if (world.isEmptyBlock(pos)) {
                break;
            }
            BlockState state = world.getBlockState(pos);
            if (state.is(BlockTags.LOGS) || state.is(BlockTags.LEAVES)) {
                result.add(pos);
            } else if (!state.is(BlockTags.LOGS)) {
                break;
            }
        }
        return result;
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1, 2};
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.625f;
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
        // 耐久 ×2.0（1:1 旧版）
        data = data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_AXE_ITEMS);
    }
}
