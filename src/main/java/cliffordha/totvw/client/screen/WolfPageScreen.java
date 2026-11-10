package cliffordha.totvw.client.screen;

import cliffordha.totvw.item.pages.FTextColor;
import cliffordha.totvw.networking.packets.WolfEventsPayload;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.ReturnPointData;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.TetherData;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.List;
import java.util.function.Supplier;

import static cliffordha.totvw.client.screen.ConfigScreen.text;
import static cliffordha.totvw.util.TextUtil.cText;

public class WolfPageScreen extends WolfTextScreen {
    private final String pageTitle;
    private boolean record;

    public WolfPageScreen(String pageTitle, Supplier<String> liveSupplier, Supplier<Wolf> wolfSupplier) {
        super(Component.literal(pageTitle).withColor(VWColors.WITHERING), liveSupplier, wolfSupplier);
        this.pageTitle = pageTitle;
        this.record = ReturnPointData.of(this.wolf).recordAfter();
    }

    @Override
    protected String titleText() {
        return this.pageTitle;
    }

    @Override
    protected void init() {
        super.init();

        Runestone type = wolf.getAttachedOrElse(WolfAttachment.RUNESTONE_TYPE, Runestone.EMPTY);
        boolean returnMethod = wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0) > 0 && ReturnPointData.of(wolf).isValid();
        boolean runestone = !type.equals(Runestone.EMPTY);

        int btnY = this.topPos + 3;
        int btnX = closeButtonX() - 97;

        if (returnMethod) {
            this.addRenderableWidget(
                    Button.builder(Component.literal("BRP: " + (record ? "Record" : "SavedPos")), b -> {
                                if (Minecraft.getInstance().hasShiftDown()) {
                                    ClientPlayNetworking.send(new WolfEventsPayload(List.of(wolf.getId(), 4)));
                                    this.onClose();
                                } else {
                                    ClientPlayNetworking.send(new WolfEventsPayload(List.of(wolf.getId(), 3)));
                                    record = !record;
                                    b.setMessage(Component.literal("BRP: " + (record ? "Record" : "SavedPos")));
                                }
                            })
                            .tooltip(Tooltip.create(text(
                                    "[Benediction Return Point] allows " + wolf.getPlainTextName() + " to teleport to a saved position or be recorded to save them from death.\n\nRead additional info in the Features Handbook. \n\n"
                                            + cText(FTextColor.GRAY, "Hold shift to remove saved position")
                            )))
                            .bounds(btnX, btnY, 95, XY_SIZE)
                            .build()
            );
        }

        if (runestone) {
            boolean isTether = type == Runestone.TETHER;
            String tether = "Click to open " + type.getName().replace("Runestone", "Manager screen");
            this.addRenderableWidget(
                    Button.builder(Component.literal("Runestone"), b -> {
                                if (Minecraft.getInstance().hasShiftDown()) {
                                    ClientPlayNetworking.send(new WolfEventsPayload(List.of(wolf.getId(), 2)));
                                    this.onClose();
                                } else if (isTether) {
                                    Minecraft.getInstance().setScreenAndShow(new TetherScreen(wolf.getId(), TetherData.of(wolf), this));
                                }
                            })
                            .tooltip(Tooltip.create(
                                    text(cText(FTextColor.GRAY, (isTether ? tether + "\n\n" : "") + cText(FTextColor.GRAY, "Hold shift to drop " + type.getName())))
                            ))
                            .bounds(btnX - (returnMethod ? 97 : 0), btnY, 95, XY_SIZE)
                            .build()
            );
        }
    }
}