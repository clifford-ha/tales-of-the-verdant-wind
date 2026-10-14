package cliffordha.totvw.client;

import cliffordha.totvw.TOTVW;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;

public class VWModelLayerProvider extends ModelLayers {
    public static final ModelLayerLocation VERDANT_SPRUCE_BOAT =
            new ModelLayerLocation(TOTVW.registerID("boat/verdant_spruce"), "main");
    public static final ModelLayerLocation VERDANT_SPRUCE_CHEST_BOAT =
            new ModelLayerLocation(TOTVW.registerID( "chest_boat/verdant_spruce"), "main");
}