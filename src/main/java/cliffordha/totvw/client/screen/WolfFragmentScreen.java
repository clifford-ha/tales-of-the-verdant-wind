package cliffordha.totvw.client.screen;

import cliffordha.totvw.item.pages.FTextColor;
import cliffordha.totvw.item.pages.FTextStyle;
import cliffordha.totvw.registry.VWColors;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.wolf.Wolf;

import java.util.function.Supplier;

import static cliffordha.totvw.util.TextUtil.cText;
import static cliffordha.totvw.util.TextUtil.fText;

public class WolfFragmentScreen extends WolfTextScreen {
    public WolfFragmentScreen(Supplier<String> liveSupplier, Supplier<Wolf> wolfSupplier) {
        super(Component.literal("Wolf Soul Fragment").withColor(VWColors.WITHERING), liveSupplier, wolfSupplier);
    }

    @Override
    protected String titleText() {
        return cText(FTextColor.DARK_PURPLE, fText(FTextStyle.BOLD, this.title.getString()));
    }

    @Override
    protected int backdropRgb() {
        return VWColors.shadeColor(VWColors.WITHERING, -0.8f);
    }
}