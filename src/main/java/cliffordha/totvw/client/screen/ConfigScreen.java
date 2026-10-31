package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.networking.packets.ClientPrefsPayload;
import cliffordha.totvw.registry.VWColors;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public class ConfigScreen extends Screen {
    private enum ListShown {
        CLIENT("Client"), SERVER("Server"), DEBUG("Debug"), INFO("Info");
        private final String label;
        ListShown(String label) { this.label = label; }
        public String getLabel() {
            return label;
        }
    }
    private interface Entry {
        String label();
        String description();
    }
    private static final List<String> RANDOM_HELLO = List.of(
            ""
    );

    private record Header(String label) implements ConfigScreen.Entry {
        public String description() { return ""; }
    }

    private record DescEntry(String label, String description) implements ConfigScreen.Entry {}
    private record BoolEntry(String label, String description, BooleanSupplier get, Consumer<Boolean> set) implements ConfigScreen.Entry {}
    private record IntEntry(String label, String description, IntSupplier get, IntConsumer set, int min, int max, int step) implements ConfigScreen.Entry {}

    // TEXTURE RELATED
    private static int ROW_W = 250;
    private static final int ROW_H = 20;
    private static final int VALUE_W = 80;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private static final Identifier ICON = TOTVW.registerID("textures/gui/totvw_translucent.png");
    private static final Identifier BANNER = TOTVW.registerID("textures/gui/banner.png");
    private static final int BANNER_TEX_W = 3840;
    private static final int BANNER_TEX_H = 1080;
    private static final int ICON_TEX_W = 1080;
    private static final int ICON_TEX_H = 1080;

    // SCROLLBAR RELATED
    private static final int SCROLLBAR_W = 5;
    private boolean draggingScrollbar = false;

    // OPTIONS
    private final List<ConfigScreen.Entry> entries = new ArrayList<>();
    private ListShown listShown = ListShown.CLIENT;
    private ListShown lastListShown = ListShown.CLIENT;
    private boolean isFromDebug = false;

    private int scroll = 0;
    private int listLeft, listTop, listBottom;
    private final Screen parent;

    public static void create() {
        Minecraft.getInstance().setScreenAndShow(new ConfigScreen());
    }
    public static Screen create(Screen parent) {
        return new ConfigScreen(parent);
    }
    public ConfigScreen(Screen parent) {
        super(Component.literal(TOTVW.MOD_NAME_LONG));
        buildEntries();
        this.parent = parent;

    }
    public ConfigScreen() {
        super(Component.literal(TOTVW.MOD_NAME_LONG));
        buildEntries();
        this.parent = null;
    }

    @Override
    protected void init() {
        ROW_W = Mth.ceil(this.width * 0.6f);
        listLeft = (int) ((((double) this.width / 2) - (double) ROW_W / 2) * 0.3);
        listTop = Mth.ceil(this.height * 0.2f);
        listBottom = this.height - Mth.ceil(this.height * 0.15f);
        scroll = Mth.clamp(scroll, 0, maxScroll());

        this.addRenderableWidget(
                Button.builder(text("Re-sync"), b -> this.onClose())
                        .bounds(listLeft, Mth.ceil(this.height * 0.85f), 75, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(text(nextLabel()), b -> {
                            if (Minecraft.getInstance().hasShiftDown() && !isFromDebug) {
                                isFromDebug = true;
                                lastListShown = listShown;
                                listShown = ListShown.DEBUG;
                            } else if (isFromDebug) {
                                isFromDebug = false;
                                listShown = lastListShown;
                            } else {
                                listShown = listShown == ListShown.CLIENT ? ListShown.SERVER : ListShown.CLIENT;
                                lastListShown = listShown;
                            }
                            b.setMessage(text(nextLabel()));
                            b.setTooltip(Tooltip.create(text(nextTooltip())));
                            buildEntries();
                        })
                        .tooltip(Tooltip.create(text(nextTooltip())))
                        .bounds(listLeft + 80, Mth.ceil(this.height * 0.85f), 75, 20)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("?").withStyle(ChatFormatting.BOLD), b -> {
                    listShown = ListShown.INFO;
                    buildEntries();
                })
                        .tooltip(Tooltip.create(text("Check info")))
                        .bounds(listLeft + 160, Mth.ceil(this.height * 0.85f), 20, 20)
                        .build()
        );
    }

    private String nextLabel() {
        if (isFromDebug) return lastListShown.getLabel();
        return (listShown == ListShown.CLIENT ? ListShown.SERVER : ListShown.CLIENT).getLabel();
    }
    private String nextTooltip() {
        return isFromDebug ? "Click to switch to default" : "Hold shift to switch to debug mode instead";
    }

    @Override
    public void tick() {
        super.tick();
        buildEntries();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int TITLE_COLOR = ARGB.color(1.0f, VWColors.RUNESTONE_TETHER);
        int COLOR_SHADE = ARGB.color(0.4f, 0x001822);
        int COLOR_BG = ARGB.color(0.7f, 0x001822);
        graphics.fillGradient(0, 0, this.width, this.height, COLOR_BG, COLOR_SHADE);

        int halfH = Mth.ceil(this.height * 0.5f);
        int halfX = Mth.ceil(this.width * 0.5f);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ICON,
                (this.width / 2) - halfH / 2, (this.height / 2) - halfH / 2,
                0.0F, 0.0F,
                halfH, halfH, // drawn size
                ICON_TEX_W, ICON_TEX_H, // full texture size
                ICON_TEX_W, ICON_TEX_H // full texture size
        );

        graphics.fill(0, 0, this.width, this.height, COLOR_BG);

        int margin = 10;
        int maxW = this.width - margin * 2;
        int maxH = this.height / 4;

        float scale = Math.min((float) maxW / BANNER_TEX_W, (float) maxH / BANNER_TEX_H);
        float rescale = listShown == ListShown.INFO ? scale * 1.3f : scale * 0.7f;

        int w = Math.max(1, (int) (BANNER_TEX_W * rescale));
        int h = Math.max(1, (int) (BANNER_TEX_H * rescale));
        int bannerXPos;
        int bannerYPos;
        if (listShown == ListShown.INFO) {
            if (this.width <= 1080) {
                rescale *= 0.8f;
                w = Math.max(1, (int) (BANNER_TEX_W * rescale));
                h = Math.max(1, (int) (BANNER_TEX_H * rescale));
            }
            bannerXPos = (this.width / 2) - w / 2;
            bannerYPos = Mth.ceil(this.height * 0.15f);
        } else {
            bannerXPos = this.listLeft - 11;
            bannerYPos = Mth.ceil(this.height * 0.1f) - margin;
        }
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                BANNER,
                bannerXPos, bannerYPos,
                0.0F, 0.0F,
                w, h, // drawn size
                BANNER_TEX_W, BANNER_TEX_H, // full texture size
                BANNER_TEX_W, BANNER_TEX_H // full texture size
        );

        int end = Math.min(entries.size(), scroll + visibleRows());

        if (listShown == ListShown.INFO) {
            int spacing = 0;
            for (int i = scroll; i < end; i++) {
                ConfigScreen.Entry e = entries.get(i);

                if (e instanceof ConfigScreen.Header header) {
                    //graphics.text(this.font, e.label(), listLeft, textY, 0xFF000000 | VWColors.VERDANT_WIND, false);
                } else {
                    graphics.text(this.font, e.label(), halfX - (font.width(e.label())) / 2, Mth.ceil(this.height * 0.4) + spacing, 0xFFFFFFFF, false);
                }
                spacing += 12;
            }

            super.extractRenderState(graphics, mouseX, mouseY, a);
            return;
        }

        int hovered = rowAt(mouseX, mouseY);
        int y = listTop;
        for (int i = scroll; i < end; i++) {
            ConfigScreen.Entry e = entries.get(i);
            int textY = y + (ROW_H - this.font.lineHeight) / 2;

            if (e instanceof ConfigScreen.Header header) {
                graphics.text(this.font, e.label(), listLeft, textY, 0xFF000000 | VWColors.VERDANT_WIND, false);

            } else {
                if (i == hovered) graphics.fill(listLeft, y, ROW_W, y + ROW_H, COLOR_HOVER);
                graphics.text(this.font, e.label(), listLeft + 4, textY, 0xFFFFFFFF, false);

                String value;
                int color;
                if (e instanceof ConfigScreen.BoolEntry b) {
                    boolean on = b.get().getAsBoolean();
                    value = on ? "ON" : "OFF";
                    color = on ? 0xFF55FF55 : 0xFFFF5555;
                } else {
                    ConfigScreen.IntEntry n = (ConfigScreen.IntEntry) e;
                    value = "<  " + n.get().getAsInt() + "  >";
                    color = 0xFFFFFFFF;
                }
                int valueX = listLeft + ROW_W - VALUE_W + (60 - this.font.width(value)) / 2;
                graphics.text(this.font, value, valueX, textY, color, false);
            }
            y += ROW_H;
        }

        // scrollbar
        if (maxScroll() > 0) {
            int x = scrollbarX();
            graphics.fill(x, listTop, x + SCROLLBAR_W, listBottom, 0x33FFFFFF);
            int ty = thumbY();
            graphics.fill(x, ty, x + SCROLLBAR_W, ty + thumbHeight(),
                    draggingScrollbar ? 0xFFFFFFFF : 0xAAFFFFFF);
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
                    .splitLines(text(
                            switch (listShown) {
                                case CLIENT -> "These options are automatically synced to the server.";
                                case SERVER -> "For server-side only. Changes to these options will only apply if you are the host or when in singleplayer mode";
                                case DEBUG -> "For debugging purposes only (server-side)";
                                case INFO -> "Mod Info";
                            }
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

    private void buildEntries() {
        entries.clear();
        VWConfig c = VWConfig.get();

        if (listShown == ListShown.CLIENT) {
            entries.add(new ConfigScreen.Header("SYNCED CONFIG"));

            entries.add(new ConfigScreen.BoolEntry(
                    "Use Notifiers",
                    "Send notifications to chat or overlay",
                    () -> c.SYNCED_ENABLE_NOTIFIERS, v -> c.SYNCED_ENABLE_NOTIFIERS = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Allow Effect Overlays",
                    "Certain mob effects will affect the screen",
                    () -> c.CLIENT_ALLOW_EFFECT_OVERLAYS, v -> c.CLIENT_ALLOW_EFFECT_OVERLAYS = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Bloodlust Overlay",
                    "Bloodlust Effect Overlay strength depends on health. Allow Effect Overlays must be enabled to work",
                    () -> c.CLIENT_BLOODLUST_EFFECT_OVERLAY, v -> c.CLIENT_BLOODLUST_EFFECT_OVERLAY = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Show Atrocity Counter",
                    "Display a counter when you hit a wolf or villager",
                    () -> c.SYNCED_SHOW_ATROCITY_COUNTER, v -> c.SYNCED_SHOW_ATROCITY_COUNTER = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Allow Lore Spoilers",
                    "When enabled, show texts on pages that contain lore regardless if the player is not in survival mode.",
                    () -> c.CLIENT_ALLOW_LORE_SPOILERS, v -> c.CLIENT_ALLOW_LORE_SPOILERS = v));


            entries.add(new ConfigScreen.Header("Benediction of the Verdant Mountains"));

            entries.add(new ConfigScreen.IntEntry(
                    "Low Health Threshold (%)",
                    "Grant Blessing when health percent is met",
                    () -> c.SYNCED_BENEDICTION_HEALTH_THRESHOLD, v -> c.SYNCED_BENEDICTION_HEALTH_THRESHOLD = v, 10, 50, 5));

            entries.add(new ConfigScreen.BoolEntry(
                    "Share Benediction Stack",
                    "Wolf will share their Benediction Stack with you when you are near them",
                    () -> c.SYNCED_WOLF_SHARES_BENEDICTION_STACK, v -> c.SYNCED_WOLF_SHARES_BENEDICTION_STACK = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Always Trigger Blessing",
                    "Grant Blessing even if the wolf could revive you",
                    () -> c.SYNCED_ALWAYS_TRIGGER_BLESSING, v -> c.SYNCED_ALWAYS_TRIGGER_BLESSING = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Teleport After Revival",
                    "Teleport to nearest wolf/owner after revival. If teleport location is out of bounds, teleport to last saved position or world respawn point; Affects both Wolf and Player TP Methods",
                    () -> c.SYNCED_TELEPORT_AFTER_SAVE, v -> c.SYNCED_TELEPORT_AFTER_SAVE = v));

            entries.add(new ConfigScreen.IntEntry(
                    "Wolf TP Method",
                    "0: wolf goes to player, 1: reverse",
                    () -> c.SYNCED_WOLF_TP_METHOD, v -> c.SYNCED_WOLF_TP_METHOD = v, 0, 1, 1));

            entries.add(new ConfigScreen.IntEntry(
                    "Player TP Method",
                    "0: player goes to wolf, 1: reverse",
                    () -> c.SYNCED_PLAYER_TP_METHOD, v -> c.SYNCED_PLAYER_TP_METHOD = v, 0, 1, 1));

            entries.add(new ConfigScreen.BoolEntry(
                    "All Wolves Get TP",
                    "Teleport all tamed wolves to the player and vice versa. Affects both Wolf and Player TP Methods.",
                    () -> c.SYNCED_WOLF_TP_ALL, v -> c.SYNCED_WOLF_TP_ALL = v));

        } else if (listShown == ListShown.SERVER) {
            entries.add(new ConfigScreen.Header("SERVER-SIDE CONFIG"));

            entries.add(new ConfigScreen.BoolEntry(
                    "Wolf Behaviour Extensions",
                    "Adds and modifies (some) of the wolf's behaviour to account for the different enchantment mechanics.",
                    () -> c.SERVER_WOLF_BEHAVIOUR_EXTENSIONS, v -> c.SERVER_WOLF_BEHAVIOUR_EXTENSIONS = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Wolf Damage Distribution",
                    "Use the new damage distribution where wolf with armors, applied damage is distributed: 25% to wolf, 75% to the armor",
                    () -> c.SERVER_WOLF_DMG_DISTRIBUTION, v -> c.SERVER_WOLF_DMG_DISTRIBUTION = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Skill Cooldowns",
                    "If disabled, active cooldowns for skills will reset",
                    () -> c.SERVER_SKILL_COOLDOWNS, v -> c.SERVER_SKILL_COOLDOWNS = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Item Cooldowns",
                    "If disabled, certain item usage won't trigger cooldowns",
                    () -> c.SERVER_ITEM_COOLDOWNS, v -> c.SERVER_ITEM_COOLDOWNS = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Other Cooldowns",
                    "",
                    () -> c.SERVER_OTHER_COOLDOWNS, v -> c.SERVER_OTHER_COOLDOWNS = v));

        } else if (listShown == ListShown.DEBUG) {
            entries.add(new ConfigScreen.Header("DEBUG"));


            entries.add(new ConfigScreen.Header("Lodestone Windcore Logger"));

            entries.add(new ConfigScreen.BoolEntry(
                    "Energy Changes",
                    "On terminal log, display when the windcore has performed auto-recharge and when activated or deactivated",
                    () -> c.LOG_WINDCORE_ENERGY_CHANGES, v -> c.LOG_WINDCORE_ENERGY_CHANGES = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Record",
                    "On terminal log, display energy consumption and scanned entities' implosion chance",
                    () -> c.LOG_WINDCORE_RECORD, v -> c.LOG_WINDCORE_RECORD = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Conversion",
                    "On terminal log, show when the windcore converts a wolf or villager to a Verdant type",
                    () -> c.LOG_WINDCORE_ENTITY_CONVERSION, v -> c.LOG_WINDCORE_ENTITY_CONVERSION = v));

            entries.add(new ConfigScreen.BoolEntry(
                    "Entity Events",
                    "On terminal log, show entities that have imploded",
                    () -> c.LOG_WINDCORE_ENTITY_EVENT, v -> c.LOG_WINDCORE_ENTITY_EVENT = v));

        } else {
            entries.add(new ConfigScreen.Header("INFORMATION"));

            entries.add(new DescEntry("Version: " + TOTVW.VERSION, ""));
            entries.add(new DescEntry("", ""));
            entries.add(new DescEntry("All Rights Reserved", ""));
            entries.add(new DescEntry("Copyright (C) 2026 Clifford HA", ""));
        }
        scroll = Mth.clamp(scroll, 0, maxScroll());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isOverScrollbar(event.x(), event.y())) {
            draggingScrollbar = true;
            setScrollFromMouse(event.y());
            return true;
        }
        if (super.mouseClicked(event, doubleClick)) return true;

        double mx = event.x(), my = event.y();
        int index = rowAt(mx, my);
        if (index < 0) return false;

        ConfigScreen.Entry e = entries.get(index);
        if (e instanceof ConfigScreen.BoolEntry b) {
            b.set().accept(!b.get().getAsBoolean());
            buildEntries();
            return true;
        }
        if (e instanceof ConfigScreen.IntEntry n) {
            double localX = mx - (listLeft + ROW_W - VALUE_W);
            if (localX < 0 || localX >= VALUE_W) return false;
            int delta = localX < VALUE_W / 2.0 ? -n.step() : n.step();
            n.set().accept(Mth.clamp(n.get().getAsInt() + delta, n.min(), n.max()));
            buildEntries();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (draggingScrollbar) {
            setScrollFromMouse(event.y());
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggingScrollbar) {
            draggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY), 0, maxScroll());
        return true;
    }

    private int visibleRows() {
        return Math.max(1, (listBottom - listTop) / ROW_H);
    }
    private int maxScroll() {
        return Math.max(0, entries.size() - visibleRows());
    }
    private int rowAt(double mx, double my) {
        if (mx < listLeft || mx >= ROW_W || my < listTop || my >= listBottom) return -1;
        int index = scroll + (int) ((my - listTop) / ROW_H);
        return index >= 0 && index < entries.size() ? index : -1;
    }
    private int scrollbarX() {
        return Mth.ceil(listLeft * 0.35f) + ROW_W;
    }
    private int thumbHeight() {
        int trackH = listBottom - listTop;
        return Math.max(12, (int) ((float) visibleRows() / entries.size() * trackH));
    }
    private int thumbY() {
        int trackH = listBottom - listTop;
        int travel = trackH - thumbHeight();
        int max = maxScroll();
        return listTop + (max == 0 ? 0 : (int) ((float) scroll / max * travel));
    }
    private void setScrollFromMouse(double my) {
        int travel = (listBottom - listTop) - thumbHeight();
        if (travel <= 0) return;
        float t = (float) ((my - listTop - thumbHeight() / 2.0) / travel);
        scroll = Mth.clamp(Math.round(t * maxScroll()), 0, maxScroll());
    }
    private boolean isOverScrollbar(double mx, double my) {
        int x = scrollbarX();
        return maxScroll() > 0 && mx >= x && mx < x + SCROLLBAR_W && my >= listTop && my < listBottom;
    }

    @Override
    public void onClose() {
        VWConfig.save();
        if (ClientPlayNetworking.canSend(ClientPrefsPayload.TYPE)) {
            VWConfig c = VWConfig.get();
            ClientPlayNetworking.send(new ClientPrefsPayload(
                    c.SYNCED_SHOW_ATROCITY_COUNTER,
                    c.SYNCED_ENABLE_NOTIFIERS,
                    c.SYNCED_BENEDICTION_HEALTH_THRESHOLD,
                    c.SYNCED_WOLF_SHARES_BENEDICTION_STACK,
                    c.SYNCED_ALWAYS_TRIGGER_BLESSING,
                    c.SYNCED_TELEPORT_AFTER_SAVE,
                    c.SYNCED_WOLF_TP_METHOD,
                    c.SYNCED_PLAYER_TP_METHOD,
                    c.SYNCED_WOLF_TP_ALL
            ));
        }
        if (parent != null) {
            Minecraft.getInstance().setScreenAndShow(parent);
        } else {
            super.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return !Minecraft.getInstance().isMultiplayerServer() && Minecraft.getInstance().isLocalServer();
    }

    protected static Component text(String s) {
        return Component.literal(s);
    }
    protected static int getCenter(int base, int src) {
        return (base / 2) - src / 2;
    }
    protected static int rescale(int value, double amount) {
        return Mth.ceil(value * amount);
    }
}
