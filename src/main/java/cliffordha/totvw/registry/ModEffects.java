package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;

import cliffordha.totvw.effect.AmplifiedMightEffect;
import cliffordha.totvw.effect.BlessingOfTheVerdantWindEffect;
import cliffordha.totvw.effect.BloodlustEffect;
import cliffordha.totvw.effect.ParalyzeEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

import static cliffordha.totvw.TOTVW.sendLog;

public class ModEffects {
    public static final Holder<MobEffect> AMPLIFIED_MIGHT = registerMobEffect("amplified_might",
            new AmplifiedMightEffect());

    public static final Holder<MobEffect> BLESSING_OF_THE_VERDANT_WIND = registerMobEffect("blessing_of_the_verdant_wind",
            new BlessingOfTheVerdantWindEffect());

    public static final Holder<MobEffect> BLOODLUST = registerMobEffect("bloodlust",
            new BloodlustEffect());

    public static final Holder<MobEffect> PARALYZE = registerMobEffect("paralyze",
            new ParalyzeEffect());

    private static Holder<MobEffect> registerMobEffect(String name, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT,
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name), effect);
    }

    public static void registerModEffects() {
        sendLog("Effects");
    }
}