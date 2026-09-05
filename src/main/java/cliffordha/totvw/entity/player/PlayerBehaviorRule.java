package cliffordha.totvw.entity.player;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class PlayerBehaviorRule {

    private final PlayerCondition condition;
    private final PlayerBehaviorAction action;

    private PlayerBehaviorRule(PlayerCondition condition, PlayerBehaviorAction action) {
        this.condition = condition;
        this.action = action;
    }

    public static PlayerBehaviorRule register(PlayerCondition condition, PlayerBehaviorAction action) {
        return new PlayerBehaviorRule(condition, action);
    }

    public void evaluate(Player player, ServerLevel level) {
        if (condition.test(player, level)) {
            action.execute(player, level);
        }
    }
}
