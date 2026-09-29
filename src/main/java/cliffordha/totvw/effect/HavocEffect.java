package cliffordha.totvw.effect;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.attachments.HavocType;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;

public class HavocEffect extends MobEffect {
    public static final ParticleOptions HAVOC_PARTICLE = new DustParticleOptions(VWColors.HAVOC_PARTICLE, 1.0f);
    public HavocEffect() {
        super(MobEffectCategory.BENEFICIAL, VWColors.BLOODLUST_EFFECT, HAVOC_PARTICLE);
    }
    public static LivingEntity mob;

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }
    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (entity instanceof Player || entity instanceof Wolf) {
            mob = entity;
        } else {
            entity.removeEffect(VWEffects.HAVOC);
        }
    }

    @Override
    public Component getDisplayName() {
        if (mob instanceof Player player) {
            HavocType type = HavocType.getType(player);
            String value = type == HavocType.NONE ? "" : ": " + type.getName();

            return Component.literal("Havoc" + value);
        }
        return super.getDisplayName();
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public void onMobRemoved(ServerLevel level, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        removeHavoc(entity);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        removeHavoc(entity);
    }

    public static void removeHavoc(LivingEntity entity) {
        if (entity instanceof Player player) {
            int cooldown;
            if (HavocType.isAnnihilation(player)) {
                cooldown = 90;
            } else if (HavocType.isExpulsion(player)) {
                cooldown = (60 * 2) + 30;
            } else if (HavocType.isVoid(player)) {
                cooldown = (60 * 7);
            } else {
                cooldown = 30;
            }
            player.setAttached(PlayerAttachment.CD_HAVOC, cooldown);
            player.removeAttached(PlayerAttachment.HAVOC_TYPE);
            player.removeAttached(PlayerAttachment.HAVOC_USAGE_COUNT);
        }
    }
}
