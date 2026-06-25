package cliffordha.totvw.item.events;

import cliffordha.totvw.registry.ModEffects;
import cliffordha.totvw.registry.ModItems;
import cliffordha.totvw.registry.ModParticleEffects;
import cliffordha.totvw.tag.ModBiomeTags;
import cliffordha.totvw.tag.ModItemTags;
import cliffordha.totvw.registry.ModColors;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.function.Predicate;

import static cliffordha.totvw.entity.TConstants.*;
import static cliffordha.totvw.entity.skill.ConfigTools.notifyFromPlayer;
import static cliffordha.totvw.entity.skill.ConfigTools.playSound;

public final class BlessingOfTheVerdantWind {
    private static final int TICK_SECONDS = 20;
    private static final int TICK_MINUTES = TICK_SECONDS * 60;
    private static int setDuration(int min, int sec) {return ((TICK_MINUTES * min) + (TICK_SECONDS * sec));}

    private static boolean isItem(Player player, TagKey<Item> tag) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        return mainHandItem.tags().anyMatch(Predicate.isEqual(tag));
    }
    public static boolean tryApply(Level level, Player player) {
        boolean hasSword = isItem(player, ItemTags.SWORDS);
        boolean hasAxe = isItem(player, ItemTags.AXES);
        boolean hasPickaxe = isItem(player, ItemTags.PICKAXES);
        boolean hasHoe = isItem(player, ItemTags.HOES);
        boolean hasShovel = isItem(player, ItemTags.SHOVELS);
        boolean hasOthers = isItem(player, ModItemTags.BENEDICTION_ENCHANTMENT_USE_QUALIFIED_ITEMS);
        
        if (hasSword) { hasSword(player);
        } else if (hasAxe) { hasAxe(player);
        } else if (hasPickaxe) { hasPickaxe(player);
        } else if (hasHoe) { hasHoe(player);
        } else if (hasShovel) { hasShovel(player);
        } else if (hasOthers) { hasOther(player);
        } else return false;
        return true;
    }
    private static void setCooldown(Player player, int cdBiome, int cd) {
        player.getMainHandItem().hurtWithoutBreaking(3, player);
        if (!player.isCreative()) {
            int cooldown = player.level().getBiome(player.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES) ? (cdBiome) : (cd);
            player.getCooldowns().addCooldown(player.getItemBySlot(EquipmentSlot.MAINHAND), cooldown);
        }
    }
    private static void grantEffect(Player player, Holder<MobEffect> effect, int inBiome, int enhancedAmp, TagKey<Biome> biomeTag) {
        int notInBiome = (int) Math.floor(inBiome * 0.7);
        int basicAmp = enhancedAmp - 1;

        if (player.level().getBiome(player.blockPosition()).is(biomeTag)) {
            player.addEffect(new MobEffectInstance(effect, inBiome, enhancedAmp));
        } else {player.addEffect(new MobEffectInstance(effect, notInBiome, basicAmp));}
        playSound(player, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT);
    }
    private static void grantEffectTierBased(Player player, boolean tier0, boolean tier1, boolean tier2, Holder<MobEffect> effect, int inBiome, int enhancedAmp, int cdBiome, int cd) {
        int basicAmp = enhancedAmp - 1;
        boolean withinBiome = player.level().getBiome(player.blockPosition()).is(ModBiomeTags.IS_VERDANT_BIOMES);

        int inBiomeDuration;
        int outBiomeDuration;

        if (tier0) {
            inBiomeDuration = inBiome;
            outBiomeDuration = (int) Math.floor(inBiome * 0.7);
        } else if (tier1) {
            inBiomeDuration = (int) Math.floor(inBiome * 1.3);
            outBiomeDuration = (int) Math.floor(inBiome * 0.7 * 1.3);
        } else if (tier2) {
            inBiomeDuration = (int) Math.floor(inBiome * 1.7);
            outBiomeDuration = (int) Math.floor(inBiome * 0.7 * 1.7);
        } else {
            inBiomeDuration = (int) Math.floor(inBiome * 1.5);
            outBiomeDuration = (int) Math.floor(inBiome * 0.7 * 1.5);
        }

        int targetDuration = withinBiome ? inBiomeDuration : outBiomeDuration;
        int targetAmp = withinBiome ? enhancedAmp : basicAmp;

        int targetDurationSec = targetDuration / 20;
        if (!player.hasEffect(ModEffects.BLESSING_OF_THE_VERDANT_WIND)) {
            grantEffect(player, ModEffects.BLESSING_OF_THE_VERDANT_WIND, sec(45), 1, ModBiomeTags.IS_VERDANT_BIOMES);
        }

        if (player.hasEffect(effect)) {
            MobEffectInstance existingDuration = player.getEffect(effect);
            if (existingDuration != null && existingDuration.getDuration() >= targetDuration) {
                if (withinBiome) {
                    notifyFromPlayer(player, ModColors.VERDANT_WIND_MUTED,
                            "Cannot grant enhanced blessing: Player already has",
                            "the same attribute with higher duration (§e" + (existingDuration.getDuration() / 20) + "§r vs " + targetDurationSec + ")");
                } else {
                    notifyFromPlayer(player, ModColors.VERDANT_WIND_MUTED,
                            "Cannot grant blessing: Player already has",
                            "the same attribute with higher duration (§e" + (existingDuration.getDuration() / 20) + "§r vs " + targetDurationSec + ")");
                }return;
            } else {player.removeEffect(effect);}
        }
        player.addEffect(new MobEffectInstance(effect, targetDuration, targetAmp));
        if (withinBiome) {
            notifyFromPlayer(player, ModColors.VERDANT_WIND, "Granted: §n" + effect.getRegisteredName().replaceAll("^[^:]*:", "").toUpperCase() + "§r (§eEnhanced§r) for §e" + targetDurationSec + "§r sec");
        } else {
            notifyFromPlayer(player, ModColors.VERDANT_WIND, "Granted: §n" + effect.getRegisteredName().replaceAll("^[^:]*:", "").toUpperCase() + "§r for §e" + targetDurationSec + "§r sec");}
        ModParticleEffects.spawnBlessingParticlesEntity(player, 2);
        playSound(player, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.AMBIENT);

        player.getMainHandItem().hurtWithoutBreaking(3, player);
        setCooldown(player, cdBiome, cd);
    }
    
    private static void hasSword(Player player) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean swordTier0 = mainHandItem.is(Items.WOODEN_SWORD)
                || mainHandItem.is(Items.STONE_SWORD)
                || mainHandItem.is(Items.COPPER_SWORD);

        boolean swordTier1 = mainHandItem.is(Items.IRON_SWORD)
                || mainHandItem.is(Items.GOLDEN_SWORD);

        boolean swordTier2 = mainHandItem.is(Items.DIAMOND_SWORD)
                || mainHandItem.is(ModItems.VERIXIUM_SWORD)
                || mainHandItem.is(Items.NETHERITE_SWORD);

        grantEffectTierBased(player,
                swordTier0, swordTier1, swordTier2,
                MobEffects.STRENGTH,
                setDuration(1, 0),
                1,
                setDuration(0, 45),
                setDuration(1, 0)
        );
    }
    private static void hasAxe(Player player) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean axeTier0 = mainHandItem.is(Items.WOODEN_AXE)
                || mainHandItem.is(Items.STONE_AXE)
                || mainHandItem.is(Items.COPPER_AXE);

        boolean axeTier1 = mainHandItem.is(Items.IRON_AXE)
                || mainHandItem.is(Items.GOLDEN_AXE);

        boolean axeTier2 = mainHandItem.is(Items.DIAMOND_AXE)
                || mainHandItem.is(ModItems.VERIXIUM_AXE);

        grantEffectTierBased(player,
                axeTier0,
                axeTier1,
                axeTier2,
                MobEffects.STRENGTH,
                setDuration(0, 45),
                2,
                setDuration(1, 0),
                setDuration(1, 30)
        );
    }
    private static void hasPickaxe(Player player) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean pickaxeTier0 = mainHandItem.is(Items.WOODEN_PICKAXE)
                || mainHandItem.is(Items.STONE_PICKAXE)
                || mainHandItem.is(Items.COPPER_PICKAXE);

        boolean pickaxeTier1 = mainHandItem.is(Items.IRON_PICKAXE)
                || mainHandItem.is(Items.GOLDEN_PICKAXE);

        boolean pickaxeTier2 = mainHandItem.is(Items.DIAMOND_PICKAXE)
                || mainHandItem.is(ModItems.VERIXIUM_PICKAXE)
                || mainHandItem.is(Items.NETHERITE_PICKAXE);

        grantEffectTierBased(player,
                pickaxeTier0,
                pickaxeTier1,
                pickaxeTier2,
                MobEffects.HASTE,
                setDuration(1, 45),
                1,
                setDuration(1, 45),
                setDuration(2, 15)
        );
    }
    private static void hasHoe(Player player) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean hoeTier0 = mainHandItem.is(Items.WOODEN_HOE)
                || mainHandItem.is(Items.STONE_HOE)
                || mainHandItem.is(Items.COPPER_HOE);

        boolean hoeTier1 = mainHandItem.is(Items.IRON_HOE)
                || mainHandItem.is(Items.GOLDEN_HOE);

        boolean hoeTier2 = mainHandItem.is(Items.DIAMOND_HOE)
                || mainHandItem.is(ModItems.VERIXIUM_HOE)
                || mainHandItem.is(Items.NETHERITE_HOE);

        grantEffectTierBased(player,
                hoeTier0,
                hoeTier1,
                hoeTier2,
                MobEffects.ABSORPTION,
                setDuration(1, 0),
                2,
                setDuration(1, 0),
                setDuration(1, 30)
        );
    }
    private static void hasShovel(Player player) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        boolean shovelTier0 = mainHandItem.is(Items.WOODEN_SHOVEL)
                || mainHandItem.is(Items.STONE_SHOVEL)
                || mainHandItem.is(Items.COPPER_SHOVEL);

        boolean shovelTier1 = mainHandItem.is(Items.IRON_SHOVEL)
                || mainHandItem.is(Items.GOLDEN_SHOVEL);

        boolean shovelTier2 = mainHandItem.is(Items.DIAMOND_SHOVEL)
                || mainHandItem.is(ModItems.VERIXIUM_SHOVEL)
                || mainHandItem.is(Items.NETHERITE_SHOVEL);

        grantEffectTierBased(player,
                shovelTier0,
                shovelTier1,
                shovelTier2,
                MobEffects.SPEED,
                setDuration(0, 45),
                1,
                setDuration(1, 0),
                setDuration(1, 45)
        );
    }
    private static void hasOther(Player player) {
        ItemStack mainHandItem = player.getItemBySlot(EquipmentSlot.MAINHAND);
        
        if (mainHandItem.is(ModItems.VERIXIUM_POWDER)) {
            grantEffect(player,
                    MobEffects.SLOW_FALLING,
                    setDuration(0, 6),
                    1,
                    ModBiomeTags.IS_VERDANT_BIOMES);
            player.getMainHandItem().consume(1, player);
            if (!player.isCreative()) {setCooldown(player, setDuration(1, 15), setDuration(2, 0)); }

        } else if (mainHandItem.is(Items.GLOWSTONE_DUST)) {
            grantEffect(player,
                    MobEffects.NIGHT_VISION,
                    TICK_MINUTES,
                    1,
                    BiomeTags.IS_NETHER);
            player.getMainHandItem().consume(1, player);
            if (!player.isCreative()) {player.getCooldowns().addCooldown(player.getItemBySlot(EquipmentSlot.MAINHAND), setDuration(1, 30)); }
        }
    }
}