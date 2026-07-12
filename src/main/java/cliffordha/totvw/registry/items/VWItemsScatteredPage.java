package cliffordha.totvw.registry.items;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.item.scatteredpages.ScatteredPageItem;
import net.minecraft.world.item.Item;

public class VWItemsScatteredPage {

    /** reserved for other testing purposes **/
    public static final Item SP_ID_TEST = createPage("scattered_page_test", 0);

    /** placeholder item **/
    public static final Item SCATTERED_PAGE = createPage("scattered_page", -1);

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


    private static Item createPage(String name, int id) {
        return TOTVW.registerItem(name, properties -> new ScatteredPageItem(properties.stacksTo(1), id));
    }

    public static void registerAdditionalItems() {}
}
