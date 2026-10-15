package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.networking.packets.ClientPrefsPayload;
import cliffordha.totvw.registry.VWColors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class DedicatedConfigScreen extends Screen {
    public static void create() {
        Minecraft.getInstance().setScreenAndShow(new DedicatedConfigScreen());
    }

    private interface Entry {
        String label();
        String description();
    }

    private record Header(String label) implements Entry {
        public String description() { return ""; }
    }

    private record BoolEntry(String label, String description, BooleanSupplier get, Consumer<Boolean> set) implements Entry {}

    private record IntEntry(String label, String description, IntSupplier get, IntConsumer set, int min, int max, int step) implements Entry {}

    private static final int ROW_W = 250;
    private static final int ROW_H = 20;
    private static final int VALUE_W = 80;
    private static final int COLOR_BG = 0x80001822;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private final List<Entry> entries = new ArrayList<>();

    private int scroll = 0;
    private int listLeft, listTop, listBottom;

    private DedicatedConfigScreen() {
        super(Component.literal(TOTVW.MOD_NAME_LONG));
        buildEntries();
    }

    private void buildEntries() {
        VWConfig c = VWConfig.get();

        entries.add(new Header("SYNCED CONFIG"));

        entries.add(new BoolEntry("Use Notifiers", "Send notifications to chat or overlay",
                () -> c.CLIENT_ENABLE_NOTIFIERS,
                v -> c.CLIENT_ENABLE_NOTIFIERS = v));

        entries.add(new BoolEntry("Allow Effect Overlays", "Certain mob effects will affect the screen",
                () -> c.CLIENT_ALLOW_EFFECT_OVERLAYS,
                v -> c.CLIENT_ALLOW_EFFECT_OVERLAYS = v));

        entries.add(new BoolEntry("Bloodlust Overlay", "Bloodlust Effect Overlay strength depends on health. Needs Allow Effect Overlays",
                () -> c.CLIENT_BLOODLUST_EFFECT_OVERLAY,
                v -> c.CLIENT_BLOODLUST_EFFECT_OVERLAY = v));

        entries.add(new BoolEntry("Show Atrocity Counter", "Display a counter when you hit a wolf or villager",
                () -> c.CLIENT_SHOW_ATROCITY_COUNTER,
                v -> c.CLIENT_SHOW_ATROCITY_COUNTER = v));


        entries.add(new Header("Benediction of the Verdant Mountains"));

        entries.add(new IntEntry("Low Health Threshold (%)", "Grant Blessing when health percent is met",
                () -> c.SERVER_BENEDICTION_HEALTH_THRESHOLD,
                v -> c.SERVER_BENEDICTION_HEALTH_THRESHOLD = v, 10, 50, 5));
        entries.add(new BoolEntry("Share Benediction Stack", "Wolf will share their Benediction Stack with you when you are near them.",
                () -> c.SERVER_WOLF_SHARES_BENEDICTION_STACK,
                v -> c.SERVER_WOLF_SHARES_BENEDICTION_STACK = v));
        entries.add(new BoolEntry("Always Trigger Blessing", "Grant Blessing even if the wolf could revive you",
                () -> c.SERVER_ALWAYS_TRIGGER_BLESSING,
                v -> c.SERVER_ALWAYS_TRIGGER_BLESSING = v));
        entries.add(new BoolEntry("Teleport After Revival", "Teleport to nearest wolf/owner after revival",
                () -> c.SERVER_TELEPORT_AFTER_SAVE,
                v -> c.SERVER_TELEPORT_AFTER_SAVE = v));

        entries.add(new IntEntry("Wolf TP Method", "0: wolf goes to player, 1: reverse",
                () -> c.SERVER_WOLF_TP_METHOD,
                v -> c.SERVER_WOLF_TP_METHOD = v, 0, 1, 1));

        entries.add(new IntEntry("Player TP Method", "0: player goes to wolf, 1: reverse",
                () -> c.SERVER_PLAYER_TP_METHOD,
                v -> c.SERVER_PLAYER_TP_METHOD = v, 0, 1, 1));

        entries.add(new BoolEntry("All Wolves Get TP", "Teleport all tamed wolves to the player. Affected by Player TP Method and only works if both are in the same dimension.",
                () -> c.SERVER_WOLF_TP_ALL,
                v -> c.SERVER_WOLF_TP_ALL = v));
    }

    @Override
    protected void init() {
        listLeft = (int) ((((double) this.width / 2) - (double) ROW_W / 2) * 0.3);
        listTop = 32;
        listBottom = this.height - 40;
        scroll = Mth.clamp(scroll, 0, maxScroll());

        this.addRenderableWidget(
                Button.builder(Component.literal("Re-sync"), b -> this.onClose())
                        .bounds(listLeft, this.height - 40, 75, 20)
                        .build()
        );
    }

    private int visibleRows() {
        return Math.max(1, (listBottom - listTop) / ROW_H);
    }

    private int maxScroll() {
        return Math.max(0, entries.size() - visibleRows());
    }

    private int rowAt(double mx, double my) {
        if (mx < listLeft || mx >= listLeft + ROW_W || my < listTop || my >= listBottom) return -1;
        int index = scroll + (int) ((my - listTop) / ROW_H);
        return index >= 0 && index < entries.size() ? index : -1;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(0, 0, this.width, this.height, COLOR_BG);
        graphics.text(this.font, this.title.getString(), this.width / 2 - this.font.width(this.title.getString()) / 2, 12, 0xFF000000 | VWColors.VERDANT_WIND, false);

        int hovered = rowAt(mouseX, mouseY);
        int end = Math.min(entries.size(), scroll + visibleRows());
        int y = listTop;

        for (int i = scroll; i < end; i++) {
            Entry e = entries.get(i);
            int textY = y + (ROW_H - this.font.lineHeight) / 2;

            if (e instanceof Header) {
                graphics.text(this.font, e.label(), listLeft, textY, 0xFF000000 | VWColors.VERDANT_WIND, false);
            } else {
                if (i == hovered) graphics.fill(listLeft, y, listLeft + ROW_W, y + ROW_H, COLOR_HOVER);
                graphics.text(this.font, e.label(), listLeft + 4, textY, 0xFFFFFFFF, false);

                String value;
                int color;
                if (e instanceof BoolEntry b) {
                    boolean on = b.get().getAsBoolean();
                    value = on ? "ON" : "OFF";
                    color = on ? 0xFF55FF55 : 0xFFFF5555;
                } else {
                    IntEntry n = (IntEntry) e;
                    value = "<  " + n.get().getAsInt() + "  >";
                    color = 0xFFFFFFFF;
                }
                int valueX = listLeft + ROW_W - VALUE_W + (VALUE_W - this.font.width(value)) / 2;
                graphics.text(this.font, value, valueX, textY, color, false);
            }
            y += ROW_H;
        }

        if (hovered >= 0) {
            String desc = entries.get(hovered).description();
            if (!desc.isEmpty()) {
                List<FormattedText> wrapped = this.font.getSplitter()
                        .splitLines(Component.translatable(desc), 170, Style.EMPTY);

                descriptionText(graphics, wrapped);
            }
        } else {
            List<FormattedText> wrapped = this.font.getSplitter()
                    .splitLines(Component.literal(
                            "These options are automatically synced to the server."
                    ), 170, Style.EMPTY);

            descriptionText(graphics, wrapped);
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private void descriptionText(GuiGraphicsExtractor graphics, List<FormattedText> wrapped) {
        int ySpacing = 0;
        for (FormattedText line : wrapped) {
            int rightPos = this.width - this.font.width(line.getString());
            graphics.text(this.font, line.getString(), rightPos - 30, (listTop + 34) - this.font.lineHeight + ySpacing, 0xFFAAAAAA, false);
            ySpacing += 10;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        double mx = event.x(), my = event.y();
        int index = rowAt(mx, my);
        if (index < 0) return false;

        Entry e = entries.get(index);
        if (e instanceof BoolEntry b) {
            b.set().accept(!b.get().getAsBoolean());
            return true;
        }
        if (e instanceof IntEntry n) {
            double localX = mx - (listLeft + ROW_W - VALUE_W);
            if (localX < 0 || localX >= VALUE_W) return false;
            int delta = localX < VALUE_W / 2.0 ? -n.step() : n.step();
            n.set().accept(Mth.clamp(n.get().getAsInt() + delta, n.min(), n.max()));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, maxScroll());
        return true;
    }

    @Override
    public void onClose() {
        VWConfig.save();
        if (ClientPlayNetworking.canSend(ClientPrefsPayload.TYPE)) {
            VWConfig c = VWConfig.get();
            ClientPlayNetworking.send(new ClientPrefsPayload(
                    c.CLIENT_SHOW_ATROCITY_COUNTER,
                    c.CLIENT_ENABLE_NOTIFIERS,
                    c.SERVER_BENEDICTION_HEALTH_THRESHOLD,
                    c.SERVER_WOLF_SHARES_BENEDICTION_STACK,
                    c.SERVER_ALWAYS_TRIGGER_BLESSING,
                    c.SERVER_TELEPORT_AFTER_SAVE,
                    c.SERVER_WOLF_TP_METHOD,
                    c.SERVER_PLAYER_TP_METHOD,
                    c.SERVER_WOLF_TP_ALL
            ));
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}