package cliffordha.totvw.client.screen;

import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class PlayerPageScreen extends ScrollableTextScreen {
    private Player player;
    public PlayerPageScreen(Player player, String pageTitle, Supplier<String> liveSupplier) {
        super(Component.literal(pageTitle), liveSupplier.get(), liveSupplier);
        this.player = player;
    }

    @Override
    protected void init() {
        initPanel();

        this.addRenderableWidget(
                Button.builder(Component.literal("Trusted Players"), b -> {
                    Minecraft.getInstance().setScreenAndShow(new TrustedPlayerScreen(PlayerAttachment.getTrustedPlayers(player), this));

                }).bounds(closeButtonX() - 95, this.topPos + 3, 90, XY_SIZE).build()
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void tick() {
        if (player == null) this.onClose();
        super.tick();
    }
}