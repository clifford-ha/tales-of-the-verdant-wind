package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.world.VWBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class VWBiomeTags extends FabricTagsProvider<Biome> {
    public VWBiomeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.BIOME, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {

        getOrCreateRawBuilder(BiomeTags.IS_OVERWORLD)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(IS_VERDANT_BIOMES)
                .add(TagEntry.element(VWBiomes.VERDANT_MOUNTAINS.identifier()))
                .add(TagEntry.element(VWBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(IS_VERDANT_MOUNTAINS)
                .add(TagEntry.element(VWBiomes.VERDANT_MOUNTAINS.identifier()));

        getOrCreateRawBuilder(IS_VERDANT_FOREST)
                .add(TagEntry.element(VWBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(HAS_VERDANT_FOREST_VILLAGE)
                .add(TagEntry.element(VWBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(HAS_VERDANT_MOUNTAINS_VILLAGE)
                .add(TagEntry.element(VWBiomes.VERDANT_MOUNTAINS.identifier()));

        getOrCreateRawBuilder(BiomeTags.IS_MOUNTAIN)
                .add(TagEntry.element(VWBiomes.VERDANT_MOUNTAINS.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_ANCIENT_CITY)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_BURIED_TREASURE)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_JUNGLE_TEMPLE)
                .add(TagEntry.element(VWBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_MINESHAFT)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_OCEAN_RUIN_COLD)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_PILLAGER_OUTPOST)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN)
                .add(TagEntry.element(VWBiomes.VERDANT_MOUNTAINS.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_RUINED_PORTAL_JUNGLE)
                .add(TagEntry.element(VWBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_STRONGHOLD)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_SWAMP_HUT)
                .add(TagEntry.element(VWBiomes.VERDANT_FOREST.identifier()));

        getOrCreateRawBuilder(BiomeTags.HAS_TRIAL_CHAMBERS)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_WOODLAND_MANSION)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

    }
    public static final TagKey<Biome> IS_VERDANT_BIOMES = create("is_verdant_biomes");
    public static final TagKey<Biome> IS_VERDANT_MOUNTAINS = create("is_verdant_mountains");
    public static final TagKey<Biome> IS_VERDANT_FOREST = create("is_verdant_forest");
    public static final TagKey<Biome> HAS_VERDANT_FOREST_VILLAGE = create("has_verdant_forest_village");
    public static final TagKey<Biome> HAS_VERDANT_MOUNTAINS_VILLAGE = create("has_verdant_mountains_village");
    public static final TagKey<Biome> HAS_VERIXIUM_CORE_PILLARS = create("has_verixium_core_pillars");
    public static final TagKey<Biome> HAS_VERDANT_UNDERGROUND_CAMPS = create("has_verdant_underground_camps");

    private static TagKey<Biome> create(String name) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
}
