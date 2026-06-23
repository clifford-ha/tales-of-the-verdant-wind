package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.world.ModBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ModBiomeTags extends FabricTagsProvider<Biome> {
    public ModBiomeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.BIOME, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {

        getOrCreateRawBuilder(BiomeTags.IS_OVERWORLD)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(ModBiomeTags.IS_VERDANT_BIOMES)
                .add(TagEntry.element(ModBiomes.VERDANT_MOUNTAINS.identifier()))
                .add(TagEntry.element(ModBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(BiomeTags.IS_MOUNTAIN)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_ANCIENT_CITY)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_BURIED_TREASURE)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_JUNGLE_TEMPLE)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_MINESHAFT)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_OCEAN_RUIN_COLD)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_PILLAGER_OUTPOST)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN)
                .add(TagEntry.element(ModBiomes.VERDANT_MOUNTAINS.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_RUINED_PORTAL_JUNGLE)
                .add(TagEntry.element(ModBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_STRONGHOLD)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_SWAMP_HUT)
                .add(TagEntry.element(ModBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_TRIAL_CHAMBERS)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_VILLAGE_TAIGA)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_WOODLAND_MANSION)
                .addTag(ModBiomeTags.IS_VERDANT_BIOMES.location());
    }
    public static final TagKey<Biome> IS_VERDANT_BIOMES = TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "is_verdant_biomes"));
}
