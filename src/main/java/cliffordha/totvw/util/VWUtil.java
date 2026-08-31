package cliffordha.totvw.util;

import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.skill.SkillUtil;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor;
import cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle;
import cliffordha.totvw.registry.VWAttachments;
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
import net.minecraft.world.Difficulty;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.DARK_GRAY;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.BOLD;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.ITALIC;

public class VWUtil {
    public static int sec(int sec) {
        return sec * 20;
    }
    public static int min(int min) {
        return min * sec(60);
    }
    public static int duration(int min, int sec) {
        return min(min) + sec(sec);
    }

    public static boolean isInBiome(LivingEntity entity, TagKey<Biome> biome) {
        return entity.level().getBiome(entity.blockPosition()).is(biome);
    }
    public static boolean isInBiome(LevelAccessor level, BlockPos pos, TagKey<Biome> biome) {
        return level.getBiome(pos).is(biome);
    }

    public static boolean isDifficulty(LivingEntity entity, Difficulty difficulty) {
        return entity.level().getDifficulty() == difficulty;
    }
    public static boolean isDifficulty(LevelAccessor level, Difficulty difficulty) {
        return level.getDifficulty() == difficulty;
    }
    public static boolean isHardcore(LivingEntity entity) {
        return entity.level().getLevelData().isHardcore();
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
        if (isHalfHealth(entity)) {
            addHiddenEffect(entity, MobEffects.WEAKNESS, minutes, 0);
        } else {
            addHiddenEffect(entity, MobEffects.WEAKNESS, minutes, 1);
        }
        String name = "[" + entity.getName().getString() + "] ";
        String constructor = name + "Verdant Wind's Blessing is now on cooldown for " + cooldown + " seconds.";
        if (entity instanceof Wolf wolf) {
            SkillUtil.startCooldown(wolf, VWWolfBehaviors.VERDANT_BLESSING, cooldown);
            sendToChat(wolf, VWColors.VERDANT_WIND, constructor);
        } else if (entity instanceof Player player) {
            SkillUtil.startCooldown(player, VWPlayerBehaviors.VERDANT_BLESSING, cooldown);
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
        if (wolf == null) return 0;
        ItemStack armor = wolf.getItemBySlot(EquipmentSlot.BODY);
        if (armor.isEmpty()) return 0;
        return armor.getEnchantments()
                .getLevel(wolf.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
    }

    public static int entityEnchantmentLVL(LivingEntity player, EquipmentSlot slot, ResourceKey<Enchantment> enchantment) {
        if (player == null) return 0;
        ItemStack itemStack = player.getItemBySlot(slot);
        if (itemStack.isEmpty()) return 0;
        return itemStack.getEnchantments()
                .getLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
    }
    public static int entityEnchantmentLVL(LivingEntity player, ResourceKey<Enchantment> enchantment) {
        if (player == null) return 0;
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack body = player.getItemBySlot(EquipmentSlot.BODY);
        int helmetLVL = 0;
        int chestLVL = 0;
        int legsLVL = 0;
        int feetLVL = 0;
        int bodyLVL = 0;

        if (!helmet.isEmpty()) helmetLVL = getEnchantment(helmet, player, enchantment);
        if (!chest.isEmpty()) chestLVL = getEnchantment(chest, player, enchantment);
        if (!legs.isEmpty()) legsLVL = getEnchantment(legs, player, enchantment);
        if (!feet.isEmpty()) feetLVL = getEnchantment(feet, player, enchantment);
        if (!body.isEmpty()) bodyLVL = getEnchantment(body, player, enchantment);

        return helmetLVL + chestLVL + legsLVL + feetLVL + bodyLVL;
    }
    private static int getEnchantment(ItemStack stack, LivingEntity player, ResourceKey<Enchantment> enchantment) {
        return stack.getEnchantments().getLevel(player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantment));
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
        ServerPlayer player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(VWAttachments.Player.PLAYER_ENABLE_NOTIFIERS, false)) return;
        sendToMain(player, overlay, String.join("\n", msg));
    }

    public static void sendToChat(LivingEntity entity, int color, String... msg) {
        ServerPlayer player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(VWAttachments.Player.PLAYER_ENABLE_NOTIFIERS, false)) return;
        sendToMain(player, color, String.join("\n", msg));
    }

    public static void sendToChat(LivingEntity entity, int color, boolean overlay, String... msg) {
        ServerPlayer player = resolveRecipient(entity);
        if (player == null) return;
        if (!player.getAttachedOrElse(VWAttachments.Player.PLAYER_ENABLE_NOTIFIERS, false)) return;
        sendToMain(player, color, overlay, String.join("\n", msg));
    }

    public static class TextUtil {
        // TEXT FORMATTING UTIL

        /** colors text **/
        public static String cText(ScatteredPageTextColor color, String text) {
            return color.getColor() + text + "§r";
        }

        /** a test-dependent text value
         * note: be careful when using ServerLevel tests **/
        public static String tText(boolean test, String isTrue, String isFalse) {
            return test ? isTrue : isFalse;
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
}