package cliffordha.totvw.effect;

import cliffordha.totvw.entity.skill.SkillCooldowns;
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
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class HavocEffect extends MobEffect {
    public static final ParticleOptions HAVOC_PARTICLE = new DustParticleOptions(VWColors.HAVOC_PARTICLE, 1.0f);
    public HavocEffect() {
        super(MobEffectCategory.BENEFICIAL, VWColors.BLOODLUST, HAVOC_PARTICLE);
    }
    private Component setName;

    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }
    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (!entity.is(EntityTypes.PLAYER) || !entity.is(EntityTypes.WOLF)) {
            entity.removeEffect(VWEffects.HAVOC);
        }
    }


    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
        if (mob instanceof Player player) {
            HavocType type = HavocType.getType(player);
            String value = type == HavocType.NONE ? "" : ": " + type.getName();

            setName = Component.literal("Havoc" + value);
        }
        return super.applyEffectTick(serverLevel, mob, amplification);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public Component getDisplayName() {
        return setName != null ? setName : super.getDisplayName();
    }

    public static void removeHavoc(LivingEntity entity) {
        if (entity instanceof Player player) {
            int cooldown = SkillCooldowns.getHavoc(player);
            player.setAttached(PlayerAttachment.CD_HAVOC, cooldown);
            player.removeAttached(PlayerAttachment.HAVOC_TYPE);
            player.removeAttached(PlayerAttachment.HAVOC_USAGE_COUNT);
        }
    }
}
