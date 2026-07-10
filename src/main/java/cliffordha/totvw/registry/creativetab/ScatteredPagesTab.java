package cliffordha.totvw.registry.creativetab;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.items.VWItemsScatteredPage;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ScatteredPagesTab extends Item {
    public ScatteredPagesTab(Properties properties) {
        super(properties);
    }
    /*

    public static final ResourceKey<CreativeModeTab> SCATTERED_PAGES_TAB_KEY = ResourceKey.create(
            BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "scattered_pages_tab"));

    public static final CreativeModeTab SCATTERED_PAGES_TAB = FabricCreativeModeTab.builder()
            .icon(() -> new ItemStack(Items.PAPER))
            .title(Component.translatable("creativeTab.scatteredPages"))
            .displayItems((params, output) -> {
                output.accept(VWItemsScatteredPage.SP_ID_1000);
                output.accept(VWItemsScatteredPage.SP_ID_1001);
                output.accept(VWItemsScatteredPage.SP_ID_1002);
            }).build();

     */
}
