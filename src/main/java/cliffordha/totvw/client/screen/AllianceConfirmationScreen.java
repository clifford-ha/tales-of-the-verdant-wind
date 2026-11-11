package cliffordha.totvw.client.screen;

import cliffordha.totvw.networking.packets.ProcessAlliancePayload;
import cliffordha.totvw.registry.VWColors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.UUID;

import static cliffordha.totvw.client.screen.VWScreen.COLOR_TEXT;
import static cliffordha.totvw.client.screen.VWScreen.XY_SIZE;

public class AllianceConfirmationScreen extends Screen {
    private final UUID playerUUID;
    private final boolean remove;
    private final Screen parent;
    protected int leftPos, topPos, panelWidth, panelHeight;
    public AllianceConfirmationScreen(Screen caller, Component title, UUID id, boolean remove) {
        super(title);
        this.parent = caller;
        this.playerUUID = id;
        this.remove = remove;
    }
    public static Component YES = Component.literal("Confirm");
    public static Component NO = Component.literal("Cancel");

    @Override
    protected void init() {
        this.panelWidth = Mth.floor(this.width * (1.0F - 0.05 * 2));
        this.panelHeight = Mth.floor(this.height * (1.0F - 0.05 * 2));
        this.leftPos = Mth.floor(this.width * 0.05);
        this.topPos = Mth.floor(this.height * 0.05);
        int centerX = this.leftPos + (this.panelWidth / 2);
        int centerY = this.topPos + (this.panelHeight / 2);

        this.addRenderableWidget(
                Button.builder(NO, b -> {
                    if (!remove && parent != null) {
                        Minecraft.getInstance().setScreenAndShow(parent);
                    } else this.onClose();

                }).bounds(centerX - 115, centerY, 100, XY_SIZE).build()
        );
        this.addRenderableWidget(
                Button.builder(YES, b -> {
                    ClientPlayNetworking.send(new ProcessAlliancePayload(playerUUID, remove, false));
                    this.onClose();

                }).bounds(centerX + 15, centerY, 100, XY_SIZE).build()
        );
        super.init();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int shade = ARGB.color(0.4f, VWColors.GRAY_MUTED);
        int bg = ARGB.color(1.0f, VWColors.GRAY_MUTED);
        graphics.fill(0, 0, this.width, this.height, shade);
        graphics.fillGradient(0, 0, this.width, this.height, bg, shade);

        String confirm;
        if (remove) {
            confirm = "Remove " + this.getTitle().getString() + " from your list of trusted players?";
        } else confirm = "Accept " + this.getTitle().getString() + "'s alliance request?";

        graphics.text(this.font, confirm, Mth.ceil(this.width * 0.5) - font.width(confirm) / 2, Mth.ceil(this.height * 0.4), COLOR_TEXT, false);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public void tick() {
        if (playerUUID == null) this.onClose();
        super.tick();
    }
}
