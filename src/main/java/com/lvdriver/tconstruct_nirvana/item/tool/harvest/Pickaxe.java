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
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * 镐（1:1 移植自 Tinkers' Antique {@code Pickaxe}）。
 * 部件：工具杆 + 镐头 + 绑定结；伤害倍率 1.0、攻速 1.2、默认合成。
 */
public class Pickaxe extends TinkerToolItem {

    public Pickaxe() {
        this(PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.PICK_HEAD.get()),
                PartMaterialType.extra(ModToolParts.BINDING.get()));
    }

    protected Pickaxe(PartMaterialType... requiredComponents) {
        super(new Item.Properties(), requiredComponents);
        addCategory(Category.HARVEST);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public float damagePotential() {
        return 1f;
    }

    @Override
    public double attackSpeed() {
        return 1.2f;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        return buildDefaultTag(materials);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_PICKAXE_ITEMS);
    }
}
