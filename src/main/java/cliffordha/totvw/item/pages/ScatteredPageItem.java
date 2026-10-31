package cliffordha.totvw.item.pages;

import cliffordha.totvw.client.screen.PageScreen;
import cliffordha.totvw.client.screen.WolfPageScreen;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.status.WolfStats;
import cliffordha.totvw.item.pages.contents.FirstBookSet;
import cliffordha.totvw.item.pages.contents.MiscBookSet;
import cliffordha.totvw.item.pages.handbooks.VWEffectsHandbook;
import cliffordha.totvw.item.pages.handbooks.VWEnchantmentsHandbook;
import cliffordha.totvw.item.pages.handbooks.VWFeaturesHandbook;
import cliffordha.totvw.item.pages.handbooks.VWItemsHandbook;
import cliffordha.totvw.registry.VWColors;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static cliffordha.totvw.item.pages.FTextColor.*;
import static cliffordha.totvw.item.pages.FTextStyle.*;
import static cliffordha.totvw.util.TextUtil.*;

public class ScatteredPageItem extends Item {
    private static final String ENCHANTMENTS_HANDBOOK_TITLE = "§lEnchantments Handbook§r";
    private static final String EFFECTS_HANDBOOK_TITLE = "§lEffects Handbook§r";
    private static final String ITEMS_HANDBOOK_TITLE = "§lItems Handbook§r";
    private static final String FEATURES_HANDBOOK_TITLE = "§lFeatures Handbook§r";

    private final int pageID;

    public ScatteredPageItem(Properties properties, int pageID) {
        super(properties);
        this.pageID = pageID;
    }

    public static final String[] invalidRef = addPage(
            cText(RED, "Oopsies! The reference no. appears to be invalid. :]"));

    public static final String[] invalidPlayer = addPage(
            cText(RED, "Oopsies! The player who called this action is missing or null :]"));

    public static final String[] invalidEntity = addPage(
            cText(RED, "Oopsies! The entity this page is looking for is missing or null :]"));

    public static final String[] invalidInSurvival = addPage(
            "Oops! The contents of this page are not available in creative mode :]"
                    + nextLine
                    + fText(UNDERLINED, cText(GRAY,"You can disable this feature (Allow Lore Spoilers) in the config file.")));

    private String getTitle(Player player, int title) {
        if (this.pageID == -2) {
            return player.getPlainTextName() + "'s Stats";
        }
        return ScatteredPageTitle.fromId(title)
                .map(t -> addTitle(t.getTitle()))
                .orElse(addTitle(ScatteredPageTitle.SP_0.getTitle()));
    }


    private static String[] getPages(Player player, int contents) {
        if (player == null) return invalidPlayer;

        if (contents == -2) {
            return MiscBookSet.get(player, -2);
        }

        if (player.isCreative() && !VWConfig.get().CLIENT_ALLOW_LORE_SPOILERS) {
            return invalidInSurvival;
        }

        if (contents >= 1001 && contents <= 2000) {
            return FirstBookSet.get(player, contents);
        } else if (contents <= 1000) {
            return MiscBookSet.get(player, contents);
        }
        return invalidRef;
    }

    public static void showSpecifiedContent(LivingEntity caller, String title, String[] pages) {
        if (caller.level().isClientSide()) {
            setScreen(title, pages);
        }
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            switch (this.pageID) {
                case -2 -> openStatScreen(player);

                case 2006 -> openScreen(ENCHANTMENTS_HANDBOOK_TITLE, VWEnchantmentsHandbook.ENCHANTMENTS_HANDBOOK_CONTENTS(player));
                case 2007 -> openScreen(EFFECTS_HANDBOOK_TITLE, VWEffectsHandbook.EFFECTS_HANDBOOK_CONTENTS());
                case 2008 -> openScreen(ITEMS_HANDBOOK_TITLE, VWItemsHandbook.ITEMS_HANDBOOK_CONTENTS());
                case 2009 -> openScreen(FEATURES_HANDBOOK_TITLE, VWFeaturesHandbook.FEATURES_HANDBOOK_CONTENTS());
                default -> openScreen(getTitle(player, pageID), getPages(player, pageID));
            }
        }

        float random = Math.min(player.getRandom().nextFloat() + 0.5f, 1.0f);
        level.playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), random, random);
        return InteractionResult.SUCCESS;
    }

    private void openScreen(String title, String[] pages) {
        switch (title) {
            case ENCHANTMENTS_HANDBOOK_TITLE -> setScreen(ENCHANTMENTS_HANDBOOK_TITLE, pages);
            case EFFECTS_HANDBOOK_TITLE -> setScreen(EFFECTS_HANDBOOK_TITLE, pages);
            case ITEMS_HANDBOOK_TITLE -> setScreen(ITEMS_HANDBOOK_TITLE, pages);
            case FEATURES_HANDBOOK_TITLE -> setScreen(FEATURES_HANDBOOK_TITLE, pages);
            default -> setScreen(title, pages);
        }
    }

    public static void openStatScreen(Player player) {
        setScreen(player.getPlainTextName() + "'s Stats", () -> getPages(player, -2));
    }

    @Environment(EnvType.CLIENT)
    public static void showLiveWolfStats(Wolf wolf) {
        //if (!wolf.level().isClientSide()) return;
        int id = wolf.getId();
        WolfStats stats = WolfStats.valueOf(wolf);
        Minecraft.getInstance().setScreenAndShow(new WolfPageScreen(
                fText(BOLD, stats.name() + "'s Stats"),
                () -> {
                    Minecraft mc = Minecraft.getInstance();
                    return mc.level != null && mc.level.getEntity(id) instanceof Wolf
                            ? EntityStats.wolfStats(wolf, stats)
                            : invalidRef;
                },
                () -> wolf
        ));
    }

    @Environment(EnvType.CLIENT)
    private static void setScreen(String title, String[] pages) {
        Minecraft.getInstance().setScreenAndShow(new PageScreen(title, pages));
    }
    @Environment(EnvType.CLIENT)
    private static void setScreen(String title, Supplier<String[]> pages) {
        Minecraft.getInstance().setScreenAndShow(new PageScreen(title, pages));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        String id = "" + this.pageID;
        if (id.startsWith("2")) {
            builder.accept(Component.literal(""));
            builder.accept(Component.literal("Tales of the Verdant Wind").withColor(VWColors.VERDANT_WIND));
            builder.accept(Component.literal("By: Clifford HA").withColor(VWColors.GRAY_MUTED));
        }
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}