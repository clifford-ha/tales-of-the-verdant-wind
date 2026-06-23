package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;

import cliffordha.totvw.tag.ModEnchantmentTags;
import cliffordha.totvw.tag.ModItemTags;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

public class ModEnchantments {
    public static final ResourceKey<Enchantment> WOLF_ARMOR_ENHANCEMENT_KIT = enchantmentKey("wolf_armor_enhancement_kit");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_IGNITION = enchantmentKey("wolf_effect_ignition");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_POISONING = enchantmentKey("wolf_effect_poisoning");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_WITHERING = enchantmentKey("wolf_effect_withering");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_LIFTING = enchantmentKey("wolf_effect_lifting");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_BLOODLUST = enchantmentKey("wolf_effect_bloodlust");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_MIGHT = enchantmentKey("wolf_effect_might");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_OOZING = enchantmentKey("wolf_effect_oozing");
    public static final ResourceKey<Enchantment> WOLF_EFFECT_GNAWING = enchantmentKey("wolf_effect_gnawing");
    public static final ResourceKey<Enchantment> BENEDICTION_OF_THE_VERDANT_MOUNTAINS = enchantmentKey("benediction_of_the_verdant_mountains");


    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        var wolfArmorTag = items.getOrThrow(ModItemTags.WOLF_ARMOR_ENCHANTABLE);
        var chestArmorTag = items.getOrThrow(ItemTags.CHEST_ARMOR);

        register(context, WOLF_ARMOR_ENHANCEMENT_KIT, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        4,
                        1,
                        Enchantment.dynamicCost(50, 0),
                        Enchantment.dynamicCost(50, 0),
                        8,
                        EquipmentSlotGroup.BODY
                )).withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES, new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_armor_enhancement_kit"),
                                Attributes.MAX_HEALTH,
                                LevelBasedValue.constant(8),
                                AttributeModifier.Operation.ADD_VALUE
                        ))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_armor_enhancement_kit"),
                                Attributes.ATTACK_SPEED,
                                LevelBasedValue.constant(1.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_armor_enhancement_kit"),
                                Attributes.KNOCKBACK_RESISTANCE,
                                LevelBasedValue.constant(0.2f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_armor_enhancement_kit"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.constant(0.15f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_armor_enhancement_kit"),
                                Attributes.WATER_MOVEMENT_EFFICIENCY,
                                LevelBasedValue.constant(0.15f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .build(WOLF_ARMOR_ENHANCEMENT_KIT.identifier())
        );
        register(context, WOLF_EFFECT_IGNITION, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        2,
                        3,
                        Enchantment.dynamicCost(14, 3),
                        Enchantment.dynamicCost(48, 9),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .exclusiveWith(enchantments.getOrThrow(ModEnchantmentTags.CONTINUOUS_DAMAGE))
                .build(WOLF_EFFECT_IGNITION.identifier())
        );
        register(context, WOLF_EFFECT_POISONING, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        2,
                        5,
                        Enchantment.dynamicCost(12, 3),
                        Enchantment.dynamicCost(55, 9),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .exclusiveWith(enchantments.getOrThrow(ModEnchantmentTags.CONTINUOUS_DAMAGE))
                .build(WOLF_EFFECT_POISONING.identifier())
        );
        register(context, WOLF_EFFECT_WITHERING, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        2,
                        3,
                        Enchantment.dynamicCost(33, 0),
                        Enchantment.dynamicCost(64, 7),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .exclusiveWith(enchantments.getOrThrow(ModEnchantmentTags.CONTINUOUS_DAMAGE))
                .build(WOLF_EFFECT_WITHERING.identifier())
        );
        register(context, WOLF_EFFECT_LIFTING, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        4,
                        3,
                        Enchantment.dynamicCost(12, 7),
                        Enchantment.dynamicCost(47, 13),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .build(WOLF_EFFECT_LIFTING.identifier())
        );
        register(context, WOLF_EFFECT_BLOODLUST, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        2,
                        3,
                        Enchantment.dynamicCost(33, 0),
                        Enchantment.dynamicCost(64, 7),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_effect_bloodlust"),
                                Attributes.ATTACK_DAMAGE,
                                LevelBasedValue.perLevel(1.0f, 1.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_effect_bloodlust"),
                                Attributes.ATTACK_KNOCKBACK,
                                LevelBasedValue.constant(2),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_effect_bloodlust"),
                                Attributes.KNOCKBACK_RESISTANCE,
                                LevelBasedValue.constant(0.2F),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_effect_bloodlust"),
                                Attributes.FALL_DAMAGE_MULTIPLIER,
                                LevelBasedValue.perLevel(-0.10f, -0.05f),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                )
                .build(WOLF_EFFECT_BLOODLUST.identifier())
        );
        register(context, WOLF_EFFECT_MIGHT, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        4,
                        5,
                        Enchantment.dynamicCost(33, 0),
                        Enchantment.dynamicCost(64, 7),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "wolf_effect_might"),
                                Attributes.ATTACK_DAMAGE,
                                LevelBasedValue.perLevel(1.0f, 1.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .build(WOLF_EFFECT_MIGHT.identifier())
        );
        register(context, WOLF_EFFECT_OOZING, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        2,
                        1,
                        Enchantment.dynamicCost(55, 15),
                        Enchantment.dynamicCost(55, 15),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .build(WOLF_EFFECT_OOZING.identifier())
        );
        register(context, WOLF_EFFECT_GNAWING, Enchantment.enchantment(
                Enchantment.definition(
                        wolfArmorTag,
                        wolfArmorTag,
                        2,
                        2,
                        Enchantment.dynamicCost(33, 0),
                        Enchantment.dynamicCost(147, 33),
                                4,
                        EquipmentSlotGroup.BODY
                ))
                .build(WOLF_EFFECT_GNAWING.identifier())
        );
        register(context, BENEDICTION_OF_THE_VERDANT_MOUNTAINS, Enchantment.enchantment(
                Enchantment.definition(
                        chestArmorTag,
                        chestArmorTag,
                        2,
                        1,
                        Enchantment.dynamicCost(80, 0),
                        Enchantment.dynamicCost(80, 20),
                        4,
                        EquipmentSlotGroup.BODY
                ))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "benediction_of_the_verdant_mountains"),
                                Attributes.ATTACK_DAMAGE,
                                LevelBasedValue.perLevel(3.0f, 0.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .build(BENEDICTION_OF_THE_VERDANT_MOUNTAINS.identifier())
        );
    }

    private static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment enchantment) {
        context.register(key, enchantment);
    }

    private static ResourceKey<Enchantment> enchantmentKey(String name) {
        return ResourceKey.create(Registries.ENCHANTMENT, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name));
    }

    public static void registerModEnchantments() {
        TOTVW.sendLog("Enchantments");
    }
}