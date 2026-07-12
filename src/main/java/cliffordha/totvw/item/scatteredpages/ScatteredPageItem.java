package cliffordha.totvw.item.scatteredpages;

import cliffordha.totvw.client.screen.ScatteredPageScreen;
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

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;

public class ScatteredPageItem extends Item {
    private final int pageID;

    public ScatteredPageItem(Properties properties, int pageID) {
        super(properties);
        this.pageID = pageID;
    }

    private static String getTitle(Player player, int title) {
        return ScatteredPageTitle.fromId(title)
                .map(t -> addTitle(t.getTitle()))
                .orElse("Error: Invalid title reference no.");
    }


    // SPOILER ALERT!!!
    // SPOILER ALERT!!!
    // SPOILER ALERT!!!
    public static String[] getPages(Player player, int contents) {
        boolean HAS_ARMOR = !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        boolean IS_UNDERWATER = player.isInWater() || player.isUnderWater() || player.isInWaterOrRain();
        boolean IN_LOWLIGHT = player.level().getMaxLocalRawBrightness(player.blockPosition(), 0) < 9;

        switch (contents) {
            case 1000 -> {
                return addPage(
                        "This is a page that has a lot of contents and is intended to be used for testing purposes (obviously). In this " + fText(UNDERLINED, "scattered page") + ", you can see a lot of text formatting options, colors, etc. used for testing this very UI you are looking at."
                        + nextParagraph
                        + "This is its second paragraph. I bet it could do more than that. What if, let's say, you have an armor while reading this text? Look here: " + tText(HAS_ARMOR, cText(RED, "COLOR"), cText(AQUA, "COLOR"))
                        + nextParagraph
                        + "Did you try it? If you have an armor, you should see a red color on the " + fText(UNDERLINED, "COLOR") + " text otherwise, you should see aqua color."
                        + nextParagraph
                        + "But, what if I wanna underline a text? Look here: " + fText(UNDERLINED, "UNDERLINED")
                        + " Do you wanna see a couple more?"
                        + nextParagraph
                        + "Look below: "
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
            case 1001 -> {
                return addPage(
                        pText(1)
                        + "...3 days later, the village cleric successfully healed the wounded villagers. The relocation to the deep forest was a success but not without problems. " + fText(STRIKETHROUGH, "Fog") + " is quite a big problem in this forest making us vulnerable to to hidden enemies and the environment itself. There was one time one of the kids nearly fell into a ravine."
                        + nextParagraph +
                        "The berry bushes also seem to behave " + bText("strangely") + " when within the this specific biome. We keep hearing distant howls too especially during the dead of the night."
                        + nextParagraph
                        + dText(20, 9, 723)
                        + "A day later after the final relocation, some folks claim to have witnessed a " + bText("wolf") + " tearing down a zombie that chased them. Of course, with how thick the fog is, some are skeptical about the incident and are saying they probably saw a wild animal's silhouette. As for me, I'll " + fText(UNDERLINED, "ask later...")
                        + nextParagraph
                        + dText(21, 9, 723)
                        + "There was a " + fText(STRIKETHROUGH, "traveler") + " who passed by yesterday. He seemed preoccupied with his own thoughts as he just asked the weaponsmith to repair his sword and left. When I asked the weaponsmith about the guy, he said " + fText(ITALIC, "\"The guy is probably a scholar of some sorts with all those papers he was holding and he didn't really look like a fighter to me.\"")
                        + pText(3)
                        + nextParagraph
                        + dText(4, 3, 724)
                        + "Remembering what he said back then, we can hide the original contents of the letter by smudging a tiny amount of " + bText("powder") + " and then writing on top of it to show the decoy. By using the crushed element of the Verixium Chunk, it will serve as a catalyst in its reaction with the Wind Charge from a " + bText("breeze") + " and that should make the text—"
                        + nextParagraph
                        + pText(2)
                );
            }
            case 1002 -> {
                return addPage(
                        pText(3)
                        + dText(3, 6, 724)
                        + "Out of curiosity, I decided to take a sample of the green liquid our weaponsmith had found the other day while scavenging. Apart from suddenly leaking out from the underground, its surrounding stones and grass hardened into deepslate."
                        + nextParagraph
                        + fText(ITALIC, "\"It gave off an ink-like odor and I think it is the same like your smelly ink too ha ha ha\"")
                        + nextParagraph
                        + "That's how he described it... Either way, I sneaked out yesterday in the dead of the night and took a sample of it."
                        + nextParagraph
                        +dText(7, 6, 724)
                        + "I don't know if I should cry or laugh. The green liquid sample that I put in a vial shattered after I fell in a pit just before I reached the plains. Well, I guess I can always go back and take another sample."
                );
            }
            case 1003 -> {
                return addPage(
                        pText(3)
                        + dText(2, 10, 724)
                        + "I finally understood what the traveler back then was talking about! Come to think of it, there was no news about him since he left that day. Regardless, I obtained the powder to " + fText(ITALIC, "synthesize a rule") + " that will serve as the lock for the hidden message. The sample I have right now is a miniscule amount so I could only create a rule when this paper is wet or under the water. There should be a text here: " + tText(IS_UNDERWATER, cText(DARK_AQUA, "The Verixium powder can react to wind, water, and fire depending on the catalyst that triggers the reaction."), "")
                        + nextParagraph
                        + "Perhaps I could go borrow some more powder to create a more complex rule..."
                );
            }
            case 1004 -> {
                return addPage(
                        pText(3)
                        + dText(20, 9, 726)
                        + "I may not have enough days to fully cover this test."
                        + nextParagraph
                        + "We tried to outrun this creature in a very pale forest. It didn't look like a creature to be honest because it looked like its skin was made out of wood barks. We weren't sure. Fortunately, we survived by using a boat and followed the river till we arived at a nearby village. According to the locals, distrubing " + fText(ITALIC, "it") + " from its slumber will agitate it and follow us until we meet our demise."
                        + nextParagraph
                        + "She and I talked about what should happen next considering I broke my left arm when were being chased by the creature. As painful as it is, I insisted on continuing the pursuit in studying the materials we got from that foggy biome I encountered a year ago."
                        + nextParagraph
                        + "I knew she has... something to say but she didn't. What am I doing..."
                        + nextParagraph
                        + nText("Below are frantic scribbles of an ancient text")
                        + nextLine
                        + fText(ITALIC, "A nature's carcass, a defiled creature devoid of " + bText("meaning") + " and mercy to life. When its gaze fall upon those unfortunate, they shall see that a \"destined\" fate is nothing short of a miracle but a deep void of unending malice. O " + bText("Nature's Whisper") + ", what fate has thou chosen for us?")
                );
            }
            case 1005 -> {
                return addPage(
                        tText(IN_LOWLIGHT, "The verdant people once lived in a cave system to avoid and escape the creatures from beyond that terrorized the forest every once in a while. Slowly their eyes transformed and now able to perceive the verixium-based ink. It wasn't much but they thrived."
                        + nextParagraph
                        + "About three years or so, the terrorizing creatures vanished without notice. When the hunters who put their lives at risk returned and broke the news, the people began to resurface and rebuild the structures that was taken away from them. Perhaps, out of curiosity, the neighboring kingdom sent out their regular traders. It was only then that the people who lived in the cave for 3 years found out that only a day has passed."
                        + pText(4), "")
                );
            }
        }
        return new String[] { "Error: Invalid page reference no." };
    }




    // TEXT FORMATTING UTIL
    private static String addTitle(String title) {
        return fText(BOLD, title);
    }

    /** colors text **/
    private static String cText(ScatteredPageTextColor color, String text) {
        return color.getColor() + text + "§r";
    }

    /** a test-dependent text value
     * note: be careful when using ServerLevel tests **/
    private static String tText(boolean test, String isTrue, String isFalse) {
        return test ? isTrue : isFalse;
    }

    /** like a docx, format text **/
    private static String fText(ScatteredPageTextStyle formatter, String text) {
        return formatter.getMarker() + text + "§r";
    }

    /** date, what else **/
    private static String dText(int day, int month, int year) {
        String cDay = day < 10 ? "0" + day : String.valueOf(day);
        String cMonth = month < 10 ? "0" + month : String.valueOf(month);
        return cText(GRAY, fText(ITALIC, cDay + "/" + cMonth + "/" + year)) + nextLine;
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
            case 3 -> predefinedText = "Scribbled gibberish";
            case 4 -> predefinedText = "Some contents have faded";
            default -> predefinedText = "Error: Invalid Predefined Text or Null";
        }
        return cText(DARK_GRAY, "[" + predefinedText + "]") + nextParagraph;
    }
    private static String nText(String text) {
        return cText(DARK_GRAY, fText(ITALIC, "[" + text + "]"));
    }

    private static final String nextLine = " §f§f§f§r\n";

    /** why... **/
    private static final String nextParagraph = " \n §f§f§f§r \n";

    /** purely made for separating *pages visually, rip brain **/
    private static String[] addPage(String text) {
        final int lengthBound = 700;
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
            openScreen(
                    getTitle(player, pageID),
                    getPages(player, pageID)
            );
        }

        float random = Math.min(player.getRandom().nextFloat() + 0.5f, 1.0f);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), random, random);
        return InteractionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private static void openScreen(String title, String[] pages) {
        Minecraft.getInstance().setScreen(new ScatteredPageScreen(title, pages));
    }
}