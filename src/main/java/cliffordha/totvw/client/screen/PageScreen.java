package cliffordha.totvw.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class PageScreen extends ScrollableTextScreen {
    private final Screen parent;

    public PageScreen(String pageTitle, Supplier<String> liveSupplier) {
        super(Component.literal(pageTitle), liveSupplier.get(), liveSupplier);
        this.parent = null;
    }

    public PageScreen(Screen callback, String pageTitle, Supplier<String> contentSupplier) {
        super(Component.literal(pageTitle), contentSupplier.get(), null);
        this.parent = callback;
    }

    @Override
    public void onClose() {
        if (this.parent != null) {
            Minecraft.getInstance().setScreenAndShow(this.parent);
            return;
        }
        super.onClose();
    }
}