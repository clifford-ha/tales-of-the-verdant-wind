package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;

import cliffordha.totvw.registry.VWEnchantments;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class VWEnchantmentTags extends FabricTagsProvider<Enchantment> {
    public VWEnchantmentTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.ENCHANTMENT, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        getOrCreateRawBuilder(VWEnchantmentTags.WOLF_ENCHANTMENTS)
                .add(TagEntry.element(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_IGNITION.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_POISONING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_WITHERING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_LIFTING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_BLOODLUST.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_MIGHT.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_OOZING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_GNAWING.identifier()));

        getOrCreateRawBuilder(VWEnchantmentTags.CONTINUOUS_DAMAGE)
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_IGNITION.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_POISONING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_WITHERING.identifier()));

        getOrCreateRawBuilder(VWEnchantmentTags.IMPAIRING_DAMAGE)
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_OOZING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_LIFTING.identifier()));

        getOrCreateRawBuilder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addTag(VWEnchantmentTags.WOLF_ENCHANTMENTS.location());

        getOrCreateRawBuilder(EnchantmentTags.ON_RANDOM_LOOT)
                .add(TagEntry.element(VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_IGNITION.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_MIGHT.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_OOZING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_LIFTING.identifier()));

        getOrCreateRawBuilder(EnchantmentTags.TREASURE)
                .add(TagEntry.element(VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_ARMOR_ENHANCEMENT_KIT.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_POISONING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_WITHERING.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_BLOODLUST.identifier()))
                .add(TagEntry.element(VWEnchantments.WOLF_EFFECT_GNAWING.identifier()));
    }
    public static final TagKey<Enchantment> WOLF_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_enchantments")
    );
    public static final TagKey<Enchantment> CONTINUOUS_DAMAGE = TagKey.create(Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "continuous_damage")
    );
    public static final TagKey<Enchantment> IMPAIRING_DAMAGE = TagKey.create(Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "impairing_damage")
    );


}
