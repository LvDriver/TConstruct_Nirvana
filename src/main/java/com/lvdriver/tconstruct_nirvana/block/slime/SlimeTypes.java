package com.lvdriver.tconstruct_nirvana.block.slime;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.Locale;

/**
 * 史莱姆方块共享枚举与状态属性（1:1 移植自 Tinkers' Antique 的
 * {@code BlockSlimeDirt.DirtType} / {@code BlockSlimeGrass.FoliageType} /
 * {@code BlockSlime.SlimeType}）。
 *
 * <p>迁移差异：旧版 slime_grass 的 DirtType 含 VANILLA（普通泥土上长草），
 * 本移植简化去除（史莱姆草皮只在 slime_dirt 上生成，岛生成 1:1 不受影响）；
 * 1.21.1 无 {@code SoundType.SLIME}，用原版史莱姆方块音效自定义。</p>
 */
public final class SlimeTypes {

    /** 史莱姆方块音效（1:1 旧版 SoundType.SLIME；1.21.1 需自定义）。 */
    public static final SoundType SLIME_SOUND = new SoundType(1.0F, 1.0F,
            SoundEvents.SLIME_BLOCK_BREAK, SoundEvents.SLIME_BLOCK_STEP,
            SoundEvents.SLIME_BLOCK_PLACE, SoundEvents.SLIME_BLOCK_HIT,
            SoundEvents.SLIME_BLOCK_FALL);

    /** 泥土变体（旧版 BlockSlimeDirt.DirtType）。 */
    public enum DirtType implements StringRepresentable {
        GREEN, BLUE, PURPLE, MAGMA;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return getSerializedName();
        }
    }

    /** 草/叶/树苗变体（旧版 BlockSlimeGrass.FoliageType）。 */
    public enum FoliageType implements StringRepresentable {
        BLUE, PURPLE, ORANGE;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return getSerializedName();
        }
    }

    /** 凝结石块变体（旧版 BlockSlime.SlimeType，去除 PINK 彩蛋）。 */
    public enum SlimeType implements StringRepresentable {
        GREEN, BLUE, PURPLE, BLOOD, MAGMA;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        @Override
        public String toString() {
            return getSerializedName();
        }
    }

    public static final EnumProperty<DirtType> DIRT_TYPE = EnumProperty.create("type", DirtType.class);
    public static final EnumProperty<FoliageType> FOLIAGE_TYPE = EnumProperty.create("foliage", FoliageType.class);
    public static final EnumProperty<SlimeType> SLIME_TYPE = EnumProperty.create("type", SlimeType.class);

    private SlimeTypes() {
    }
}
