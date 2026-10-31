package cliffordha.totvw.entity;

import cliffordha.totvw.util.TimeUtil;
import net.minecraft.server.level.ServerLevel;

public interface TickCondition {
    boolean test(ServerLevel level);

    /*
    default TickCondition and(TickCondition other) {
        return (level) -> this.test(level) && other.test(level);
    }
    default TickCondition or(TickCondition other) {
        return (level) -> this.test(level) || other.test(level);
    }
    default TickCondition negate() {
        return (level) -> !this.test(level);
    }
     */

    static TickCondition alwaysTrue() {return (_) -> true;}

    static TickCondition tick(int min, int sec) {
        return (level) -> level.getGameTime() % TimeUtil.tickDuration(min, sec) == 0;
    }
    static TickCondition tick() {
        return (world) -> world.getGameTime() % 20 == 0;
    }
    static TickCondition halfTick() {
        return (world) -> world.getGameTime() % 10 == 0;
    }
    static TickCondition quarterTick() {
        return (world) -> world.getGameTime() % 5 == 0;
    }
}
