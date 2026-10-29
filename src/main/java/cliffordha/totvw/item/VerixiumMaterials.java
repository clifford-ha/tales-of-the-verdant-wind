package cliffordha.totvw.item;

import cliffordha.totvw.tag.VWItemTags;
import cliffordha.totvw.util.MaterialUtil;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.EquipmentAsset;

public class VerixiumMaterials {
    private static final ResourceKey<EquipmentAsset> VERIXIUM_MATERIAL_KEY = MaterialUtil.createKey("verixium");

    public static final ToolMaterial TOOL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            1800,
            8.5F,
            3.5F,
            13,
            VWItemTags.REPAIRS_VERIXIUM
    );

    public static final ArmorMaterial ARMOR = new ArmorMaterial(
            35,
            MaterialUtil.makeDefense(3, 7, 5, 3, 6),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            0.0F,
            0.0F,
            VWItemTags.REPAIRS_VERIXIUM,
            VERIXIUM_MATERIAL_KEY
    );
    public static final ArmorMaterial ENTITY_ARMOR = new ArmorMaterial(
            35,
            MaterialUtil.bodyDefense(15),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            0.0F,
            0.0F,
            VWItemTags.REPAIRS_VERIXIUM,
            VERIXIUM_MATERIAL_KEY
    );
}