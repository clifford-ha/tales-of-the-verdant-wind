package cliffordha.totvw.util;

import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.entity.skill.VWSkillProcessor;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.tag.VWBiomeTags;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.Nullable;

public final class VWUtil {
    public static int sec(int sec) {
        return sec * 20;
    }
    public static int min(int min) {
        return min * sec(60);
    }

    public static boolean isInBiomes(LivingEntity entity, TagKey<Biome> biome) {
        return entity.level().getBiome(entity.blockPosition()).is(biome);
    }
    public static boolean isInBiomes(LevelAccessor level,BlockPos pos, TagKey<Biome> biome) {
        return level.getBiome(pos).is(biome);
    }

    public static float triggerHeal(LivingEntity granter, LivingEntity grantee) {
        float triggerHeal;
        if (isInBiomes(granter, VWBiomeTags.IS_VERDANT_BIOMES)) {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.5f));
        } else {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.3f));}
        return triggerHeal;
    }
    public static void verdantBlessingAfterEffects(LevelAccessor level, LivingEntity entity) {
        if (!TOTVWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        int minutes = 60;
        int cooldown = setDifficultyBasedValue(level, minutes * 3, minutes * 9, minutes * 15, minutes * 21);
        if (isHalfHealth(entity)) {
            addHiddenEffect(entity, MobEffects.WEAKNESS, minutes, 0);
        } else {
            addHiddenEffect(entity, MobEffects.WEAKNESS, minutes, 1);
        }
        if (entity instanceof Wolf wolf) {
            SkillUtil.startCooldown(wolf, VWWolfBehaviors.VERDANT_BLESSING, cooldown);
            sendToChat(wolf, VWColors.VERDANT_WIND_MUTED, "Cooldown: " + cooldown + " sec");
        } else if (entity instanceof Player player) {
            SkillUtil.startCooldown(player, VWPlayerBehaviors.VERDANT_BLESSING, cooldown);
            sendToChat(player, VWColors.VERDANT_WIND_MUTED, "Cooldown: " + cooldown + " sec");
        }
    }


    public static int setDifficultyBasedValue(LevelAccessor level, int peacefulCD, int easyCD, int normalCD, int hardCD) {
        int finalCD;
        switch (level.getDifficulty()) {
            case PEACEFUL -> finalCD = peacefulCD;
            case EASY -> finalCD = easyCD;
            case NORMAL -> finalCD = normalCD;
            default -> finalCD = hardCD;
        }
        return finalCD;
    }

    public static float setDifficultyBasedValue(LevelAccessor level, float peacefulCD, float easyCD, float normalCD, float hardCD) {
        float finalCD;
        switch (level.getDifficulty()) {
            case PEACEFUL -> finalCD = peacefulCD;
            case EASY -> finalCD = easyCD;
            case NORMAL -> finalCD = normalCD;
            default -> finalCD = hardCD;
        }
        return finalCD;
    }

    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource source) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        var posX = entity.getX();
        var posY = entity.getY();
        var posZ = entity.getZ();
        var random = level.getRandom().nextFloat();
        level.playSound(null, posX, posY, posZ, sound, source, 0.3f + random, 0.5f + random);
    }
    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource source, float volume, float pitch) {
        if (!(entity.level() instanceof ServerLevel level)) return;
        var posX = entity.getX();
        var posY = entity.getY();
        var posZ = entity.getZ();
        level.playSound(null, posX, posY, posZ, sound, source, volume, pitch);
    }

    public static boolean isHalfHealth(LivingEntity entity) {
        return entity.getHealth() >= entity.getMaxHealth() * 0.5f;
    }

    public static void addParticle(ServerLevel level, BlockPos pos, ParticleOptions particle, int frequency) {
        for (int i = 0; i < (4 * (frequency + 1)); i++) {
            float randA = level.getRandom().nextFloat();
            float randB = level.getRandom().nextFloat();
            level.sendParticles(particle, pos.getX() + randB, pos.getY() + randA, pos.getZ() + randB, 1, 0.0D, 0.0D, 0.0D, 0);
        }
    }

    public static void rewriteEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) {
            entity.removeEffect(effect);
        }
        entity.addEffect(new MobEffectInstance(effect, sec, amp, false, false));
    }

    public static void addEffect( LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) return;
        entity.addEffect(new MobEffectInstance(effect, sec, amp));
    }
    public static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) {
            int currentAmp = entity.getEffect(effect).getAmplifier();
            int currentDuration = entity.getEffect(effect).getDuration();
            int DURATION_STACK;

            if (amp < currentAmp) {
                DURATION_STACK = currentDuration + sec;
                rewriteEffect(entity, effect, DURATION_STACK, currentAmp);
            } else {
                DURATION_STACK = (int) (currentDuration + Math.ceil(sec * 0.5f));
                rewriteEffect(entity, effect, DURATION_STACK, currentAmp + 1);
            }
        } else {
            rewriteEffect(entity, effect, sec, amp);
        }
    }

    public static void addHiddenEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) return;
        entity.addEffect(new MobEffectInstance(effect, sec, amp, false, false));
    }

    public static void removeEffect(LivingEntity entity, Holder<MobEffect> effect) {
        if (entity.hasEffect(effect)) {
            entity.removeEffect(effect);
        }
    }


    public static int wolfEnchantmentLVL(Wolf wolf, ResourceKey<Enchantment> enchantment) {
        if (!wolf.isWearingBodyArmor()) return 0;
        return wolf.getItemBySlot(EquipmentSlot.BODY).getEnchantments()
                .getLevel(wolf.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));}

    public static int playerEnchantmentLVL(LivingEntity player, ResourceKey<Enchantment> enchantment) {
        if (player == null) return 0;
        return player.getItemBySlot(EquipmentSlot.CHEST).getEnchantments()
                .getLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
    }

    private static void sendToMain(ServerPlayer player, boolean overlay, String msg) {
        player.sendSystemMessage(Component.literal(msg), overlay);
    }
    private static void sendToMain(ServerPlayer player, int color, String msg) {
        player.sendSystemMessage(Component.literal(msg).withColor(color));
    }
    private static void sendToMain(ServerPlayer player, int color, boolean overlay, String msg) {
        player.sendSystemMessage(Component.literal(msg).withColor(color), overlay);
    }

    private static @Nullable ServerPlayer resolveRecipient(LivingEntity entity) {
        if (entity instanceof ServerPlayer serverPlayer) return serverPlayer;
        if (entity instanceof Wolf wolf && wolf.getOwner() instanceof ServerPlayer serverPlayer) return serverPlayer;
        return null;
    }

    public static void sendToChat(LivingEntity entity, boolean overlay, String... msg) {
        if (!TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS) return;
        ServerPlayer player = resolveRecipient(entity);
        if (player == null) return;
        sendToMain(player, overlay, String.join("\n", msg));
    }

    public static void sendToChat(LivingEntity entity, int color, String... msg) {
        if (!TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS) return;
        ServerPlayer player = resolveRecipient(entity);
        if (player == null) return;
        sendToMain(player, color, String.join("\n", msg));
    }

    public static void sendToChat(LivingEntity entity, int color, boolean overlay, String... msg) {
        if (!TOTVWConfig.get().CLIENT_ENABLE_NOTIFIERS) return;
        ServerPlayer player = resolveRecipient(entity);
        if (player == null) return;
        sendToMain(player, color, overlay, String.join("\n", msg));
    }
}