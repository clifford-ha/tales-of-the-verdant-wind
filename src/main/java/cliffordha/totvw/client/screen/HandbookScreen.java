package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.item.pages.FTextStyle;
import cliffordha.totvw.item.pages.handbooks.EffectHandbook;
import cliffordha.totvw.item.pages.handbooks.EnchantmentHandbook;
import cliffordha.totvw.item.pages.handbooks.FeatureHandbook;
import cliffordha.totvw.item.pages.handbooks.ItemHandbook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Supplier;

import static cliffordha.totvw.util.TextUtil.fText;

public class HandbookScreen extends VWScreen {
    public record Entry(String label, Supplier<String> info) {}

    private static final int ROW_H = 20;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private static final List<Entry> ENCHANTMENT_ENTRIES = List.of(
            new Entry(EnchantmentHandbook.titleBenediction, () -> EnchantmentHandbook.benedictionInfo(player())),
            new Entry(EnchantmentHandbook.titleEnhancementKit, EnchantmentHandbook::enhancementKitInfo),
            new Entry(EnchantmentHandbook.titleArcticWinds, EnchantmentHandbook::arcticWindsEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleIgnition, EnchantmentHandbook::ignitionEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleBloodlust, EnchantmentHandbook::bloodlustEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleMight, EnchantmentHandbook::mightEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleWithering, EnchantmentHandbook::witheringEnchantmentInfo),
            new Entry(EnchantmentHandbook.titlePoisoning, EnchantmentHandbook::poisoningEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleLifting, EnchantmentHandbook::liftingEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleGnawing, EnchantmentHandbook::gnawingEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleMending, EnchantmentHandbook::mendingEnchantmentInfo),
            new Entry(EnchantmentHandbook.titleOozing, EnchantmentHandbook::oozingEnchantmentInfo)
    );
    private static final List<Entry> ITEM_ENTRIES = List.of(
            new Entry(ItemHandbook.titleVerixiumResources, ItemHandbook::verixiumResourcesInfo),
            new Entry(ItemHandbook.titleVerixiumChunkSynthesis, ItemHandbook::verixiumChunkSynthesisInfo),
            new Entry(ItemHandbook.titleLodestoneWindCore, ItemHandbook::lodestoneWindCoreInfo),
            new Entry(ItemHandbook.titleScatteredPages, ItemHandbook::scatteredPagesInfo),
            new Entry(ItemHandbook.titleIridescentGlass, ItemHandbook::iridescentGlassInfo),
            new Entry(ItemHandbook.titleVerdantSpruceStorageBox, ItemHandbook::verdantSpruceStorageBoxInfo),
            new Entry(ItemHandbook.titleVerixiumArmorUpgradeTemplate, ItemHandbook::verixiumArmorUpgradeTemplateInfo),
            new Entry(ItemHandbook.titleGenesisRunestonePlate, ItemHandbook::genesisRunestonePlateInfo),
            new Entry(ItemHandbook.titleEfflorescenceRunestonePlate, ItemHandbook::efflorescenceRunestonePlateInfo),
            new Entry(ItemHandbook.titleSoulRunestonePlate, ItemHandbook::soulRunestonePlateInfo),
            new Entry(ItemHandbook.titleTetherRunestonePlate, ItemHandbook::tetherRunestonePlateInfo),
            new Entry(ItemHandbook.titleHavocRunestonePlate, ItemHandbook::havocRunestonePlateInfo)
    );
    private static final List<Entry> EFFECT_ENTRIES = List.of(
            new Entry(EffectHandbook.titleBlessing, EffectHandbook::blessingOfTheVerdantWind),
            new Entry(EffectHandbook.titleAmplifiedMight, EffectHandbook::amplifiedMight),
            new Entry(EffectHandbook.titleBloodlust, EffectHandbook::bloodlust),
            new Entry(EffectHandbook.titleParalyze, EffectHandbook::paralyze),
            new Entry(EffectHandbook.titleHavoc, EffectHandbook::havoc),
            new Entry(EffectHandbook.titleWindVeil, EffectHandbook::windVeil)
    );
    private static final List<Entry> FEATURE_ENTRIES = List.of(
            new Entry(FeatureHandbook.titleVerdantTypeWolves, FeatureHandbook::verdantTypeWolves),
            new Entry(FeatureHandbook.titleVerdantTypeVillagers, FeatureHandbook::verdantTypeVillagers),
            new Entry(FeatureHandbook.titleRevivalByProxy, FeatureHandbook::revivalByProxy),
            new Entry(FeatureHandbook.titleBenedictionReturnPosition, FeatureHandbook::benedictionSpawnpointInfo),
            new Entry(FeatureHandbook.titleTrustSystem, FeatureHandbook::trustSystemInfo)
    );

    private static List<Entry> entries(int type) {
        switch (type) {
            case 0 -> {return ENCHANTMENT_ENTRIES;}
            case 1 -> {return EFFECT_ENTRIES;}
            case 2 -> {return ITEM_ENTRIES;}
            case 3 -> {return FEATURE_ENTRIES;}
            default -> {return List.of();}
        }
    }

    private final String title;
    private static List<Entry> entries;

    private int listLeft, listRight, listTop, listBottom;

    private static String getTitle(int type) {
        String title;
        switch (type) {
            case 0 -> title = "ENCHANTMENT";
            case 1 -> title = "EFFECT";
            case 2 -> title = "ITEM";
            case 3 -> title = "FEATURE";
            default -> title = "INVALID";
        }
        return title + " ENTRIES";
    }

    public static void create(int type) {
        Minecraft.getInstance().setScreenAndShow(new HandbookScreen(getTitle(type), type));
    }

    public HandbookScreen(String pageTitle, int type) {
        super(Component.literal(TOTVW.MOD_NAME_LONG));
        this.title = pageTitle;
        entries = entries(type);
    }

    private static Player player() {
        return Minecraft.getInstance().player;
    }

    @Override
    protected void init() {
        initPanel();

        listRight = Mth.ceil(this.width * 0.6f);
        listLeft = (int) ((((double) this.width / 2) - (double) listRight / 2) * 0.3);
        listTop = Mth.ceil(this.height * 0.17f);
        listBottom = this.height - Mth.ceil(this.height * 0.1f);

        setupTrack(listTop, listBottom, (listBottom - listTop) / ROW_H, entries.size());
    }

    @Override
    protected int scrollStep() {
        return 1;
    }

    @Override
    public void tick() {
        if (player() == null) {
            this.onClose();
            return;
        }
        super.tick();
    }

    private int rowAt(double mx, double my) {
        if (mx < listLeft || mx >= listRight || my < listTop || my >= listBottom) return -1;
        int index = scroll + (int) ((my - listTop) / ROW_H);
        return index >= 0 && index < entries.size() ? index : -1;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        drawBackdrop(graphics);

        graphics.text(this.font, fText(FTextStyle.BOLD, this.title), listLeft, Mth.ceil(this.height * 0.12), COLOR_TEXT, false);

        int hovered = rowAt(mouseX, mouseY);
        int end = Math.min(entries.size(), scroll + visibleItems);
        int y = listTop;
        for (int i = scroll; i < end; i++) {
            if (i == hovered) graphics.fill(listLeft, y, listRight, y + ROW_H, COLOR_HOVER);
            graphics.text(this.font, entries.get(i).label(), listLeft + 8, y + (ROW_H - this.font.lineHeight) / 2, COLOR_TEXT, false);
            y += ROW_H;
        }

        drawScrollbar(graphics);
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        int index = rowAt(event.x(), event.y());
        if (index < 0) return false;

        Entry e = entries.get(index);

        if (entries.equals(entries(1))) {
            Minecraft.getInstance().setScreenAndShow(new EffectHandbookScreen(this, e.label().toUpperCase(), e.info(), entries.indexOf(e)));
        } else {
            Minecraft.getInstance().setScreenAndShow(new PageScreen(this, e.label().toUpperCase(), e.info()));
        }
        return true;
    }
}