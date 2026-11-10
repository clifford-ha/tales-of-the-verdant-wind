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
    private static final int ROW_H = 20;
    private static final int VALUE_W = 90;
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
    private int listTop, listBottom;

    private static final int BTN_H = 20;
    private static final int GAP = 8;

    private int contentLeft, contentRight, contentTop, contentBottom;
    private int optionsLeft, optionsRight;
    private int scrollSlotLeft, scrollSlotRight, scrollbarX;
    private int descLeft, descRight;
    private int bannerX, bannerY, bannerW, bannerH;
    private int buttonY;

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
        layout();

        this.addRenderableWidget(
                Button.builder(text("Re-sync"), b -> this.onClose())
                        .bounds(optionsLeft, buttonY, 75, BTN_H)
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
                            layout();
                        })
                        .tooltip(Tooltip.create(text(nextTooltip())))
                        .bounds(optionsLeft + 80, buttonY, 75, BTN_H)
                        .build()
        );
        this.addRenderableWidget(
                Button.builder(Component.literal("?").withStyle(ChatFormatting.BOLD), b -> {
                            listShown = ListShown.INFO;
                            buildEntries();
                            layout();
                        })
                        .tooltip(Tooltip.create(text("Check info")))
                        .bounds(optionsLeft + 160, buttonY, 20, BTN_H)
                        .build()
        );
    }

    @Override
    public void tick() {
        super.tick();
        buildEntries();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int COLOR_SHADE = ARGB.color(0.4f, 0x001822);
        int COLOR_BG = ARGB.color(0.7f, 0x001822);
        graphics.fillGradient(0, 0, this.width, this.height, COLOR_BG, COLOR_SHADE);

        int iconSize = Mth.ceil(this.height * 0.5f);
        graphics.blit(RenderPipelines.GUI_TEXTURED, ICON,
                (this.width - iconSize) / 2, (this.height - iconSize) / 2,
                0.0F, 0.0F, iconSize, iconSize,
                ICON_TEX_W, ICON_TEX_H, ICON_TEX_W, ICON_TEX_H);

        graphics.fill(0, 0, this.width, this.height, COLOR_BG);

        graphics.blit(RenderPipelines.GUI_TEXTURED, BANNER,
                bannerX, bannerY, 0.0F, 0.0F, bannerW, bannerH,
                BANNER_TEX_W, BANNER_TEX_H, BANNER_TEX_W, BANNER_TEX_H);

        int end = Math.min(entries.size(), scroll + visibleRows());

        if (listShown == ListShown.INFO) {
            int centerX = (contentLeft + contentRight) / 2;
            int y = listTop;
            for (int i = scroll; i < end; i++) {
                ConfigScreen.Entry e = entries.get(i);
                if (!(e instanceof ConfigScreen.Header)) {
                    graphics.text(this.font, e.label(), centerX - font.width(e.label()) / 2, y, 0xFFFFFFFF, false);
                }
                y += 12;
            }
            super.extractRenderState(graphics, mouseX, mouseY, a);
            return;
        }

        int hovered = rowAt(mouseX, mouseY);
        int y = listTop;
        for (int i = scroll; i < end; i++) {
            ConfigScreen.Entry e = entries.get(i);
            int textY = y + (ROW_H - this.font.lineHeight) / 2;

            if (e instanceof ConfigScreen.Header) {
                graphics.text(this.font, e.label(), optionsLeft, textY, 0xFF000000 | VWColors.VERDANT_WIND, false);
            } else {
                if (i == hovered) graphics.fill(optionsLeft, y, optionsRight, y + ROW_H, COLOR_HOVER);
                graphics.text(this.font, e.label(), optionsLeft + 8, textY, 0xFFFFFFFF, false);

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
                int valueLeft = optionsRight - VALUE_W;
                graphics.text(this.font, value, valueLeft + (VALUE_W - this.font.width(value)) / 2, textY, color, false);
            }
            y += ROW_H;
        }

        // scrollbar
        if (maxScroll() > 0) {
            graphics.fill(scrollbarX, listTop, scrollbarX + SCROLLBAR_W, listBottom, 0x33FFFFFF);
            int ty = thumbY();
            graphics.fill(scrollbarX, ty, scrollbarX + SCROLLBAR_W, ty + thumbHeight(),
                    draggingScrollbar ? 0xFFFFFFFF : 0xAAFFFFFF);
        }

        // description panel
        int descW = Math.max(20, descRight - descLeft);
        String note;
        List<FormattedText> wrapped;
        if (hovered >= 0 && !entries.get(hovered).description().isEmpty()) {
            note = "DESCRIPTION";
            wrapped = this.font.getSplitter().splitLines(Component.translatable(entries.get(hovered).description()), descW, Style.EMPTY);
        } else if (hovered >= 0) {
            note = "";
            wrapped = List.of();
        } else {
            note = "NOTE :3";
            wrapped = this.font.getSplitter().splitLines(text(
                    switch (listShown) {
                        case CLIENT -> "These options are automatically synced to the server.";
                        case SERVER -> "For server-side only. Changes to these options will only apply if you are the host or when in singleplayer mode";
                        case DEBUG -> "For debugging purposes only (server-side)";
                        case INFO -> "Mod Info";
                    }), descW, Style.EMPTY);
        }
        graphics.text(this.font, note, descLeft, listTop, 0xFFFFFFFF, false);
        int dy = listTop + 16;
        for (FormattedText line : wrapped) {
            graphics.text(this.font, line.getString(), descLeft, dy, 0xFFFFFFFF, false);
            dy += 10;
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
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
                    "Show Wolf Effect Indicators",
                    "When an effect is triggered by a wolf's enchantment, show an indicator on the screen. 'Show Effect Indicators' must be enabled to work. Currently an experimental content!",
                    () -> c.SYNCED_SHOW_WOLF_EFFECT_INDICATORS, v -> c.SYNCED_SHOW_WOLF_EFFECT_INDICATORS = v));

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
                    "Show Effect Indicators",
                    "An indicator will be displayed on the screen. Currently an experimental content!",
                    () -> c.SERVER_SHOW_EFFECT_INDICATORS, v -> c.SERVER_SHOW_EFFECT_INDICATORS = v));

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
    private void layout() {
        int marginX = Mth.floor(this.width * 0.07f);
        int marginY = Mth.floor(this.height * 0.07f);

        contentLeft = marginX;
        contentRight = this.width - marginX;
        contentTop = marginY;
        contentBottom = this.height - marginY;
        int contentW = contentRight - contentLeft;

        int optionsW = Mth.floor(contentW * 0.65f);
        int scrollW = Mth.floor(contentW * 0.05f);

        optionsLeft = contentLeft;
        optionsRight = optionsLeft + optionsW;

        scrollSlotLeft = optionsRight;
        scrollSlotRight = optionsRight + scrollW;
        scrollbarX = scrollSlotLeft + (scrollW - SCROLLBAR_W) / 2;

        descLeft = scrollSlotRight;
        descRight = contentRight;

        // banner
        if (listShown == ListShown.INFO) {
            float scale = Math.min(contentW * 0.8f / BANNER_TEX_W, this.height * 0.3f / BANNER_TEX_H);
            bannerW = Math.max(1, (int) (BANNER_TEX_W * scale));
            bannerH = Math.max(1, (int) (BANNER_TEX_H * scale));
            bannerX = contentLeft + (contentW - bannerW) / 2;
        } else {
            float scale = Math.min((float) optionsW / BANNER_TEX_W, this.height * 0.2f / BANNER_TEX_H);
            bannerW = Math.max(1, (int) (BANNER_TEX_W * scale));
            bannerH = Math.max(1, (int) (BANNER_TEX_H * scale));
            bannerX = Mth.ceil(this.width * 0.05);
        }
        bannerY = contentTop;

        buttonY = contentBottom - BTN_H;
        listTop = bannerY + Mth.ceil(bannerH * 0.75) + GAP;
        int rows = Math.max(1, (buttonY - GAP - listTop) / ROW_H);
        listBottom = listTop + rows * ROW_H;

        scroll = Mth.clamp(scroll, 0, maxScroll());
    }
    private String nextLabel() {
        if (isFromDebug) return lastListShown.getLabel();
        return (listShown == ListShown.CLIENT ? ListShown.SERVER : ListShown.CLIENT).getLabel();
    }
    private String nextTooltip() {
        return isFromDebug ? "Click to switch to default" : "Hold shift to switch to debug mode instead";
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
            double localX = mx - (optionsRight - VALUE_W);
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
        if (mx < optionsLeft || mx >= optionsRight || my < listTop || my >= listBottom) return -1;
        int index = scroll + (int) ((my - listTop) / ROW_H);
        return index >= 0 && index < entries.size() ? index : -1;
    }
    private int thumbHeight() {
        return Math.max(12, (int) ((float) visibleRows() / Math.max(1, entries.size()) * (listBottom - listTop)));
    }
    private int thumbY() {
        int travel = (listBottom - listTop) - thumbHeight();
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
        return maxScroll() > 0 && mx >= scrollSlotLeft && mx < scrollSlotRight && my >= listTop && my < listBottom;
    }

    @Override
    public void onClose() {
        VWConfig.save();
        if (ClientPlayNetworking.canSend(ClientPrefsPayload.TYPE)) {
            VWConfig c = VWConfig.get();
            ClientPlayNetworking.send(new ClientPrefsPayload(
                    c.SYNCED_SHOW_ATROCITY_COUNTER,
                    c.SYNCED_ENABLE_NOTIFIERS,
                    c.SYNCED_SHOW_WOLF_EFFECT_INDICATORS,

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
