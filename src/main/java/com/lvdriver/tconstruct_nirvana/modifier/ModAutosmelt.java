package com.lvdriver.tconstruct_nirvana.modifier;

import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.List;
import java.util.Optional;

/**
 * 自动冶炼（1:1 移植自 Tinkers' Antique {@code ModAutosmelt} 的简化版）。
 * 单次应用；方块掉落改为熔炼产物（RecipeManager 查询烧炼配方）。
 */
public class ModAutosmelt extends Modifier {

    public ModAutosmelt() {
        super("mod_autosmelt");
        aspects.add(new ModifierAspect.SingleAspect(identifier));
        aspects.add(new ModifierAspect.DataAspect(identifier, 0xff5500));
        aspects.add(new ModifierAspect.FreeModifierAspect(1));
        // 1:1 旧版 ModAutosmelt.canApplyTogether：拒 trait autosmelt、silktouch
        // （blasting 为掉落转化修饰符，双向互斥，review 会话11 补充）
        aspects.add(new ModifierAspect.ExclusiveAspect("autosmelt", "silktouch", "blasting"));
    }

    @Override
    public void blockHarvestDrops(ItemStack tool, BlockDropsEvent event) {
        if (!ToolHelper.hasModifier(tool, identifier)) {
            return;
        }
        if (!(event.getLevel() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        // 按掉落物匹配烧炼配方，命中则替换为熔炼产物（1:1 旧版 autosmelt 行为）
        var smeltingRecipes = serverLevel.getRecipeManager().getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMELTING);
        for (net.minecraft.world.entity.item.ItemEntity entity : List.copyOf(event.getDrops())) {
            ItemStack stack = entity.getItem();
            for (var holder : smeltingRecipes) {
                if (holder.value() instanceof net.minecraft.world.item.crafting.SmeltingRecipe smelting
                        && smelting.getIngredients().stream().anyMatch(ing -> ing.test(stack))) {
                    ItemStack result = smelting.getResultItem(serverLevel.registryAccess()).copy();
                    result.setCount(stack.getCount());
                    entity.setItem(result);
                    break;
                }
            }
        }
    }
}
