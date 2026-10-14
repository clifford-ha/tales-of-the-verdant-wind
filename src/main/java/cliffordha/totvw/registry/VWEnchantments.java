package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.tag.VWEnchantmentTags;
import cliffordha.totvw.tag.VWItemTags;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;

public class VWEnchantments {
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


    public static boolean getBenediction(LivingEntity entity) {
        int value;
        if (entity instanceof Wolf wolf) {
            value = wolfEnchantmentLVL(wolf, BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        } else {
            value = entityEnchantmentLVL(entity, EquipmentSlot.CHEST, BENEDICTION_OF_THE_VERDANT_MOUNTAINS);
        }
        return value > 0;
    }
    public static boolean getEnhancementKit(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_ARMOR_ENHANCEMENT_KIT) > 0;
    }
    public static int getIgnition(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_IGNITION);
    }
    public static int getPoisoning(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_POISONING);
    }
    public static int getWithering(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_WITHERING);
    }
    public static int getLifting(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_LIFTING);
    }
    public static int getBloodLust(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_BLOODLUST);
    }
    public static int getMight(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_MIGHT);
    }
    public static int getOozing(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_OOZING);
    }
    public static int getGnawing(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, WOLF_EFFECT_GNAWING);
    }

    public static int getProtection(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, Enchantments.PROTECTION);
    }
    public static int getFireProtection(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, Enchantments.FIRE_PROTECTION);
    }
    public static int getBlastProtection(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, Enchantments.BLAST_PROTECTION);
    }
    public static int getProjectileProtection(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, Enchantments.PROJECTILE_PROTECTION);
    }
    public static int getMending(Wolf wolf) {
        return wolfEnchantmentLVL(wolf, Enchantments.MENDING);
    }


    public static int wolfEnchantmentLVL(Wolf wolf, ResourceKey<Enchantment> enchantment) {
        if (wolf == null) return 0;
        ItemStack armor = wolf.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) return 0;
        return getEnchantment(armor, wolf, enchantment);
    }
    public static int entityEnchantmentLVL(LivingEntity player, EquipmentSlot slot, ResourceKey<Enchantment> enchantment) {
        if (player == null) return 0;
        ItemStack itemStack = player.getItemBySlot(slot);
        if (itemStack.isEmpty()) return 0;
        return getEnchantment(itemStack, player, enchantment);
    }
    public static int entityEnchantmentLVL(LivingEntity player, ResourceKey<Enchantment> enchantment) {
        if (player == null) return 0;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack body = player.getItemBySlot(EquipmentSlot.BODY);
        int helmetLVL = 0;
        int chestLVL = 0;
        int legsLVL = 0;
        int feetLVL = 0;
        int bodyLVL = 0;

        if (!helmet.isEmpty()) helmetLVL = getEnchantment(helmet, player, enchantment);
        if (!chest.isEmpty()) chestLVL = getEnchantment(chest, player, enchantment);
        if (!legs.isEmpty()) legsLVL = getEnchantment(legs, player, enchantment);
        if (!feet.isEmpty()) feetLVL = getEnchantment(feet, player, enchantment);
        if (!body.isEmpty()) bodyLVL = getEnchantment(body, player, enchantment);

        return helmetLVL + chestLVL + legsLVL + feetLVL + bodyLVL;
    }
    private static int getEnchantment(ItemStack stack, LivingEntity player, ResourceKey<Enchantment> enchantment) {
        return stack.getEnchantments().getLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
    }


    public static void bootstrap(BootstrapContext<Enchantment> context) {
        HolderGetter<Enchantment> enchantments = context.lookup(Registries.ENCHANTMENT);
        HolderGetter<Item> items = context.lookup(Registries.ITEM);

        var wolfArmorTag = items.getOrThrow(VWItemTags.WOLF_ARMOR_ENCHANTABLE);
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
                                TOTVW.registerID( "wolf_armor_enhancement_kit"),
                                Attributes.MAX_HEALTH,
                                LevelBasedValue.constant(8),
                                AttributeModifier.Operation.ADD_VALUE
                        ))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TOTVW.registerID( "wolf_armor_enhancement_kit"),
                                Attributes.ATTACK_SPEED,
                                LevelBasedValue.constant(1.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TOTVW.registerID( "wolf_armor_enhancement_kit"),
                                Attributes.KNOCKBACK_RESISTANCE,
                                LevelBasedValue.constant(0.1f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TOTVW.registerID( "wolf_armor_enhancement_kit"),
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
                .exclusiveWith(enchantments.getOrThrow(VWEnchantmentTags.CONTINUOUS_DAMAGE))
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
                .exclusiveWith(enchantments.getOrThrow(VWEnchantmentTags.CONTINUOUS_DAMAGE))
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
                .exclusiveWith(enchantments.getOrThrow(VWEnchantmentTags.CONTINUOUS_DAMAGE))
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
                                TOTVW.registerID( "wolf_effect_bloodlust"),
                                Attributes.ATTACK_DAMAGE,
                                LevelBasedValue.perLevel(1.0f, 1.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TOTVW.registerID( "wolf_effect_bloodlust"),
                                Attributes.ATTACK_KNOCKBACK,
                                LevelBasedValue.constant(2),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TOTVW.registerID( "wolf_effect_bloodlust"),
                                Attributes.KNOCKBACK_RESISTANCE,
                                LevelBasedValue.constant(0.1F),
                                AttributeModifier.Operation.ADD_VALUE
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
                                TOTVW.registerID( "wolf_effect_might"),
                                Attributes.ATTACK_DAMAGE,
                                LevelBasedValue.perLevel(1.0f, 1.0f),
                                AttributeModifier.Operation.ADD_VALUE
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                TOTVW.registerID( "wolf_effect_might"),
                                Attributes.FALL_DAMAGE_MULTIPLIER,
                                LevelBasedValue.perLevel(-0.10f, -0.05f),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
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
                                TOTVW.registerID( "benediction_of_the_verdant_mountains"),
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
        return ResourceKey.create(Registries.ENCHANTMENT, TOTVW.registerID(name));
    }

    public static void register() {
        TOTVW.sendClassRegisterLog("Enchantments");
    }
}