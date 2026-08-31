package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWBlocks;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWItems;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.advancements.triggers.PlayerInteractTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.advancements.triggers.InventoryChangeTrigger.TriggerInstance.hasItems;

public class VWAdvancements extends AdvancementProvider {
    public VWAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new TOTVWAdvancements()));
    }

    public static class TOTVWAdvancements implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
            var items = registries.lookupOrThrow(Registries.ITEM);
            HolderLookup<EntityType<?>> entityTypes = registries.lookupOrThrow(Registries.ENTITY_TYPE);

            String talesOfTheVerdantWindID = "tales_of_the_verdant_wind";
            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            VWItems.VERIXIUM_PICKAXE,
                            Component.literal("Tales of the Verdant Wind").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Explore the verdant place\nwith your companions").withColor(VWColors.VERDANT_WIND_MUTED),
                            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "block/verdant_moss_block"),
                            AdvancementType.TASK,
                            false, false, false)
                    .addCriterion(talesOfTheVerdantWindID,
                            hasItems(ItemPredicate.Builder.item().of(items, VWBlocks.VERDANT_SPRUCE_LOG)))
                    .save(output, TOTVW.MOD_ID + talesOfTheVerdantWindID);

            String weightlessMineralsID = "weightless_minerals";
            AdvancementHolder weightlessMinerals = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            VWItems.VERIXIUM_CHESTPLATE,
                            Component.literal("Weightless Minerals").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Obtain a Verixium Ingot").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            false, true, false)
                    .addCriterion(weightlessMineralsID, hasItems(
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_CHUNK)))
                    .save(output, TOTVW.MOD_ID + weightlessMineralsID);

            String lightAsTheWindID = "light_as_the_wind";
            Advancement.Builder.advancement()
                    .parent(weightlessMinerals)
                    .display(
                            VWItems.VERIXIUM_HELMET,
                            Component.literal("Light As The Wind").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Equip a full set of Verixium armor").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion(lightAsTheWindID, hasItems(
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_HELMET),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_CHESTPLATE),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_LEGGINGS),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_BOOTS)))
                    .save(output, TOTVW.MOD_ID + lightAsTheWindID);

            String aWolfAccompaniedByTheWindsID = "a_wolf_accompanied_by_the_winds";
            AdvancementHolder aWolfAccompaniedByTheWinds = Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_WOLF_ARMOR,
                            Component.literal("A Wolf Accompanied by The Winds").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Give your companion Verixium armor").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion(aWolfAccompaniedByTheWindsID,
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_WOLF_ARMOR),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityTypes.WOLF)))))
                    .save(output, TOTVW.MOD_ID + aWolfAccompaniedByTheWindsID);

            String aLightCompanionID = "a_light_companion";
            Advancement.Builder.advancement()
                    .parent(aWolfAccompaniedByTheWinds)
                    .display(VWItems.SOUL_RUNESTONE_PLATE,
                            Component.literal("A \"Light\" Companion").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Use a Soul Runestone Plate to store your companion's soul within you").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, true)
                    .addCriterion(aLightCompanionID,
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, VWItems.SOUL_RUNESTONE_PLATE),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityTypes.WOLF)))))
                    .save(output, TOTVW.MOD_ID + aLightCompanionID);

            String tastesLikeInkID = "tastes_like_ink";
            Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_FLUID_BUCKET,
                            Component.literal("Tastes Like Ink"),
                            Component.literal("Fill a bucked with Verixium fluid"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion(tastesLikeInkID,
                            InventoryChangeTrigger.TriggerInstance.hasItems(VWItems.VERIXIUM_FLUID_BUCKET))
                    .save(output, TOTVW.MOD_ID + tastesLikeInkID);

            String condensedWindID = "condensed_wind";
            Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE,
                            Component.literal("Condensed Wind").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Obtain a Verixium Armor Upgrade Template").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            true)
                    .addCriterion(condensedWindID,
                            InventoryChangeTrigger.TriggerInstance.hasItems(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE))
                    .save(output, TOTVW.MOD_ID + condensedWindID);

        }
    }
}