package cliffordha.totvw.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import java.util.List;

public record DamageZip(DamageSource source, float originalDamage, float reduction, Entity target) {
    public DamageZip(DamageSource source, float originalDamage, Entity target) {
        this(source, originalDamage, 0.0f, target);
    }

    public static void apply(DamageZip data, ServerLevel level, float override) {
        apply(new DamageZip(data.source(), override, data.target()), level);
    }

    public static void apply(DamageZip data, ServerLevel level, DamageSource override) {
        apply(new DamageZip(override, data.originalDamage(), data.target()), level);
    }

    public static void apply(DamageZip data, ServerLevel level) {
        if (data.target() == null) return;
        float finalizedAmount = data.reduction() > 0.0f ? data.originalDamage() * data.reduction() : data.originalDamage();
        data.target().hurtServer(level, data.source(), finalizedAmount);
    }

    public static void applyList(List<DamageZip> list, ServerLevel level) {
        if (list.isEmpty()) return;

        for (DamageZip data : list) {
            apply(data, level);
        }
    }
}
