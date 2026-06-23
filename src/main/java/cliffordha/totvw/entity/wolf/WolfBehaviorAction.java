package cliffordha.totvw.entity.wolf;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.wolf.Wolf;

@FunctionalInterface
public interface WolfBehaviorAction {

    void execute(Wolf wolf, ServerLevel level);
    
    default WolfBehaviorAction andThen(WolfBehaviorAction after) {
        return (wolf, level) -> {
            this.execute(wolf, level);
            after.execute(wolf, level);
        };
    }
}
