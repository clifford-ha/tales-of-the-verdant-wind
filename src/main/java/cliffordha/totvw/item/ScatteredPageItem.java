package cliffordha.totvw.item;

import cliffordha.totvw.item.events.ReadScatteredPages;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class ScatteredPageItem extends Item {
    public ScatteredPageItem(Properties properties, int page) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            ReadScatteredPages.tryApply(level, player);
        }
        return InteractionResult.SUCCESS;
    }
}
