package cliffordha.totvw.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.function.Supplier;

public abstract class WolfTextScreen extends ScrollableTextScreen {
    protected final Wolf wolf;

    protected WolfTextScreen(Component title, Supplier<String> liveSupplier, Supplier<Wolf> wolfSupplier) {
        super(title, liveSupplier.get(), liveSupplier);
        this.wolf = wolfSupplier.get();
    }

    @Override
    protected boolean shouldStayOpen() {
        if (this.wolf == null || this.minecraft.player == null) return true;
        return this.wolf.getOwner() == this.minecraft.player && this.wolf.distanceTo(this.minecraft.player) <= 8;
    }
}