package cliffordha.totvw.registry.items;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.item.scatteredpages.ScatteredPageItem;
import net.minecraft.world.item.Item;

public class VWItemsScatteredPage {
    public static final Item SP_ID_1000 = TOTVW.registerItem("scattered_page_1000",
            properties -> new ScatteredPageItem(properties.fireResistant(), 1000)
    );

    public static final Item SP_ID_1001 = TOTVW.registerItem("scattered_page_1001",
            properties -> new ScatteredPageItem(properties, 1001)
    );

    public static final Item SP_ID_1002 = TOTVW.registerItem("scattered_page_1002",
            properties -> new ScatteredPageItem(properties, 1002)
    );


    public static void registerAdditionalItems() {}
}
