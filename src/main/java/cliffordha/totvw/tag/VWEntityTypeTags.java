package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.concurrent.CompletableFuture;
import static cliffordha.totvw.tag.VWTagHelpers.entity;

public class VWEntityTypeTags extends FabricTagsProvider.EntityTypeTagsProvider {
    public VWEntityTypeTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        getOrCreateRawBuilder(IGNORES_STRONG_WIND_CORE_PULSE)
                .add(entity(EntityTypes.WOLF))
                .add(entity(EntityTypes.ARMOR_STAND))
                .add(entity(EntityTypes.PAINTING))
                .add(entity(EntityTypes.ITEM_FRAME));
    }

    public static final TagKey<EntityType<?>> IGNORES_STRONG_WIND_CORE_PULSE = create("ignores_strong_wind_core_pulse");

    private static TagKey<EntityType<?>> create(String name) {
        return TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }
}
