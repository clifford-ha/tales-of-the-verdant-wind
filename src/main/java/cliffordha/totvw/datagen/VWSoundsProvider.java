package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWSounds;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.concurrent.CompletableFuture;

public class VWSoundsProvider extends FabricSoundsProvider {
    public VWSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    public static final String ALEX_JAUK = "alex_jauk";
    public static final String DRAGON_STUDIO = "dragon_studio";

    @Override
    protected void configure(HolderLookup.Provider registryLookup, SoundExporter exporter) {

        addSound(exporter, ALEX_JAUK, VWSounds.WOLF_HOWL_A);

        addSound(exporter, DRAGON_STUDIO,
                VWSounds.NOTIFY,
                VWSounds.WOLF_HOWL_B1,
                VWSounds.WOLF_HOWL_B2,
                VWSounds.WOLF_HOWL_B3,
                VWSounds.WOLF_SKILL_PARALYZE,
                VWSounds.LODESTONE_WIND_CORE_AMBIENT
        );
    }

    private static void addSound(SoundExporter exporter, String artist, SoundEvent... soundList) {
        for (SoundEvent sound : soundList) {
            exporter.add(sound, SoundTypeBuilder.of(sound)
                    .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, artist + "/" + sound.location().getPath()))));
        }
    }

    @Override
    public String getName() {
        return "TOTVW Sounds";
    }
}
