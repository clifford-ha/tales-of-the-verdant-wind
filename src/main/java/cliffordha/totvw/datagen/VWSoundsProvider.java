package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWSounds;
import net.fabricmc.fabric.api.client.datagen.v1.builder.SoundTypeBuilder;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricSoundsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.concurrent.CompletableFuture;

public class VWSoundsProvider extends FabricSoundsProvider {
    public VWSoundsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registryLookup, SoundExporter exporter) {
        exporter.add(VWSounds.WOLF_HOWL_A, SoundTypeBuilder.of(VWSounds.WOLF_HOWL_A)
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "alex-jauk-howling-wolf-268894"))));
        exporter.add(VWSounds.WOLF_HOWL_B1, SoundTypeBuilder.of(VWSounds.WOLF_HOWL_B1)
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "dragon-studio-wolf-howl-2-359870"))));
        exporter.add(VWSounds.WOLF_HOWL_B2, SoundTypeBuilder.of(VWSounds.WOLF_HOWL_B2)
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "dragon-studio-wolf-howl-359873"))));
        exporter.add(VWSounds.WOLF_HOWL_B3, SoundTypeBuilder.of(VWSounds.WOLF_HOWL_B3)
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "dragon-studio-howling-wolves-515977"))));

        exporter.add(VWSounds.WOLF_SKILL_PARALYZE, SoundTypeBuilder.of(VWSounds.WOLF_SKILL_PARALYZE)
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "dragon-studio-sci-fi-portal-jump-05-416165"))));

        exporter.add(VWSounds.NOTIFY, SoundTypeBuilder.of(VWSounds.NOTIFY)
                .sound(SoundTypeBuilder.RegistrationBuilder.ofFile(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "dragon-studio-notification-bell-sound-1-376885"))));

    }

    @Override
    public String getName() {
        return "TOTVW Sounds";
    }
}
