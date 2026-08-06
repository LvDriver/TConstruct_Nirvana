package com.lvdriver.tconstruct_nirvana.item.part;

import com.lvdriver.tconstruct_nirvana.material.Material;
import com.lvdriver.tconstruct_nirvana.material.MaterialTypes;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Set;

/**
 * 部件-材料类型绑定（1:1 移植自 Tinkers' Antique {@code PartMaterialType}）。
 *
 * <p>描述"某工具槽位需要哪个部件 + 该部件材料须具备哪些属性类型"：
 * 部件任一匹配即可（{@link #neededParts}），材料须具备全部所需属性类型
 * （{@link #neededTypes}）。工具组装会话（ToolBuilder）将用它校验部件。</p>
 */
public class PartMaterialType {

    /** 任一匹配即可的部件。 */
    private final Set<ToolPart> neededParts;
    /** 材料须全部具备的属性类型。 */
    private final List<String> neededTypes;

    public PartMaterialType(ToolPart part, String... statIDs) {
        this.neededParts = Set.of(part);
        this.neededTypes = List.of(statIDs);
    }

    /** 校验物品是否为合法部件且材料可用。 */
    public boolean isValid(ItemStack stack) {
        if (stack.getItem() instanceof ToolPart toolPart) {
            return isValid(toolPart, toolPart.getMaterial(stack));
        }
        return false;
    }

    public boolean isValid(ToolPart part, Material material) {
        return isValidItem(part) && isValidMaterial(material);
    }

    public boolean isValidItem(ToolPart part) {
        return neededParts.contains(part);
    }

    public boolean isValidMaterial(Material material) {
        for (String type : neededTypes) {
            if (!material.hasStats(type)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 该槽位是否使用指定属性类型。
     * 注意：不表示具备该属性的材料一定可用（可能还需其他属性）。
     */
    public boolean usesStat(String statID) {
        return neededTypes.contains(statID);
    }

    /** 可能的部件集合。 */
    public Set<ToolPart> getPossibleParts() {
        return neededParts;
    }

    /* ---------- 便捷工厂（1:1 旧版） ---------- */

    public static PartMaterialType head(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.HEAD);
    }

    public static PartMaterialType handle(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.HANDLE);
    }

    public static PartMaterialType extra(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.EXTRA);
    }

    public static PartMaterialType bow(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.BOW, MaterialTypes.HEAD);
    }

    public static PartMaterialType bowstring(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.BOWSTRING);
    }

    public static PartMaterialType arrowHead(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.HEAD, MaterialTypes.PROJECTILE);
    }

    public static PartMaterialType arrowShaft(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.SHAFT);
    }

    public static PartMaterialType fletching(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.FLETCHING);
    }

    public static PartMaterialType crossbow(ToolPart part) {
        return new PartMaterialType(part, MaterialTypes.HANDLE, MaterialTypes.EXTRA);
    }
}
