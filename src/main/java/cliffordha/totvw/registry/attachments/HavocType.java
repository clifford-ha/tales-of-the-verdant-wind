package cliffordha.totvw.registry.attachments;

import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.player.Player;

public enum HavocType {
    NONE("None"),
    ANNIHILATION("Annihilation"),
    EXPULSION("Expulsion"),
    VOID("Void"),
    ;

    private final String name;
    public static final Codec<HavocType> CODEC = Codec.STRING.xmap(HavocType::valueOf, HavocType::name);

    HavocType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isAnnihilation(Player player) {
        return player.getAttachedOrElse(PlayerAttachment.HAVOC_TYPE, NONE) == HavocType.ANNIHILATION;
    }
    public static boolean isExpulsion(Player player) {
        return player.getAttachedOrElse(PlayerAttachment.HAVOC_TYPE, NONE) == HavocType.EXPULSION;
    }
    public static boolean isVoid(Player player) {
        return player.getAttachedOrElse(PlayerAttachment.HAVOC_TYPE, NONE) == HavocType.VOID;
    }
    public static boolean isNone(Player player) {
        return player.getAttachedOrElse(PlayerAttachment.HAVOC_TYPE, NONE) == NONE;
    }
    public static HavocType getType(Player player) {
        return player.getAttachedOrElse(PlayerAttachment.HAVOC_TYPE, NONE);
    }
    public static int getUsage(Player player) {
        return player.getAttachedOrElse(PlayerAttachment.HAVOC_USAGE_COUNT, 0);
    }
}
