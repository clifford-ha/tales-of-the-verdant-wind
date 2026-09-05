package cliffordha.totvw.item;

import cliffordha.totvw.tag.VWItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;

public class VWToolMaterials {
    public static final ToolMaterial VERIXIUM_TOOL_MATERIAL = new ToolMaterial(
            BlockTags.INCORRECT_FOR_DIAMOND_TOOL,
            1800,
            8.5F,
            3.5F,
            13,
            VWItemTags.REPAIRS_VERIXIUM
    );
}
