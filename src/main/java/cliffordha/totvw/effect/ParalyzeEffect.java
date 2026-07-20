package cliffordha.totvw.effect;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWAttachments;
import cliffordha.totvw.registry.VWColors;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.List;

import static cliffordha.totvw.registry.VWEffects.*;

public class ParalyzeEffect extends MobEffect {
    public ParalyzeEffect() {super(MobEffectCategory.NEUTRAL, VWColors.PARALYZE);}
    private static final Identifier PARALYZE_EFFECT = Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "paralyze_effect");
    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        onEffectAdded(entity, amplifier);
    }


    @Override
    public void onEffectAdded(LivingEntity entity, int amplifier) {
        AttributeMap attributes = entity.getAttributes();

        entity.setAttached(VWAttachments.ENTITY_IS_PARALYZED, true);

        if (entity instanceof Mob mob && !entity.is(EntityType.PLAYER)) {
            mob.setNoAi(true);
        } else if (entity instanceof Player) {
            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.ARMOR, -1000, AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.KNOCKBACK_RESISTANCE, -1000, AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.MAX_ABSORPTION, -1000, AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.MOVEMENT_SPEED, -1000, AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.ATTACK_DAMAGE, -1000, AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.ATTACK_KNOCKBACK, -1000, AttributeModifier.Operation.ADD_VALUE);

            addModifier(attributes, PARALYZE_EFFECT,
                    Attributes.JUMP_STRENGTH, -1000, AttributeModifier.Operation.ADD_VALUE);
        }
    }

    @Override
    public void onMobRemoved(ServerLevel level, LivingEntity entity, int amplifier, Entity.RemovalReason reason) {
        removeModifiers(entity);
    }

    @Override
    public void onEffectRemoved(MobEffectInstance effectInstance, LivingEntity entity) {
        removeModifiers(entity);
    }

    private void removeModifiers(LivingEntity entity) {
        entity.setAttached(VWAttachments.ENTITY_IS_PARALYZED, false);
        if (entity instanceof Mob mob && !entity.is(EntityType.PLAYER)) {
            mob.setNoAi(false);
        } else if (entity instanceof Player) {
            removeAllModifiers(entity, PARALYZE_EFFECT,
                    List.of(
                            Attributes.ARMOR,
                            Attributes.KNOCKBACK_RESISTANCE,
                            Attributes.MAX_ABSORPTION,
                            Attributes.MOVEMENT_SPEED,
                            Attributes.ATTACK_DAMAGE,
                            Attributes.ATTACK_KNOCKBACK,
                            Attributes.JUMP_STRENGTH
                    )
            );
        }
    }
}