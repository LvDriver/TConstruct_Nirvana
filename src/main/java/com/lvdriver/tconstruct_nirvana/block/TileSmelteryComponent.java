package com.lvdriver.tconstruct_nirvana.block;

import com.lvdriver.tconstruct_nirvana.multiblock.IServantLogic;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 冶炼炉附属方块实体基类（1:1 移植自 Tinkers' Antique
 * {@code TileSmelteryComponent} / Mantle {@code MultiServantLogic} 简化版）。
 *
 * <p>记录所属主机（冶炼炉控制器）位置；主机结构检测时调用
 * {@link #overrideMaster} 绑定。主机失效时附属同时失效。</p>
 */
public class TileSmelteryComponent extends BlockEntity implements IServantLogic {

    public static final String TAG_MASTER = "master";

    protected BlockPos master;

    public TileSmelteryComponent(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean hasValidMaster() {
        return master != null;
    }

    @Override
    public BlockPos getMasterPosition() {
        return master;
    }

    @Override
    public void overrideMaster(BlockPos pos) {
        this.master = pos;
        setChanged();
        syncToClient();
    }

    @Override
    public void removeMaster() {
        this.master = null;
        setChanged();
        syncToClient();
    }

    /** 同步 master（BE 数据）到客户端：sendBlockUpdated 只带 blockstate 不带 BE
     *  数据，客户端 TileDrain.getSmeltery() 因 hasValidMaster()=false 恒返回 null，
     *  fluid capability 缺失 → 客户端行为与服务端不一致。 */
    public void syncToClient() {
        if (level != null && !level.isClientSide && level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            var packet = getUpdatePacket();
            if (packet != null) {
                serverLevel.getChunkSource().chunkMap
                        .getPlayers(new net.minecraft.world.level.ChunkPos(
                                worldPosition.getX() >> 4, worldPosition.getZ() >> 4), false)
                        .forEach(p -> p.connection.send(packet));
            }
        }
    }

    @Override
    public @Nullable net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void notifyMasterOfChange() {
        if (level != null && master != null && level.isLoaded(master)) {
            if (level.getBlockEntity(master) instanceof com.lvdriver.tconstruct_nirvana.multiblock.IMasterLogic masterLogic) {
                masterLogic.notifyChange(this, worldPosition);
            }
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (master != null) {
            tag.putLong(TAG_MASTER, master.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        master = tag.contains(TAG_MASTER) ? BlockPos.of(tag.getLong(TAG_MASTER)) : null;
    }
}
