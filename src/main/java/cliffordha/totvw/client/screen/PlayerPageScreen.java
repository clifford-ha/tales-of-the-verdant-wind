package cliffordha.totvw.client.screen;

import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public class PlayerPageScreen extends ScrollableTextScreen {
    private Player player;
    public PlayerPageScreen(Player player, String pageTitle, Supplier<String> liveSupplier) {
        super(Component.literal(pageTitle), player.getPlainTextName(), liveSupplier);
        this.player = player;
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(
                Button.builder(Component.literal("Trusted Players"), b -> {
                    Minecraft.getInstance().setScreenAndShow(new TrustedPlayerScreen(() -> player, PlayerAttachment.getTrustedPlayers(player), this));

                }).bounds(closeButtonX() - 100, this.topPos + 3, 95, XY_SIZE).build()
        );
    }

    @Override
    public void tick() {
        if (player == null) this.onClose();
        super.tick();
    }
}