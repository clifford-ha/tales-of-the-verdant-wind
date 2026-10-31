package cliffordha.totvw.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class PageScreen extends Screen {
    public static final int COLOR_LINE = 0x66FFFFFF;
    public static final int COLOR_TEXT = 0xFFFFFFFF;
    public static final int COLOR_SCROLLBAR_TRACK = 0x33FFFFFF;
    public static final int COLOR_SCROLLBAR_THUMB = 0xAAFFFFFF;
    private static final float PADDING_RATIO = 0.05F;
    private static final int PADDING = 10;

    private final String pageTitle;
    private List<String> pages;
    private final List<FormattedCharSequence> allLines = new ArrayList<>();
    private final @Nullable Supplier<String[]> liveSupplier;
    private int textAreaWidth;
    private int refreshTimer = 0;

    private int leftPos;
    private int topPos;
    private int panelWidth;
    private int panelHeight;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int textStartY;
    private int lineHeight;
    private int visibleLines;

    private boolean draggingScrollbar = false;
    private int scrollbarX;
    private int scrollbarY;
    private static final int scrollbarWidth = 6;
    private int scrollbarHeight;

    public PageScreen(String pageTitle, String... pages) {
        super(Component.literal(pageTitle));
        this.pageTitle = pageTitle;
        this.pages = Arrays.asList(pages);
        this.liveSupplier = null;
    }
    public PageScreen(String pageTitle, Supplier<String[]> liveSupplier) {
        super(Component.literal(pageTitle));
        this.pageTitle = pageTitle;
        this.pages = Arrays.asList(liveSupplier.get());
        this.liveSupplier = liveSupplier;
    }

    @Override
    protected void init() {
        this.panelWidth = Mth.floor(this.width * (1.0F - PADDING_RATIO * 2));
        this.panelHeight = Mth.floor(this.height * (1.0F - PADDING_RATIO * 2));
        this.leftPos = Mth.floor(this.width * PADDING_RATIO);
        this.topPos = Mth.floor(this.height * PADDING_RATIO);

        int xySize = 20;
        int xposBTN = this.leftPos + this.panelWidth - xySize - 3;
        int yposBTN = this.topPos + 3;

        this.addRenderableWidget(
                Button.builder(Component.literal("✖"), b -> this.onClose())
                        .bounds(xposBTN, yposBTN, xySize, xySize)
                        .build()
        );

        this.textAreaWidth = this.panelWidth - (PADDING * 2) - scrollbarWidth - 6;
        this.textStartY = this.topPos + 32;
        int textEndY = this.topPos + this.panelHeight - PADDING;
        this.lineHeight = this.font.lineHeight + 2;
        this.visibleLines = Math.max(1, (textEndY - this.textStartY) / this.lineHeight);

        this.buildLines();
        this.scrollOffset = 0;

        this.scrollbarX = this.leftPos + this.panelWidth - PADDING - scrollbarWidth;
        this.scrollbarY = this.textStartY;
        this.scrollbarHeight = textEndY - this.textStartY;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int COLOR_SHADE = ARGB.color(0.4f, 0x001822);
        int COLOR_BG = ARGB.color(1.0f, 0x001822);
        int x = this.leftPos;
        int y = this.topPos;

        if (liveSupplier != null) {
            graphics.fill(0, 0, this.width, this.height, COLOR_SHADE);
            graphics.fillGradient(0, 0, this.width, this.height, COLOR_BG, COLOR_SHADE);

        } else {
            int fillX = x + this.panelWidth;
            int fillY = y + this.panelHeight;
            graphics.fill(0, 0, this.width, this.height, COLOR_SHADE);
            graphics.fill(x, y, fillX, fillY, COLOR_SHADE);
        }

        // title separator
        graphics.fill(x + PADDING, y + 26, x + this.panelWidth - PADDING, y + 27, COLOR_LINE);

        // title
        graphics.text(this.font, this.pageTitle, x + PADDING, y + 11, COLOR_TEXT, false);

        // text
        int lineY = this.textStartY;
        int end = Math.min(this.allLines.size(), this.scrollOffset + this.visibleLines);
        for (int i = this.scrollOffset; i < end; i++) {
            graphics.text(this.font, this.allLines.get(i), x + PADDING, lineY, COLOR_TEXT, false);
            lineY += this.lineHeight;
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

    private void buildLines() {
        this.allLines.clear();
        StringBuilder combined = new StringBuilder();
        for (int i = 0; i < this.pages.size(); i++) {
            combined.append(this.pages.get(i));
            if (i < this.pages.size() - 1) combined.append(" ");
        }
        this.allLines.addAll(this.font.split(Component.literal(combined.toString()), this.textAreaWidth));
        this.maxScroll = Math.max(0, this.allLines.size() - this.visibleLines);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.liveSupplier == null) return;
        if (++this.refreshTimer < 5) return;
        this.refreshTimer = 0;

        List<String> next = Arrays.asList(this.liveSupplier.get());
        if (next.equals(this.pages)) return;

        int keepScroll = this.scrollOffset;
        this.pages = next;
        this.buildLines();
        this.scrollOffset = Mth.clamp(keepScroll, 0, this.maxScroll);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isOverScrollbar(event.x(), event.y())) {
            this.draggingScrollbar = true;
            setScrollFromMouse(event.y());
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (this.draggingScrollbar) {
            setScrollFromMouse(event.y());
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.draggingScrollbar) {
            this.draggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public void mouseMoved(double x, double y) {
        super.mouseMoved(x, y);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.maxScroll > 0) {
            this.scrollOffset = Mth.clamp(this.scrollOffset - (int) Math.signum(scrollY) * 3, 0, this.maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int thumbHeight() {
        return Math.max(12, (int) ((float) this.visibleLines / this.allLines.size() * this.scrollbarHeight));
    }

    private int thumbY() {
        int travel = this.scrollbarHeight - thumbHeight();
        return this.scrollbarY + (this.maxScroll == 0 ? 0 : (int) ((float) this.scrollOffset / this.maxScroll * travel));
    }

    private void setScrollFromMouse(double my) {
        int travel = this.scrollbarHeight - thumbHeight();
        if (travel <= 0) return;
        float t = (float) ((my - this.scrollbarY - thumbHeight() / 2.0) / travel);
        this.scrollOffset = Mth.clamp(Math.round(t * this.maxScroll), 0, this.maxScroll);
    }

    private boolean isOverScrollbar(double mx, double my) {
        return this.maxScroll > 0
                && mx >= this.scrollbarX - 2 && mx < this.scrollbarX + scrollbarWidth + 2
                && my >= this.scrollbarY && my < this.scrollbarY + this.scrollbarHeight;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}