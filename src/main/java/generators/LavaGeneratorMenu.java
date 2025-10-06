package generators;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class LavaGeneratorMenu extends AbstractContainerMenu {
    private final ContainerData data;
    private final LavaGeneratorBlockEntity blockEntity;

    public LavaGeneratorMenu(int windowId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(windowId, playerInventory, getBlockEntity(playerInventory, extraData), new SimpleContainerData(4));
    }

    public LavaGeneratorMenu(int windowId, Inventory playerInventory, LavaGeneratorBlockEntity blockEntity, ContainerData data) {
        super(GeneratorMod.LAVA_GENERATOR_MENU.get(), windowId);
        this.data = data;
        this.blockEntity = blockEntity;

        checkContainerSize(playerInventory, 2);

        // Rensa alla slots först
        this.slots.clear();

        if (blockEntity != null) {
            // Input slot för lava buckets (slot 0) - UPPE TILL VÄNSTER
            this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 0, 56, 17) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Items.LAVA_BUCKET;
                }
            });

            // Output slot för tomma buckets (slot 1) - NERE TILL VÄNSTER
            this.addSlot(new SlotItemHandler(blockEntity.itemHandler, 1, 56, 53) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        } else {
            // Fallback slots om block entity inte finns
            this.addSlot(new Slot(playerInventory, 0, 56, 36) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() == Items.LAVA_BUCKET;
                }
            });

            this.addSlot(new Slot(playerInventory, 1, 56, 56) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        // Spelarens inventory
        layoutPlayerInventorySlots(new InvWrapper(playerInventory), 8, 84);

        // Lägg till data synkning
        addDataSlots(data);
    }

    private static LavaGeneratorBlockEntity getBlockEntity(Inventory playerInventory, FriendlyByteBuf extraData) {
        Player player = playerInventory.player;
        BlockPos pos = extraData.readBlockPos(); // Läs position från packet
        return (LavaGeneratorBlockEntity) player.level().getBlockEntity(pos);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 2) {
                // Flytta från generator till spelarens inventory
                if (!this.moveItemStackTo(itemstack1, 2, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Flytta från spelarens inventory till generator
                if (itemstack1.getItem() == Items.LAVA_BUCKET) {
                    if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 2 && index < 29) {
                    // Flytta inom huvud-inventory
                    if (!this.moveItemStackTo(itemstack1, 29, 38, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 29 && index < 38) {
                    // Flytta från hotbar till huvud-inventory
                    if (!this.moveItemStackTo(itemstack1, 2, 29, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity != null) {
            return blockEntity.getBlockPos().closerToCenterThan(player.position(), 8.0D);
        }
        return true;
    }

    private void layoutPlayerInventorySlots(IItemHandler playerInventory, int leftCol, int topRow) {
        // Player inventory (3 rader med 9 slots)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new SlotItemHandler(playerInventory, col + row * 9 + 9, leftCol + col * 18, topRow + row * 18));
            }
        }

        // Hotbar (översta raden i spelarens inventory)
        topRow += 58;
        for (int col = 0; col < 9; col++) {
            addSlot(new SlotItemHandler(playerInventory, col, leftCol + col * 18, topRow));
        }
    }

    public int getLavaAmount() {
        return data.get(0);
    }

    public int getMaxLava() {
        return data.get(1);
    }

    public int getEnergy() {
        return data.get(2);
    }

    public int getMaxEnergy() {
        return data.get(3);
    }
}