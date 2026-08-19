package cliffordha.totvw.client.screen;

import cliffordha.totvw.registry.VWColors;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScatteredPageScreen extends Screen {
    private static final float PADDING_RATIO = 0.05F;
    private static final int PADDING = 10;

    private static final int COLOR_BG = 0x80001822;
    private static final int COLOR_LINE = 0x66FFFFFF;
    private static final int COLOR_TEXT = 0xFFFFFFFF;
    private static final int COLOR_SCROLLBAR_TRACK = 0x33FFFFFF;
    private static final int COLOR_SCROLLBAR_THUMB = 0xAAFFFFFF;

    private final int type;
    private final String pageTitle;
    private final List<String> pages;
    private final List<FormattedCharSequence> allLines = new ArrayList<>();

    private int leftPos;
    private int topPos;
    private int panelWidth;
    private int panelHeight;

    private int scrollOffset = 0;
    private int maxScroll = 0;
    private int textStartY;
    private int lineHeight;
    private int visibleLines;

    private int scrollbarX;
    private int scrollbarY;
    private final int scrollbarWidth = 6;
    private int scrollbarHeight;

    public ScatteredPageScreen(int type, String pageTitle, String... pages) {
        super(Component.literal(pageTitle));
        this.pageTitle = pageTitle;
        this.pages = Arrays.asList(pages);
        this.type = type;
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

        int textAreaWidth = this.panelWidth - (PADDING * 2) - this.scrollbarWidth - 6;
        this.textStartY = this.topPos + 32;
        int textEndY = this.topPos + this.panelHeight - PADDING;
        this.lineHeight = this.font.lineHeight + 2;
        this.visibleLines = Math.max(1, (textEndY - this.textStartY) / this.lineHeight);

        this.allLines.clear();
        StringBuilder combined = new StringBuilder();

        for (int i = 0; i < this.pages.size(); i++) {
            combined.append(this.pages.get(i));
            if (i < this.pages.size() - 1) combined.append(" ");
        }
        this.allLines.addAll(this.font.split(Component.literal(combined.toString()), textAreaWidth));

        this.maxScroll = Math.max(0, this.allLines.size() - this.visibleLines);
        this.scrollOffset = 0;

        this.scrollbarX = this.leftPos + this.panelWidth - PADDING - this.scrollbarWidth;
        this.scrollbarY = this.textStartY;
        this.scrollbarHeight = textEndY - this.textStartY;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.leftPos;
        int y = this.topPos;
        int bgColor;
        int textColor;

        switch (this.type) {
            case 1 -> {
                bgColor = VWColors.setColor(0x241E1C, 90);
                textColor = VWColors.setColor(0xFBFBFB);
            }
            case 2 -> {
                bgColor = VWColors.setColor(0xF9F6F0, 200);
                textColor = VWColors.setColor(0x241E1C);
            }
            default -> {
                bgColor = COLOR_BG;
                textColor = COLOR_TEXT;
            }
        }

        // background
        graphics.fill((int) Math.floor(x * PADDING_RATIO), (int) Math.floor(y * PADDING_RATIO), this.width, this.height, 0x80000000);
        graphics.fill(x, y, x + this.panelWidth, y + this.panelHeight, bgColor);

        // title separator
        graphics.fill(x + PADDING, y + 26, x + this.panelWidth - PADDING, y + 27, COLOR_LINE);

        // title
        graphics.text(this.font, this.pageTitle, x + PADDING, y + 11, textColor, false);

        // text
        int lineY = this.textStartY;
        int end = Math.min(this.allLines.size(), this.scrollOffset + this.visibleLines);
        for (int i = this.scrollOffset; i < end; i++) {
            graphics.text(this.font, this.allLines.get(i), x + PADDING, lineY, textColor, false);
            lineY += this.lineHeight;
        }

        // scrollbar
        if (this.maxScroll > 0) {
            graphics.fill(this.scrollbarX, this.scrollbarY, this.scrollbarX + this.scrollbarWidth, this.scrollbarY + this.scrollbarHeight, COLOR_SCROLLBAR_TRACK);

            int thumbHeight = Math.max(12, (int) ((float) this.visibleLines / this.allLines.size() * this.scrollbarHeight));
            int thumbY = this.scrollbarY + (int) ((float) this.scrollOffset / this.maxScroll * (this.scrollbarHeight - thumbHeight));

            graphics.fill(this.scrollbarX, thumbY, this.scrollbarX + this.scrollbarWidth, thumbY + thumbHeight, COLOR_SCROLLBAR_THUMB);
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
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

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}