package com.lvdriver.tconstruct_nirvana.client.model;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 工具多层模型的烘焙结果（1.21.1 BakedModel）。
 *
 * <p>自身不产出 quads：渲染走 {@link ToolItemOverrides#resolve} 按 ItemStack
 * 材料组合多层 quads（1:1 旧版 BakedToolModel + ItemOverrideList 模式）。
 * bake 时预取每层全部候选贴图，供 resolve 直接选用。</p>
 */
public class ToolBakedModel implements BakedModel {

    /** 单层烘焙数据：贴图引用 + 材料后缀变体。 */
    public record BakedLayer(int partIndex, TextureAtlasSprite base, @Nullable TextureAtlasSprite brokenBase,
                             Map<String, TextureAtlasSprite> variants, Map<String, TextureAtlasSprite> brokenVariants) {
    }

    private final List<BakedLayer> layers;
    private final ItemTransforms transforms;
    private final TextureAtlasSprite particle;
    private final TextureAtlasSprite missing;
    private final ToolItemOverrides overrides;

    private ToolBakedModel(List<BakedLayer> layers, ItemTransforms transforms,
                           TextureAtlasSprite particle, TextureAtlasSprite missing) {
        this.layers = layers;
        this.transforms = transforms;
        this.particle = particle;
        this.missing = missing;
        this.overrides = new ToolItemOverrides(this);
    }

    /** bake 入口：预取每层基础/损坏贴图 + 全部后缀变体。 */
    public static ToolBakedModel create(String toolName, ItemTransforms transforms,
                                        Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState) {
        TextureAtlasSprite missing = spriteGetter.apply(new Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS,
                net.minecraft.client.renderer.texture.MissingTextureAtlasSprite.getLocation()));
        List<BakedLayer> layers = new ArrayList<>();
        for (ToolModelData.Layer def : ToolModelData.LAYERS.get(toolName)) {
            ResourceLocation baseLoc = ToolUnbakedGeometry.modTexture(def.texture());
            TextureAtlasSprite base = spriteGetter.apply(new Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS, baseLoc));
            TextureAtlasSprite brokenBase = def.broken() != null
                    ? spriteGetter.apply(new Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS,
                    ToolUnbakedGeometry.modTexture(def.broken())))
                    : null;
            Map<String, TextureAtlasSprite> variants = new HashMap<>();
            Map<String, TextureAtlasSprite> brokenVariants = new HashMap<>();
            for (String suffix : ToolModelData.SUFFIXES) {
                variants.put(suffix, spriteGetter.apply(new Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS,
                        ToolUnbakedGeometry.modTexture(def.texture() + "_" + suffix))));
                if (def.broken() != null) {
                    brokenVariants.put(suffix, spriteGetter.apply(new Material(net.minecraft.client.renderer.texture.TextureAtlas.LOCATION_BLOCKS,
                            ToolUnbakedGeometry.modTexture(def.broken() + "_" + suffix))));
                }
            }
            layers.add(new BakedLayer(def.partIndex(), base, brokenBase, variants, brokenVariants));
        }
        TextureAtlasSprite particle = layers.isEmpty() ? missing : layers.get(0).base();
        return new ToolBakedModel(layers, transforms, particle, missing);
    }

    public List<BakedLayer> layers() {
        return layers;
    }

    public TextureAtlasSprite missing() {
        return missing;
    }

    @Override
    public List<net.minecraft.client.renderer.block.model.BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction direction, RandomSource random) {
        // 物品渲染一律走 getOverrides().resolve()，此处不产出 quads
        return List.of();
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
        return overrides;
    }
}
