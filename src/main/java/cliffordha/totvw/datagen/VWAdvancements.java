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

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            VWItems.VERIXIUM_PICKAXE,
                            Component.literal("Tales of the Verdant Wind").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Explore the verdant place\nwith your companions").withColor(VWColors.VERDANT_WIND_MUTED),
                            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "block/verdant_moss_block"),
                            AdvancementType.TASK,
                            false, false, false)
                    .addCriterion("has_verdant_spruce_log",
                            hasItems(ItemPredicate.Builder.item().of(items, VWBlocks.VERDANT_SPRUCE_LOG)))
                    .save(output, TOTVW.MOD_ID + ":root");

            AdvancementHolder fullVerixiumArmor = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            VWItems.VERIXIUM_CHESTPLATE,
                            Component.literal("Light As The Wind").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Equip a full set of Verixium armor").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion("full_verixium_armor_set", hasItems(
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_HELMET),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_CHESTPLATE),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_LEGGINGS),
                            ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_BOOTS)))
                    .save(output, TOTVW.MOD_ID + ":full_verixium_armor");

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_WOLF_ARMOR,
                            Component.literal("A \"Light\" Companion").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Give your companion Verixium armor").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion("wolf_verixium_armor",
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, VWItems.VERIXIUM_WOLF_ARMOR),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityTypes.WOLF)))))
                    .save(output, TOTVW.MOD_ID + ":wolf_verixium_armor");

            AdvancementHolder verixiumFluidBucket = Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_FLUID_BUCKET,
                            Component.literal("Is It Drinkable?"),
                            Component.literal("Fill a bucked with Verixium fluid"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("verixium_fluid_bucket",
                            InventoryChangeTrigger.TriggerInstance.hasItems(VWItems.VERIXIUM_FLUID_BUCKET))
                    .save(output, TOTVW.MOD_ID + ":verixium_fluid_bucket");

            AdvancementHolder verixiumArmorUpgrade = Advancement.Builder.advancement()
                    .parent(root)
                    .display(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE,
                            Component.literal("Condensed Wind").withColor(VWColors.VERDANT_WIND),
                            Component.literal("Obtain a Verixium Armor Upgrade Template").withColor(VWColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            true)
                    .addCriterion("verixium_armor_upgrade",
                            InventoryChangeTrigger.TriggerInstance.hasItems(VWItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE))
                    .save(output, TOTVW.MOD_ID + ":verixium_armor_upgrade");

        }
    }
}