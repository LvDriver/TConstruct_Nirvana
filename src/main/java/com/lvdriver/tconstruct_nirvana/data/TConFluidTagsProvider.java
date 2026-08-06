package com.lvdriver.tconstruct_nirvana.data;

import com.lvdriver.tconstruct_nirvana.TConstructNirvana;
import com.lvdriver.tconstruct_nirvana.fluid.ModFluids;
import com.lvdriver.tconstruct_nirvana.util.TConTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/**
 * 流体 Tag 生成（DataGen）。
 *
 * <p>全部本 mod 流体打 {@code c:<name>} common tag（如 c:molten_iron，NeoForge
 * 1.21.1 标准风格，文件在 data/c/tags/fluid/），供配方（合金/熔炼）、附属 mod
 * 与 JEI 使用（500mod 兼容）。</p>
 */
public class TConFluidTagsProvider extends FluidTagsProvider {

    public TConFluidTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (ModFluids.FluidEntry entry : ModFluids.FLUIDS_ALL) {
            tag(TConTags.fluidTag(entry.id().getPath())).add(entry.still().get());
        }
    }
}
