package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.tag.VWBiomeTags;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public class VWWolfVariants {
    public static final ResourceKey<WolfVariant> VERDANT_SNOWY = create("verdant_snowy");
    public static final ResourceKey<WolfVariant> VERDANT_BLACK = create("verdant_black");

    public static void bootstrap(BootstrapContext<WolfVariant> context) {
        register(context, VERDANT_SNOWY, "verdant_snowy", VWBiomeTags.IS_VERDANT_MOUNTAINS);
        register(context, VERDANT_BLACK, "verdant_black", VWBiomeTags.IS_VERDANT_FOREST);
    }

    private static ResourceKey<WolfVariant> create(String name) {
        return ResourceKey.create(Registries.WOLF_VARIANT, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
    }

    private static void register(final BootstrapContext<WolfVariant> context, final ResourceKey<WolfVariant> name, final String fileName, final TagKey<Biome> spawnBiome) {
        register(context, name, fileName, highPrioBiome(context.lookup(Registries.BIOME).getOrThrow(spawnBiome)));
    }

    private static SpawnPrioritySelectors highPrioBiome(final HolderSet<Biome> biomes) {
        return SpawnPrioritySelectors.single(new BiomeCheck(biomes), 1);
    }

    private static void register(final BootstrapContext<WolfVariant> context, final ResourceKey<WolfVariant> name, final String fileName, final SpawnPrioritySelectors selectors) {
        Identifier wildTexture = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "entity/wolf/wolf_" + fileName);
        Identifier tameTexture = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "entity/wolf/wolf_" + fileName + "_tame");
        Identifier angryTexture = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID,"entity/wolf/wolf_" + fileName + "_angry");
        Identifier babyTexture = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID,"entity/wolf/wolf_" + fileName + "_baby");
        Identifier tameBabyTexture = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "entity/wolf/wolf_" + fileName + "_tame_baby");
        Identifier angryBabyTexture = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID,"entity/wolf/wolf_" + fileName + "_angry_baby");
        context.register(name, new WolfVariant(new WolfVariant.AssetInfo(new ClientAsset.ResourceTexture(wildTexture), new ClientAsset.ResourceTexture(tameTexture), new ClientAsset.ResourceTexture(angryTexture)), new WolfVariant.AssetInfo(new ClientAsset.ResourceTexture(babyTexture), new ClientAsset.ResourceTexture(tameBabyTexture), new ClientAsset.ResourceTexture(angryBabyTexture)), selectors));
    }
}
