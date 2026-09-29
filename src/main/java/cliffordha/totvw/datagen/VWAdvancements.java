package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.tag.VWItemTags;
import cliffordha.totvw.worldgen.dimension.VWDimensions;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.advancements.criterion.InventoryChangeTrigger.TriggerInstance.hasItems;


public class VWAdvancements extends FabricAdvancementProvider {
    public VWAdvancements(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(output, registryLookup);
    }

    @Override
    public void generateAdvancement(HolderLookup.Provider registryLookup, Consumer<AdvancementHolder> consumer) {
            var items = registryLookup.lookupOrThrow(Registries.ITEM);
            HolderLookup<EntityType<?>> entityTypes = registryLookup.lookupOrThrow(Registries.ENTITY_TYPE);

            String talesOfTheVerdantWindID = "tales_of_the_verdant_wind";
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            VWItems.VERIXIUM_PICKAXE,
                            title("Tales of the Verdant Wind", VWColors.VERDANT_WIND),
                            description("Explore the verdant place\nwith your companions"),
                            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "block/verdant_moss_block"),
                            AdvancementType.TASK,
                            false, false, false)
                    .addCriterion(talesOfTheVerdantWindID,
                            hasItems(ItemPredicate.Builder.item().of(items, VWBlocks.VERDANT_SPRUCE_LOG)))
                    .save(consumer, TOTVW.registerID(talesOfTheVerdantWindID));

            String weightlessMineralsID = "weightless_minerals";
            AdvancementHolder weightlessMinerals = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            VWItems.VERIXIUM_CHUNK,
                            title("Weightless Minerals", VWColors.VERDANT_WIND),
                            description("Obtain a Verixium Chunk"),
                            TOTVW.registerID(weightlessMineralsID),
                            AdvancementType.TASK,
                            false, true, false)
                    .addCriterion(weightlessMineralsID, hasItems(
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_CHUNK)))

                    .save(consumer, TOTVW.registerID(weightlessMineralsID));

            String lightAsTheWindID = "light_as_the_wind";
            Advancement.Builder.advancement()
                    .parent(weightlessMinerals)
                    .display(
                            VWItems.VERIXIUM_CHESTPLATE,
                            title("Light As The Wind", VWColors.VERDANT_WIND),
                            description("Equip a full set of Verixium armor"),
                            TOTVW.registerID(lightAsTheWindID),
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion(lightAsTheWindID, hasItems(
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_HELMET),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_CHESTPLATE),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_LEGGINGS),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_BOOTS)))
                    .save(consumer, TOTVW.registerID(lightAsTheWindID));

        String condensedWindID = "condensed_wind";
        AdvancementHolder condensedWind = Advancement.Builder.advancement()
                .parent(weightlessMinerals)
                .display(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE,
                        title("Condensed Wind", VWColors.VERDANT_WIND),
                        description("Obtain a Verixium Armor Upgrade Template"),
                        TOTVW.registerID(condensedWindID),
                        AdvancementType.CHALLENGE,
                        true,
                        true,
                        true)
                .addCriterion(condensedWindID,
                        InventoryChangeTrigger.TriggerInstance.hasItems(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE))
                .save(consumer, TOTVW.registerID(condensedWindID));

            String aWolfAccompaniedByTheWindsID = "a_wolf_accompanied_by_the_winds";
            AdvancementHolder aWolfAccompaniedByTheWinds = Advancement.Builder.advancement()
                    .parent(condensedWind)
                    .display(VWItems.VERIXIUM_WOLF_ARMOR,
                            title("A Wolf Accompanied by The Winds", VWColors.VERDANT_WIND),
                            description("Give your companion Verixium armor"),
                            TOTVW.registerID(aWolfAccompaniedByTheWindsID),
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion(aWolfAccompaniedByTheWindsID,
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_WOLF_ARMOR),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.WOLF)))))
                    .save(consumer, TOTVW.registerID(aWolfAccompaniedByTheWindsID));

            String aLightCompanionID = "a_light_companion";
            Advancement.Builder.advancement()
                    .parent(aWolfAccompaniedByTheWinds)
                    .display(VWItems.SOUL_RUNESTONE_PLATE,
                            title("A \"Light\" Companion", VWColors.VERDANT_WIND),
                            description("Use a Soul Runestone Plate to store your companion's soul within you"),
                            TOTVW.registerID(aLightCompanionID),
                            AdvancementType.CHALLENGE,
                            true, true, true)
                    .addCriterion(aLightCompanionID,
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, VWItems.SOUL_RUNESTONE_PLATE),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.WOLF)))))
                    .save(consumer, TOTVW.registerID(aLightCompanionID));

            String tastesLikeInkID = "tastes_like_ink";
            Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_FLUID_BUCKET,
                            title("Tastes Like Ink"),
                            description("Fill a bucked with Verixium fluid"),
                            TOTVW.registerID(tastesLikeInkID),
                            AdvancementType.TASK,
                            true, true, false)
                    .addCriterion(tastesLikeInkID,
                            InventoryChangeTrigger.TriggerInstance.hasItems(VWItems.VERIXIUM_FLUID_BUCKET))
                    .save(consumer, TOTVW.registerID(tastesLikeInkID));

            String worldLostInTimeID = "world_lost_in_time";
            Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWBlocks.VERDANT_SPRUCE_SAPLING.asItem(),
                            title("A World Lost In Time", VWColors.VERDANT_WIND),
                            description("Discover the hidden, fragmented world"),
                            TOTVW.registerID(worldLostInTimeID),
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            true
                    )
                    .addCriterion(worldLostInTimeID,
                            PlayerTrigger.TriggerInstance.located(
                                    new LocationPredicate.Builder()
                                            .setDimension(VWDimensions.NOLAYAN_LEVEL_KEY)
                            ))
                    .save(consumer, TOTVW.registerID(worldLostInTimeID));

            String powerBeyondReasonID =  "power_beyond_reason";
            AdvancementHolder powerBeyondReason = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            VWItems.SOUL_RUNESTONE_PLATE,
                            title("Power Beyond Reason"),
                            description("Obtain a runestone plate"),
                            TOTVW.registerID(powerBeyondReasonID),
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            false
                    )
                    .addCriterion(powerBeyondReasonID,
                            InventoryChangeTrigger.TriggerInstance.hasItems(
                                    ItemPredicate.Builder.item().of(
                                            items,
                                            VWItemTags.RUNESTONE_PLATES
                                    )
                            ))
                    .save(consumer, TOTVW.registerID(powerBeyondReasonID));

        String boundByAnInvisibleThreadID = "bound_by_a_thread";
        Advancement.Builder.advancement()
                .parent(powerBeyondReason)
                .display(VWItems.TETHER_RUNESTONE_PLATE,
                        title("Bound By A Thread", VWColors.MIGHT_EFFECT),
                        description("Use the power of Tether Runestone on your companion so it can gain the Link status"),
                        TOTVW.registerID(boundByAnInvisibleThreadID),
                        AdvancementType.CHALLENGE,
                        true, true, true)
                .addCriterion(boundByAnInvisibleThreadID,
                        PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(items, VWItems.TETHER_RUNESTONE_PLATE),
                                Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.WOLF)))))
                .save(consumer, TOTVW.registerID(boundByAnInvisibleThreadID));

        String doFlowersBloomWhenIWalkID = "holder_of_life";
        Advancement.Builder.advancement()
                .parent(powerBeyondReason)
                .display(
                        VWBlocks.AIR_PLACEHOLDER.asItem(),
                        title("Holder of Life", VWColors.INDICATOR_80),
                        description("Find a way to make your companion's step bloom with life"),
                        TOTVW.registerID(doFlowersBloomWhenIWalkID),
                        AdvancementType.CHALLENGE,
                        true, true, true)
                .addCriterion(doFlowersBloomWhenIWalkID,
                        PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(items, VWItems.EFFLORESCENCE_RUNESTONE_PLATE),
                                Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.WOLF)))))
                .save(consumer, TOTVW.registerID(doFlowersBloomWhenIWalkID));

        String deathlyDefianceID = "deathly_defiance";
        Advancement.Builder.advancement()
                .parent(powerBeyondReason)
                .display(VWItems.HAVOC_RUNESTONE_PLATE,
                        title("Deathly Defiance", VWColors.HAVOC_PARTICLE),
                        description("Use the Havoc Runestone Plate to be able to gain the Havoc status effect"),
                        TOTVW.registerID(deathlyDefianceID),
                        AdvancementType.CHALLENGE,
                        true, true, true)
                .addCriterion(deathlyDefianceID,
                        PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                ItemPredicate.Builder.item().of(items, VWItems.HAVOC_RUNESTONE_PLATE),
                                Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.WOLF)))))
                .save(consumer, TOTVW.registerID(deathlyDefianceID));
    }

    private static Component title(String text) {
        return Component.literal(text);
    }
    private static Component title(String text, int color) {
        return Component.literal(text).withColor(color);
    }
    private static Component description(String text) {
        return Component.literal(text).withColor(VWColors.VERDANT_WIND_MUTED);
    }
}