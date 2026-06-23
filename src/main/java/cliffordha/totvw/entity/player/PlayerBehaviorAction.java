package cliffordha.totvw.entity.player;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public interface PlayerBehaviorAction {
    void execute(Player player, ServerLevel level);

    default PlayerBehaviorAction andThen(PlayerBehaviorAction after) {
        return (player, level) -> {
            this.execute(player, level);
            after.execute(player, level);
        };
    }
}
