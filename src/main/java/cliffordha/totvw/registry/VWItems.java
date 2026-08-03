package cliffordha.totvw.registry;

import cliffordha.totvw.item.scatteredpages.ScatteredPageItem;
import cliffordha.totvw.TOTVW;
import cliffordha.totvw.item.VWArmorMaterials;
import cliffordha.totvw.item.VWToolMaterials;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;

import java.util.function.Function;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;
import static cliffordha.totvw.registry.VWItems.Util.*;

public class VWItems {
    public static final Item VERIXIUM_HELMET = registerItem("verixium_helmet",
            properties -> new Item(properties
                    .humanoidArmor(VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL, ArmorType.HELMET)
                    .fireResistant()
                    .attributes(
                            VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.HELMET)
                                    .withModifierAdded(
                                            Attributes.OXYGEN_BONUS,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    0.2F,
                                                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                            ),
                                            EquipmentSlotGroup.HEAD
                                    )
                    )
            ));
    public static final Item VERIXIUM_CHESTPLATE = registerItem("verixium_chestplate",
            properties -> new Item(properties
                    .humanoidArmor(VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL, ArmorType.CHESTPLATE)
                    .fireResistant()
                    .attributes(
                            VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.CHESTPLATE)
                                    .withModifierAdded(
                                            Attributes.KNOCKBACK_RESISTANCE,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    0.1F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.CHEST
                                    )
                                    .withModifierAdded(
                                            Attributes.ARMOR_TOUGHNESS,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    2,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.CHEST
                                    )

                    )
            ));
    public static final Item VERIXIUM_LEGGINGS = registerItem("verixium_leggings",
            properties -> new Item(properties
                    .humanoidArmor(VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL, ArmorType.LEGGINGS)
                    .fireResistant()
                    .attributes(
                            VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.LEGGINGS)
                                    .withModifierAdded(
                                            Attributes.SNEAKING_SPEED,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    0.15F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.LEGS
                                    )
                                    .withModifierAdded(
                                            Attributes.KNOCKBACK_RESISTANCE,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    0.1F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.LEGS
                                    )
                                    .withModifierAdded(
                                            Attributes.ARMOR_TOUGHNESS,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    2,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.LEGS
                                    )
                    )
            ));
    public static final Item VERIXIUM_BOOTS = registerItem("verixium_boots",
            properties -> new Item(properties
                    .humanoidArmor(VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL, ArmorType.BOOTS)
                    .fireResistant()
                    .attributes(
                            VWArmorMaterials.VERIXIUM_ARMOR_MATERIAL.createAttributes(ArmorType.BOOTS)
                                    .withModifierAdded(
                                            Attributes.MOVEMENT_EFFICIENCY,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    0.3F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.FEET
                                    )
                                    .withModifierAdded(
                                            Attributes.KNOCKBACK_RESISTANCE,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    0.1F,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.FEET
                                    )
                                    .withModifierAdded(
                                            Attributes.ARMOR_TOUGHNESS,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_ARMOR_EQUIPPED,
                                                    2,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.FEET
                                    )
                    )
            ));
    public static final Item VERIXIUM_WOLF_ARMOR = registerItem("verixium_wolf_armor",
            properties -> new Item(properties
                    .fireResistant()
                    .wolfArmor(VWArmorMaterials.VERIXIUM_ENTITY_ARMOR)
                    .enchantable(15)
                    .attributes(
                            VWArmorMaterials.VERIXIUM_ENTITY_ARMOR.createAttributes(ArmorType.BODY)
                                    .withModifierAdded(
                                            Attributes.MOVEMENT_SPEED,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_WOLF_ARMOR_EQUIPPED,
                                                    0.05,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.BODY
                                    )
                    )
            ));

    public static final Item VERIXIUM_HORSE_ARMOR = registerItem( "verixium_horse_armor",
            properties -> new Item(properties
                    .fireResistant()
                    .horseArmor(VWArmorMaterials.VERIXIUM_ENTITY_ARMOR)
                    .enchantable(15)
                    .attributes(
                            VWArmorMaterials.VERIXIUM_ENTITY_ARMOR.createAttributes(ArmorType.BODY)
                                    .withModifierAdded(
                                            Attributes.JUMP_STRENGTH,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_HORSE_ARMOR_EQUIPPED,
                                                    0.5,
                                                    AttributeModifier.Operation.ADD_VALUE
                                            ),
                                            EquipmentSlotGroup.BODY
                                    )
                                    .withModifierAdded(
                                            Attributes.FALL_DAMAGE_MULTIPLIER,
                                            new AttributeModifier(
                                                    VWIdentifiers.VERIXIUM_HORSE_ARMOR_EQUIPPED,
                                                    -0.2f,
                                                    AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                                            ),
                                            EquipmentSlotGroup.BODY
                                    )
                    )
            )
    );


    //VERIXIUM ITEMS
    public static final Item VERIXIUM_CHUNK = registerItem("verixium_chunk",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item CONDENSED_VERIXIUM = registerItem("condensed_verixium",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SHARD = registerItem("verixium_shard",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_POWDER = registerItem("verixium_powder",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_INGOT = registerItem("verixium_ingot",
            properties -> new Item(properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_PAPER = registerItem("verixium_paper",
            properties -> new Item(properties
                    .fireResistant())
            );
    public static final Item VERIXIUM_ARMOR_UPGRADE_TEMPLATE = registerItem("verixium_armor_upgrade_template",
            properties -> new Item(properties
                    .rarity(Rarity.EPIC)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SPEAR = registerItem("verixium_spear",
            properties -> new Item(properties
                    .spear(VWToolMaterials.VERIXIUM_TOOL_MATERIAL, 1.10f, 1.1f, 0.5f, 1.3f, 9.0f, 6.0f, 5.1f, 9.10f, 4.6f)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SWORD = registerItem("verixium_sword",
            properties -> new Item(properties
                    .sword(VWToolMaterials.VERIXIUM_TOOL_MATERIAL, 3.0F, -2.4f)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_AXE = registerItem("verixium_axe",
            properties -> new AxeItem(VWToolMaterials.VERIXIUM_TOOL_MATERIAL, 5.0F, -2.5f, properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_PICKAXE = registerItem("verixium_pickaxe",
            properties -> new Item(properties
                    .pickaxe(VWToolMaterials.VERIXIUM_TOOL_MATERIAL, 1.0F, -2.8f)
                    .fireResistant()
            ));
    public static final Item VERIXIUM_HOE = registerItem("verixium_hoe",
            properties -> new HoeItem(VWToolMaterials.VERIXIUM_TOOL_MATERIAL, 3.0F, 0.0f, properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_SHOVEL = registerItem("verixium_shovel",
            properties -> new ShovelItem(VWToolMaterials.VERIXIUM_TOOL_MATERIAL, 1.5F, -3.0f, properties
                    .fireResistant()
            ));
    public static final Item VERIXIUM_FLUID_BUCKET = registerItem("verixium_fluid_bucket",
            properties -> new BucketItem(VWFluids.VERIXIUM_FLUID, properties
                    .stacksTo(1)
                    .craftRemainder(Items.BUCKET)
                    .fireResistant()
            ));
    public static final Item VERDANT_SPRUCE_BOAT = registerItem("verdant_spruce_boat",
            properties -> new BoatItem(VWEntities.VERDANT_SPRUCE_BOAT, properties.stacksTo(1)));
    public static final Item VERDANT_SPRUCE_CHEST_BOAT = registerItem("verdant_spruce_chest_boat",
            properties -> new BoatItem(VWEntities.VERDANT_SPRUCE_CHEST_BOAT, properties.stacksTo(1)
            ));
    public static final Item VERDANT_SPRUCE_SIGN = registerItem("verdant_spruce_sign",
            properties -> new SignItem(VWBlocks.VERDANT_SPRUCE_SIGN, VWBlocks.VERDANT_SPRUCE_WALL_SIGN, properties
                    .stacksTo(16)
            ));
    public static final Item VERDANT_SPRUCE_HANGING_SIGN = registerItem("verdant_spruce_hanging_sign",
            properties -> new HangingSignItem(VWBlocks.VERDANT_SPRUCE_HANGING_SIGN, VWBlocks.VERDANT_SPRUCE_WALL_HANGING_SIGN, properties
                    .stacksTo(16)
            ));

    public static class Pages {

        /** reserved for other testing purposes **/
        public static final Item SP_ID_TEST = createPage("scattered_page_test", 0);

        /** placeholder items **/
        public static final Item SCATTERED_PAGE = createPlaceholder("scattered_page");
        public static final Item SCATTERED_PAGE_VARIANT_1 = createPlaceholder("scattered_page_variant_1");
        public static final Item SCATTERED_PAGE_VARIANT_2 = createPlaceholder("scattered_page_variant_2");
        public static final Item SCATTERED_PAGE_VARIANT_3 = createPlaceholder("scattered_page_variant_3");

        public static final Item OLD_SCATTERED_PAGE = createPlaceholder("old_scattered_page");
        public static final Item OLD_SCATTERED_PAGE_VARIANT_1 = createPlaceholder("old_scattered_page_variant_1");
        public static final Item OLD_SCATTERED_PAGE_VARIANT_2 = createPlaceholder("old_scattered_page_variant_2");
        public static final Item OLD_SCATTERED_PAGE_VARIANT_3 = createPlaceholder("old_scattered_page_variant_3");


        /** a page to test multiple types of tests at once **/
        public static final Item SP_ID_1000 = createPage("scattered_page_1000", 1000);

        public static final Item SP_ID_1001 = createPage("scattered_page_1001", 1001);
        public static final Item SP_ID_1002 = createPage("scattered_page_1002", 1002);
        public static final Item SP_ID_1003 = createPage("scattered_page_1003", 1003);
        public static final Item SP_ID_1004 = createPage("scattered_page_1004", 1004);

        public static final Item SP_ID_1005 = createPage("scattered_page_1005", 1005);
        public static final Item SP_ID_1006 = createPage("scattered_page_1006", 1006);
        public static final Item SP_ID_1007 = createPage("scattered_page_1007", 1007);
        public static final Item SP_ID_1008 = createPage("scattered_page_1008", 1008);
        public static final Item SP_ID_1009 = createPage("scattered_page_1009", 1009);


        /** under testing **/
        public static final Item PLAYER_STATS = createPage("player_stats", -2);
        public static final Item LODESTONE_WIND_CORE_MANUAL = createPage("lodestone_wind_core_manual", 333);

        public static void register() {}
    }


    public static void register() {
        Pages.register();
        VWCreativeTabs.register();
        sendClassRegisterLog("Items");
    }
    
    public static class Util {
        public static Item registerItem(String name, Function<Item.Properties, Item> function) {
            return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name),
                    function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name)))));
        }

        public static Item createPage(String name, int id) {
            return VWItems.Util.registerItem(name, properties -> new ScatteredPageItem(properties.stacksTo(1), id));
        }
        public static Item createPlaceholder(String name) {
            return VWItems.Util.registerItem(name, properties -> new Item(properties.stacksTo(1)));
        }
    }
}
