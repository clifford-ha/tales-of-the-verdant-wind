package cliffordha.totvw.item.scatteredpages;

import cliffordha.totvw.client.screen.PageContentScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTitle.*;
import static cliffordha.totvw.item.scatteredpages.TextColorEnum.*;
import static cliffordha.totvw.item.scatteredpages.TextFormatterEnum.*;

public class ScatteredPageItem extends Item {
    private final int pageID;

    public ScatteredPageItem(Properties properties, int pageID) {
        super(properties);
        this.pageID = pageID;
    }

    private static String getTitle(Player player, int title) {
        switch (title) {
            case 1001 -> {
                return addTitle(SP_1001.getTitle());
            }
            case 1002 -> {
                return addTitle(SP_1002.getTitle());
            }
            default -> {
                return addTitle(SP_1000.getTitle());
            }
        }
    }

    public static String[] getPages(Player player, int contents) {
        boolean hasArmor = !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();

        switch (contents) {
            case 1001 -> {
                return addPage(
                        pText(1)
                        + "...3 days later, the village cleric successfully healed the wounded villagers. The relocation to the deep forest was a success but not without problems. " + fText(STRIKETHROUGH, "Fog") + " is quite a big problem in this forest making us vulnerable to to hidden enemies and the environment itself. There was one time one of the kids nearly fell into a ravine."
                        + nextParagraph +
                        "The berry bushes also seem to behave " + bText("strangely") + " when within the this specific biome. We keep hearing distant howls too especially during the dead of the night."
                        + nextParagraph
                        + "A day later after the final relocation, some folks claim to have witnessed a " + bText("wolf") + " tearing down a zombie that chased them. " + fText(UNDERLINED, "Ask later...")
                        + nextParagraph
                        + "There was also a " + fText(STRIKETHROUGH, "trader") + " who passed by yesterday. He seemed—"
                        + nextParagraph
                        + pText(2)
                );
            }
            case 1002 -> {
                return addPage(
                        "This is a page that has a lot of contents and is intended to be used for testing purposes (obviously). In this " + fText(UNDERLINED, "scattered page") + ", you can see a lot of text formatting options, colors, etc. used for testing this very UI you are looking at."
                        + nextParagraph
                        + "This is its second paragraph. I bet it could do more than that. What if, let's say, you have an armor while reading this text? Look here: " + tText(hasArmor, cText(RED, "COLOR"), cText(AQUA, "COLOR"))
                        + nextParagraph
                        + "Did you try it? If you have an armor, you should see a red color on the " + fText(UNDERLINED, "COLOR") + " text otherwise, you should see aqua color."
                        + nextParagraph
                        + "But, what if I wanna underline a text? Look here: " + fText(UNDERLINED, "UNDERLINED")
                        + " Do you wanna see a couple more? Look below: "
                        + nextLine
                        + fText(ITALIC, "ITALIC")
                        + nextLine
                        + fText(BOLD, "BOLD")
                        + nextLine
                        + fText(STRIKETHROUGH, "STRIKETHROUGH")
                        + nextParagraph
                        + "There is a lot to test to achieve a desirable output and that is because each scattered page (item) has different contents which means it also has different text formatting."
                        + nextParagraph
                        + "This is the end of this page. Hope you enjoyed it!"
                );
            }
        }
        return new String[] { "Error: Invalid Pages or Null" };
    }




    // TEXT FORMATTING
    private static String addTitle(String title) {
        return fText(BOLD, title);
    }

    /** colors text **/
    private static String cText(TextColorEnum color, String text) {
        return color.getColor() + text + "§r";
    }

    /** a test-dependent text value
     * note: be careful when using ServerLevel tests **/
    private static String tText(boolean test, String isTrue, String isFalse) {
        return test ? isTrue : isFalse;
    }

    /** like a docx, format text **/
    private static String fText(TextFormatterEnum formatter, String text) {
        return formatter.getMarker() + text + "§r";
    }

    /** convert and iterate every letter from the input text and turn it into a block **/
    private static String bText(String text) {
        return "▌".repeat(text.length());
    }

    /** a set of predefined text **/
    private static String pText(int p) {
        String predefinedText;
        switch (p) {
            case 1 -> predefinedText = "Some contents are intentionally omitted";
            case 2 -> predefinedText = "The text trails and ends here...";
            default -> predefinedText = "Error: Invalid Predefined Text or Null";
        }
        return cText(DARK_GRAY, "[" + predefinedText + "]") + nextParagraph;
    }

    private static final String nextLine = "\n";

    /** why... **/
    private static final String nextParagraph = "\n\n";

    /** purely made for separating *pages visually, rip brain **/
    private static String[] addPage(String text) {
        final int lengthBound = 550;
        int charCount = text.length();
        List<String> pages = new ArrayList<>();
        int start = 0;

        while (start < charCount) {
            while (start < charCount && Character.isWhitespace(text.charAt(start))) {
                start++;
            }

            if (start >= charCount) {
                break;
            }

            int end = Math.min(start + lengthBound, charCount);

            if (end < charCount) {
                int split = end;

                while (split > start && !Character.isWhitespace(text.charAt(split - 1))) {
                    split--;
                }

                if (split > start) {
                    end = split;
                }
            }

            pages.add(text.substring(start, end).trim());
            start = end;
        }

        return pages.toArray(new String[0]);
    }





    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {

        if (level.isClientSide()) {
            String title = getTitle(player, pageID);
            String[] pages = getPages(player, pageID);
            openScreen(title, pages);
        }

        float random = Math.min(player.getRandom().nextFloat() + 0.5f, 1.0f);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), random, random);
        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private static void openScreen(String title, String[] pages) {
        Minecraft.getInstance().setScreen(new PageContentScreen(title, pages));
    }
}