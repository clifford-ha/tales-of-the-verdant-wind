package cliffordha.totvw.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public abstract class VWScreen extends Screen {
    public record Entry(String label, Supplier<UUID> info) {}

    public static final int COLOR_LINE = 0x66FFFFFF;
    public static final int COLOR_TEXT = 0xFFFFFFFF;
    public static final int COLOR_SCROLLBAR_TRACK = 0x33FFFFFF;
    public static final int COLOR_SCROLLBAR_THUMB = 0xAAFFFFFF;
    public static final int COLOR_SCROLLBAR_THUMB_ACTIVE = 0xFFFFFFFF;
    public static final int BACKDROP_RGB = 0x001822;

    public static final float PADDING_RATIO = 0.05F;
    public static final int PADDING = 10;
    public static final int XY_SIZE = 20;
    public static final int SCROLLBAR_W = 6;
    public static final int HEADER_H = 32;

    // panel
    protected int leftPos, topPos, panelWidth, panelHeight;

    // scrolls
    protected int scroll, maxScroll, visibleItems, totalItems;
    protected int trackTop, trackHeight, scrollbarX;
    protected boolean draggingScrollbar = false;

    protected VWScreen(Component title) {
        super(title);
    }

    // panel and add close button
    protected void initPanel() {
        this.panelWidth = Mth.floor(this.width * (1.0F - PADDING_RATIO * 2));
        this.panelHeight = Mth.floor(this.height * (1.0F - PADDING_RATIO * 2));
        this.leftPos = Mth.floor(this.width * PADDING_RATIO);
        this.topPos = Mth.floor(this.height * PADDING_RATIO);

        this.addRenderableWidget(
                Button.builder(Component.literal("✖"), b -> this.onCloseButton())
                        .bounds(closeButtonX(), this.topPos + 3, XY_SIZE, XY_SIZE)
                        .build()
        );
    }

    protected List<Entry> entries() {
        return List.of();
    }

    protected int closeButtonX() {
        return this.leftPos + this.panelWidth - XY_SIZE - 3;
    }

    protected void onCloseButton() {
        this.onClose();
    }

    protected void setupTrack(int top, int bottom, int visible, int total) {
        this.trackTop = top;
        this.trackHeight = bottom - top;
        this.scrollbarX = this.leftPos + this.panelWidth - PADDING - SCROLLBAR_W;
        this.visibleItems = Math.max(1, visible);
        updateTotal(total);
    }

    protected void updateTotal(int total) {
        this.totalItems = total;
        this.maxScroll = Math.max(0, total - this.visibleItems);
        this.scroll = Mth.clamp(this.scroll, 0, this.maxScroll);
    }

    protected int scrollStep() {
        return 3;
    }

    protected int backdropRgb() {
        return BACKDROP_RGB;
    }

    protected void drawBackdrop(GuiGraphicsExtractor graphics) {
        int shade = ARGB.color(0.4f, backdropRgb());
        int bg = ARGB.color(1.0f, backdropRgb());
        graphics.fill(0, 0, this.width, this.height, shade);
        graphics.fillGradient(0, 0, this.width, this.height, bg, shade);
    }

    protected void drawScrollbar(GuiGraphicsExtractor graphics) {
        if (this.maxScroll <= 0) return;
        graphics.fill(this.scrollbarX, this.trackTop, this.scrollbarX + SCROLLBAR_W, this.trackTop + this.trackHeight, COLOR_SCROLLBAR_TRACK);
        int ty = thumbY();
        graphics.fill(this.scrollbarX, ty, this.scrollbarX + SCROLLBAR_W, ty + thumbHeight(),
                this.draggingScrollbar ? COLOR_SCROLLBAR_THUMB_ACTIVE : COLOR_SCROLLBAR_THUMB);
    }

    private int thumbHeight() {
        return Math.max(12, (int) ((float) this.visibleItems / Math.max(1, this.totalItems) * this.trackHeight));
    }

    private int thumbY() {
        int travel = this.trackHeight - thumbHeight();
        return this.trackTop + (this.maxScroll == 0 ? 0 : (int) ((float) this.scroll / this.maxScroll * travel));
    }

    private void setScrollFromMouse(double my) {
        int travel = this.trackHeight - thumbHeight();
        if (travel <= 0) return;
        float t = (float) ((my - this.trackTop - thumbHeight() / 2.0) / travel);
        this.scroll = Mth.clamp(Math.round(t * this.maxScroll), 0, this.maxScroll);
    }

    private boolean isOverScrollbar(double mx, double my) {
        return this.maxScroll > 0
                && mx >= this.scrollbarX - 2 && mx < this.scrollbarX + SCROLLBAR_W + 2
                && my >= this.trackTop && my < this.trackTop + this.trackHeight;
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
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (this.maxScroll > 0) {
            this.scroll = Mth.clamp(this.scroll - (int) Math.signum(scrollY) * scrollStep(), 0, this.maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    protected static Minecraft mc() {
        return Minecraft.getInstance();
    }
}