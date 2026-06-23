package cliffordha.totvw.item;

import java.util.Map;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.tag.ModItemTags;
import com.google.common.collect.Maps;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

public class ModArmorMaterial {
    private static final ResourceKey<EquipmentAsset> VERIXIUM_ARMOR_MATERIAL_KEY = ResourceKey.create(EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium"));
    private static final ResourceKey<EquipmentAsset> VERIXIUM_WOLF_ARMOR_MATERIAL_KEY = ResourceKey.create(EquipmentAssets.ROOT_ID,
            Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "verixium"));

    public static final ArmorMaterial VERIXIUM_ARMOR_MATERIAL = new ArmorMaterial(
            35,
            makeDefense(3, 7, 5, 3, 6),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            0.0F,
            0.0F,
            ModItemTags.REPAIRS_VERIXIUM,
            VERIXIUM_ARMOR_MATERIAL_KEY
    );
    public static final ArmorMaterial VERIXIUM_ENTITY_ARMOR = new ArmorMaterial(
            35,
            bodyDefense(15),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            0.0F,
            0.0F,
            ModItemTags.REPAIRS_VERIXIUM,
            VERIXIUM_WOLF_ARMOR_MATERIAL_KEY
    );

    static Map<ArmorType, Integer> makeDefense(final int head, final int chest, final int legs, final int feet, final int body) {
        return Maps.newEnumMap(Map.of(ArmorType.HELMET, head, ArmorType.CHESTPLATE, chest, ArmorType.LEGGINGS, legs, ArmorType.BOOTS, feet, ArmorType.BODY, body));
    }
    static Map<ArmorType, Integer> bodyDefense(final int body) {
        return Maps.newEnumMap(Map.of(ArmorType.BODY, body));
    }
}