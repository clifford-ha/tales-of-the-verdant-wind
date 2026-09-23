package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.ItemStack;

public enum Runestone {
    EMPTY("EMPTY", "None"),

    GENESIS("Genesis Runestone", "None"),
    SOUL("Soul Runestone", "None"),
    TETHER("Tether Runestone", "Link"),
    HAVOC("Havoc Runestone", "Ruin"),
    ;

    private final String name;
    private final String buff;
    public static final Codec<Runestone> CODEC = Codec.STRING.xmap(Runestone::valueOf, Runestone::name);

    Runestone(String name, String buff) {
        this.name = name;
        this.buff = buff;
    }

    public String getName() {
        return name;
    }
    public String getBuff() {
        return buff;
    }
    public static String getNameByStack(ItemStack stack) {
        if (stack.is(VWItems.GENESIS_RUNESTONE_PLATE)) {
            return GENESIS.name;
        } else if (stack.is(VWItems.SOUL_RUNESTONE_PLATE)) {
            return SOUL.name;
        } else if (stack.is(VWItems.TETHER_RUNESTONE_PLATE)) {
            return TETHER.name;
        } else if (stack.is(VWItems.HAVOC_RUNESTONE_PLATE)) {
            return HAVOC.name;
        } else {
            return "Invalid Runestone";
        }
    }
    public static ItemStack getStack(Runestone type) {
        ItemStack stack;
        final ItemStack DEFAULT = new ItemStack(VWItems.GENESIS_RUNESTONE_PLATE);
        final ItemStack GENESIS = new ItemStack(VWItems.GENESIS_RUNESTONE_PLATE);
        final ItemStack SOUL = new ItemStack(VWItems.SOUL_RUNESTONE_PLATE);
        final ItemStack TETHER = new ItemStack(VWItems.TETHER_RUNESTONE_PLATE);
        final ItemStack HAVOC = new ItemStack(VWItems.HAVOC_RUNESTONE_PLATE);
        switch (type) {
            case GENESIS -> stack = GENESIS;
            case SOUL -> stack = SOUL;
            case TETHER -> stack = TETHER;
            case HAVOC -> stack = HAVOC;
            default -> stack = DEFAULT;
        }
        return stack;
    }

    public static boolean hasGenesis(Wolf wolf) {
        return hasRunestone(wolf, Runestone.GENESIS);
    }
    public static boolean hasSoul(Wolf wolf) {
        return hasRunestone(wolf, Runestone.SOUL);
    }
    public static boolean hasTether(Wolf wolf) {
        return hasRunestone(wolf, Runestone.TETHER);
    }
    public static boolean hasHavoc(Wolf wolf) {
        return hasRunestone(wolf, Runestone.HAVOC);
    }

    private static boolean hasRunestone(Wolf wolf, Runestone type) {
        return wolf.getAttachedOrElse(WolfAttachment.RUNESTONE_TYPE, Runestone.EMPTY) == type;
    }
}
