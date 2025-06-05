package net.duck.magicmod.block.pedestal;

import net.duck.magicmod.block.ModBlockEntities;
import net.duck.magicmod.block.pedestal.recipe.AltarRecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PedestalBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger("PedestalBlockEntity");
    private float rotation = 0f;

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    // === Crafting Shit ===
    public List<BlockPos> getNearbyPedestalPositions() {
        List<BlockPos> positions = new ArrayList<>();
        BlockPos.betweenClosedStream(worldPosition.offset(-7, 0, -7), worldPosition.offset(7, 0, 7))
                .forEach(pos -> {
                    if(!pos.equals(worldPosition)) {
                        BlockEntity be = level.getBlockEntity(pos);
                        if(be instanceof MiniPedestalBlockEntity) {
                            positions.add(pos.immutable());
                        }
                    }
                });
        return positions;
    }
    public List<ItemStack> getNearbyPedestalItems() {
        List<ItemStack> items = new ArrayList<>();
        for(BlockPos pos : getNearbyPedestalPositions()) {
            BlockEntity be = level.getBlockEntity(pos);
            if(be instanceof MiniPedestalBlockEntity mini) {
                ItemStack held = mini.getStoredItem();
                if(!held.isEmpty()) {
                    items.add(held.copy());
                }
            }
        }
        return items;
    }

    // === Tick Handling ===
    // === Fields ===

    private static final int CONSUME_INTERVAL_TICKS = 20;
    boolean isCrafting = false;
    ItemStack cachedResult = ItemStack.EMPTY;
    List<BlockPos> cachedPedestalPositions = new ArrayList<>();
    int craftDelayTicks = 0;
    int currentInputIndex = 0;

    public static <T extends PedestalBlockEntity> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
        if(level.isClientSide()) return;
        if(!blockEntity.isCrafting) {
            List<ItemStack> nearbyItems = blockEntity.getNearbyPedestalItems();
            ItemStack storedItem = blockEntity.getStoredItem();
            ItemStack result = AltarRecipeManager.tryCraft(nearbyItems, storedItem);
            if(!result.isEmpty()) {
                blockEntity.cachedResult = result.copy();
                blockEntity.cachedPedestalPositions = blockEntity.getNearbyPedestalPositions();
                blockEntity.isCrafting = true;
                blockEntity.craftDelayTicks = 0;
                blockEntity.currentInputIndex = 0;
            } else {
                blockEntity.resetCraftingProgress();
            }
        } else {
            blockEntity.craftDelayTicks++;
            if (blockEntity.craftDelayTicks % CONSUME_INTERVAL_TICKS == 0) {
                if(!blockEntity.consumeNextInputItem(level)) {
                    blockEntity.inventory.setStackInSlot(0, blockEntity.cachedResult);
                    blockEntity.setChanged();
                    blockEntity.resetCraftingProgress();
                }
            }
            if(level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        ParticleTypes.ENCHANT,
                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                        10, 0.3, 0.3, 0.3, 0.01
                );
            }
        }
    }

    // === Methods ===
    public boolean consumeNextInputItem(Level level) {
        if(currentInputIndex >= cachedPedestalPositions.size()) {
            return false;
        }
        BlockPos pedestalPos = cachedPedestalPositions.get(currentInputIndex);
        BlockEntity be = level.getBlockEntity(pedestalPos);
        if(be instanceof MiniPedestalBlockEntity mini) {
            mini.clearItem();
        }
        currentInputIndex++;
        return true;
    }
    public void resetCraftingProgress() {
        isCrafting = false;
        craftDelayTicks = 0;
        currentInputIndex = 0;
        cachedResult = ItemStack.EMPTY;
        cachedPedestalPositions.clear();
    }

    // === Floating item Shit ===

    void clearInputs() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        for(BlockPos pos : getNearbyPedestalPositions()) {
            BlockEntity be = level.getBlockEntity(pos);
            if(be instanceof MiniPedestalBlockEntity mini) {
                mini.clearContents();
            }
        }
    }

    public PedestalBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PEDESTAL_BE.get(), pos, state);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        return rotation >= 360f ? (rotation = 0) : rotation;
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer container = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            container.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, container);
    }

    // === NBT Sync ===

    public ItemStack getStoredItem() {
        return inventory.getStackInSlot(0);
    }
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.putInt("CraftDelayTicks", craftDelayTicks);
        tag.put("inventory", inventory.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        craftDelayTicks = tag.getInt("CraftDelayTicks");
        inventory.deserializeNBT(provider, tag.getCompound("inventory"));
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return saveWithoutMetadata(provider);
    }

}