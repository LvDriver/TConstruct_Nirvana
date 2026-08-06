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
 * 锤（1:1 移植自 Tinkers' Antique {@code Hammer}）。
 * 部件：坚韧工具杆 + 锤头 + 大板 + 大板；主头双权重合成、采掘等级取主头、
 * 耐久 ×2.5、挖掘速度 0.4、伤害倍率 1.2、攻速 0.8、3×3×1 范围挖掘、
 * 对亡灵 +3~6 额外伤害。
 */
public class Hammer extends AoeToolItem {

    public static final float DURABILITY_MODIFIER = 2.5f;

    public Hammer() {
        super(new Item.Properties(),
                PartMaterialType.handle(ModToolParts.TOUGH_TOOL_ROD.get()),
                PartMaterialType.head(ModToolParts.HAMMER_HEAD.get()),
                PartMaterialType.head(ModToolParts.LARGE_PLATE.get()),
                PartMaterialType.head(ModToolParts.LARGE_PLATE.get()));
        addCategory(Category.WEAPON);
    }

    @Override
    public boolean isEffective(BlockState state) {
        return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public float miningSpeedModifier() {
        return 0.4f;
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
    public float modifyDamage(ItemStack stack, net.minecraft.world.entity.LivingEntity attacker,
                              net.minecraft.world.entity.Entity target, float damage) {
        // 对亡灵额外伤害（1:1 旧版：+3 ~ +6，1.21.1 用 EntityTypeTags 判定）
        if (target.getType().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
            damage += 3 + attacker.level().getRandom().nextInt(4);
        }
        return damage;
    }

    @Override
    public List<BlockPos> getAOEBlocks(ItemStack stack, Level world, Player player, BlockPos origin) {
        return calcAOEBlocks(stack, world, player, origin, 3, 3, 1);
    }

    @Override
    public int[] getRepairParts() {
        return new int[]{1, 2, 3};
    }

    @Override
    public float getRepairModifierForPart(int index) {
        return index == 1 ? DURABILITY_MODIFIER : DURABILITY_MODIFIER * 0.6f;
    }

    @Override
    protected ToolData buildTagData(List<Material> materials) {
        Material handle = materials.get(0);
        Material head = materials.get(1);
        Material plate1 = materials.get(2);
        Material plate2 = materials.get(3);

        com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handleStats = handle.getStatsOrUnknown(MaterialTypes.HANDLE);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats plate1Stats = plate1.getStatsOrUnknown(MaterialTypes.HEAD);
        com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats plate2Stats = plate2.getStatsOrUnknown(MaterialTypes.HEAD);

        // 主头双权重（1:1 旧版 data.head(head, head, plate1, plate2)）
        ToolData data = ToolData.empty()
                .head(new int[]{headStats.durability(), headStats.durability(), plate1Stats.durability(), plate2Stats.durability()},
                        new int[]{headStats.harvestLevel(), headStats.harvestLevel(), plate1Stats.harvestLevel(), plate2Stats.harvestLevel()},
                        new float[]{headStats.attack(), headStats.attack(), plate1Stats.attack(), plate2Stats.attack()},
                        new float[]{headStats.miningspeed(), headStats.miningspeed(), plate1Stats.miningspeed(), plate2Stats.miningspeed()})
                .handle(handleStats.modifier(), handleStats.durability());
        // 采掘等级始终由主头决定（1:1 旧版）
        data = data.withHarvestLevel(headStats.harvestLevel());
        // 耐久 ×2.5
        data = data.withDurability(Math.round(data.durability() * DURABILITY_MODIFIER));
        return data.withModifiers(TinkerToolItem.DEFAULT_MODIFIERS);
    }

    @Override
    public List<TagKey<Item>> getMineableTags() {
        return List.of(TConTags.MINEABLE_PICKAXE_ITEMS);
    }
}
