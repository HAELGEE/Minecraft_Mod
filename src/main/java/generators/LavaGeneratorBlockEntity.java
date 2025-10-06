package generators;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LavaGeneratorBlockEntity extends BlockEntity implements MenuProvider {
    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.getItem() == Items.LAVA_BUCKET; // Input - bara lava buckets
                case 1 -> false; // Output - kan inte placera items här
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private int lavaAmount = 0;
    private final int maxLava = 4; // Max 4 lava buckets
    private int burnTime = 0; // Tick counter för lava consumption

    // ENERGI SYSTEM
    private int energy = 0;
    private final int maxEnergy = 100000; // 100,000 FE max lagring
    private final int energyPerTick = 20; // 20 FE/tick
    private final int ticksPerLavaBucket = 2000; // 100 sekunder per lava bucket (20 ticks * 100)

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();

    protected final ContainerData data;

    public LavaGeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(GeneratorMod.LAVA_GENERATOR_BE.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> lavaAmount;
                    case 1 -> maxLava;
                    case 2 -> energy;
                    case 3 -> maxEnergy;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> lavaAmount = value;
                    case 2 -> energy = value;
                }
            }

            @Override
            public int getCount() {
                return 4; // Uppdaterad till 4 för energi data
            }
        };
    }

    // TICK METOD - denna körs varje game tick
    public static void tick(Level level, BlockPos pos, BlockState state, LavaGeneratorBlockEntity blockEntity) {
        if (!level.isClientSide) {
            blockEntity.processLava();
            blockEntity.generateEnergy();
            blockEntity.sendEnergyToNeighbors();
        }
    }

    private void processLava() {
        ItemStack inputStack = itemHandler.getStackInSlot(0);

        // Kolla om vi kan lägga till lava
        if (inputStack.getItem() == Items.LAVA_BUCKET && lavaAmount < maxLava) {
            // Öka lava mängd
            lavaAmount++;

            // Ta bort lava bucket från input
            itemHandler.extractItem(0, 1, false);

            // Lägg till tom bucket i output
            ItemStack outputStack = itemHandler.getStackInSlot(1);
            if (outputStack.isEmpty()) {
                itemHandler.setStackInSlot(1, new ItemStack(Items.BUCKET));
            } else if (outputStack.getItem() == Items.BUCKET && outputStack.getCount() < outputStack.getMaxStackSize()) {
                outputStack.grow(1);
            }

            setChanged();
            updateBlockState();
        }
    }

private void generateEnergy() {
    // Only generate energy if we have lava AND room for more energy
    if (lavaAmount > 0 && energy < maxEnergy) {
        // Generate energy
        int energyToGenerate = Math.min(energyPerTick, maxEnergy - energy);
        energy += energyToGenerate;

        // Increase burn time
        burnTime++;

        // Consume lava when we've generated a full bucket's worth of energy
        if (burnTime >= ticksPerLavaBucket) {
            lavaAmount--;
            burnTime = 0; // Reset burn time
            setChanged();
        }

        updateBlockState();
    } else if (lavaAmount == 0) {
        burnTime = 0; // Reset if no lava left
        updateBlockState();
    }
}

    private void sendEnergyToNeighbors() {
        if (energy > 0) {
            for (Direction direction : Direction.values()) {
                BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(direction));
                if (neighbor != null) {
                    neighbor.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(neighborEnergy -> {
                        if (neighborEnergy.canReceive()) {
                            int energyToSend = Math.min(energy, 1000); // Max 1000 FE/tick
                            int energyReceived = neighborEnergy.receiveEnergy(energyToSend, false);
                            energy -= energyReceived;
                            setChanged();
                        }
                    });
                }
            }
        }
    }

    // ENERGY STORAGE IMPLEMENTATION
    private final IEnergyStorage energyStorage = new IEnergyStorage() {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0; // Generatorn kan inte ta emot energi, bara generera
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int energyExtracted = Math.min(energy, maxExtract);
            if (!simulate) {
                energy -= energyExtracted;
                setChanged();
            }
            return energyExtracted;
        }

        @Override
        public int getEnergyStored() {
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            return maxEnergy;
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return false; // Generatorn kan bara ge energi, inte ta emot
        }
    };

    @Override
    public Component getDisplayName() {
        return Component.literal("Lava Generator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new LavaGeneratorMenu(id, inventory, this, this.data);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("lavaAmount", lavaAmount);
        tag.putInt("burnTime", burnTime);
        tag.putInt("energy", energy);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        lavaAmount = tag.getInt("lavaAmount");
        burnTime = tag.getInt("burnTime");
        energy = tag.getInt("energy");
    }

    public int getLavaAmount() {
        return lavaAmount;
    }

    public int getMaxLava() {
        return maxLava;
    }

    public int getEnergy() {
        return energy;
    }

    public int getMaxEnergy() {
        return maxEnergy;
    }

    /* VISUAL FOR BLOCK UNDER HERE */
    private void updateBlockState() {
        if (level != null && level.getBlockState(worldPosition).getBlock() instanceof LavaGeneratorBlock generatorBlock) {
            generatorBlock.updateBlockState(level, worldPosition, energy, lavaAmount > 0);
        }
    }

}