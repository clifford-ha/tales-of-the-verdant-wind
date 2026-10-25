package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.networking.packets.ClientPrefsPayload;
import cliffordha.totvw.registry.VWColors;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

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

    private static int ROW_W = 250;
    private static final int ROW_H = 20;
    private static final int VALUE_W = 80;
    private static final int COLOR_HOVER = 0x33FFFFFF;
    private static final Identifier BANNER = TOTVW.registerID("textures/gui/banner.png");
    private static final int BANNER_TEX_W = 3840;
    private static final int BANNER_TEX_H = 1080;

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
        ROW_W = Mth.ceil(this.width * 0.6f);
        listLeft = (int) ((((double) this.width / 2) - (double) ROW_W / 2) * 0.3);
        listTop = Mth.ceil(this.height * 0.2f);
        listBottom = this.height - Mth.ceil(this.height * 0.15f);
        scroll = Mth.clamp(scroll, 0, maxScroll());

        this.addRenderableWidget(
                Button.builder(Component.literal("Re-sync"), b -> this.onClose())
                        .bounds(listLeft, Mth.ceil(this.height * 0.85f), 75, 20)
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
        int COLOR_BG = ARGB.color(0.7f, 0x001822);
        graphics.fill(0, 0, this.width, this.height, COLOR_BG);

        int margin = 10;
        int maxW = this.width - margin * 2;
        int maxH = this.height / 4;

        float scale = Math.min((float) maxW / BANNER_TEX_W, (float) maxH / BANNER_TEX_H);
        float rescale = scale * 0.7f;
        int w = Math.max(1, (int) (BANNER_TEX_W * rescale));
        int h = Math.max(1, (int) (BANNER_TEX_H * rescale));

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BANNER,
                this.listLeft - 11, margin,
                0.0F, 0.0F,
                w, h,                          // drawn size
                BANNER_TEX_W, BANNER_TEX_H,    // region of the texture to sample
                BANNER_TEX_W, BANNER_TEX_H     // full texture size
        );

        int hovered = rowAt(mouseX, mouseY);
        int end = Math.min(entries.size(), scroll + visibleRows());
        int y = listTop;

        for (int i = scroll; i < end; i++) {
            Entry e = entries.get(i);
            int textY = y + (ROW_H - this.font.lineHeight) / 2;

            if (e instanceof Header) {
                graphics.text(this.font, e.label(), listLeft, textY, 0xFF000000 | VWColors.VERDANT_WIND, false);
            } else {
                if (i == hovered) graphics.fill(listLeft, y, Mth.ceil(listLeft * 0.7f) + ROW_W, y + ROW_H, COLOR_HOVER);
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
                int valueX = listLeft + ROW_W - VALUE_W + (60 - this.font.width(value)) / 2;
                graphics.text(this.font, value, valueX, textY, color, false);
            }
            y += ROW_H;
        }

        int maxX = (int) (this.width * 0.25f);
        String note;
        if (hovered >= 0) {
            String desc = entries.get(hovered).description();
            note = !desc.isEmpty() ? "DESCRIPTION" : "";
            if (!desc.isEmpty()) {
                List<FormattedText> wrapped = this.font.getSplitter()
                        .splitLines(Component.translatable(desc), maxX, Style.EMPTY);

                descriptionText(graphics, wrapped);
            }
        } else {
            note = "NOTE :3";
            List<FormattedText> wrapped = this.font.getSplitter()
                    .splitLines(Component.literal(
                            "These options are automatically synced to the server."
                    ), maxX, Style.EMPTY);

            descriptionText(graphics, wrapped);
        }
        graphics.text(
                this.font, note,
                Mth.ceil(this.width * 0.675f), Mth.ceil(this.height * 0.2f),
                0xffffffff,
                false
        );


        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private void descriptionText(GuiGraphicsExtractor graphics, List<FormattedText> wrapped) {
        int leftStart = Mth.ceil(this.width * 0.675f);
        int ySpacing = 16;
        for (FormattedText line : wrapped) {

            graphics.text(
                    this.font, line.getString(),
                    leftStart, Mth.ceil(this.height * 0.2f) + ySpacing,
                    0xffffffff,
                    false
            );

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