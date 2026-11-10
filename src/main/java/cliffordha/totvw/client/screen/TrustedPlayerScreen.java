package cliffordha.totvw.client.screen;

import cliffordha.totvw.item.pages.FTextStyle;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.NameHolder;
import cliffordha.totvw.util.TextUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class TrustedPlayerScreen extends Screen {
    public record Entry(String name, UUID uuid) {}

    private static final int LIST_W = 450;
    private static final int ROW_H = 14;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private final List<Entry> entries = new ArrayList<>();
    private List<Entry> filtered = new ArrayList<>();
    private final Screen parentScreen;

    private EditBox search;
    private int scroll = 0;
    private int listLeft, listTop, listBottom;

    public TrustedPlayerScreen(List<NameHolder> players, Screen parent) {
        super(text("Trusted Players"));
        this.parentScreen = parent;

        for (NameHolder holder : players) {
            entries.add(new Entry(holder.name(), holder.uuid()));
        }
        entries.sort((a, b) -> String.CASE_INSENSITIVE_ORDER.compare(a.name(), b.name()));
    }

    private void refresh() {
        String query = search == null ? "" : search.getValue().toLowerCase(Locale.ROOT).trim();
        filtered = entries.stream()
                .filter(e -> (e.name() + " " + e.uuid()).toLowerCase(Locale.ROOT).contains(query))
                .toList();
        scroll = Mth.clamp(scroll, 0, maxScroll());
    }

    private int visibleRows() {
        return Math.max(1, (listBottom - listTop) / ROW_H);
    }

    private int maxScroll() {
        return Math.max(0, filtered.size() - visibleRows());
    }

    @Override
    protected void init() {
        listLeft = Mth.ceil(this.width * 0.05);
        listTop = Mth.ceil(this.height * 0.25);
        listBottom = this.height - 20;
        int btnY = Mth.ceil(this.height * 0.15);

        int searchW = Mth.ceil(this.width * 0.2);
        search = new EditBox(this.font, listLeft, btnY + 1, searchW, 18, text("Search"));
        search.setHint(text("Name..."));
        search.setResponder(_ -> { scroll = 0; refresh(); });
        this.addRenderableWidget(search);
        this.setInitialFocus(search);

        this.addRenderableWidget(
                Button.builder(text("✖"), button -> this.onClose())
                        .bounds(Mth.ceil(this.width * 0.91), btnY, 20, 20).build()
        );
        refresh();
    }

    @Override
    public void onClose() {
        if (parentScreen != null) {
            Minecraft.getInstance().setScreenAndShow(parentScreen);
            return;
        }
        super.onClose();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int TITLE_COLOR = ARGB.color(1.0f, VWColors.RUNESTONE_TETHER);
        int COLOR_SHADE = ARGB.color(0.4f, 0x001822);
        int COLOR_BG = ARGB.color(1.0f, 0x001822);

        graphics.fill(0, 0, this.width, this.height, COLOR_SHADE);

        graphics.fillGradient(0, 0, this.width, this.height, COLOR_BG, COLOR_SHADE);
        graphics.text(this.font, TextUtil.fText(FTextStyle.BOLD, "Trusted Players"), listLeft, Mth.ceil(this.height * 0.1), TITLE_COLOR, false);

        int end = Math.min(filtered.size(), scroll + visibleRows());
        int y = listTop;

        for (int i = scroll; i < end; i++) {
            Entry e = filtered.get(i);
            String uuid = e.uuid().toString();
            boolean hovered = mouseX >= listLeft && mouseX < listLeft + LIST_W && mouseY >= y && mouseY < y + ROW_H;

            if (hovered) graphics.fill(listLeft, y, Mth.ceil(this.width * 0.9) + listLeft, y + ROW_H, COLOR_HOVER);

            int idWidth = this.font.width(uuid);
            String name = this.font.plainSubstrByWidth(e.name(), LIST_W - idWidth - 12);

            graphics.text(this.font, name, listLeft, y + 3, 0xFFFFFFFF, false);
            graphics.text(this.font, uuid, this.width - idWidth - Mth.ceil(this.width * 0.05), y + 3, 0xFF888888, false);
            y += ROW_H;
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        double mx = event.x(), my = event.y();
        if (mx < listLeft || mx >= listLeft + LIST_W || my < listTop || my >= listBottom) return false;

        int index = scroll + (int) ((my - listTop) / ROW_H);
        if (index < 0 || index >= filtered.size()) return false;

        Entry e = filtered.get(index);
        Minecraft.getInstance().setScreenAndShow(new AllianceConfirmationScreen(Component.literal(e.name()), e.uuid(), true));
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY) * 3, 0, maxScroll());
        return true;
    }

    @Override
    public void tick() {
        refresh();
        super.tick();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static Component text(String s) {
        return Component.literal(s);
    }
}