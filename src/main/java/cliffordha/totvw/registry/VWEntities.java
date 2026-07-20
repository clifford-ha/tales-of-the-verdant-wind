package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.entity.VWGlobalEntityBehaviors;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.vehicle.boat.Boat;
import net.minecraft.world.entity.vehicle.boat.ChestBoat;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;

public class VWEntities {

    public static final ResourceKey<EntityType<?>> VERDANT_SPRUCE_BOAT_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_boat"));
    public static final ResourceKey<EntityType<?>> VERDANT_SPRUCE_CHEST_BOAT_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_chest_boat"));

    public static final EntityType<Boat> VERDANT_SPRUCE_BOAT = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_boat"),
            EntityType.Builder.<Boat>of((entityType, level) -> new Boat(entityType, level, () -> VWItems.VERDANT_SPRUCE_BOAT),
                            MobCategory.MISC).sized(1.375f, 0.5625f)
                    .clientTrackingRange(10).build(VERDANT_SPRUCE_BOAT_KEY));
    public static final EntityType<ChestBoat> VERDANT_SPRUCE_CHEST_BOAT = Registry.register(BuiltInRegistries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verdant_spruce_chest_boat"),
            EntityType.Builder.<ChestBoat>of((entityType, level) -> new ChestBoat(entityType, level, () -> VWItems.VERDANT_SPRUCE_CHEST_BOAT),
                            MobCategory.MISC).sized(1.375f, 0.5625f)
                    .clientTrackingRange(10).build(VERDANT_SPRUCE_CHEST_BOAT_KEY));

    public static void register() {
        VWPlayerBehaviors.registerModPlayerBehaviors();
        VWWolfBehaviors.registerModWolfBehaviors();
        VWGlobalEntityBehaviors.register();

        sendClassRegisterLog("Entities and Entity Behaviors");
    }
}
