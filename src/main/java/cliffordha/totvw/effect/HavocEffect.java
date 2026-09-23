package cliffordha.totvw.effect;

import cliffordha.totvw.registry.VWColors;
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
        mob = entity;
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
            player.removeAttached(PlayerAttachment.HAVOC_TYPE);
            player.removeAttached(PlayerAttachment.HAVOC_USAGE_COUNT);
        }
    }
}
