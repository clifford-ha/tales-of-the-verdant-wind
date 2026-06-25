package cliffordha.totvw.registry;

import cliffordha.totvw.registry.blocks.VerdantBlocks;
import cliffordha.totvw.registry.creativetab.VerdantItemsTab;
import cliffordha.totvw.item.ItemInstance;
import cliffordha.totvw.TOTVW;

import cliffordha.totvw.item.ModArmorMaterial;
import cliffordha.totvw.item.ModToolMaterial;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;

import static cliffordha.totvw.TOTVW.sendLog;

public class ModItems {
    public static final Identifier VERIXIUM_ARMOR_EQUIPPED = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_armor_equipped");
    public static final Item VERIXIUM_HELMET = TOTVW.registerItem("verixium_helmet",
            properties -> new Item(properties
                    .humanoidArmor(ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL, ArmorType.HELMET)
                    .fireResistant()
                    .attributes(
                            ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.HELMET)
                                    .withModifierAdded(
                                            Attributes.OXYGEN_BONUS,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    0.2F,
                                                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                            ),
                                            EquipmentSlotGroup.HEAD
                                    )
                    )
            ));
    public static final Item VERIXIUM_CHESTPLATE = TOTVW.registerItem("verixium_chestplate",
            properties -> new Item(properties
                    .humanoidArmor(ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
                    .fireResistant()
                    .attributes(
                            ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.CHESTPLATE)
                                    .withModifierAdded(
                                            Attributes.KNOCKBACK_RESISTANCE,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    0.1F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.CHEST
                                    )
                                    .withModifierAdded(
                                            Attributes.ARMOR_TOUGHNESS,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    2,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.CHEST
                                    )

                    )
            ));
    public static final Item VERIXIUM_LEGGINGS = TOTVW.registerItem("verixium_leggings",
            properties -> new Item(properties
                    .humanoidArmor(ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL, ArmorType.LEGGINGS)
                    .fireResistant()
                    .attributes(
                            ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.LEGGINGS)
                                    .withModifierAdded(
                                            Attributes.SNEAKING_SPEED,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    0.15F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.LEGS
                                    )
                                    .withModifierAdded(
                                            Attributes.KNOCKBACK_RESISTANCE,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    0.1F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.LEGS
                                    )
                                    .withModifierAdded(
                                            Attributes.ARMOR_TOUGHNESS,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    2,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.LEGS
                                    )
                    )
            ));
    public static final Item VERIXIUM_BOOTS = TOTVW.registerItem("verixium_boots",
            properties -> new Item(properties
                    .humanoidArmor(ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL, ArmorType.BOOTS)
                    .fireResistant()
                    .attributes(
                            ModArmorMaterial.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.BOOTS)
                                    .withModifierAdded(
                                            Attributes.MOVEMENT_EFFICIENCY,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    0.3F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.FEET
                                    )
                                    .withModifierAdded(
                                            Attributes.KNOCKBACK_RESISTANCE,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    0.1F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.FEET
                                    )
                                    .withModifierAdded(
                                            Attributes.ARMOR_TOUGHNESS,
                                            new AttributeModifier(
                                                    VERIXIUM_ARMOR_EQUIPPED,
                                                    2,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.FEET
                                    )
                    )
            ));


    public static final Identifier VERIXIUM_WOLF_ARMOR_EQUIPPED = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_wolf_armor_equipped");
    public static final Identifier VERIXIUM_HORSE_ARMOR_EQUIPPED = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium_horse_armor_equipped");
    public static final Item VERIXIUM_WOLF_ARMOR = TOTVW.registerItem("verixium_wolf_armor",
            properties -> new Item(properties
                    .fireResistant()
                    .wolfArmor(ModArmorMaterial.VERIXIUM_ENTITY_ARMOR)
                    .enchantable(15)
                    .attributes(
                            ModArmorMaterial.VERIXIUM_ENTITY_ARMOR.createAttributes(ArmorType.BODY)
                                    .withModifierAdded(
                                            Attributes.BURNING_TIME,
                                            new AttributeModifier(
                                                    VERIXIUM_WOLF_ARMOR_EQUIPPED,
                                                    -0.2F,
                                                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                            ),
                                            EquipmentSlotGroup.BODY
                                    )
                    )
            ));

    public static final Item VERIXIUM_HORSE_ARMOR = TOTVW.registerItem( "verixium_horse_armor",
            properties -> new Item(properties
                    .fireResistant()
                    .horseArmor(ModArmorMaterial.VERIXIUM_ENTITY_ARMOR)
                    .enchantable(15)
                    .attributes(
                            ModArmorMaterial.VERIXIUM_ENTITY_ARMOR.createAttributes(ArmorType.BODY)
                                    .withModifierAdded(
                                            Attributes.JUMP_STRENGTH,
                                            new AttributeModifier(
                                                    VERIXIUM_HORSE_ARMOR_EQUIPPED,
                                                    0.5,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.BODY
                                    )
                                    .withModifierAdded(
                                            Attributes.FALL_DAMAGE_MULTIPLIER,
                                            new AttributeModifier(
                                                    VERIXIUM_HORSE_ARMOR_EQUIPPED,
                                                    -0.2f,
                                                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                            ),
                                            EquipmentSlotGroup.BODY
                                    )
                    )
            )
    );


    //VERIXIUM ITEMS
    public static final Item VERIXIUM_CHUNK = TOTVW.registerItem("verixium_chunk",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item CONDENSED_VERIXIUM = TOTVW.registerItem("condensed_verixium",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SHARD = TOTVW.registerItem("verixium_shard",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_POWDER = TOTVW.registerItem("verixium_powder",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_INGOT = TOTVW.registerItem("verixium_ingot",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_PAPER = TOTVW.registerItem("verixium_paper",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_ARMOR_UPGRADE_TEMPLATE = TOTVW.registerItem("verixium_armor_upgrade_template",
            properties -> new Item(properties
                    .rarity(Rarity.EPIC)
                    .fireResistant()
            ));
    public static final float V = 0.5f;
    public static final Item VERIXIUM_SPEAR = TOTVW.registerItem("verixium_spear",
            properties -> new Item(properties
                    .spear(ModToolMaterial.VERIXIUM_TOOL_MATERIAL, 1.10f, 1.1f, 0.5f, 1.5f, 9.0f, 6.0f, 5.1f, 9.10f, 4.6f)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SWORD = TOTVW.registerItem("verixium_sword",
            properties -> new ItemInstance(properties
                    .sword(ModToolMaterial.VERIXIUM_TOOL_MATERIAL, 3.0F, -2.4f)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_AXE = TOTVW.registerItem("verixium_axe",
            properties -> new AxeItem(ModToolMaterial.VERIXIUM_TOOL_MATERIAL, 5.0F, -3.0f + V, properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_PICKAXE = TOTVW.registerItem("verixium_pickaxe",
            properties -> new Item(properties
                    .pickaxe(ModToolMaterial.VERIXIUM_TOOL_MATERIAL, 1.0F, -2.8f)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_HOE = TOTVW.registerItem("verixium_hoe",
            properties -> new HoeItem(ModToolMaterial.VERIXIUM_TOOL_MATERIAL, 3.0F, 0.0f, properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SHOVEL = TOTVW.registerItem("verixium_shovel",
            properties -> new ShovelItem(ModToolMaterial.VERIXIUM_TOOL_MATERIAL, 1.5F, -3.0f, properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_FLUID_BUCKET = TOTVW.registerItem("verixium_fluid_bucket",
            properties -> new BucketItem(ModFluids.VERIXIUM_FLUID, properties
                    .stacksTo(1)
                    .craftRemainder(Items.BUCKET)
                    .fireResistant()
            ));
    public static final Item VERDANT_SPRUCE_BOAT = TOTVW.registerItem("verdant_spruce_boat",
            properties -> new BoatItem(ModEntities.VERDANT_SPRUCE_BOAT, properties.stacksTo(1)));
    public static final Item VERDANT_SPRUCE_CHEST_BOAT = TOTVW.registerItem("verdant_spruce_chest_boat",
            properties -> new BoatItem(ModEntities.VERDANT_SPRUCE_CHEST_BOAT, properties.stacksTo(1)));

    public static final Item VERDANT_SPRUCE_SIGN = TOTVW.registerItem("verdant_spruce_sign",
            properties -> new SignItem(VerdantBlocks.VERDANT_SPRUCE_SIGN, VerdantBlocks.VERDANT_SPRUCE_WALL_SIGN, properties
                    .stacksTo(16)
            ));
    public static final Item VERDANT_SPRUCE_HANGING_SIGN = TOTVW.registerItem("verdant_spruce_hanging_sign",
            properties -> new HangingSignItem(VerdantBlocks.VERDANT_SPRUCE_HANGING_SIGN, VerdantBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN, properties
                    .stacksTo(16)
            ));


    public static void registerModItems() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, VerdantItemsTab.VERDANT_ITEMS_TAB_KEY, VerdantItemsTab.VERDANT_ITEMS_TAB);
        sendLog("Items"); }
}
