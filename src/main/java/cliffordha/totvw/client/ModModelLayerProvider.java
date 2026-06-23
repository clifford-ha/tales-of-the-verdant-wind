package cliffordha.totvw.client;

import cliffordha.totvw.TOTVW;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.Identifier;

public class ModModelLayerProvider extends ModelLayers {
    public static final ModelLayerLocation VERDANT_SPRUCE_BOAT =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "boat/verdant_spruce"), "main");
    public static final ModelLayerLocation VERDANT_SPRUCE_CHEST_BOAT =
            new ModelLayerLocation(Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "chest_boat/verdant_spruce"), "main");
}