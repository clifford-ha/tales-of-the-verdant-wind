package cliffordha.totvw.util;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skill.SkillManager;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor;
import cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWSounds;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.tag.VWBiomeTags;

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
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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
import net.minecraft.world.phys.AABB;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.DARK_GRAY;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.*;

public class VWUtil {
    public static boolean isInBiome(LivingEntity entity, TagKey<Biome> biome) {
        return entity.level().getBiome(entity.blockPosition()).is(biome);
    }
    public static boolean isInBiome(LevelAccessor level, BlockPos pos, TagKey<Biome> biome) {
        return level.getBiome(pos).is(biome);
    }
    public static boolean isDimension(LivingEntity entity, ResourceKey<Level> dimension) {
        return entity.level().dimension().equals(dimension);
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


    public static void sendParticles(ParticleOptions type, ServerLevel level, BlockPos pos, int count, double deviation) {
        for (int i = 0; i < count; i++) {
            level.sendParticles(type, pos.getX() + deviation, pos.getY() + deviation, pos.getZ() + deviation, 1, deviation, deviation, deviation, 0);
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
            mob.drop(item, false, false);
        }
    }
    public static void addToInventory(Player player, Item itemToAdd) {
        ItemStack item = new ItemStack(itemToAdd);
        Inventory inv = player.getInventory();
        int slot = inv.getFreeSlot();

        if (slot > 0) {
            inv.setItem(slot, item);
        } else {
            player.drop(item, false, false);
        }
    }


    public static float triggerHeal(LivingEntity granter, LivingEntity grantee) {
        float triggerHeal;
        if (isInBiome(granter, VWBiomeTags.IS_VERDANT_BIOMES)) {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.5f));
        } else {
            triggerHeal = Math.round((granter.getHealth() * 0.5f) + (grantee.getMaxHealth() * 0.3f));}
        return triggerHeal;
    }
    public static void verdantBlessingAfterEffects(LevelAccessor level, LivingEntity entity) {
        if (!VWConfig.get().SERVER_SKILL_COOLDOWNS) return;
        int minutes = 60;
        int cooldown = setDifficultyBasedValue(level, minutes * 3, minutes * 9, minutes * 15, minutes * 21);
        if (entity.getHealth() >= entity.getMaxHealth() * 0.5f) {
            addHiddenEffect(entity, MobEffects.WEAKNESS, minutes, 0);
        } else {
            addHiddenEffect(entity, MobEffects.WEAKNESS, minutes, 1);
        }
        String name = "[" + entity.getName().getString() + "] ";
        String constructor = name + "Verdant Wind's Blessing is now on cooldown for " + cooldown + " seconds.";
        if (entity instanceof Wolf wolf) {
            SkillManager.startCooldown(wolf, VWWolfBehaviors.VERDANT_BLESSING, cooldown);
            sendToChat(wolf, VWColors.VERDANT_WIND, constructor);
        } else if (entity instanceof Player player) {
            SkillManager.startCooldown(player, VWPlayerBehaviors.VERDANT_BLESSING, cooldown);
            sendToChat(player, VWColors.VERDANT_WIND, constructor);
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


    public static void rewriteEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        if (entity.hasEffect(effect)) {
            entity.removeEffect(effect);
        }
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
    public static void addEffect( LivingEntity entity, Holder<MobEffect> effect, int sec, int amp) {
        entity.addEffect(new MobEffectInstance(effect, sec, amp));
    }
    public static void addOrStackEffect(LivingEntity entity, Holder<MobEffect> effect, int sec, int amp, boolean durationOnly) {
        if (entity.hasEffect(effect)) {
            int currentAmp = entity.getEffect(effect).getAmplifier();
            int currentDuration = entity.getEffect(effect).getDuration();
            int DURATION_STACK;
            if (durationOnly) {
                DURATION_STACK = currentDuration + sec;
                rewriteEffect(entity, effect, DURATION_STACK, currentAmp);
            } else {
                if (amp < currentAmp) {
                    DURATION_STACK = currentDuration + sec;
                    rewriteEffect(entity, effect, DURATION_STACK, currentAmp);
                } else {
                    DURATION_STACK = (int) (currentDuration + Math.ceil(sec * 0.5f));
                    rewriteEffect(entity, effect, DURATION_STACK, currentAmp + 1);
                }
            }
        } else {
            addEffect(entity, effect, sec, amp);
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


    public static AABB scanArea(Entity entity, int range) {
        return entity.getBoundingBox().inflate(range);
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


    private static @Nullable Player resolveRecipient(LivingEntity entity) {
        return switch (entity) {
            case Player player -> player;
            case Wolf wolf when wolf.getOwner() instanceof Player player -> player;
            case null, default -> null;
        };
    }
    public static void sendToChat(LivingEntity entity, boolean overlay, String... msg) {
        Player player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().CLIENT_ENABLE_NOTIFIERS)) return;
        sendToMain(player, overlay, String.join("\n", msg));
    }

    public static void sendToChat(LivingEntity entity, int color, String... msg) {
        Player player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().CLIENT_ENABLE_NOTIFIERS)) return;
        sendToMain(player, color, String.join("\n", msg));
    }

    public static void sendToChat(LivingEntity entity, int color, boolean overlay, String... msg) {
        Player player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().CLIENT_ENABLE_NOTIFIERS)) return;
        sendToMain(player, color, overlay, String.join("\n", msg));
    }


    public static void playNotification(Player player) {
        if (!player.getAttachedOrElse(PlayerPrefs.ENABLE_NOTIFIERS, VWConfig.get().CLIENT_ENABLE_NOTIFIERS)) return;
        player.level().playSound(null, player.blockPosition(), VWSounds.NOTIFY, SoundSource.PLAYERS);
    }
    public static boolean isNotValidForTP(Level level, BlockPos pos) {
        if (level.getBlockState(pos.below()).isAir()) return true;
        if (level.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) return true;
        return level.getBlockState(pos).isSuffocating(level, pos)
                && level.getBlockState(pos.below()).isAir();
    }

    public static class TextUtil {
        // TEXT FORMATTING UTIL

        /** colors text **/
        public static String cText(ScatteredPageTextColor color, String text) {
            if (color == null) return text;
            return color.getColor() + text + "§r";
        }

        /** a test-dependent text value
         * note: be careful when using ServerLevel tests **/
        public static String tText(boolean test, String isTrue, String isFalse) {
            return test ? isTrue : isFalse;
        }
        public static String tText(boolean test, String isTrue) {
            return test ? isTrue : "";
        }

        /** like a docx, format text **/
        public static String fText(ScatteredPageTextStyle formatter, String text) {
            return formatter.getMarker() + text + "§r";
        }

        /** date, what else **/
        public static String dText(int day, int month, int year) {
            String cDay = day < 10 ? "0" + day : String.valueOf(day);
            String cMonth = month < 10 ? "0" + month : String.valueOf(month);
            return cText(DARK_GRAY, fText(ITALIC, cDay + "/" + cMonth + "/" + year)) + nextLine;
        }

        /** convert and iterate every letter from the input text and turn it into a block **/
        public static String bText(String text) {
            return "▌".repeat(text.length());
        }

        /** a set of predefined text **/
        public static String pText(int p) {
            String predefinedText;
            switch (p) {
                case 1 -> predefinedText = "Some contents are intentionally omitted";
                case 2 -> predefinedText = "The text trails and ends here...";
                case 3 -> predefinedText = "Scribbled gibberish";
                case 4 -> predefinedText = "Some contents have faded";
                default -> predefinedText = "Error: Invalid Predefined Text or Null";
            }
            return cText(DARK_GRAY, "[" + predefinedText + "]") + nextParagraph;
        }
        public static String nText(String text) {
            return cText(DARK_GRAY, fText(ITALIC, "[" + text + "]"));
        }

        public static final String nextLine = " §f§f§f§r\n";

        /** why... **/
        public static final String nextParagraph = " \n §f§f§f§r \n";

        public static final String addSeparator = nextParagraph + nextParagraph;

        public static String addTitle(String title) {
            return fText(BOLD, title);
        }

        /** purely made for separating *pages visually, rip brain **/
        public static String[] addPage(String text) {
            final int lengthBound = 700;
            int charCount = text.length();
            List<String> pages = new ArrayList<>();
            int start = 0;

            while (start < charCount) {
                while (start < charCount && Character.isWhitespace(text.charAt(start))) {
                    start++;
                }

                if (start >= charCount) {
                    break;
                }

                int end = Math.min(start + lengthBound, charCount);

                if (end < charCount) {
                    int split = end;

                    while (split > start && !Character.isWhitespace(text.charAt(split - 1))) {
                        split--;
                    }

                    if (split > start) {
                        end = split;
                    }
                }

                pages.add(text.substring(start, end).trim());
                start = end;
            }

            return pages.toArray(new String[0]);
        }
    }

    public static class TimeUtil {
        public static int sec(int sec) {
            return sec * 20;
        }
        public static int min(int min) {
            return min * sec(60);
        }
        public static int duration(int min, int sec) {
            return min(min) + sec(sec);
        }
    }
}