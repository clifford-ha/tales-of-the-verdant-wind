package cliffordha.totvw.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ScrollableTextScreen extends VWScreen {
    private final List<FormattedCharSequence> allLines = new ArrayList<>();
    private final @Nullable Supplier<?> liveSupplier;
    private String content;

    private int textAreaWidth;
    private int textStartY;
    private int lineHeight;
    private int refreshTimer = 0;

    protected ScrollableTextScreen(Component title, String content, @Nullable Supplier<?> liveSupplier) {
        super(title);
        this.content = content;
        this.liveSupplier = liveSupplier;
    }

    @Override
    protected void init() {
        initPanel();

        this.textAreaWidth = this.panelWidth - (PADDING * 2) - SCROLLBAR_W - 6;
        this.textStartY = this.topPos + HEADER_H;
        int textEndY = this.topPos + this.panelHeight - PADDING;
        this.lineHeight = this.font.lineHeight + 2;

        setupTrack(this.textStartY, textEndY, (textEndY - this.textStartY) / this.lineHeight, 0);
        buildLines();
        this.scroll = 0;
    }

    protected String titleText() {
        return this.title.getString();
    }

    protected boolean shouldStayOpen() {
        return true;
    }

    private void buildLines() {
        this.allLines.clear();
        this.allLines.addAll(this.font.split(Component.literal(this.content), this.textAreaWidth));
        updateTotal(this.allLines.size());
    }

    @Override
    public void tick() {
        super.tick();
        if (!shouldStayOpen()) {
            this.onClose();
            return;
        }
        if (this.liveSupplier == null) return;
        if (++this.refreshTimer < 5) return;
        this.refreshTimer = 0;

        String next = (String) this.liveSupplier.get();
        if (next.equals(this.content)) return;

        int keepScroll = this.scroll;
        this.content = next;
        buildLines();
        this.scroll = Mth.clamp(keepScroll, 0, this.maxScroll);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        drawBackdrop(graphics);

        int x = this.leftPos;
        int y = this.topPos;

        graphics.fill(x + PADDING, y + 26, x + this.panelWidth - PADDING, y + 27, COLOR_LINE);
        graphics.text(this.font, titleText(), x + PADDING, y + 11, COLOR_TEXT, false);

        int lineY = this.textStartY;
        int end = Math.min(this.allLines.size(), this.scroll + this.visibleItems);
        for (int i = this.scroll; i < end; i++) {
            graphics.text(this.font, this.allLines.get(i), x + PADDING, lineY, COLOR_TEXT, false);
            lineY += this.lineHeight;
        }

        drawScrollbar(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}