package cliffordha.totvw.item.pages.contents;

import cliffordha.totvw.item.pages.FTextStyle;
import cliffordha.totvw.item.pages.ScatteredPageItem;
import net.minecraft.world.entity.player.Player;

import static cliffordha.totvw.item.pages.FTextColor.*;
import static cliffordha.totvw.item.pages.FTextStyle.*;
import static cliffordha.totvw.util.TextUtil.*;

public class FirstBookSet {
    public static String get(Player player, int id) {
        if (player == null)
            return ScatteredPageItem.invalidPlayer;

        switch (id) {
            case 1001 -> {
                return get1001();
            }
            case 1002 -> {
                return get1002();
            }
            case 1003 -> {
                return get1003(player);
            }
            case 1004 -> {
                return get1004();
            }
            case 1005 -> {
                return get1005(player);
            }
            case 1006 -> {
                return get1006();
            }
            case 1007 -> {
                return get1007();
            }
            case 1008 -> {
                return get1008();
            }
        }
        return ScatteredPageItem.invalidRef;
    }
    private static String get1001() {
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
    private static String get1002() {
        return addPage(
                pText(3)
                        + dText(3, 6, 724)
                        + "Out of curiosity, I decided to take a sample of the green liquid our weaponsmith had found the other day while scavenging. Apart from suddenly leaking out from the underground, its surrounding stones and grass hardened into deepslate."
                        + nextParagraph
                        + fText(ITALIC, "\"It gave off an ink-like odor and I think it is the same like your smelly ink too ha ha ha\"")
                        + nextParagraph
                        + "That's how he described it... Either way, I sneaked out yesterday in the dead of the night and took a sample of it."
                        + nextParagraph
                        + dText(7, 6, 724)
                        + "I don't know if I should cry or laugh. The green liquid sample that I put in a vial shattered after I fell in a pit just before I reached the plains. Well, I guess I can always go back and take another sample."
        );
    }
    private static String get1003(Player player) {
        boolean IS_UNDERWATER = player.isInWater() || player.isUnderWater() || player.isInWaterOrRain();
        return addPage(
                pText(3)
                        + dText(2, 10, 724)
                        + "I finally understood what the traveler back then was talking about! Come to think of it, there was no news about him since he left that day. Regardless, I obtained the powder to " + fText(ITALIC, "synthesize a rule") + " that will serve as the lock for the hidden message. The sample I have right now is a miniscule amount so I could only create a rule when this paper is wet or under the water. There should be a text here: " + tText(IS_UNDERWATER, cText(DARK_AQUA, "The Verixium powder can react to wind, water, and fire depending on the catalyst that triggers the reaction."))
                        + nextParagraph
                        + "Perhaps I could go borrow some more powder to create a more complex rule..."
        );
    }
    private static String get1004() {
        return addPage(
                pText(3)
                        + dText(20, 9, 726)
                        + "I may not have enough days to fully cover this test."
                        + nextParagraph
                        + "We tried to outrun this creature in a very pale forest. It didn't look like a creature to be honest as it looked like its skin was made out of wood barks. We weren't sure. Fortunately, we survived by using a boat and followed the river till we arived at a nearby village. According to the locals, disturbing " + fText(ITALIC, "it") + " from its slumber will agitate it and follow us until we meet our demise."
                        + nextParagraph
                        + "She and I talked about what should happen next considering I broke my left arm when were being chased by the creature. As painful as it is, I insisted on continuing the pursuit in studying the materials we got from that foggy biome I encountered a year ago."
                        + nextParagraph
                        + "I knew she has... something to say but she didn't. Anyhow, we found something..."
                        + nextParagraph
                        + nText("Below are frantic scribbles of an ancient text")
                        + nextLine
                        + fText(ITALIC, "A nature's carcass, a defiled creature devoid of " + bText("meaning") + " and mercy to life. When its gaze falls upon those unfortunates, they shall see that a \"destined\" fate is nothing short of a miracle but a deep void of unending malice. O " + bText("Nature's Whisper") + ", what fate has thou chosen for us?")
        );
    }
    private static String get1005(Player player) {
        boolean IN_LOWLIGHT = player.level().getMaxLocalRawBrightness(player.blockPosition(), 0) < 9;
        return addPage(
                tText(IN_LOWLIGHT, "The verdant people once lived in a cave system to avoid and escape the creatures from beyond that terrorized the forest every once in a while. Slowly their eyes transformed and now able to perceive the verixium-based ink. It wasn't much but they thrived."
                        + nextParagraph
                        + "About three years or so, the terrorizing creatures vanished without notice. When the hunters who put their lives at risk returned and broke the news, the people began to resurface and rebuild the structures that was taken away from them. Perhaps, out of curiosity, the neighboring kingdom sent out their regular traders. It was only then that the people who lived in the cave for 3 years found out that only a day has passed."
                        + nextParagraph
                        + pText(4))
        );
    }
    private static String get1006() {
        return addPage(
                pText(4)
                        + "The remaining stock has been successfully transferred to the affected bunkers to alleviate the situation caused by starvation. The creatures from beyond, or what our scholars call the " + fText(ITALIC, "ongtan(s)") + ", have multiplied in numbers since the rift appeared. Threat level remains the same."
                        + nextParagraph
                        + "My companions and I have failed to secure the scholars' defense pillar... Fortunately, all of them have been rescued on time, including their papers that may help us in formulating an offensive strategy. Second problem, due to said failure of securing the defense pillar, we had to prioritize evacuation and halt the reconstruction of another core to prevent further casualty."
                        + nextParagraph
                        + pText(4)
        );
    }
    private static String get1007() {
        return addPage(pText(4)
                + "...many opposed the idea of using past memories as 'anchors' to guide them when summoned and it was not the first time either. The common reason they state is that 'the past is as fragile as sheet of thin ice.'"
                + nextParagraph

                + "Truly, I fail to see the rationale behind such, thus I took it upon myself and decided to find a way to stabilize the extraction process. During this time, I have come across a rather interesting interaction between the thing they call 'soul' with the extracted memories... they seem to strengthen each of their own while I was in the Nether. A working theory is that, the Nether can maintain a relatively strong connection to the fragmented world unlike any other dimensions. A colleague of mine have suggested that it may be due to the Nether being a 'world full of undead souls.' Another suggested that it is where the undead are from."
                + nextParagraph
                + nextParagraph

                + pText(1)
                + "Oh how I despise their incompetence... It wasn't supposed to be permanently stabilized but to make it stable enough that it would be usable for a period of time! That way, I could anchor multiple memories to multiple souls at once after which I can command them to annihilate those pesky creatures from beyond. Truly, a marvelous work of mine!"
                + nextParagraph

                + "Just a little more time and you'll both be recognized too for your efforts."
                + nextParagraph
                + nextParagraph

                + pText(3)
                + "The withering... I have underestimated its speed and it appears I too will succumb to its effect in just a short while. Paralyzed from the waist down, I can only do so much with a left arm that have already started to crumble and fall bit-by-bit. My son didn't say a word too when he saw the current predicament I was in when he found me collapsed during that time. I'm sure he has a myriad of reasons to scold me now but I guess this is his way of showing he cares..."
                + nextParagraph

                + "Rather than sit idly by and wait for my demise, I have written the process of extracting memories from the fragmented world and use it to form a temporary projection of a soul. I just hope they won't burn it to save others from pursuing the same endeavors I once had. But even if they did, I would still be grateful... in a way."
                + nextParagraph

                + pText(3)
                + pText(3)
                + pText(4)
                + fText(ITALIC, "May your soul rest peacefully, dear Pa...")
        );
    }
    private static String get1008() {
        return addPage(fText(BOLD, fText(ITALIC, "Memory Extraction Method for ∎∎∎∎ Projection"))
               + nextParagraph

                + pText("A glued page with heavy highlights appears to be attached...", false) + nextLine
                + "Due to severe health-related concerns, public distribution of this material is prohibited. Anyone who is caught violating this order will be prosecuted to the full extent of the law and disbarred from further participation in the research field." + nextLine
                + "Signed by: ∎∎∎∎∎∎∎∎∎∎∎"
                + nextParagraph

                + "As established by the former late ∎∎∎∎∎∎, a memory is an unstable form of energy and extracting it is tantamount to directly holding a burning coal without gloves or any protection for that matter. The duration of extraction heavily depends on the environment and quality of equipment used. ∎∎∎∎∎∎ has greatly contributed on that part as he was able to document and list out the following criterion for such environment: " + pText(1)

                + "An anonymous individual from ∎∎∎∎∎∎ helped and lead the chanting process, allowing us two extract the memories more efficiently. After such, we utilized the the Lodestone ∎∎∎∎ ∎∎∎∎ as it has an unintended effect of being able to also record the wolves that helped in eliminating the threat of the creatures from beyond. More importantly, the " + pText(1)

                + "The last and crucial step is to anchor the memories onto the projected soul before it naturally fades away. To do so, a volunteer must be close to the core to constantly stabilize it while the extracted memories are being written into the soul. We have also found out that ∎∎∎∎∎∎ roses are efficient at nullifying the..."
                + nextParagraph

                + pText(1)
        );
    }
}
