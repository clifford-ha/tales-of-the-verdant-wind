package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.world.VWBiomes;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.tag.VWTagHelpers.biome;

public class VWBiomeTags extends FabricTagsProvider<Biome> {
    public VWBiomeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.BIOME, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {

        getOrCreateRawBuilder(BiomeTags.IS_OVERWORLD)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(IS_VERDANT_BIOMES)
                .add(biome(VWBiomes.VERDANT_MOUNTAINS))
                .add(biome(VWBiomes.VERDANT_FOREST));

        getOrCreateRawBuilder(FOREST_WHERE_WOLVES_HOWL)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location())
                .add(biome(Biomes.FOREST))
                .add(biome(Biomes.FLOWER_FOREST))
                .add(biome(Biomes.DARK_FOREST));

        getOrCreateRawBuilder(IS_VERDANT_MOUNTAINS)
                .add(biome(VWBiomes.VERDANT_MOUNTAINS));

        getOrCreateRawBuilder(IS_VERDANT_FOREST)
                .add(biome(VWBiomes.VERDANT_FOREST));

        getOrCreateRawBuilder(HAS_VERDANT_FOREST_VILLAGE)
                .add(biome(VWBiomes.VERDANT_FOREST));

        getOrCreateRawBuilder(HAS_VERDANT_MOUNTAINS_VILLAGE)
                .add(biome(VWBiomes.VERDANT_MOUNTAINS));

        getOrCreateRawBuilder(BiomeTags.IS_MOUNTAIN)
                .add(biome(VWBiomes.VERDANT_MOUNTAINS));

        getOrCreateRawBuilder(BiomeTags.HAS_ANCIENT_CITY)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_BURIED_TREASURE)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_JUNGLE_TEMPLE)
                .add(biome(VWBiomes.VERDANT_FOREST));

        getOrCreateRawBuilder(BiomeTags.HAS_MINESHAFT)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_PILLAGER_OUTPOST)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_RUINED_PORTAL_MOUNTAIN)
                .add(biome(VWBiomes.VERDANT_MOUNTAINS));

        getOrCreateRawBuilder(BiomeTags.HAS_RUINED_PORTAL_JUNGLE)
                .add(biome(VWBiomes.VERDANT_FOREST));

        getOrCreateRawBuilder(BiomeTags.HAS_STRONGHOLD)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

        getOrCreateRawBuilder(BiomeTags.HAS_SWAMP_HUT)
                .add(biome(VWBiomes.VERDANT_FOREST));

        getOrCreateRawBuilder(BiomeTags.HAS_TRIAL_CHAMBERS)
                .addTag(VWBiomeTags.IS_VERDANT_BIOMES.location());

    }
    public static final TagKey<Biome> IS_VERDANT_BIOMES = create("is_verdant_biomes");
    public static final TagKey<Biome> IS_VERDANT_MOUNTAINS = create("is_verdant_mountains");
    public static final TagKey<Biome> IS_VERDANT_FOREST = create("is_verdant_forest");
    public static final TagKey<Biome> HAS_VERDANT_FOREST_VILLAGE = create("has_verdant_forest_village");
    public static final TagKey<Biome> HAS_VERDANT_MOUNTAINS_VILLAGE = create("has_verdant_mountains_village");
    public static final TagKey<Biome> FOREST_WHERE_WOLVES_HOWL = create("forest_where_wolves_howl");

    private static TagKey<Biome> create(String name) {
        return TagKey.create(Registries.BIOME, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
}
