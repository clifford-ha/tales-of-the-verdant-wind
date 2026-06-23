package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

import static cliffordha.totvw.TOTVW.MOD_NAME;

public class ModPotions {
    private static final int TICK_SECONDS = 20;
    private static final int TICK_MINUTES = TICK_SECONDS * 60;
    private static int setTime(int min, int sec) {return ((TICK_MINUTES * min) + (TICK_SECONDS * sec));}

    public static final Holder<Potion> SACRED_VERDANT_POTION = registerPotion("sacred_verdant_potion",
            new Potion("sacred_verdant_potion", new MobEffectInstance(ModEffects.BLESSING_OF_THE_VERDANT_WIND, setTime(1, 30), 0)));

    public static final Holder<Potion> LONG_SACRED_VERDANT_POTION = registerPotion("long_sacred_verdant_potion",
            new Potion("sacred_verdant_potion", new MobEffectInstance(ModEffects.BLESSING_OF_THE_VERDANT_WIND, setTime(3, 0), 0)));

    public static final Holder<Potion> MIGHT_AMPLIFIER_POTION = registerPotion("might_amplifier_potion",
            new Potion("might_amplifier_potion", new MobEffectInstance(ModEffects.AMPLIFIED_MIGHT, setTime(1, 30), 0)));

    public static final Holder<Potion> LONG_MIGHT_AMPLIFIER_POTION = registerPotion("long_might_amplifier_potion",
            new Potion("might_amplifier_potion", new MobEffectInstance(ModEffects.AMPLIFIED_MIGHT, setTime(4, 0), 0)));

    public static final Holder<Potion> BALEFUL_STRENGTH_POTION = registerPotion("baleful_strength_potion",
            new Potion("baleful_strength_potion", new MobEffectInstance(ModEffects.BLOODLUST, setTime(1, 15), 0)));

    public static final Holder<Potion> STRONG_BALEFUL_STRENGTH_POTION = registerPotion("strong_baleful_strength_potion",
            new Potion("baleful_strength_potion", new MobEffectInstance(ModEffects.BLOODLUST, setTime(1, 15), 1)));

    private static Holder<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), potion);
    }

    public static void registerModPotions() {
        TOTVW.LOGGER.info(MOD_NAME + " - Potions initialized!");
    }
}
