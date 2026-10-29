package cliffordha.totvw.util;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.Prediction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

public class ExtUtil {
    public static boolean isInBiome(LivingEntity entity, TagKey<Biome> biome) {
        return entity.level().getBiome(entity.blockPosition()).is(biome);
    }
    public static boolean isInBiome(LevelAccessor level, BlockPos pos, TagKey<Biome> biome) {
        return level.getBiome(pos).is(biome);
    }
    public static boolean isDimension(LivingEntity entity, ResourceKey<Level> dimension) {
        return entity.level().dimension().equals(dimension);
    }
    public static boolean isDimension(ServerLevel level, ResourceKey<Level> dimension) {
        return level.dimension().equals(dimension);
    }

    public static boolean isSolidBelow(BlockPos pos, LevelAccessor level) {
        return !level.getBlockState(pos).isAir();
    }
    public static boolean isBlockState(LevelAccessor level, BlockPos pos, Block block) {
        return level.getBlockState(pos).is(block);
    }

    public static String getEntityID(Entity entity) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString();
    }
    public static boolean isEqualEntityID(String test, Entity type) {
        return getEntityID(type).equals(test);
    }

    /** note: generic **/
    public static RandomSource random = RandomSource.create();

    public static boolean isDifficulty(LevelAccessor level, Difficulty difficulty) {
        return level.getDifficulty() == difficulty;
    }

    public static void sendParticles(ParticleOptions type, LevelAccessor level, BlockPos pos, int count, double deviation) {
        for (int i = 0; i < count; i++) {
            if (level instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(type, pos.getX(), pos.getY(), pos.getZ(), 1, deviation, deviation, deviation, 0);
            } else {
                level.addParticle(type, pos.getX(), pos.getY(), pos.getZ(), deviation, deviation, deviation);
            }
        }
    }
    public static void sendParticles(ParticleOptions type, ServerLevel level, BlockPos pos, int count, double deviation, float yDist) {
        for (int i = 0; i < count; i++) {
            level.sendParticles(type, pos.getX(), pos.getY(), pos.getZ(), 1, deviation, yDist, deviation, 0);
        }
    }
    public static void addToInventory(Player player, Item itemToAdd, LivingEntity dropFrom) {
        ItemStack item = new ItemStack(itemToAdd);
        Inventory inv = player.getInventory();
        int slot = inv.getFreeSlot();

        if (slot > 0) {
            inv.setItem(slot, item);
        } else {
            LivingEntity mob = dropFrom != null ? dropFrom : player;
            mob.drop(item, false, Prediction.SERVER_ONLY);
        }
    }
    public static void addToInventory(Player player, Item itemToAdd) {
        ItemStack item = new ItemStack(itemToAdd);
        Inventory inv = player.getInventory();
        int slot = inv.getFreeSlot();

        if (slot > 0) {
            inv.setItem(slot, item);
        } else {
            player.drop(item, false, Prediction.SERVER_ONLY);
        }
    }
    public static void consumeItem(Player player, ItemStack itemStack) {
        if (player.isCreative()) return;
        itemStack.shrink(1);
    }
    public static void consumeItem(Player player, ItemStack itemStack, int amount) {
        if (player.isCreative()) return;
        itemStack.shrink(amount);
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
    public static int cooldownFromBase(LevelAccessor level, int base, float... reductions) {
        int finalCD;
        switch (level.getDifficulty()) {
            case PEACEFUL -> finalCD = Mth.ceil(base * 0.7f);
            case EASY -> finalCD = Mth.ceil(base * 0.85);
            case NORMAL -> finalCD = base;
            default -> finalCD = Mth.ceil(base * 1.3f);
        }
        if (reductions.length == 0) {
            return finalCD;
        }
        return Mth.ceil(finalCD * reductions[0]);
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

    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource source, boolean local) {
        if (local) {
            if (!(entity.level() instanceof Level level && level.isClientSide())) return;
            level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), sound, source, 0.3f, 0.5f, false);
        } else {
            if (!(entity.level() instanceof ServerLevel level)) return;
            BlockPos pos = entity.blockPosition();
            var random = level.getRandom().nextFloat();
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, source, 0.3f + random, 0.5f + random);
        }
    }
    public static void playSound(LivingEntity entity, SoundEvent sound, SoundSource source, float volume, float pitch, boolean local) {
        if (local) {
            if (!(entity.level() instanceof Level level && level.isClientSide())) return;
            level.playLocalSound(entity.getX(), entity.getY(), entity.getZ(), sound, source, volume, pitch, false);
        } else {
            if (!(entity.level() instanceof ServerLevel level)) return;
            BlockPos pos = entity.blockPosition();
            level.playSound(null, pos.getX(), pos.getY(), pos.getZ(), sound, source, volume, pitch);
        }
    }

    public static void addEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        entity.addEffect(new MobEffectInstance(effect, TimeUtil.tickSeconds(sec), amp));
    }
    public static void rewriteEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        entity.removeEffect(effect);
        addEffect(entity, effect, sec, amp);
    }
    public static void replaceEffect(LivingEntity entity, Holder<MobEffect> effect, int sec) {
        int remaining = entity.hasEffect(effect) ? entity.getEffect(effect).getDuration() : 0;
        if (remaining <= sec) {
            entity.removeEffect(effect);
        } else {
            entity.removeEffect(effect);
            addEffect(entity, effect, sec, 0);
        }
    }
    public static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int maxSec, int amp, boolean durationOnly) {
        if (durationOnly) {
            addOrStackEffect(entity, effect, sec, maxSec, amp, amp, true);
        } else {
            addOrStackEffect(entity, effect, sec, amp);
        }
    }
    public static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int maxSec, int amp, int maxAmp, boolean durationOnly) {
        MobEffectInstance existing = entity.getEffect(effect);
        if (existing != null) {
            int currentAmp = existing.getAmplifier();
            int currentSec = Mth.ceil(existing.getDuration() / 20.0f);
            int durationStack;
            if (durationOnly) {
                durationStack = Math.min(currentSec + sec, maxSec);
                rewriteEffect(entity, effect, durationStack, currentAmp);
            } else if (amp < currentAmp) {
                durationStack = Math.min(currentSec + sec, maxSec);
                rewriteEffect(entity, effect, durationStack, Math.min(currentAmp, maxAmp));
            } else {
                durationStack = Math.min(currentSec + Mth.ceil(sec * 0.5f), maxSec);
                rewriteEffect(entity, effect, durationStack, Math.min(currentAmp + 1, maxAmp));
            }
        } else {
            addEffect(entity, effect, sec, amp);
        }
    }
    public static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        addOrStackEffect(entity, effect, sec, 60 * 5, amp, 4, false);
    }
    public static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp, boolean durationOnly) {
        addOrStackEffect(entity, effect, sec, 60 * 5, amp, durationOnly ? amp : 4, durationOnly);
    }
    public static void addHiddenEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) return;
        entity.addEffect(new MobEffectInstance(effect, TimeUtil.tickSeconds(sec), amp, false, false));
    }
    public static void removeEffect(LivingEntity entity, Holder<MobEffect> effect) {
        if (entity.hasEffect(effect)) {
            entity.removeEffect(effect);
        }
    }

    public static AABB scanArea(Entity entity, int range) {
        return entity.getBoundingBox().inflate(range);
    }

    private static @Nullable Player resolveRecipient(LivingEntity entity) {
        return switch (entity) {
            case Player player -> player;
            case Wolf wolf when wolf.getOwner() instanceof Player player -> player;
            case null, default -> null;
        };
    }
    private static void sendToMain(Player player, boolean overlay, String msg) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(msg), overlay);
        } else {
            if (overlay) {
                player.sendOverlayMessage(Component.literal(msg));
            } else {
                player.sendSystemMessage(Component.literal(msg));
            }
        }
    }
    private static void sendToMain(Player player, int color, String msg) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(msg).withColor(color), false);
        } else {
            player.sendSystemMessage(Component.literal(msg).withColor(color));
        }
    }
    private static void sendToMain(Player player, int color, boolean overlay, String msg) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.sendSystemMessage(Component.literal(msg).withColor(color), overlay);
        } else {
            if (overlay) {
                player.sendOverlayMessage(Component.literal(msg).withColor(color));
            } else {
                player.sendSystemMessage(Component.literal(msg).withColor(color));
            }
        }
    }

    public static void sendToChat(LivingEntity entity, boolean overlay, String... msg) {
        Player player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().SYNCED_ENABLE_NOTIFIERS)) return;
        sendToMain(player, overlay, String.join("\n", msg));
    }
    public static void sendToChat(LivingEntity entity, int color, String... msg) {
        Player player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().SYNCED_ENABLE_NOTIFIERS)) return;
        sendToMain(player, color, String.join("\n", msg));
    }
    public static void sendToChat(LivingEntity entity, int color, boolean overlay, String... msg) {
        Player player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().SYNCED_ENABLE_NOTIFIERS)) return;
        sendToMain(player, color, overlay, String.join("\n", msg));
    }

    public static void playNotification(Player player) {
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().SYNCED_ENABLE_NOTIFIERS)) return;
        player.level().playSound(null, player.blockPosition(), VWSounds.NOTIFY, SoundSource.PLAYERS);
    }
    private static boolean isWater(BlockState state) {
        return state.getFluidState().is(FluidTags.WATER);
    }
}