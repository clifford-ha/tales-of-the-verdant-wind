package cliffordha.totvw.client.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public class ScatteredPageScreen extends Screen {
    private static final int PANEL_WIDTH = 400;
    private static final int PANEL_HEIGHT = 225;
    private static final int PADDING = 10;

    private static final int COLOR_OUTER = 0xFF5C3D1E;
    private static final int COLOR_BG = 0xFF9C7B52;
    private static final int COLOR_INNER = 0xFFEDD9A3;
    private static final int COLOR_LINE = 0xFF7A5230;
    private static final int COLOR_TEXT = 0xff2c2720;

    private final String pageTitle;
    private final List<String> pages;
    private int currentPage = 0;

    private int leftPos;
    private int topPos;

    public ScatteredPageScreen(String pageTitle, String... pages) {
        super(Component.literal(pageTitle));
        this.pageTitle = pageTitle;
        this.pages = Arrays.asList(pages);
    }

    @Override
    protected void init() {
        this.leftPos = (this.width  - PANEL_WIDTH)  / 2;
        this.topPos  = (this.height - PANEL_HEIGHT) / 2;

        int xySize = 20;

        int xposBTN = this.leftPos + PANEL_WIDTH + 3;
        int yposBTN = this.topPos - 3;

        if (this.pages.size() > 1 && this.currentPage >= 0) {
            this.addRenderableWidget(
                    Button.builder(Component.literal("▼"), b -> {
                        if (this.currentPage < this.pages.size() - 1) this.currentPage++;
                    }).bounds(xposBTN, yposBTN + 40, xySize, xySize).build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("▲"), b -> {
                        if (this.currentPage > 0) this.currentPage--;
                    }).bounds(xposBTN, yposBTN + 20, xySize, xySize).build()
            );
        }

        this.addRenderableWidget(
                Button.builder(Component.literal("✖"), b -> this.onClose())
                        .bounds(xposBTN, yposBTN, xySize, xySize)
                        .build()
        );
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        int x = this.leftPos;
        int y = this.topPos;

        // can anyone pls make a screen visualizer...?

        // border
        graphics.fill(x - 3, y - 3, x + PANEL_WIDTH + 3, y + PANEL_HEIGHT + 3, COLOR_OUTER);

        // main bg
        graphics.fill(x, y, x + PANEL_WIDTH, y + PANEL_HEIGHT, COLOR_BG);

        // inner parchment area
        graphics.fill(x + 5, y + 5, x + PANEL_WIDTH - 5, y + PANEL_HEIGHT - 5, COLOR_INNER);

        // title separator
        graphics.fill(x + PADDING, y + 26, x + PANEL_WIDTH - PADDING, y + 27, COLOR_LINE);

        // title
        graphics.text(this.font, this.pageTitle, x + 10, y + 11, COLOR_TEXT, false);

        // scattered pages
        if (!this.pages.isEmpty() && this.currentPage < this.pages.size()) {
            int textAreaWidth = PANEL_WIDTH - (PADDING * 2);
            int textStartY = y + 32;
            int textEndY = y + PANEL_HEIGHT;

            String readMore = (this.pages.size() > 1 && this.currentPage == 0) ? " §8......more§r" : "";
            List<FormattedCharSequence> lines = this.font.split(
                    Component.literal(this.pages.get(this.currentPage) + readMore), textAreaWidth
            );

            int lineY = textStartY;
            int lineHeight = this.font.lineHeight + 2;

            for (FormattedCharSequence line : lines) {
                if (lineY + this.font.lineHeight > textEndY) break;
                graphics.text(this.font, line, x + PADDING, lineY, COLOR_TEXT, false);
                lineY += lineHeight;
            }
        }

        // page indicator
        if (this.pages.size() > 1 && this.currentPage >= 0) {
            String pageCount = String.valueOf(this.pages.size());
            String currentPage = String.valueOf(this.currentPage + 1);

            graphics.text(this.font, "§8" + currentPage + " / " + pageCount, ((this.width - Mth.ceil(PANEL_WIDTH * 0.17) - PANEL_WIDTH)  / 2 ) + PANEL_WIDTH, y + PANEL_HEIGHT - Mth.ceil(PANEL_HEIGHT * 0.07), COLOR_TEXT, false);
        }
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}