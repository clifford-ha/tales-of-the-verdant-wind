package cliffordha.totvw.registry.attachments;

import com.mojang.serialization.Codec;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

public enum HavocType {
    NONE("None"),
    ANNIHILATION("Annihilation"),
    EXPULSION("Expulsion"),
    VOID("Void"),
    RETRIBUTION("Retribution"),
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
        return getType(player) == HavocType.ANNIHILATION;
    }
    public static boolean isExpulsion(Player player) {
        return getType(player) == HavocType.EXPULSION;
    }
    public static boolean isVoid(Player player) {
        return getType(player) == HavocType.VOID;
    }
    public static boolean isNone(Player player) {
        return getType(player) == NONE;
    }

    public static boolean isRetribution(Wolf wolf) {
        return getType(wolf) == RETRIBUTION;
    }

    private static HavocType getType(LivingEntity entity) {
        return HavocData.getData(entity).type();
    }
}
