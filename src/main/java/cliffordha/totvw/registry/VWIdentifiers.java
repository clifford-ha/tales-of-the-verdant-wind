package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import net.minecraft.resources.Identifier;

public class VWIdentifiers {
    public static final Identifier VERIXIUM_ARMOR_EQUIPPED = register("verixium_armor_equipped");
    public static final Identifier VERIXIUM_WOLF_ARMOR_EQUIPPED = register("verixium_wolf_armor_equipped");
    public static final Identifier VERIXIUM_HORSE_ARMOR_EQUIPPED = register("verixium_horse_armor_equipped");

    public static final Identifier EFFECT_AMPLIFIED_MIGHT = register("effect_amplified_might");
    public static final Identifier EFFECT_BLESSING_OF_THE_VERDANT_WIND = register("effect_blessing_of_the_verdant_wind");
    public static final Identifier EFFECT_BLOODLUST = register("effect_bloodlust");
    public static final Identifier EFFECT_BLOODLUST_ADDITIONAL = register("effect_bloodlust_additional");
    public static final Identifier EFFECT_PARALYZE = register("effect_paralyze");

    public static final Identifier VERDANT_OMEN = register("verdant_omen");
    public static final Identifier VERDANT_WOLF_PERMANENT_MODIFIERS = register("verdant_wolf_permanent_modifiers");

    private static Identifier register(String name) {
        return TOTVW.registerID(name);
    }
}
