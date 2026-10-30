package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.item.pages.FTextStyle;
import cliffordha.totvw.networking.packets.TetherDataPayload;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.TetherData;
import cliffordha.totvw.util.TextUtil;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.*;

import static cliffordha.totvw.client.screen.ConfigScreen.getCenter;
import static cliffordha.totvw.client.screen.ConfigScreen.rescale;

public class TetherScreen extends Screen {
    private record Entry(String id, String name, String search) {}

    private static final int LIST_W = 450;
    private static final int ROW_H = 14;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private final int wolfId;
    private final List<Entry> entries = new ArrayList<>();
    private List<Entry> filtered = new ArrayList<>();

    private static final Identifier ICON = TOTVW.registerID("textures/gui/tether_translucent.png");
    private static final int ICON_TEX_W = 1920;
    private static final int ICON_TEX_H = 1080;

    private EditBox search;
    private boolean onlyListed = false;
    private boolean autoAttack;
    private boolean loadChunks;
    private int scroll = 0;
    private int listLeft, listTop, listBottom;
    private TetherData tetherData;
    private final Screen parentScreen;

    public TetherScreen(int wolfId, TetherData newData, Screen parent) {
        super(text("Tether Manager"));
        this.tetherData = newData;
        this.autoAttack = newData.autoAttackTethered();
        this.loadChunks = newData.tetherChunkTicks();
        this.wolfId = wolfId;
        this.parentScreen = parent;

        for (var e : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            EntityType<?> type = e.getValue();
            if (type == EntityTypes.WOLF) continue;
            if (!DefaultAttributes.hasSupplier(type)) continue;

            String id = e.getKey().identifier().toString();
            String name = type.getDescription().getString();
            entries.add(new Entry(id, name, (name + " " + id).toLowerCase(Locale.ROOT)));
        }
        entries.sort(Comparator.comparing(Entry::name, String.CASE_INSENSITIVE_ORDER));
    }

    private Wolf wolf() {
        Minecraft mc = Minecraft.getInstance();
        return mc.level != null && mc.level.getEntity(wolfId) instanceof Wolf w ? w : null;
    }

    private List<String> blacklist() {
        Wolf wolf = wolf();
        return wolf == null ? List.of() : tetherData.blacklistedEntities();
    }

    private void refresh() {
        String query = search == null ? "" : search.getValue().toLowerCase(Locale.ROOT).trim();
        Set<String> listed = new HashSet<>(blacklist());
        filtered = entries.stream()
                .filter(e -> (!onlyListed || listed.contains(e.id())) && e.search().contains(query))
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
        listTop = Mth.ceil(this.height * 0.25);;
        listBottom = this.height - 20;
        int btnY = Mth.ceil(this.height * 0.15);

        int searchX = Mth.ceil(this.width * 0.2);
        search = new EditBox(this.font, Mth.ceil(this.width * 0.05), btnY + 1, searchX, 18, text("Search"));
        search.setHint(text("Name..."));
        search.setResponder(s -> { scroll = 0; refresh(); });
        this.addRenderableWidget(search);
        this.setInitialFocus(search);

        int btnLeft = listLeft + searchX + 2;
        this.addRenderableWidget(
                Button.builder(text("Filter: " + (onlyListed ? "ON" : "OFF")), b -> {
                    onlyListed = !onlyListed;
                    b.setMessage(text("Filter: " + (onlyListed ? "ON" : "OFF")));
                    scroll = 0;
                    refresh();
                }).tooltip(Tooltip.create(
                        text("Only show blacklisted entities")
                )).bounds(btnLeft, btnY, 70, 20).build()
        );
        this.addRenderableWidget(
                Button.builder(autoAttack ? text("Auto Attack") : mute("Auto Attack"), button -> {
                    autoAttack = !autoAttack;
                    button.setMessage(autoAttack ? text("Auto Attack") : mute("Auto Attack"));
                    update();
                }).tooltip(Tooltip.create(
                        text("When you have less than 50% health, tethered entities will be automatically attacked when they are within 8 blocks")
                )).bounds(btnLeft + 72, btnY, 70, 20).build()
        );
        this.addRenderableWidget(
                Button.builder(loadChunks ? text("Load Chunk") : mute("Load Chunk"), button -> {
                    loadChunks = !loadChunks;
                    button.setMessage(loadChunks ? text("Load Chunk") : mute("Load Chunk"));
                    update();
                }).tooltip(Tooltip.create(
                        text("Allow wolf to load chunks")
                )).bounds(btnLeft + 144, btnY, 70, 20).build()
        );
        this.addRenderableWidget(
                Button.builder(text("✖"), button ->
                        this.onClose()
                ).bounds(Mth.ceil(this.width * 0.91), btnY, 20, 20).build()
        );
        refresh();
    }

    @Override
    public void tick() {
        super.tick();
        Wolf wolf = wolf();
        Minecraft mc = Minecraft.getInstance();
        if (wolf == null || !wolf.isAlive() || mc.player == null
                || mc.player.distanceTo(wolf) > 8 || !Runestone.hasTether(wolf)) {
            this.onClose();
            return;
        }
        refresh();
        update();
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

        int baseHeight = this.height;
        graphics.fill(0, 0, this.width, baseHeight, COLOR_SHADE);

        int imgX = rescale(ICON_TEX_W, 0.2);
        int imgY = rescale(ICON_TEX_H, 0.2);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ICON,
                getCenter(this.width, imgX), getCenter(this.height, imgY),
                0.0F, 0.0F,
                imgX, imgY, // drawn size
                ICON_TEX_W, ICON_TEX_H, // full texture size
                ICON_TEX_W, ICON_TEX_H // full texture size
        );

        graphics.fillGradient(0, 0, this.width, this.height, COLOR_BG, COLOR_SHADE);
        graphics.text(this.font, TextUtil.fText(FTextStyle.BOLD, "Runestone: Tether Manager"), listLeft, Mth.ceil(this.height * 0.1), TITLE_COLOR, false);

        Set<String> listed = new HashSet<>(blacklist());
        int end = Math.min(filtered.size(), scroll + visibleRows());
        int y = listTop;

        for (int i = scroll; i < end; i++) {
            Entry e = filtered.get(i);
            boolean hovered = mouseX >= listLeft && mouseX < listLeft + LIST_W && mouseY >= y && mouseY < y + ROW_H;
            boolean isListed = listed.contains(e.id());

            if (hovered) graphics.fill(listLeft, y, Mth.ceil(this.width * 0.9) + listLeft, y + ROW_H, COLOR_HOVER);

            String mark = isListed ? "•  " : "   ";
            int idWidth = this.font.width(e.id());
            String name = this.font.plainSubstrByWidth(mark + e.name(), LIST_W - idWidth - 12);

            graphics.text(this.font, name, listLeft, y + 3, isListed ? 0xFFFF5555 : 0xFFFFFFFF, false);
            graphics.text(this.font, e.id(), this.width - idWidth - Mth.ceil(this.width * 0.05), y + 3, 0xFF888888, false);
            y += ROW_H;
        }
        if (!onlyListed) {
            graphics.text(this.font, filtered.size() + " / " + entries.size() + " entity types  |  " + listed.size() + " blacklisted",
                    listLeft, listBottom + 8, 0xFFAAAAAA, false);
        } else {
            String name = wolf() == null ? "?" : wolf().getPlainTextName();
            String filter = filtered.size() > 1 ? " entities" : " entity";
            graphics.text(this.font, filtered.size() + filter + " won't be recorded by the Link status of " + name,
                    listLeft, listBottom + 8, 0xFFAAAAAA, false);
        }

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
    public void update() {
        ClientPlayNetworking.send(new TetherDataPayload(wolfId,
                new TetherData(tetherData.tetheredEntities(), tetherData.blacklistedEntities(), autoAttack, loadChunks)
        ));
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        double mx = event.x(), my = event.y();
        if (mx < listLeft || mx >= listLeft + LIST_W || my < listTop || my >= listBottom) return false;

        int index = scroll + (int) ((my - listTop) / ROW_H);
        if (index < 0 || index >= filtered.size()) return false;

        Entry e = filtered.get(index);
        List<String> list = new ArrayList<>(tetherData.blacklistedEntities());
        if (!list.remove(e.id())) list.add(e.id());
        tetherData = TetherData.updateBlacklisted(tetherData, List.copyOf(list));
        update();
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        scroll = Mth.clamp(scroll - (int) Math.signum(scrollY) * 3, 0, maxScroll());
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
    private static Component text(String s) {
        return Component.literal(s);
    }
    private static Component mute(String s) {
        return Component.literal(s).withColor(VWColors.GRAY).withStyle(ChatFormatting.STRIKETHROUGH);
    }
}