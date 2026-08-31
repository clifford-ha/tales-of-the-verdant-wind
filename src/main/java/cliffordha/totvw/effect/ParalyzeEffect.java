package cliffordha.totvw.effect;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWIdentifiers;

import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;

public class ParalyzeEffect extends MobEffect {
    private final Identifier ID = VWIdentifiers.EFFECT_PARALYZE;

    public ParalyzeEffect() {
        super(MobEffectCategory.NEUTRAL, VWColors.PARALYZE);
    }

    private final List<Holder<Attribute>> PARALYZE_ATTRIBUTES = List.of(
            Attributes.ARMOR,
            Attributes.ARMOR_TOUGHNESS,
            Attributes.KNOCKBACK_RESISTANCE,
            Attributes.MAX_ABSORPTION,
            Attributes.OXYGEN_BONUS,
            Attributes.ATTACK_DAMAGE,
            Attributes.ATTACK_KNOCKBACK,
            Attributes.MOVEMENT_SPEED,
            Attributes.WATER_MOVEMENT_EFFICIENCY,
            Attributes.JUMP_STRENGTH,
            Attributes.BLOCK_BREAK_SPEED,
            Attributes.BLOCK_INTERACTION_RANGE,
            Attributes.ENTITY_INTERACTION_RANGE
    );
    
    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }

    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        if (entity instanceof Mob mob && !entity.is(EntityType.PLAYER)) {
            mob.setNoAi(true);
        } else if (entity instanceof Player player) {
            if (player.getAbilities().instabuild || player.isSpectator()) return;
            applyParalyzeAttributes(player);

        }
    }

    @Override
    public void onMobRemoved(ServerLevel level, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        removeModifiers(entity);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int tickCount, int amplification) {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel serverLevel, LivingEntity mob, int amplification) {
        if (mob instanceof Player player && player.hasEffect(PARALYZE)) {
            if (!player.getAbilities().instabuild && !player.isSpectator()) {
                applyParalyzeAttributes(player);
            } else {
                removeAllModifiers(player, ID, PARALYZE_ATTRIBUTES);
            }
        }
        return super.applyEffectTick(serverLevel, mob, amplification);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        removeModifiers(entity);
    }

    private void applyParalyzeAttributes(Player player) {
        AttributeMap attributes = player.getAttributes();
        addMultipleModifier(
                attributes,
                ID,
                -1000,
                AttributeModifier.Operation.ADD_VALUE,
                PARALYZE_ATTRIBUTES
        );
    }

    private void removeModifiers(LivingEntity entity) {
        if (entity instanceof Mob mob && !entity.is(EntityType.PLAYER)) {
            mob.setNoAi(false);
        } else if (entity instanceof Player) {
            removeAllModifiers(entity, ID, PARALYZE_ATTRIBUTES);
        }
    }
}