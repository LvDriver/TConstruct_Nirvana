package com.lvdriver.tconstruct_nirvana.item.tool.harvest;

import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.ModToolParts;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.tool.Category;
import com.lvdriver.tconstruct_nirvana.item.tool.TinkerToolItem;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 铲（1:1 移植自 Tinkers' Antique {@code Shovel}）。
 * 部件：工具杆 + 铲头 + 绑定结；伤害倍率 0.9、攻速 1.0、默认合成。
 */
public class Shovel extends TinkerToolItem {

    public Shovel() {
        this(PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.SHOVEL_HEAD.get()),
                PartMaterialType.extra(ModToolParts.BINDING.get()));
    }

    protected Shovel(PartMaterialType... requiredComponents) {
        super(new Item.Properties(), requiredComponents);
        addCategory(Category.HARVEST);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public double attackSpeed() {
        return 1f;
    }

    @Override
    public float damagePotential() {
        return 0.9f;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        return buildDefaultTag(materials);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_SHOVEL_ITEMS);
    }
}
