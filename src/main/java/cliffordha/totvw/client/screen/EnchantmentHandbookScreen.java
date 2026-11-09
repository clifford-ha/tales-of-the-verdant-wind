package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.item.pages.FTextStyle;
import cliffordha.totvw.item.pages.handbooks.VWEnchantmentsHandbook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import static cliffordha.totvw.client.screen.PageScreen.*;
import static cliffordha.totvw.item.pages.handbooks.VWEnchantmentsHandbook.*;
import static cliffordha.totvw.util.TextUtil.fText;

public class EnchantmentHandbookScreen extends Screen {
    private interface Entry {
        String label();
    }

    private record ButtonEntry(String label, Supplier<String> info) implements EnchantmentHandbookScreen.Entry {}

    // TEXTURE RELATED
    private static int ROW_W = 250;
    private static final int ROW_H = 20;
    private static final int VALUE_W = 80;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private int leftPos;
    private int topPos;
    private int panelWidth;
    private int panelHeight;

    private int maxScroll = 0;
    private int textStartY;

    private boolean draggingScrollbar = false;
    private int scrollbarX;
    private int scrollbarY;
    private static final int scrollbarWidth = 6;
    private int scrollbarHeight;

    // OPTIONS
    private final List<EnchantmentHandbookScreen.Entry> entries = new ArrayList<>();

    private int scroll = 0;
    private int listLeft, listTop, listBottom;
    private static Player player() {
        return Minecraft.getInstance().player;
    }

    public static void create() {
        Minecraft.getInstance().setScreenAndShow(new EnchantmentHandbookScreen());
    }

    public EnchantmentHandbookScreen() {
        super(Component.literal(TOTVW.MOD_NAME_LONG));
        buildEntries();
    }

    @Override
    protected void init() {
        ROW_W = Mth.ceil(this.width * 0.6f);
        listLeft = (int) ((((double) this.width / 2) - (double) ROW_W / 2) * 0.3);
        listTop = Mth.ceil(this.height * 0.17f);
        listBottom = this.height - Mth.ceil(this.height * 0.1f);
        scroll = Mth.clamp(scroll, 0, maxScroll());

        this.panelWidth = Mth.floor(this.width * (1.0F - PADDING_RATIO * 2));
        this.panelHeight = Mth.floor(this.height * (1.0F - PADDING_RATIO * 2));
        this.leftPos = Mth.floor(this.width * PADDING_RATIO);
        this.topPos = Mth.floor(this.height * PADDING_RATIO);

        int xposBTN = this.leftPos + this.panelWidth - XY_SIZE - 3;
        int yposBTN = this.topPos + 3;

        this.addRenderableWidget(
                Button.builder(Component.literal("✖"), b -> {
                    this.onClose();
                }).bounds(xposBTN, yposBTN, 20, 20).build()
        );

        int textEndY = this.topPos + this.panelHeight - PADDING;
        this.textStartY = this.topPos + 32;
        this.scrollbarX = this.leftPos + this.panelWidth - PADDING - scrollbarWidth;
        this.scrollbarY = this.textStartY;
        this.scrollbarHeight = textEndY - this.textStartY;
    }

    @Override
    public void tick() {
        if (player() == null) this.onClose();
        super.tick();
        buildEntries();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int COLOR_SHADE = ARGB.color(0.4f, 0x001822);
        int COLOR_BG = ARGB.color(1.0f, 0x001822);

        graphics.fill(0, 0, this.width, this.height, COLOR_SHADE);
        graphics.fillGradient(0, 0, this.width, this.height, COLOR_BG, COLOR_SHADE);

        graphics.text(this.font, fText(FTextStyle.BOLD, "ENCHANTMENT LIST"), this.leftPos, Mth.ceil(this.height * 0.1), COLOR_TEXT, false);

        int end = Math.min(entries.size(), scroll + visibleRows());

        int hovered = rowAt(mouseX, mouseY);
        int y = listTop;
        for (int i = scroll; i < end; i++) {
            EnchantmentHandbookScreen.Entry e = entries.get(i);
            int textY = y + (ROW_H - this.font.lineHeight) / 2;

            if (i == hovered) graphics.fill(listLeft, y, ROW_W, y + ROW_H, COLOR_HOVER);
            graphics.text(this.font, e.label(), listLeft + 4, textY, 0xFFFFFFFF, false);

            y += ROW_H;
        }

        // scrollbar
        if (this.maxScroll > 0) {
            graphics.fill(this.scrollbarX, this.scrollbarY, this.scrollbarX + scrollbarWidth, this.scrollbarY + this.scrollbarHeight, COLOR_SCROLLBAR_TRACK);
            int ty = thumbY();
            graphics.fill(this.scrollbarX, ty, this.scrollbarX + scrollbarWidth, ty + thumbHeight(),
                    this.draggingScrollbar ? 0xFFFFFFFF : COLOR_SCROLLBAR_THUMB);
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    private void buildEntries() {
        if (player() == null) this.onClose();
        entries.clear();

        entries.add(new ButtonEntry(
                titleBenediction,
                () -> benedictionOfTheVerdantMountainsInfo(player())
        ));
        entries.add(new ButtonEntry(
                titleWolfArmorEnhancementKit,
                VWEnchantmentsHandbook::wolfArmorEnhancementKitInfo
        ));
        entries.add(new ButtonEntry(
                titleArcticWinds,
                VWEnchantmentsHandbook::arcticWindsEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleIgnition,
                VWEnchantmentsHandbook::ignitionEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleBloodlust,
                VWEnchantmentsHandbook::bloodlustEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleMight,
                VWEnchantmentsHandbook::mightEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleWithering,
                VWEnchantmentsHandbook::witheringEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titlePoisoning,
                VWEnchantmentsHandbook::poisoningEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleLifting,
                VWEnchantmentsHandbook::liftingEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleGnawing,
                VWEnchantmentsHandbook::gnawingEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleMending,
                VWEnchantmentsHandbook::mendingEnchantmentInfo
        ));
        entries.add(new ButtonEntry(
                titleOozing,
                VWEnchantmentsHandbook::oozingEnchantmentInfo
        ));
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

        EnchantmentHandbookScreen.Entry e = entries.get(index);
        if (e instanceof ButtonEntry(String label, Supplier<String> info)) {
            Minecraft.getInstance().setScreenAndShow(new PageScreen(this, label.toUpperCase(), info));
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
        return maxScroll() > 0 && mx >= x && mx < x + scrollbarWidth && my >= listTop && my < listBottom;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected static Component text(String s) {
        return Component.literal(s);
    }
}
