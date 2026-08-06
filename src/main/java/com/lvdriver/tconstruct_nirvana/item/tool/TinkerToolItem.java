package com.lvdriver.tconstruct_nirvana.item.tool;

import com.lvdriver.tconstruct_nirvana.api.event.ModifierTriggerEvent;
import com.lvdriver.tconstruct_nirvana.api.event.ToolBuildEvent;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.data.ModifierData;
import com.lvdriver.tconstruct_nirvana.data.ToolData;
import com.lvdriver.tconstruct_nirvana.item.part.PartMaterialType;
import com.lvdriver.tconstruct_nirvana.item.part.SharpeningKit;
import com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats;
import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import com.lvdriver.tconstruct_nirvana.modifier.Modifier;
import com.lvdriver.tconstruct_nirvana.modifier.Modifiers;
import com.lvdriver.tconstruct_nirvana.trait.Trait;
import com.lvdriver.tconstruct_nirvana.trait.Traits;
import com.lvdriver.tconstruct_nirvana.util.TConUtil;
import com.lvdriver.tconstruct_nirvana.util.ToolHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 匠魂工具基类（1:1 移植自 Tinkers' Antique {@code ToolCore}/{@code TinkersItem}，DataComponent 版）。
 *
 * <p>职责：<ul>
 * <li>组装：部件栈 → 材料列表 → {@link #buildTagData} 计算属性（1:1 公式见 {@link ToolData}）→
 * 写入 BASE_MATERIALS / TOOL_DATA(+ORIG) / MAX_DAMAGE / ATTRIBUTE_MODIFIERS / 挂载材料 Trait；</li>
 * <li>行为：攻击命中（{@link #hurtEnemy}）、方块破坏（{@link #mineBlock}）、物品 tick
 * （{@link #inventoryTick}）；挖掘速度与采掘判定经 NeoForge 事件（{@code TinkerToolEvents}）；</li>
 * <li>工具特有系数：{@link #miningSpeedModifier} / {@link #damagePotential} / {@link #attackSpeed} /
 * {@link #damageCutoff} / {@link #knockback}，数值由各工具 1:1 覆写。</li>
 * </ul></p>
 */
public abstract class TinkerToolItem extends Item {

    /** 默认免费强化槽数（1:1 旧版 ToolCore.DEFAULT_MODIFIERS）。 */
    public static final int DEFAULT_MODIFIERS = 3;

    /** 部件槽定义（顺序即组装槽位顺序）。 */
    protected final PartMaterialType[] requiredComponents;

    /** 工具分类（TOOL/HARVEST/WEAPON/LAUNCHER/AOE…）。 */
    protected final Set<Category> categories;

    protected TinkerToolItem(Item.Properties properties, PartMaterialType... requiredComponents) {
        super(properties.stacksTo(1));
        this.requiredComponents = requiredComponents;
        this.categories = new LinkedHashSet<>();
    }

    /* ---------- 部件与分类 ---------- */

    public List<PartMaterialType> getRequiredComponents() {
        return List.of(requiredComponents);
    }

    protected void addCategory(Category... categories) {
        for (Category category : categories) {
            this.categories.add(category);
        }
    }

    public boolean hasCategory(Category category) {
        return categories.contains(category);
    }

    /** 槽位部件/材料合法性校验（旧版 validComponent）。 */
    public boolean validComponent(int slot, ItemStack stack) {
        if (slot >= requiredComponents.length || slot < 0) {
            return false;
        }
        return requiredComponents[slot].isValid(stack);
    }

    /* ---------- 组装（1:1 旧版 buildItemFromStacks / buildItem / buildItemNBT） ---------- */

    /**
     * 由部件栈组装工具：槽位数须精确匹配且逐槽合法，任一不符返回空栈。
     */
    public ItemStack buildItemFromStacks(List<ItemStack> stacks) {
        long itemCount = stacks.stream().filter(stack -> !stack.isEmpty()).count();
        if (itemCount != requiredComponents.length) {
            return ItemStack.EMPTY;
        }

        List<Material> materials = new ArrayList<>((int) itemCount);
        for (int i = 0; i < itemCount; i++) {
            if (!validComponent(i, stacks.get(i))) {
                return ItemStack.EMPTY;
            }
            materials.add(getMaterialFromStack(stacks.get(i)));
        }
        return buildItem(materials);
    }

    /** 由材料列表组装工具（数据完整版，供创造标签页/配方/测试使用）。 */
    public ItemStack buildItem(List<Material> materials) {
        ItemStack tool = new ItemStack(this);
        // 材料列表（旧版 BASE_MATERIALS）
        tool.set(ModDataComponents.BASE_MATERIALS, materials.stream().map(m -> m.identifier).toList());
        // 基础属性（旧版 TOOL_DATA + TOOL_DATA_ORIG）
        ToolData toolData = buildTagData(materials);
        // API: 工具组装事件（附属可修改基础属性/取消组装，NeoForge.EVENT_BUS）
        ToolBuildEvent buildEvent = new ToolBuildEvent(this, tool, materials, toolData);
        if (NeoForge.EVENT_BUS.post(buildEvent).isCanceled()) {
            return ItemStack.EMPTY;
        }
        toolData = buildEvent.getToolData();
        ToolHelper.setToolData(tool, toolData);
        tool.set(ModDataComponents.TOOL_DATA_ORIG, toolData);
        tool.set(ModDataComponents.BASE_MODIFIERS, List.of());
        tool.set(ModDataComponents.MODIFIERS, List.of());
        tool.set(ModDataComponents.traitsType(), List.of());
        // 材料特质（旧版 addMaterialTraits）
        addMaterialTraits(tool, materials);
        // 攻击/攻速属性（1.21.1 ATTRIBUTE_MODIFIERS 组件，替代旧版 getAttributeModifiers）
        ToolHelper.updateAttributes(tool);
        return tool;
    }

    /** 从部件栈读取材料（部件材料 DataComponent，旧版 TinkerUtil.getMaterialFromStack）。 */
    private static Material getMaterialFromStack(ItemStack stack) {
        String id = stack.getOrDefault(ModDataComponents.PART_MATERIAL, null);
        return id != null ? Material.getByIdentifier(id) : Material.UNKNOWN;
    }

    /**
     * 计算工具基础属性（1:1 公式见 {@link ToolData}：head → extra → handle）。
     * 子类覆写实现特殊合成（Hammer 主头双权重等）。
     */
    protected abstract ToolData buildTagData(List<Material> materials);

    /** 默认 3 槽合成（handle/head/extra），1:1 旧版 buildDefaultTag。 */
    protected ToolData buildDefaultTag(List<Material> materials) {
        ToolData data = ToolData.empty();
        if (materials.size() >= 2) {
            Material handle = materials.get(0);
            Material head = materials.get(1);

            com.lvdriver.tconstruct_nirvana.material.HandleMaterialStats handleStats = handle.getStatsOrUnknown("handle");
            com.lvdriver.tconstruct_nirvana.material.HeadMaterialStats headStats = head.getStatsOrUnknown("head");

            data = data.head(headStats.durability(), headStats.harvestLevel(), headStats.attack(), headStats.miningspeed());

            if (materials.size() >= 3) {
                com.lvdriver.tconstruct_nirvana.material.ExtraMaterialStats extraStats = materials.get(2).getStatsOrUnknown("extra");
                data = data.extra(extraStats.extraDurability());
            }

            data = data.handle(handleStats.modifier(), handleStats.durability());
        }
        return data.withModifiers(DEFAULT_MODIFIERS);
    }

    /** 挂载材料特质（1:1 旧版 addMaterialTraits + ToolBuilder.addTrait）。 */
    protected void addMaterialTraits(ItemStack stack, List<Material> materials) {
        Set<String> traitIds = new LinkedHashSet<>();
        int size = Math.min(requiredComponents.length, materials.size());
        for (int i = 0; i < size; i++) {
            PartMaterialType pmt = requiredComponents[i];
            Material material = materials.get(i);
            for (String statType : pmt.getNeededTypes()) {
                for (String traitId : material.getAllTraitsForStats(statType)) {
                    traitIds.add(traitId);
                }
            }
        }
        for (String traitId : traitIds) {
            Trait trait = Traits.get(traitId);
            if (trait != null) {
                trait.applyEffect(stack, new ModifierData(traitId, 1, trait.color, ""));
            }
        }
    }

    /** 材料组合是否合法（所有槽位材料具备所需属性，旧版 hasValidMaterials）。 */
    public boolean hasValidMaterials(ItemStack stack) {
        List<Material> materials = ToolHelper.getMaterials(stack);
        if (materials.size() != requiredComponents.length) {
            return false;
        }
        for (int i = 0; i < materials.size(); i++) {
            if (!requiredComponents[i].isValidMaterial(materials.get(i))) {
                return false;
            }
        }
        return true;
    }

    /* ---------- 挖掘 ---------- */

    @Override
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return ToolHelper.calcDigSpeed(stack, state);
    }

    /** 工具是否天然有效于该方块（子类覆写，如镐的石头/剑的蜘蛛网）。 */
    public boolean isEffective(BlockState state) {
        return false;
    }

    /** 工具采掘等级（DataComponent，旧版 ToolHelper.getHarvestLevelStat）。 */
    public int getHarvestLevelStat(ItemStack stack) {
        return ToolHelper.getHarvestLevelStat(stack);
    }

    /* ---------- 攻击命中（1:1 旧版 hitEntity/afterHit/耐久损耗，1.21.1 走原版攻击流程） ---------- */

    /** 伤害修正钩子（LivingIncomingDamageEvent 调用，子类覆写特殊攻击加成）。 */
    public float modifyDamage(ItemStack stack, LivingEntity attacker, Entity target, float damage) {
        return damage;
    }

    /** 实际结算伤害（保留给子类覆写特殊结算，如细剑混合伤害）。 */
    public boolean dealDamage(ItemStack stack, LivingEntity attacker, Entity target, float damage) {
        return target.hurt(attacker instanceof Player p
                ? p.damageSources().playerAttack(p)
                : attacker.damageSources().mobAttack(attacker), damage);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (ToolHelper.isBroken(stack)) {
            return false;
        }
        // 击打耐久损耗（1:1 旧版：max(1, damage/10)，非武器类 ×2）
        // afterHit 钩子由 LivingDamageEvent.Post 驱动（可拿到实际结算伤害，necrotic 吸血依赖）
        if (attacker instanceof Player player) {
            if (!player.getAbilities().instabuild) {
                reduceDurabilityOnHit(stack, player, 1f);
            }
            player.causeFoodExhaustion(0.3f);
        } else {
            reduceDurabilityOnHit(stack, null, 1f);
        }
        // API: 修饰符触发事件（攻击命中，只读监听）
        NeoForge.EVENT_BUS.post(new ModifierTriggerEvent(
                ModifierTriggerEvent.Trigger.ATTACK, stack, attacker, ToolHelper.getActiveModifiers(stack)));
        return true;
    }

    /** 击打耐久损耗（1:1 旧版 reduceDurabilityOnHit 简化版：按工具实际伤害折算）。 */
    public void reduceDurabilityOnHit(ItemStack stack, Player player, float damage) {
        float actual = ToolHelper.getActualAttack(stack);
        damage = Math.max(1f, Math.max(damage, actual) / 10f);
        if (!hasCategory(Category.WEAPON)) {
            damage *= 2;
        }
        ToolHelper.damageTool(stack, (int) damage, player);
    }

    /* ---------- 方块破坏（1.21.1 mineBlock 替代旧版 onBlockDestroyed） ---------- */

    @Override
    public boolean mineBlock(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity) {
        if (ToolHelper.isBroken(stack)) {
            return false;
        }
        boolean effective = isEffective(state) || ToolHelper.isToolEffective(stack, state);
        int damage = effective ? 1 : 2;
        afterBlockBreak(stack, world, state, pos, entity, damage, effective);
        return true;
    }

    /** 破坏后处理（钩子 + 耐久损耗，1:1 旧版 ToolCore.afterBlockBreak）。 */
    public void afterBlockBreak(ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity player, int damage, boolean wasEffective) {
        for (com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
            modifier.afterBlockBreak(stack, world, state, pos, player, wasEffective);
        }
        ToolHelper.damageTool(stack, damage, player);
        // API: 修饰符触发事件（方块破坏后，只读监听）
        NeoForge.EVENT_BUS.post(new ModifierTriggerEvent(
                ModifierTriggerEvent.Trigger.BLOCK_BREAK, stack, player, ToolHelper.getActiveModifiers(stack)));
    }

    /* ---------- 物品 tick（1.21.1 inventoryTick 替代旧版 onUpdate） ---------- */

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int itemSlot, boolean isSelected) {
        boolean selectedOrOffhand = isSelected
                || (entity instanceof Player player && player.getOffhandItem() == stack);
        for (com.lvdriver.tconstruct_nirvana.modifier.Modifier modifier : ToolHelper.getActiveModifiers(stack)) {
            modifier.onUpdate(stack, world, entity, itemSlot, selectedOrOffhand);
        }
    }

    /* ---------- 工具特有系数（子类 1:1 覆写） ---------- */

    /** 实际挖掘速度倍率（旧版 miningSpeedModifier，Hammer 0.4 / Sword 0.5…）。 */
    public float miningSpeedModifier() {
        return 1f;
    }

    /** 攻击倍率（旧版 damagePotential）。 */
    public abstract float damagePotential();

    /** 伤害削减阈值（旧版 damageCutoff，默认 15）。 */
    public float damageCutoff() {
        return 15.0f;
    }

    /** 攻击速度（旧版 attackSpeed，4 = 原版标准）。 */
    public abstract double attackSpeed();

    /** 击退系数（旧版 knockback，默认 1.0）。 */
    public float knockback() {
        return 1.0f;
    }

    /* ---------- 显示 ---------- */

    @Override
    public Component getName(ItemStack stack) {
        Set<Material> nameMaterials = new LinkedHashSet<>();
        List<Material> materials = ToolHelper.getMaterials(stack);
        for (int index : getRepairParts()) {
            if (index < materials.size()) {
                nameMaterials.add(materials.get(index));
            }
        }
        return Material.getCombinedItemName(super.getName(stack), nameMaterials);
    }

    /** 修复用部件索引（旧版 getRepairParts，默认 index 1 = 头部）。 */
    public int[] getRepairParts() {
        return new int[]{1};
    }

    /** 修复量倍率（旧版 getRepairModifierForPart）。 */
    public float getRepairModifierForPart(int index) {
        return 1f;
    }

    /* ---------- 修复（1:1 旧版 TinkersItem.repair，工作台 RepairRecipe 调用） ---------- */

    /**
     * 用磨刀石（SharpeningKit）修复工具（1:1 旧版 repair 简化版）。
     * 每个修复部件（{@link #getRepairParts()}）的材料需要一个同材料磨刀石；
     * 输入必须全部被消耗，否则视为无效配方（防白嫖）。
     *
     * @return 修复后的工具栈；无效输入返回 {@link ItemStack#EMPTY}
     */
    public ItemStack repair(ItemStack repairable, List<ItemStack> repairItems) {
        if (!repairable.isDamaged() && !ToolHelper.isBroken(repairable)) {
            // 无损伤且未损坏 —— 无需修复
            return ItemStack.EMPTY;
        }
        List<Material> materials = ToolHelper.getMaterials(repairable);
        if (materials.isEmpty()) {
            return ItemStack.EMPTY;
        }

        // 第一遍（模拟）：检查每个修复部件的材料是否有对应磨刀石，且输入全部可消耗
        List<ItemStack> items = new ArrayList<>();
        for (ItemStack s : repairItems) {
            if (!s.isEmpty()) {
                items.add(s.copy());
            }
        }
        boolean foundMatch = false;
        for (int index : getRepairParts()) {
            if (index >= materials.size()) {
                continue;
            }
            Material material = materials.get(index);
            for (int i = 0; i < items.size(); i++) {
                ItemStack s = items.get(i);
                if (s.isEmpty() || !(s.getItem() instanceof SharpeningKit kit)) {
                    continue;
                }
                if (kit.getMaterial(s) == material) {
                    items.set(i, ItemStack.EMPTY);
                    foundMatch = true;
                    break;
                }
            }
        }
        if (!foundMatch) {
            return ItemStack.EMPTY;
        }
        // 所有输入必须被消耗
        for (ItemStack s : items) {
            if (!s.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        // 第二遍（真实修复）：反复消耗磨刀石直到耐久满或材料用完
        ItemStack item = repairable.copy();
        List<ItemStack> realItems = new ArrayList<>(repairItems);
        do {
            int amount = calculateRepairAmount(materials, realItems);
            if (amount <= 0) {
                break;
            }
            ToolHelper.repairTool(item, calculateRepair(item, amount));
            item.set(ModDataComponents.REPAIR_COUNT, item.getOrDefault(ModDataComponents.REPAIR_COUNT, 0) + 1);
        } while (item.getDamageValue() > 0);

        return item;
    }

    /**
     * 计算修复量基数（1:1 旧版 calculateRepairAmount）：
     * 每个去重材料：head 耐久 × 磨刀石数量 × 部件倍率 / 144；
     * 多材料奖励 ×(1 + (材料数-1)/9)。消耗列表中的磨刀石。
     */
    protected int calculateRepairAmount(List<Material> materials, List<ItemStack> repairItems) {
        java.util.Set<Material> materialsMatched = new java.util.HashSet<>();
        float durability = 0f;
        for (int index : getRepairParts()) {
            if (index >= materials.size()) {
                continue;
            }
            Material material = materials.get(index);
            if (materialsMatched.contains(material)) {
                continue;
            }
            // 统计该材料磨刀石数量
            int count = 0;
            for (ItemStack s : repairItems) {
                if (!s.isEmpty() && s.getItem() instanceof SharpeningKit kit && kit.getMaterial(s) == material) {
                    count++;
                }
            }
            if (count > 0) {
                HeadMaterialStats stats = material.getStatsOrUnknown(MaterialTypes.HEAD);
                materialsMatched.add(material);
                durability += (float) stats.durability() * count * getRepairModifierForPart(index) / 144f;
            }
        }
        // 移除已匹配材料的磨刀石（供 do-while 循环下一轮）
        for (int i = 0; i < repairItems.size(); i++) {
            ItemStack s = repairItems.get(i);
            if (!s.isEmpty() && s.getItem() instanceof SharpeningKit kit && materialsMatched.contains(kit.getMaterial(s))) {
                repairItems.set(i, ItemStack.EMPTY);
            }
        }

        durability *= 1f + ((float) materialsMatched.size() - 1) / 9f;
        return (int) durability;
    }

    /**
     * 修复量换算（1:1 旧版 calculateRepair）：
     * 基数 × min(10, 当前/原始耐久系数)；下限 当前耐久/64；
     * 修饰符数量惩罚（1/2/3+ → 0.95/0.9/0.85）；修复次数收益递减（下限 0.5）。
     */
    protected int calculateRepair(ItemStack tool, int amount) {
        float origDur = ToolHelper.getOriginalToolData(tool).durability();
        float actualDur = ToolHelper.getDurabilityStat(tool);
        // 计算改变总耐久的修饰符（如钻石）不应惩罚玩家：系数 = 当前/原始
        float durabilityFactor = actualDur / origDur;
        float increase = amount * Math.min(10f, durabilityFactor);
        increase = Math.max(increase, actualDur / 64f);

        int modifiersUsed = ToolHelper.getBaseModifiers(tool).size();
        float mods = 1f;
        if (modifiersUsed == 1) {
            mods = 0.95f;
        } else if (modifiersUsed == 2) {
            mods = 0.9f;
        } else if (modifiersUsed >= 3) {
            mods = 0.85f;
        }
        increase *= mods;

        int repair = tool.getOrDefault(ModDataComponents.REPAIR_COUNT, 0);
        // 1:1 旧版：repair/2 为整数除法
        float repairDiminishingReturns = (100 - repair / 2) / 100f;
        if (repairDiminishingReturns < 0.5f) {
            repairDiminishingReturns = 0.5f;
        }
        increase *= repairDiminishingReturns;

        return (int) Math.ceil(increase);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        if (ToolHelper.isBroken(stack)) {
            tooltip.add(Component.translatable("tooltip.tool.broken").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD));
        }
        ToolData data = ToolHelper.getToolData(stack);
        tooltip.add(Component.translatable("stat.durability", data.durability()));
        if (hasCategory(Category.HARVEST)) {
            tooltip.add(Component.translatable("stat.mininglevel", data.harvestLevel()));
            tooltip.add(Component.translatable("stat.miningspeed", data.speed()));
        }
        tooltip.add(Component.translatable("stat.attack", ToolHelper.getActualAttack(stack)));
        int free = ToolHelper.getFreeModifiers(stack);
        if (free > 0) {
            tooltip.add(Component.translatable("stat.free_modifiers", free));
        }
        // 修饰符/特质列表
        for (ModifierData modifierData : ToolHelper.getModifierData(stack)) {
            Modifier modifier = Modifiers.get(modifierData.identifier());
            if (modifier != null) {
                String name = modifier.getLocalizedName();
                if (modifierData.level() > 1) {
                    name += " " + TConUtil.toRoman(modifierData.level());
                }
                tooltip.add(Component.literal(name).withColor(modifierData.color() & 0xFFFFFF));
            }
        }
    }

    /** 工具标识（注册名 path，1:1 旧版 getIdentifier；未注册时返回类名小写）。 */
    public String getIdentifier() {
        var key = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this);
        return key != null && !key.equals(net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(net.minecraft.world.item.Items.AIR))
                ? key.getPath()
                : getClass().getSimpleName().toLowerCase(java.util.Locale.ROOT);
    }

    /** 该工具可采掘的 mineable 物品 tag 列表（默认空，子类覆写；DataGen 据此打 tag）。 */
    public List<net.minecraft.tags.TagKey<net.minecraft.world.item.Item>> getMineableTags() {
        return List.of();
    }
}
