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
 * 斧（1:1 移植自 Tinkers' Antique {@code Hatchet}）。
 * 部件：工具杆 + 斧头 + 绑定结；伤害倍率 1.1、攻速 1.1、击退 1.3、攻击 +0.5、
 * 砍树叶不耗耐久、可破盾。
 */
public class Hatchet extends TinkerToolItem {

    public Hatchet() {
        this(PartMaterialType.handle(ModToolParts.TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.AXE_HEAD.get()),
                PartMaterialType.extra(ModToolParts.BINDING.get()));
    }

    protected Hatchet(PartMaterialType... requiredComponents) {
        super(new Item.Properties(), requiredComponents);
        addCategory(Category.HARVEST, Category.WEAPON);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public float damagePotential() {
        return 1.1f;
    }

    @Override
    public double attackSpeed() {
        return 1.1f;
    }

    @Override
    public float knockback() {
        return 1.3f;
    }

    @Override
    public void afterBlockBreak(ItemStack stack, net.minecraft.world.level.Level world, BlockState state,
                                net.minecraft.core.BlockPos pos, net.minecraft.world.entity.LivingEntity player,
                                int damage, boolean wasEffective) {
        // 砍树叶不耗耐久（1:1 旧版）
        if (state.is(BlockTags.LEAVES)) {
            damage = 0;
        }
        super.afterBlockBreak(stack, world, state, pos, player, damage, wasEffective);
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        ToolData data = buildDefaultTag(materials);
        return data.withAttack(data.attack() + 0.5f);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_AXE_ITEMS);
    }
}
