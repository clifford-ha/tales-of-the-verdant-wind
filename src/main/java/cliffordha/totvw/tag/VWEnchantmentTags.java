package cliffordha.totvw.tag;

import cliffordha.totvw.TOTVW;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

import static cliffordha.totvw.tag.VWTagHelpers.enchantment;
import static cliffordha.totvw.registry.VWEnchantments.*;
public class VWEnchantmentTags extends FabricTagsProvider<Enchantment> {
    public VWEnchantmentTags(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookupFuture) {
        super(output, Registries.ENCHANTMENT, registryLookupFuture);
    }

    @Override
    protected void addTags(HolderLookup.@NonNull Provider registries) {
        getOrCreateRawBuilder(WOLF_ENCHANTMENTS)
                .add(enchantment(BENEDICTION_OF_THE_VERDANT_MOUNTAINS))
                .add(enchantment(WOLF_ARMOR_ENHANCEMENT_KIT))
                .add(enchantment(WOLF_EFFECT_IGNITION))
                .add(enchantment(WOLF_EFFECT_POISONING))
                .add(enchantment(WOLF_EFFECT_WITHERING))
                .add(enchantment(WOLF_EFFECT_LIFTING))
                .add(enchantment(WOLF_EFFECT_BLOODLUST))
                .add(enchantment(WOLF_EFFECT_MIGHT))
                .add(enchantment(WOLF_EFFECT_OOZING))
                .add(enchantment(WOLF_EFFECT_GNAWING));

        getOrCreateRawBuilder(CONTINUOUS_DAMAGE)
                .add(enchantment(WOLF_EFFECT_IGNITION))
                .add(enchantment(WOLF_EFFECT_POISONING))
                .add(enchantment(WOLF_EFFECT_WITHERING));

        getOrCreateRawBuilder(IMPAIRING_DAMAGE)
                .add(enchantment(WOLF_EFFECT_OOZING))
                .add(enchantment(WOLF_EFFECT_LIFTING));

        getOrCreateRawBuilder(EnchantmentTags.IN_ENCHANTING_TABLE)
                .addTag(VWEnchantmentTags.WOLF_ENCHANTMENTS.location());

        getOrCreateRawBuilder(EnchantmentTags.ON_RANDOM_LOOT)
                .add(enchantment(WOLF_ARMOR_ENHANCEMENT_KIT))
                .add(enchantment(WOLF_EFFECT_IGNITION))
                .add(enchantment(WOLF_EFFECT_MIGHT))
                .add(enchantment(WOLF_EFFECT_OOZING))
                .add(enchantment(WOLF_EFFECT_LIFTING));

        getOrCreateRawBuilder(EnchantmentTags.TREASURE)
                .add(enchantment(BENEDICTION_OF_THE_VERDANT_MOUNTAINS))
                .add(enchantment(WOLF_ARMOR_ENHANCEMENT_KIT))
                .add(enchantment(WOLF_EFFECT_POISONING))
                .add(enchantment(WOLF_EFFECT_WITHERING))
                .add(enchantment(WOLF_EFFECT_BLOODLUST))
                .add(enchantment(WOLF_EFFECT_GNAWING));
    }
    public static final TagKey<Enchantment> WOLF_ENCHANTMENTS = create("wolf_enchantments");
    public static final TagKey<Enchantment> CONTINUOUS_DAMAGE = create("continuous_damage");
    public static final TagKey<Enchantment> IMPAIRING_DAMAGE = create("impairing_damage");

    private static TagKey<Enchantment> create(String name) {
        return TagKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)); }

}
