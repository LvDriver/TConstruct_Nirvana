package com.lvdriver.tconstruct_nirvana.item.tool.harvest;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.AoeToolItem;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.material.Material;
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
 * 镰刀（1:1 移植自 Tinkers' Antique {@code Kama}）。
 * 部件：工具杆 + 镰刀头 + 绑定结；伤害倍率 1.0、攻速 1.3；3×3×1 收割植物。
 */
public class Kama extends AoeToolItem {

    public Kama() {
        this(PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.KAMA_HEAD.get()),
                PartMaterialType.extra(ModToolParts.BINDING.get()));
    }

    public Kama(PartMaterialType... requiredComponents) {
        super(new Item.Properties(), requiredComponents);
        addCategory(Category.HARVEST, Category.WEAPON);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.LEAVES) || state.is(BlockTags.CROPS) || state.is(BlockTags.REPLACEABLE_BY_TREES)
                || state.is(BlockTags.CORALS) || state.is(BlockTags.FLOWERS) || state.is(BlockTags.WOOL);
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public double attackSpeed() {
        return 1.3f;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin) {
        return calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        return buildDefaultTag(materials);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of();
    }
}
