package cliffordha.totvw.util;

import cliffordha.totvw.TOTVW;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;

import java.util.Map;

public class MaterialUtil {
    public static Map<ArmorType, Integer> makeDefense(final int head, final int chest, final int legs, final int feet, final int body) {
        return Maps.newEnumMap(Map.of(ArmorType.HELMET, head, ArmorType.CHESTPLATE, chest, ArmorType.LEGGINGS, legs, ArmorType.BOOTS, feet, ArmorType.BODY, body));
    }
    public static Map<ArmorType, Integer> bodyDefense(final int body) {
        return Maps.newEnumMap(Map.of(ArmorType.BODY, body));
    }
    public static ResourceKey<EquipmentAsset> createKey(String name) {
        return ResourceKey.create(EquipmentAssets.ROOT_ID, TOTVW.registerID(name));
    }
}
