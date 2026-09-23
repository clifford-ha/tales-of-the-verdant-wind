package cliffordha.totvw.item.custom;

import cliffordha.totvw.config.VWConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

import java.util.List;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class TetherRunestonePlate extends Item {
    public TetherRunestonePlate(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel serverLevel && player.isCrouching()) {
            List<Wolf> recall = serverLevel.getEntitiesOfClass(Wolf.class,
                    player.getBoundingBox().inflate(VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16),
                    t -> t.getOwner() == player
            );
            if (recall.isEmpty()) {
                sendToChat(player, true, "No nearby tamed wolves to recall!");
                return InteractionResult.FAIL;
            } else {
                for (Wolf wolf : recall) {
                    wolf.teleportTo(player.getX(), player.getY(), player.getZ());
                }
                sendToChat(player, true, "Recalled " + recall.size() + " nearby tamed wolves.");
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return super.use(level, player, hand);
    }
}
