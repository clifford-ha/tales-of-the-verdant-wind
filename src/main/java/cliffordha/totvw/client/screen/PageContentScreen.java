package cliffordha.totvw.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class PageContentScreen extends Screen {
    private static final int PANEL_WIDTH  = 400;
    private static final int PANEL_HEIGHT = 200;
    private static final int PADDING      = 10;

    private static final int COLOR_OUTER  = 0xFF5C3D1E;
    private static final int COLOR_BG     = 0xFF9C7B52;
    private static final int COLOR_INNER  = 0xFFEDD9A3;
    private static final int COLOR_LINE   = 0xFF7A5230;
    private static final int COLOR_TEXT   = 0xff2c2720;
    private static final int SCREEN_OVERLAY = 0x20000000;

    private final String pageTitle;
    private final List<String> pages;
    private int currentPage = 0;

    private int leftPos;
    private int topPos;

    public PageContentScreen(String pageTitle, String... pages) {
        super(Component.literal(pageTitle));
        this.pageTitle = pageTitle;
        this.pages = Arrays.asList(pages);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width  - PANEL_WIDTH)  / 2;
        this.topPos  = (this.height - PANEL_HEIGHT) / 2;

        int navY = this.topPos + PANEL_HEIGHT - 30;

        this.addRenderableWidget(
                Button.builder(Component.literal("◀"), b -> {
                    if (this.currentPage > 0) this.currentPage--;
                }).bounds(this.leftPos + PADDING, navY, 20, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(Component.literal("▶"), b -> {
                    if (this.currentPage < this.pages.size() - 1) this.currentPage++;
                }).bounds(this.leftPos + PANEL_WIDTH - PADDING - 20, navY, 20, 20).build()
        );

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), b -> this.onClose())
                        .bounds(this.width / 2 - 40, this.topPos + PANEL_HEIGHT + 6, 80, 20)
                        .build()
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.leftPos;
        int y = this.topPos;
        int w = PANEL_WIDTH;
        int h = PANEL_HEIGHT;

        int titleCenter = (int) (((float) w / 2) - (w * 0.05f));

        graphics.fill(x, y, x + w, y + h, SCREEN_OVERLAY);

        // border
        graphics.fill(x - 3, y - 3, x + w + 3, y + h + 3, COLOR_OUTER);
        // main bg
        graphics.fill(x, y, x + w, y + h, COLOR_BG);
        // inner parchment area
        graphics.fill(x + 5, y + 5, x + w - 5, y + h - 5, COLOR_INNER);
        // title separator
        graphics.fill(x + PADDING, y + 26, x + w - PADDING, y + 27, COLOR_LINE);


        // title
        graphics.text(this.font, this.pageTitle, titleCenter, y + 10, COLOR_TEXT, false);

        // scattered pages
        if (!this.pages.isEmpty() && this.currentPage < this.pages.size()) {
            int textAreaWidth = w - (PADDING * 2);
            int textStartY    = y + 32;
            int textEndY      = y + h - 36;

            List<FormattedCharSequence> lines = this.font.split(
                    Component.literal(this.pages.get(this.currentPage)), textAreaWidth
            );

            int lineY = textStartY;
            int lineHeight = this.font.lineHeight + 2;

            for (FormattedCharSequence line : lines) {
                if (lineY + this.font.lineHeight > textEndY) break;
                graphics.text(this.font, line, x + PADDING, lineY, COLOR_TEXT, false);
                lineY += lineHeight;
            }
        }

        // Page indicator
        if (this.pages.size() > 1) {
            String indicator = (this.currentPage + 1) + " / " + this.pages.size();
            graphics.text(this.font, indicator, titleCenter, y + h - 26, COLOR_TEXT, false);
        }
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}