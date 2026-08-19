package cliffordha.totvw.item.scatteredpages;

import cliffordha.totvw.client.screen.ScatteredPageScreen;
import cliffordha.totvw.registry.VWColors;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;
import static cliffordha.totvw.item.scatteredpages.VWEffectsHandbook.*;
import static cliffordha.totvw.item.scatteredpages.VWEnchantmentsHandbook.*;
import static cliffordha.totvw.util.VWUtil.TextUtil.*;

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
            case 333 -> {
                return addPage(fText(BOLD, fText(ITALIC, "An Experimental Research on Wind-charged Monster Deterring Field to Combat the Catastrophic Effects of the Creatures from Beyond"))
                        + nextParagraph

                        + fText(BOLD, "APPROVAL") + nextLine
                        + "\"By virtue granted by our god, 'we', the people who reside among the Verdant Forest, grant the qualified Scholars access to resources nurtured by our land for an indefinite period of time. Purpose of access extends only to the following agreed upon use: studying, developing, and manufacturing solutions that may bring end to disasters caused by the creatures from beyond.\""
                        + nextParagraph

                        + "Grantor(s): Signed by the people's chief." + nextLine
                        + "Grantee(s): Scholars from the Nation of Erudites"

                        + addSeparator
                        + fText(BOLD, "ABSTRACT") + nextLine
                        + "By modifying the Lodestone's attracting energy properties, we essentially create a wind field that can be used to deter any living things nearby. This core will serve as a protection field for the Scholars and hunters to minimize the risk of injury as well as mortality rate when such individuals are within places where safety is a concern."
                        + nextParagraph
                        + "Project's deterring performance showed promising results as it deterred the qualified subjects across different environment, including different variables, with 97% success rate. This has surpassed its prototype's deterring performance by at least 40%. However, its energy efficiency has dropped down to 70% unlike its protype with a staggering 96% at normal conditions. Project has been marked for further testing."
                        + addSeparator
                        + fText(BOLD, "METHOD") + nextLine
                        + "To operate the Lodestone Wind Core (proposed name), an energy source is required before it can be activated by a special Verixium-based paper. Usable energy types are as follows: " + fText(STRIKETHROUGH, "Block of Redstone") + ", Verixium Powder Block, Verixium Powder, " + fText(STRIKETHROUGH, "Water Fluid") + ", Wind Charge. " + cText(GRAY, "Request for additional information for other energy sources is pending...")
                        + nextParagraph
                        + "Energy consumption may vary depending on the environment. That being said, the core will operate at stronger frequencies when enough energy is readily available for use."
                        + nextParagraph
                        + "Normal Parameters: Wind energy < 60000, stable, qualified subjects are scanned at optimal distance." + nextLine
                        + "High Parameters: Wind energy > 60000, unstable, close contact with the core may cause nausea and fatigue however, qualified subjects are scanned at longer distances."
                        + nextParagraph

                        + "Once the core is activated, it will immediately start harnessing the surrounding Wind Energy and simultaneously convert it into two separate fields:"
                        + nextParagraph

                        + fText(BOLD, "PROTECTION FIELD") + nextLine
                        + "At intervals, the core will grant healing and random effects to nearby wolves (hunters) and scholars. Code 024 shows that when the core enters the " + bText("unstable") + " state, said grantee also receives stronger effects than usual. No consistent records yet."
                        + nextParagraph

                        + fText(BOLD, "DETERRING FIELD") + nextLine
                        + "At intervals, the core will deter marked subjects until they get incapacitated. The deterring field will also siphon the subject's internal pressure which will create a pressure difference (PD) point. When such point exceed the standard 100PD point threshold, the subject may suffer from their own implosion. The rate at which PD accumulates depend on the surrounding environment and the subjects mass."

                        + addSeparator
                        + fText(BOLD, "USE NOTICE") + nextLine
                        + "• High Priority: If any related incident arises, be it from using this material or its content(s), please notify and report to the Head of Scholars or authorities from the " + bText("Nolayan") + " people immediately." + nextLine
                        + "• High Priority: DO NOT STAND VERY CLOSELY TO THE CORE WHEN TESTING FOR HIGH ENERGY EFFICIENCY!" + nextLine
                        + "• Recalibrate the qualified variables every day to prevent unnecessary checks and save energy." + nextLine
                        + "• Report energy use every 30 minutes." + nextLine
                        + "• Do not let the core run indefinitely in scorching environments."

                        + addSeparator
                        + tText(IN_LOWLIGHT,fText(BOLD, "INCIDENT REPORT") + nextLine
                        + "• " + cText(DARK_GRAY, "023: ") + "Scholars who had altercations with the locals seem to suffer fatigue when near the core. Due to the nature of incident, speculations are dismissed and the investigation is made unavailable to other scholars." + nextLine
                        + "• " + cText(DARK_GRAY, "024: ") + "The core occasionally enters state where Wind Energy readings are abnormally high despite having shown no negative effects to wolves and scholars.", "")
                );
            }
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
                        + "We tried to outrun this creature in a very pale forest. It didn't look like a creature to be honest as it looked like its skin was made out of wood barks. We weren't sure. Fortunately, we survived by using a boat and followed the river till we arived at a nearby village. According to the locals, distrubing " + fText(ITALIC, "it") + " from its slumber will agitate it and follow us until we meet our demise."
                        + nextParagraph
                        + "She and I talked about what should happen next considering I broke my left arm when were being chased by the creature. As painful as it is, I insisted on continuing the pursuit in studying the materials we got from that foggy biome I encountered a year ago."
                        + nextParagraph
                        + "I knew she has... something to say but she didn't. Anyhow, we found something..."
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
                                + nextParagraph
                        + pText(4), "")
                );
            }
            case 1006 -> {
                return addPage(
                        pText(4)
                        + "The remaining stock has been successfully transferred to the affected bunkers to alleviate the situation caused by starvation. The creatures from beyond, or what our scholars call the " + fText(ITALIC, "ongtan(s)") + ", have multiplied in numbers since the rift appeared. Threat level remain the same."
                        + nextParagraph
                        + "My companions and I have failed to secure the scholars' defense pillar... Fortunately, all of them have been rescued on time, including their papers that may help us in formulating an offensive strategy. Second problem, due to said failure of securing the defense pillar, we had to prioritize evacuation and halt the reconstruction of another core to prevent further casualty."
                        + nextParagraph
                        + pText(4)
                );
            }
        }
        return new String[] { "Error: Invalid page reference no." };
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            if (this.pageID == 2006) {
                openScreen(ENCHANTMENTS_HANDBOOK_TITLE, ENCHANTMENTS_HANDBOOK_CONTENTS());
            } else if (this.pageID == 2007) {
                openScreen(EFFECTS_HANDBOOK_TITLE, EFFECTS_HANDBOOK_CONTENTS());
            } else {
                openScreen(getTitle(player, pageID), getPages(player, pageID));
            }
        }

        float random = Math.min(player.getRandom().nextFloat() + 0.5f, 1.0f);
        player.level().playSound(null, player.blockPosition(), SoundEvents.BOOK_PAGE_TURN, player.getSoundSource(), random, random);
        return InteractionResult.SUCCESS;
    }

    private static void openScreen(String title, String[] pages) {
        if (title.equals(ENCHANTMENTS_HANDBOOK_TITLE)) {
            Minecraft.getInstance().setScreen(new ScatteredPageScreen(0, ENCHANTMENTS_HANDBOOK_TITLE, pages));
        } else if (title.equals(EFFECTS_HANDBOOK_TITLE)) {
            Minecraft.getInstance().setScreen(new ScatteredPageScreen(0, EFFECTS_HANDBOOK_TITLE, pages));
        } else {
            Minecraft.getInstance().setScreen(new ScatteredPageScreen(2, title, pages));
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
        if (this.pageID == 2006 || this.pageID == 2007) {
            builder.accept(Component.literal(""));
            builder.accept(Component.literal("Tales of the Verdant Wind").withColor(VWColors.VERDANT_WIND));
            builder.accept(Component.literal("By: Clifford HA").withColor(VWColors.GRAY_MUTED));
        }
        super.appendHoverText(itemStack, context, display, builder, tooltipFlag);
    }
}