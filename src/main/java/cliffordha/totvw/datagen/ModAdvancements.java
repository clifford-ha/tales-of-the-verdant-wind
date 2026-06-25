package cliffordha.totvw.datagen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.registry.blocks.VerdantBlocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.advancements.AdvancementProvider;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static net.minecraft.advancements.criterion.InventoryChangeTrigger.TriggerInstance.hasItems;

public class ModAdvancements extends AdvancementProvider {
    public ModAdvancements(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, List.of(new TOTVWAdvancements()));
    }

    public static class TOTVWAdvancements implements AdvancementSubProvider {
        @Override
        public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> output) {
            var items = registries.lookupOrThrow(Registries.ITEM);
            HolderLookup<EntityType<?>> entityTypes = registries.lookupOrThrow(Registries.ENTITY_TYPE);

            AdvancementHolder root = Advancement.Builder.advancement()
                    .display(
                            ModItems.VERIXIUM_PICKAXE,
                            Component.literal("Tales of the Verdant Wind").withColor(ModColors.VERDANT_WIND),
                            Component.literal("Explore the verdant place\nwith your companions").withColor(ModColors.VERDANT_WIND_MUTED),
                            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "block/verdant_moss_block"),
                            AdvancementType.TASK,
                            false, false, false)
                    .addCriterion("has_verdant_spruce_log",
                            hasItems(ItemPredicate.Builder.item().of(items, VerdantBlocks.VERDANT_SPRUCE_LOG)))
                    .save(output, TOTVW.MOD_ID + ":tales-of-the-verdant-wind/root");

            AdvancementHolder fullVerixiumArmor = Advancement.Builder.advancement()
                    .parent(root)
                    .display(
                            ModItems.VERIXIUM_CHESTPLATE,
                            Component.literal("Light As The Wind").withColor(ModColors.VERDANT_WIND),
                            Component.literal("Equip a full set of Verixium armor").withColor(ModColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion("full_verixium_armor_set", hasItems(
                            ItemPredicate.Builder.item().of(items, ModItems.VERIXIUM_HELMET),
                            ItemPredicate.Builder.item().of(items, ModItems.VERIXIUM_CHESTPLATE),
                            ItemPredicate.Builder.item().of(items, ModItems.VERIXIUM_LEGGINGS),
                            ItemPredicate.Builder.item().of(items, ModItems.VERIXIUM_BOOTS)))
                    .save(output, TOTVW.MOD_ID + ":tales-of-the-verdant-wind/full_verixium_armor");

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.VERIXIUM_WOLF_ARMOR,
                            Component.literal("A \"Light\" Companion").withColor(ModColors.VERDANT_WIND),
                            Component.literal("Give your companion Verixium armor").withColor(ModColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, false)
                    .addCriterion("wolf_verixium_armor",
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, ModItems.VERIXIUM_WOLF_ARMOR),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.WOLF)))))
                    .save(output, TOTVW.MOD_ID + ":tales-of-the-verdant-wind/wolf_verixium_armor");

            Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.VERIXIUM_POWDER,
                            Component.literal("We Stand And Stick Together"),
                            Component.literal("Try to trust inanimate objects"),
                            null,
                            AdvancementType.CHALLENGE,
                            true, true, true)
                    .addCriterion("trust_inanimate_object",
                            PlayerInteractTrigger.TriggerInstance.itemUsedOnEntity(
                                    ItemPredicate.Builder.item().of(items, ModItems.VERIXIUM_POWDER),
                                    Optional.of(EntityPredicate.wrap(EntityPredicate.Builder.entity().of(entityTypes, EntityType.ARMOR_STAND)))))
                    .save(output, TOTVW.MOD_ID + ":tales-of-the-verdant-wind/trust_inanimate_object");

            AdvancementHolder verixiumFluidBucket = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.VERIXIUM_FLUID_BUCKET,
                            Component.literal("Is It Drinkable?"),
                            Component.literal("Fill a bucked with Verixium fluid"),
                            null,
                            AdvancementType.TASK,
                            true,
                            true,
                            false)
                    .addCriterion("verixium_fluid_bucket",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VERIXIUM_FLUID_BUCKET))
                    .save(output, ":tales-of-the-verdant-wind/verixium_fluid_bucket");

            AdvancementHolder verixiumArmorUpgrade = Advancement.Builder.advancement()
                    .parent(root)
                    .display(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE,
                            Component.literal("Condensed Wind").withColor(ModColors.VERDANT_WIND),
                            Component.literal("Obtain a Verixium Armor Upgrade Template").withColor(ModColors.VERDANT_WIND_MUTED),
                            null,
                            AdvancementType.CHALLENGE,
                            true,
                            true,
                            true)
                    .addCriterion("verixium_armor_upgrade",
                            InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.VERIXIUM_ARMOR_UPGRADE_TEMPLATE))
                    .save(output, ":tales-of-the-verdant-wind/verixium_armor_upgrade");

        }
    }
}