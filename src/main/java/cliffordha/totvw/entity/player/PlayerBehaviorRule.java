package cliffordha.totvw.entity.player;

import cliffordha.totvw.entity.TickCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

public class PlayerBehaviorRule {
    private final TickCondition condition;
    private final PlayerBehaviorAction action;

    private PlayerBehaviorRule(TickCondition condition, PlayerBehaviorAction action) {
        this.condition = condition;
        this.action = action;
    }

    public static PlayerBehaviorRule register(TickCondition condition, PlayerBehaviorAction action) {
        return new PlayerBehaviorRule(condition, action);
    }

    public void evaluate(Player player, ServerLevel level) {
        if (condition.test(level)) {
            action.execute(player, level);
        }
    }
}
