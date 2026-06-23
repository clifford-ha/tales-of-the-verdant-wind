package cliffordha.totvw.entity.wolf;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;

// signle rule that fires (a) WolfBehaviorAction when a WolfCondition is met
public final class WolfBehaviorRule {
    public enum Scope {
        TAMED,
        WILD,
        ANY
    }

    private final Scope scope;
    private final WolfCondition condition;
    private final WolfBehaviorAction action;

    private WolfBehaviorRule(Scope scope, WolfCondition condition, WolfBehaviorAction action) {
        this.scope     = scope;
        this.condition = condition;
        this.action    = action;
    }

    // factories
    public static WolfBehaviorRule forTamed(WolfCondition condition, WolfBehaviorAction action) {
        return new WolfBehaviorRule(Scope.TAMED, condition, action);
    }
    public static WolfBehaviorRule forWild(WolfCondition condition, WolfBehaviorAction action) {
        return new WolfBehaviorRule(Scope.WILD, condition, action);
    }
    public static WolfBehaviorRule forAny(WolfCondition condition, WolfBehaviorAction action) {
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
        if (condition.test(wolf, level)) {
            action.execute(wolf, level);
        }
    }
    public Scope getScope() { return scope; }
}
