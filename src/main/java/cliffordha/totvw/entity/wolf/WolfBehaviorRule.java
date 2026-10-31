package cliffordha.totvw.entity.wolf;

import cliffordha.totvw.entity.TickCondition;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;

public class WolfBehaviorRule {
    public enum Scope {
        TAMED,
        WILD,
        ANY
    }

    private final Scope scope;
    private final TickCondition condition;
    private final WolfBehaviorAction action;

    private WolfBehaviorRule(Scope scope, TickCondition condition, WolfBehaviorAction action) {
        this.scope     = scope;
        this.condition = condition;
        this.action    = action;
    }

    // factories
    public static WolfBehaviorRule forTamed(TickCondition condition, WolfBehaviorAction action) {
        return new WolfBehaviorRule(Scope.TAMED, condition, action);
    }
    public static WolfBehaviorRule forWild(TickCondition condition, WolfBehaviorAction action) {
        return new WolfBehaviorRule(Scope.WILD, condition, action);
    }
    public static WolfBehaviorRule forAny(TickCondition condition, WolfBehaviorAction action) {
        return new WolfBehaviorRule(Scope.ANY, condition, action);
    }
    public boolean isApplicableTo(Wolf wolf) {
        return switch (scope) {
            case TAMED -> wolf.isTame();
            case WILD  -> !wolf.isTame();
            case ANY   -> true;
        };
    }
    public void evaluate(Wolf wolf, ServerLevel level) {
        if (condition.test(level)) {
            action.execute(wolf, level);
        }
    }
    public Scope getScope() { return scope; }
}
