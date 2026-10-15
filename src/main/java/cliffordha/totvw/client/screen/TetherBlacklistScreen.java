package cliffordha.totvw.client.screen;

import cliffordha.totvw.networking.packets.TetherBlacklistPayload;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.*;

public class TetherBlacklistScreen extends Screen {
    private record Entry(String id, String name, String search) {}

    private static final int LIST_W = 450;
    private static final int ROW_H = 14;
    private static final int COLOR_BG = 0x80001822;
    private static final int COLOR_HOVER = 0x33FFFFFF;

    private final int wolfId;
    private final List<Entry> entries = new ArrayList<>();
    private List<Entry> filtered = new ArrayList<>();

    private EditBox search;
    private boolean onlyListed = false;
    private int scroll = 0;
    private int listLeft, listTop, listBottom;

    public TetherBlacklistScreen(int wolfId) {
        super(text("Link Blacklist"));
        this.wolfId = wolfId;

        for (var e : BuiltInRegistries.ENTITY_TYPE.entrySet()) {
            EntityType<?> type = e.getValue();
            if (type == EntityTypes.PLAYER) continue;
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
        return wolf == null ? List.of() : WolfAttachment.getTetherBlacklist(wolf);
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
        listLeft = this.width / 2 - LIST_W / 2;
        listTop = 46;
        listBottom = this.height - 20;

        search = new EditBox(this.font, listLeft, 18, 196, 18, text("Search"));
        search.setHint(text("Search name or mod id..."));
        search.setResponder(s -> { scroll = 0; refresh(); });
        this.addRenderableWidget(search);
        this.setInitialFocus(search);

        int btnLeft = listLeft + 200;
        this.addRenderableWidget(
                Button.builder(text("Filter: " + (onlyListed ? "ON" : "OFF")), b -> {
                    onlyListed = !onlyListed;
                    b.setMessage(text("Filter: " + (onlyListed ? "ON" : "OFF")));
                    scroll = 0;
                    refresh();
                }).tooltip(Tooltip.create(
                        text("Only show blacklisted entities")
                )).bounds(btnLeft, 17, 100, 20).build()
        );
        this.addRenderableWidget(
                Button.builder(text("✖"), button ->
                        this.onClose()
                ).bounds((int) (btnLeft + Math.floor(btnLeft * 0.5)), 17, 20, 20).build()
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
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.fill(0, 0, this.width, this.height, 0x80000000);
        graphics.fill(0, 0, this.width, this.height, COLOR_BG);

        Set<String> listed = new HashSet<>(blacklist());
        int end = Math.min(filtered.size(), scroll + visibleRows());
        int y = listTop;

        for (int i = scroll; i < end; i++) {
            Entry e = filtered.get(i);
            boolean hovered = mouseX >= listLeft && mouseX < listLeft + LIST_W && mouseY >= y && mouseY < y + ROW_H;
            boolean isListed = listed.contains(e.id());

            if (hovered) graphics.fill(listLeft, y, listLeft + LIST_W, y + ROW_H, COLOR_HOVER);

            String mark = isListed ? "•  " : "   ";
            int idWidth = this.font.width(e.id());
            String name = this.font.plainSubstrByWidth(mark + e.name(), LIST_W - idWidth - 12);

            graphics.text(this.font, name, listLeft + 2, y + 3, isListed ? 0xFFFF5555 : 0xFFFFFFFF, false);
            graphics.text(this.font, e.id(), listLeft + LIST_W - idWidth - 2, y + 3, 0xFF888888, false);
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

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (super.mouseClicked(event, doubleClick)) return true;

        double mx = event.x(), my = event.y();
        if (mx < listLeft || mx >= listLeft + LIST_W || my < listTop || my >= listBottom) return false;

        int index = scroll + (int) ((my - listTop) / ROW_H);
        if (index < 0 || index >= filtered.size()) return false;

        Entry e = filtered.get(index);
        boolean listed = blacklist().contains(e.id());
        ClientPlayNetworking.send(new TetherBlacklistPayload(wolfId, e.id(), !listed));
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
}