package com.lvdriver.tconstruct_nirvana.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

/**
 * 沟槽方块实体（1:1 移植自 Tinkers' Antique {@code TileChannel}）。
 *
 * <p>流体分配方块：侧向连接（IN 接收 / OUT 输出 / NONE 断开）+ 向下输出
 * （红石控制，1:1 {@code connectedDown}）。容量 36mb（1:1 旧版 ChannelTank(36)），
 * 每 tick 优先向下输出，否则按输出口数量均分侧向流速（各最多 6mb）；
 * {@code locked} 机制防止同 tick 刚注入的液体被立即抽出（1:1 旧版 ChannelTank.locked）。</p>
 *
 * <p>1.21.1 适配：连接状态写入 BlockState 属性（无 getActualState 机制，模型由属性驱动）；
 * 客户端流动动画简化（按连接 + 液量渲染，不单独同步 isFlowing）。</p>
 */
public class TileChannel extends BlockEntity {

    /** 沟槽容量（1:1 旧版 ChannelTank(36)）。 */
    public static final int CAPACITY = 36;

    /** 每侧每 tick 最大传输量（1:1 旧版 Config.liquidTransferRate，同龙头）。 */
    public static final int LIQUID_TRANSFER = TileFaucet.LIQUID_TRANSFER;

    /** 侧向连接状态（1:1 旧版 TileChannel.ChannelConnection）。 */
    public enum ChannelConnection {
        NONE,
        IN,
        OUT;

        public ChannelConnection getOpposite() {
            return switch (this) {
                case IN -> OUT;
                case OUT -> IN;
                default -> NONE;
            };
        }

        /** 循环切换（1:1 旧版 getNext(reverse)）。 */
        public ChannelConnection getNext(boolean reverse) {
            return switch (this) {
                case NONE -> reverse ? IN : OUT;
                case IN -> reverse ? OUT : NONE;
                case OUT -> reverse ? NONE : IN;
            };
        }

        public static ChannelConnection fromIndex(int index) {
            return index < 0 || index >= values().length ? NONE : values()[index];
        }
    }

    /** 四个水平方向的连接（索引 = Direction.get2DDataValue）。 */
    private final ChannelConnection[] connections = new ChannelConnection[4];
    private boolean connectedDown;
    private boolean wasPowered;
    private int numOutputs;
    /** 中央液罐。 */
    private final FluidTank tank = new FluidTank(CAPACITY);
    /** 本 tick 已注入量（防止同 tick 抽出，1:1 旧版 ChannelTank.locked）。 */
    private int locked;

    public TileChannel(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /* ---------- 流动逻辑（1:1 旧版 update） ---------- */

    public static void tick(Level level, BlockPos pos, BlockState state, TileChannel channel) {
        if (level.isClientSide) {
            return;
        }
        FluidStack fluid = channel.tank.getFluid();
        if (!fluid.isEmpty()) {
            // 向下优先（1:1 旧版）
            boolean hasFlown = false;
            if (channel.isConnectedDown()) {
                hasFlown = channel.trySide(Direction.DOWN, LIQUID_TRANSFER);
            }
            // 侧向均分（1:1 旧版：不偏向任一方向）
            if (!hasFlown && channel.numOutputs > 0) {
                int flowRate = Math.max(1, Math.min(channel.usableFluid() / channel.numOutputs, LIQUID_TRANSFER));
                for (Direction side : Direction.Plane.HORIZONTAL) {
                    channel.trySide(side, flowRate);
                }
            }
        }
        // 解锁（1:1 旧版 tank.freeFluid）
        channel.locked = 0;
    }

    protected boolean trySide(Direction side, int flowRate) {
        if (tank.getFluid().isEmpty() || getConnection(side) != ChannelConnection.OUT) {
            return false;
        }
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
        // 沟槽间快速路径（1:1 旧版：对方须为 IN）；直连 tank 需手动补 locked（security_review）
        if (neighbor instanceof TileChannel other) {
            if (other.getConnection(side.getOpposite()) == ChannelConnection.IN) {
                int before = other.tank.getFluidAmount();
                boolean flown = fill(side, other.tank, flowRate);
                other.locked += other.tank.getFluidAmount() - before;
                return flown;
            }
        } else if (neighbor != null) {
            IFluidHandler toFill = level.getCapability(
                    net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                    worldPosition.relative(side), side.getOpposite());
            if (toFill != null) {
                return fill(side, toFill, flowRate);
            }
        }
        return false;
    }

    protected boolean fill(Direction side, IFluidHandler handler, int amount) {
        FluidStack fluid = getUsableFluid();
        if (fluid.isEmpty()) {
            return false;
        }
        int toFill = Math.min(fluid.getAmount(), amount);
        int filled = toFill == 0 ? 0 : handler.fill(fluid.copyWithAmount(toFill), IFluidHandler.FluidAction.SIMULATE);
        if (filled > 0) {
            handler.fill(fluid.copyWithAmount(filled), IFluidHandler.FluidAction.EXECUTE);
            tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
            return true;
        }
        return false;
    }

    /** 可用液体（扣除本 tick 已注入量）。 */
    public FluidStack getUsableFluid() {
        if (tank.getFluid().isEmpty()) {
            return FluidStack.EMPTY;
        }
        return tank.getFluid().copyWithAmount(Math.max(0, tank.getFluidAmount() - locked));
    }

    public int usableFluid() {
        return Math.max(0, tank.getFluidAmount() - locked);
    }

    /* ---------- 连接管理 ---------- */

    public ChannelConnection getConnection(Direction side) {
        // 上恒为 IN（接收），下看布尔
        if (side == Direction.UP) {
            return ChannelConnection.IN;
        }
        if (side == Direction.DOWN) {
            return connectedDown ? ChannelConnection.OUT : ChannelConnection.NONE;
        }
        int index = side.get2DDataValue();
        ChannelConnection connection = index >= 0 ? connections[index] : null;
        return connection == null ? ChannelConnection.NONE : connection;
    }

    public boolean isConnectedDown() {
        return connectedDown;
    }

    public void setConnection(Direction side, ChannelConnection connection) {
        if (side == Direction.DOWN) {
            this.connectedDown = connection == ChannelConnection.OUT;
        } else {
            int index = side.get2DDataValue();
            if (index < 0) {
                return;
            }
            ChannelConnection oldConnection = this.connections[index];
            if (oldConnection != ChannelConnection.OUT && connection == ChannelConnection.OUT) {
                numOutputs++;
            } else if (oldConnection == ChannelConnection.OUT && connection != ChannelConnection.OUT) {
                numOutputs--;
            }
            this.connections[index] = connection;
        }
        updateBlockstate();
    }

    /** 连接状态写入 BlockState（驱动模型，1.21.1 无 getActualState）。 */
    private void updateBlockstate() {
        if (level == null || level.isClientSide) {
            return;
        }
        BlockState state = getBlockState();
        BlockState newState = state
                .setValue(BlockChannel.NORTH, connections[Direction.NORTH.get2DDataValue()] != ChannelConnection.NONE)
                .setValue(BlockChannel.SOUTH, connections[Direction.SOUTH.get2DDataValue()] != ChannelConnection.NONE)
                .setValue(BlockChannel.WEST, connections[Direction.WEST.get2DDataValue()] != ChannelConnection.NONE)
                .setValue(BlockChannel.EAST, connections[Direction.EAST.get2DDataValue()] != ChannelConnection.NONE)
                .setValue(BlockChannel.DOWN, connectedDown);
        if (newState != state) {
            level.setBlock(worldPosition, newState, 2);
        }
    }

    /**
     * 放置时按点击面建立连接（1:1 旧版 onPlaceBlock）。
     *
     * @param hit   点击面
     * @param sneak 潜行（连接方向取反：IN）
     */
    public void onPlaceBlock(Direction hit, boolean sneak) {
        Direction side = hit.getOpposite();
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
        if (neighbor == null) {
            return;
        }
        if (side == Direction.UP) {
            // 放在沟槽下方 → 上方沟槽向下连接
            if (neighbor instanceof TileChannel above) {
                above.connectedDown = true;
                above.updateBlockstate();
            }
        } else if (neighbor instanceof TileChannel other) {
            // 沟槽互连（1:1 旧版：点击面默认 OUT，潜行 IN）
            if (side == Direction.DOWN) {
                this.connectedDown = true;
            } else {
                ChannelConnection connection = sneak ? ChannelConnection.IN : ChannelConnection.OUT;
                this.setConnection(side, connection.getOpposite());
                other.setConnection(hit, connection);
            }
            updateBlockstate();
        } else if (level.getCapability(
                net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                worldPosition.relative(side), side.getOpposite()) != null) {
            // 其他流体容器 → 输出连接
            this.setConnection(side, ChannelConnection.OUT);
        }
        this.wasPowered = level.hasNeighborSignal(worldPosition);
    }

    /** 邻居变化：清理失效连接 + 红石控制向下输出（1:1 旧版 handleBlockUpdate 简化）。 */
    public void handleBlockUpdate(BlockPos fromPos, boolean isPowered) {
        if (level.isClientSide) {
            return;
        }
        Direction side = directionFromOffset(worldPosition, fromPos);
        // 自身更新或上方不处理
        if (side == null || side == Direction.UP) {
            return;
        }
        BlockEntity neighbor = level.getBlockEntity(fromPos);
        boolean isValid = neighbor instanceof TileChannel
                || (neighbor != null && level.getCapability(
                        net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                        fromPos, side.getOpposite()) != null);
        ChannelConnection connection = getConnection(side);
        if (connection != ChannelConnection.NONE && !isValid) {
            // 连接失效 → 断开
            this.setConnection(side, ChannelConnection.NONE);
        }

        // 红石控制向下输出（1:1 旧版：powered 且下方有接收者 → connectedDown）
        if (isPowered != wasPowered) {
            BlockEntity below = level.getBlockEntity(worldPosition.below());
            boolean canReceive = below instanceof TileChannel
                    || (below != null && level.getCapability(
                            net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                            worldPosition.below(), Direction.UP) != null);
            this.connectedDown = canReceive && isPowered;
            updateBlockstate();
            wasPowered = isPowered;
        }
    }

    /**
     * 右键交互：切换侧向连接（NONE→OUT→IN 循环，潜行反向）/ 向下开关（1:1 旧版 interact）。
     */
    public boolean interact(Player player, Direction side) {
        BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(side));
        boolean isChannel = neighbor instanceof TileChannel;
        if (!isChannel && side != Direction.DOWN) {
            boolean hasHandler = neighbor != null && level.getCapability(
                    net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK,
                    worldPosition.relative(side), side.getOpposite()) != null;
            if (!hasHandler) {
                // 无效侧：已是 NONE 则尝试下方（1:1 旧版）
                if (getConnection(side) == ChannelConnection.NONE) {
                    if (side != Direction.DOWN) {
                        return interact(player, Direction.DOWN);
                    }
                } else {
                    this.setConnection(side, ChannelConnection.NONE);
                    return false;
                }
            }
        }

        if (side == Direction.DOWN) {
            this.connectedDown = !this.connectedDown;
            updateBlockstate();
            sendMessage(player, connectedDown ? "message.tconstruct_nirvana.channel.connected_down.allow"
                    : "message.tconstruct_nirvana.channel.connected_down.disallow");
        } else {
            ChannelConnection newConnect = getConnection(side).getNext(player.isShiftKeyDown());
            this.setConnection(side, newConnect);
            // 沟槽邻居同步（1:1 旧版）
            if (isChannel) {
                ((TileChannel) neighbor).setConnection(side.getOpposite(), newConnect.getOpposite());
            }
            sendMessage(player, switch (newConnect) {
                case OUT -> "message.tconstruct_nirvana.channel.connected.out";
                case IN -> "message.tconstruct_nirvana.channel.connected.in";
                default -> "message.tconstruct_nirvana.channel.connected.none";
            });
        }
        return true;
    }

    private void sendMessage(Player player, String key) {
        player.displayClientMessage(Component.translatable(key), true);
    }

    /** 由邻居偏移计算方向（1:1 旧版 Util.facingFromNeighbor；自身偏移返回 null）。 */
    private static Direction directionFromOffset(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();
        if (dx > 0) {
            return Direction.EAST;
        }
        if (dx < 0) {
            return Direction.WEST;
        }
        if (dy > 0) {
            return Direction.UP;
        }
        if (dy < 0) {
            return Direction.DOWN;
        }
        if (dz > 0) {
            return Direction.SOUTH;
        }
        if (dz < 0) {
            return Direction.NORTH;
        }
        return null;
    }

    /** 中央液罐（客户端渲染用）。 */
    public FluidTank getTank() {
        return tank;
    }

    /* ---------- capability（1:1 旧版：仅 IN 侧与上方可注入，不可抽出） ---------- */

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ModBlockEntities.CHANNEL.get(),
                (be, side) -> {
                    // 仅允许注入侧访问（1:1 旧版 hasCapability：IN 或 UP；DOWN 为输出侧）
                    if (side != null && side != Direction.UP
                            && be.getConnection(side) != ChannelConnection.IN) {
                        return null;
                    }
                    return new ChannelFluidHandler(be);
                });
    }

    /** 沟槽流体 handler：可注入（计入 locked）不可抽出（1:1 旧版 ChannelTank.canDrain=false）。 */
    private static class ChannelFluidHandler implements IFluidHandler {

        private final TileChannel be;

        ChannelFluidHandler(TileChannel be) {
            this.be = be;
        }

        @Override
        public int getTanks() {
            return be.tank.getTanks();
        }

        @Override
        public int getTankCapacity(int tank) {
            return be.tank.getTankCapacity(tank);
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return be.tank.getFluidInTank(tank);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            int filled = be.tank.fill(resource, action);
            if (filled > 0 && action.execute()) {
                be.locked += filled;
            }
            return filled;
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            return FluidStack.EMPTY;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            return be.tank.isFluidValid(tank, stack);
        }
    }

    /* ---------- 保存 ---------- */

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) (connections[i] == null ? 0 : connections[i].ordinal());
        }
        tag.putByteArray("connections", bytes);
        tag.putBoolean("connected_down", connectedDown);
        tag.putBoolean("was_powered", wasPowered);
        tank.writeToNBT(registries, tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        numOutputs = 0;
        if (tag.contains("connections")) {
            byte[] bytes = tag.getByteArray("connections");
            for (int i = 0; i < 4 && i < bytes.length; i++) {
                connections[i] = ChannelConnection.fromIndex(bytes[i]);
                // 仅输出连接计入均分（security_review：与 setConnection 的增减规则一致）
                if (connections[i] == ChannelConnection.OUT) {
                    numOutputs++;
                }
            }
        }
        connectedDown = tag.getBoolean("connected_down");
        wasPowered = tag.getBoolean("was_powered");
        tank.readFromNBT(registries, tag);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }
}
