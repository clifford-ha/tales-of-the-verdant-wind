package cliffordha.totvw.block.entity;

import cliffordha.totvw.block.ModStorageBlock;
import cliffordha.totvw.registry.ModBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.ContainerUser;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrelBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.List;

public class ModStorageBlockEntity extends RandomizableContainerBlockEntity {
    private NonNullList<ItemStack> items;
    private final ContainerOpenersCounter openersCounter;

    public ModStorageBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(ModBlockEntityTypes.STORAGE_BOX, worldPosition, blockState);

        this.items = NonNullList.withSize(5 * 9, ItemStack.EMPTY);
        this.openersCounter = new ContainerOpenersCounter() {
            protected void onOpen(Level level, BlockPos pos, BlockState state) {
                ModStorageBlockEntity.this.playSound(state, SoundEvents.BARREL_OPEN);
                ModStorageBlockEntity.this.updateBlockState(state, true);
            }

            protected void onClose(Level level, BlockPos pos, BlockState state) {
                ModStorageBlockEntity.this.playSound(state, SoundEvents.BARREL_CLOSE);
                ModStorageBlockEntity.this.updateBlockState(state, false);
            }

            protected void openerCountChanged(Level level, BlockPos pos, BlockState blockState, int previous, int current) {
            }

            public boolean isOwnContainer(final Player player) {
                if (player.containerMenu instanceof ChestMenu) {
                    Container container = ((ChestMenu)player.containerMenu).getContainer();
                    return container == ModStorageBlockEntity.this;
                } else {
                    return false;
                }
            }
        };
    }

    @Override
    public BlockEntityType<?> getType() {
        return ModBlockEntityTypes.STORAGE_BOX;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.tales-of-the-verdant-wind.storage_box");
    }

    @Override
    protected Component getDefaultName() {
        return getDisplayName();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.fiveRows(containerId, inventory);
    }

    @Override
    public int getContainerSize() {
        return 5 * 9;
    }

    @Override
    public void startOpen(ContainerUser containerUser) {
        if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
            this.openersCounter.incrementOpeners(containerUser.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState(), containerUser.getContainerInteractionRange());
        }

    }

    @Override
    public void stopOpen(ContainerUser containerUser) {
        if (!this.remove && !containerUser.getLivingEntity().isSpectator()) {
            this.openersCounter.decrementOpeners(containerUser.getLivingEntity(), this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    @Override
    protected void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        if (!this.trySaveLootTable(output)) {
            ContainerHelper.saveAllItems(output, this.items);
        }

    }

    @Override
    protected void loadAdditional(final ValueInput input) {
        super.loadAdditional(input);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        if (!this.tryLoadLootTable(input)) {
            ContainerHelper.loadAllItems(input, this.items);
        }

    }

    @Override
    public List<ContainerUser> getEntitiesWithContainerOpen() {
        return this.openersCounter.getEntitiesWithContainerOpen(this.getLevel(), this.getBlockPos());
    }

    public void recheckOpen() {
        if (!this.remove) {
            this.openersCounter.recheckOpeners(this.getLevel(), this.getBlockPos(), this.getBlockState());
        }
    }

    private void updateBlockState(BlockState state, boolean isOpen) {
        this.level.setBlock(this.getBlockPos(), state.setValue(ModStorageBlock.OPEN, isOpen), 3);
    }

    private void playSound(BlockState state, SoundEvent event) {
        Vec3i direction = state.getValue(ModStorageBlock.FACING).getUnitVec3i();
        double x = (double)this.worldPosition.getX() + 0.5 + (double)direction.getX() / 2.0;
        double y = (double)this.worldPosition.getY() + 0.5 + (double)direction.getY() / 2.0;
        double z = (double)this.worldPosition.getZ() + 0.5 + (double)direction.getZ() / 2.0;
        this.level.playSound(null, x, y, z, event, SoundSource.BLOCKS, 0.5F, this.level.getRandom().nextFloat() * 0.1F + 0.9F);
    }
}
