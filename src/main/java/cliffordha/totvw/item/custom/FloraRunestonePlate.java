package cliffordha.totvw.item.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FloraRunestonePlate extends BundleItem {
    public FloraRunestonePlate(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        BundleContents contents = this.components().getOrDefault(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY);
        List<ItemStackTemplate> stacks = new ArrayList<>(contents.items().stream().toList());

        if (!stacks.isEmpty()) {
            for (ItemStackTemplate stack : stacks) {
                ItemStack item = stack.create();
            }
        }

        return super.use(level, player, hand);
    }
}
