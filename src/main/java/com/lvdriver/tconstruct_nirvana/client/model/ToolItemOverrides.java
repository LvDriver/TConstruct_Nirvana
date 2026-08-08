package com.lvdriver.tconstruct_nirvana.client.model;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lvdriver.tconstruct_nirvana.data.ModDataComponents;
import com.lvdriver.tconstruct_nirvana.material.Material;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IQuadTransformer;
import net.neoforged.neoforge.client.model.QuadTransformers;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 工具物品覆写（1.21.1 ItemOverrides，1:1 旧版 BakedToolModel.ToolItemOverrideList）。
 *
 * <p>按 ItemStack 的 {@link ModDataComponents#BASE_MATERIALS}（部件材料列表）与
 * {@link ModDataComponents#BROKEN}（损坏标志）组合多层 quads：每层取对应槽位
 * 材料颜色顶点着色；材料有渲染后缀且存在 {@code <part>_<suffix>} 变体贴图时
 * 直接用变体贴图（原色）；损坏时替换 broken 层贴图。结果按材料组合缓存。</p>
 */
public class ToolItemOverrides extends ItemOverrides {

    /** 缓存 key（材料列表 + 损坏标志）。 */
    private record Key(List<String> materials, boolean broken) {
    }

    private final ToolBakedModel baseModel;
    private final Cache<Key, BakedModel> cache = CacheBuilder.newBuilder()
            .maximumSize(2000)
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build();

    public ToolItemOverrides(ToolBakedModel baseModel) {
        this.baseModel = baseModel;
    }

    @Override
    public BakedModel resolve(BakedModel model, ItemStack stack, @Nullable ClientLevel level,
                              @Nullable LivingEntity entity, int seed) {
        List<String> materials = stack.getOrDefault(ModDataComponents.BASE_MATERIALS, List.of());
        boolean broken = stack.getOrDefault(ModDataComponents.BROKEN, false);
        Key key = new Key(materials, broken);
        BakedModel cached = cache.getIfPresent(key);
        if (cached != null) {
            return cached;
        }
        BakedModel built = buildModel(materials, broken);
        cache.put(key, built);
        return built;
    }

    /** 按材料列表组合多层 quads（材料越界时兜底复用最后一个，1:1 shuriken 单材料多层）。 */
    private BakedModel buildModel(List<String> materials, boolean broken) {
        List<BakedQuad> quads = new ArrayList<>();
        TextureAtlasSprite particle = baseModel.getParticleIcon();
        int layerIndex = 0;
        for (ToolBakedModel.BakedLayer layer : baseModel.layers()) {
            if (materials.isEmpty()) {
                break;
            }
            // 材料（越界兜底最后一个）
            int matIndex = Math.min(layer.partIndex(), materials.size() - 1);
            Material mat = Material.getByIdentifier(materials.get(matIndex));

            boolean useBroken = broken && layer.brokenBase() != null && layer.brokenBase() != baseModel.missing();
            TextureAtlasSprite sprite = useBroken ? layer.brokenBase() : layer.base();

            // 材料后缀变体贴图（如 flint → head_contrast、bone → handle_bone_base）
            String suffix = mat != null ? ToolModelData.MATERIAL_SUFFIX.get(mat.identifier) : null;
            if (suffix != null) {
                TextureAtlasSprite variant = (useBroken ? layer.brokenVariants() : layer.variants()).get(suffix);
                if (variant != null && variant != baseModel.missing()) {
                    sprite = variant;
                }
            }

            if (sprite == null || sprite == baseModel.missing()) {
                layerIndex++;
                continue;
            }

            // 生成该层 quads（item layer 样式，层索引控制 z 偏移）
            TextureAtlasSprite usedSprite = sprite;
            List<BakedQuad> layerQuads = UnbakedGeometryHelper.bakeElements(
                    UnbakedGeometryHelper.createUnbakedItemElements(layerIndex, sprite),
                    tex -> usedSprite, BlockModelRotation.X0_Y0);

            // 材料颜色顶点着色（有变体贴图时保持原色）
            if (mat != null && suffix == null) {
                IQuadTransformer color = QuadTransformers.applyingColor(mat.materialTextColor);
                for (BakedQuad quad : layerQuads) {
                    color.processInPlace(quad);
                }
            }
            quads.addAll(layerQuads);
            layerIndex++;
        }
        return new ToolBakedItemModel(quads, baseModel.getTransforms(), particle);
    }

    /**
     * 组合结果模型（1:1 旧版 BakedSimpleItem）：持有已生成的 quads 与显示变换。
     */
    static class ToolBakedItemModel implements BakedModel {

        private final List<BakedQuad> quads;
        private final ItemTransforms transforms;
        private final TextureAtlasSprite particle;

        ToolBakedItemModel(List<BakedQuad> quads, ItemTransforms transforms, TextureAtlasSprite particle) {
            this.quads = quads;
            this.transforms = transforms;
            this.particle = particle;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
            return quads;
        }

        @Override
        public boolean useAmbientOcclusion() {
            return false;
        }

        @Override
        public boolean isGui3d() {
            return false;
        }

        @Override
        public boolean usesBlockLight() {
            return false;
        }

        @Override
        public boolean isCustomRenderer() {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return particle;
        }

        @Override
        public ItemTransforms getTransforms() {
            return transforms;
        }

        @Override
        public ItemOverrides getOverrides() {
            return ItemOverrides.EMPTY;
        }
    }
}
