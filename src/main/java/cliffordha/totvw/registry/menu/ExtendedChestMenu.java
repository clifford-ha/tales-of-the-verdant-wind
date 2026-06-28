package cliffordha.totvw.registry.menu;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ExtendedChestMenu extends AbstractContainerMenu {
    private final Container container;
    private final int rows;
    private final int size;

    protected ExtendedChestMenu(MenuType<ChestMenu> chest, int containerId, Container container, int rows, int size) {
        super(chest, containerId);
        this.container = container;
        this.rows = rows * 9;
        this.size = size;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.container.stopOpen(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack clicked = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            clicked = stack.copy();
            if (slotIndex < this.rows) {
                if (!this.moveItemStackTo(stack, this.rows, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, this.rows, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return clicked;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.container.stillValid(player);
    }
}
