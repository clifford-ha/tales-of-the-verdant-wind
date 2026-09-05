package cliffordha.totvw.entity;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.entity.player.VWPlayerBehaviors;
import cliffordha.totvw.entity.wolf.VWWolfBehaviors;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.VWPlayerPrefs;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.TOTVW.sendClassRegisterLog;
import static cliffordha.totvw.util.VWUtil.*;

public class VWGlobalEntityBehaviors {
    public static void register() {
        onDamageOrDeathEvent();
        configSync();

        if (TOTVW.IN_DEVELOPMENT) {
            developmentTick();
        }

        VWPlayerBehaviors.registerModPlayerBehaviors();
        VWWolfBehaviors.registerModWolfBehaviors();
        sendClassRegisterLog("Custom Entity Behaviors");
    }

    private static void configSync() {
    }


    private static void developmentTick() {
        ServerTickEvents.END_SERVER_TICK.register((MinecraftServer server) -> {
            for (var serverLevel : server.getAllLevels()) {
                serverLevel.getEntities(EntityType.PLAYER, _ -> true).forEach(player -> {
                    if (!player.entityTags().contains(player.getStringUUID() + "-reminderStamp")) {
                        sendToChat(player, VWColors.VERDANT_WIND, false, "TOTVW mod version is a development build.");
                        player.entityTags().add(player.getStringUUID() + "-reminderStamp");
                    }
                    if (!player.getAttachedOrElse(VWAttachments.player.PLAYER_IS_DEV_MODE, false)) {
                        player.setAttached(VWAttachments.player.PLAYER_IS_DEV_MODE, true);
                    }
                });
            }
        });
    }

    private static void onDamageOrDeathEvent() {
        ServerLivingEntityEvents.AFTER_DEATH.register(
                (victim, damageSource) -> atrocityProcessor(victim, damageSource, true)
        );
        ServerLivingEntityEvents.AFTER_DAMAGE.register(
                (victim, damageSource, _, _, _) -> atrocityProcessor(victim, damageSource, false)
        );
        ServerLivingEntityEvents.ALLOW_DEATH.register(
                VWGlobalEntityBehaviors::revivePlayerIfPossible
        );
    }

    /**
     * Revives the player when a tamed wolf within
     * the user's specified distance returns a false value.
     */
    private static boolean revivePlayerIfPossible(LivingEntity entity, DamageSource damageSource, float v) {
        if (entity instanceof Player player) {
            if (damageSource.is(DamageTypes.GENERIC_KILL)) return true;
            if (!player.getAttachedOrElse(VWPlayerPrefs.BENEDICTION_SHARE_STACK, VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK)) return true;

            Level getLevel = player.level();
            ServerLevel level = (ServerLevel) getLevel;

            double distance = VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;
            AttachmentType<Integer> BENEDICTION_STACK = VWAttachments.wolf.WOLF_BENEDICTION;

            List<Wolf> wolves = level.getEntities(EntityType.WOLF, player.getBoundingBox().inflate(distance), wolf ->
                    wolf.getOwner() != null && wolf.getOwner().is(player));

            if (!wolves.isEmpty()) {
                List<Wolf> wolfWithStack = wolves.stream().filter(wolf -> wolf.getAttachedOrElse(BENEDICTION_STACK, 0) > 1).toList();
                if (!wolfWithStack.isEmpty()) {
                    Wolf mainWolf = wolfWithStack.getFirst();
                    getTeleportToWolf(player, mainWolf, false, mainWolf);

                    if (player.getAttachedOrElse(VWPlayerPrefs.BENEDICTION_WOLF_TP_ALL, VWConfig.get().SERVER_WOLF_TP_ALL)) {
                        for (Wolf wolf : wolves) {
                            getTeleportToWolf(player, wolf, true, mainWolf);
                        }
                        for (Wolf otherMainWolf : wolfWithStack) {
                            getTeleportToWolf(player, otherMainWolf, true, mainWolf);
                        }
                    }

                    applyBenedictionBlessings(player);

                    int benediction = mainWolf.getAttachedOrElse(BENEDICTION_STACK, 0);
                    mainWolf.setAttached(BENEDICTION_STACK, benediction - 1);

                    String name = mainWolf.getName().getString();
                    int STACK_AFTER = mainWolf.getAttachedOrElse(BENEDICTION_STACK, 0);
                    if (STACK_AFTER == 0) {
                        sendToChat(mainWolf, VWColors.BLOODLUST_EFFECT_MUTED, name + " used up all Benediction stacks");
                    } else {
                        sendToChat(mainWolf, VWColors.VERDANT_WIND_MUTED, "A Benediction stack has been shared by " + name + ". " + STACK_AFTER + " remaining.");
                    }

                    mainWolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
                    level.broadcastEntityEvent(player, (byte) 35);
                    return false;
                }
            }

            // Last check to revive player if no wolves with stack found
            return checkWolfSouls(player, level);
        }
        return true;
    }

    private static boolean checkWolfSouls(Player player, ServerLevel level) {
        if (!player.getInventory().contains(new ItemStack(VWItems.SOUL_RUNESTONE_PLATE))) return true;
        if (player.getAttachedOrElse(VWAttachments.player.PLAYER_WOLF_SOULS_COUNTER, 0) < 1) return true;
        List<CompoundTag> souls = player.getAttachedOrElse(VWAttachments.player.PLAYER_WOLF_SOULS, List.of());
        CompoundTag stack = souls.stream().filter(soul ->
                soul.getCompoundOrEmpty("fabric:attachments")
                        .getIntOr(TOTVW.MOD_ID + ":wolf_benediction", 0) > 1).toList().getFirst();

        if (!souls.isEmpty() && wolfHasBenedictionEnchantment(stack)) {
            processRevivalThroughRunestone(level, player, souls, stack);
            return false;
        }
        return true;
    }
    private static void getTeleportToWolf(Player player, Wolf wolf, boolean tpAll, Wolf mainWolf) {
        if (!player.canTeleport(player.level(), wolf.level())) return;
        if (!player.getAttachedOrElse(VWPlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, VWConfig.get().SERVER_TELEPORT_AFTER_SAVE)) return;
        if (player.distanceTo(wolf) < 16) return;

        BlockPos wolfPos = wolf.blockPosition();
        BlockPos playerPos = player.blockPosition();
        boolean tpMode = player.getAttachedOrElse(VWPlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, VWConfig.get().SERVER_PLAYER_TP_METHOD) < 1;

        if (tpAll) {
            BlockPos mainWolfPos = mainWolf.blockPosition();
            if (tpMode) {
                if (isNotValidForTP(mainWolf.level(), mainWolfPos)) return;
                player.teleportTo(mainWolfPos.getX(), mainWolfPos.getY(), mainWolfPos.getZ());
                wolf.teleportToAroundBlockPos(mainWolfPos);
            } else {
                if (isNotValidForTP(player.level(), playerPos)) return;
                wolf.teleportToAroundBlockPos(playerPos);
                mainWolf.teleportToAroundBlockPos(mainWolfPos);
            }
            untetherWolf(mainWolf);
        } else {
            if (tpMode) {
                if (isNotValidForTP(wolf.level(), wolfPos)) return;
                player.teleportTo(wolfPos.getX(), wolfPos.getY(), wolfPos.getZ());
            } else {
                if (isNotValidForTP(player.level(), playerPos)) return;
                wolf.teleportToAroundBlockPos(playerPos);
            }
        }
        untetherWolf(wolf);
    }
    public static boolean isNotValidForTP(Level level, BlockPos pos) {
        if (level.getBlockState(pos.below()).isAir()) return true;
        if (level.getBlockState(pos).getFluidState().is(FluidTags.LAVA)) return true;
        return level.getBlockState(pos).isSuffocating(level, pos)
                && level.getBlockState(pos.below()).isAir();
    }
    private static void untetherWolf(Wolf wolf) {
        wolf.unRide();
        wolf.dropLeash();
        wolf.setOrderedToSit(false);
    }
    private static boolean wolfHasBenedictionEnchantment(CompoundTag stack) {
        if (stack.isEmpty()) return false;

        return stack.getCompoundOrEmpty("equipment")
                .getCompoundOrEmpty("body")
                .getCompoundOrEmpty("components")
                .getCompoundOrEmpty("minecraft:enchantments")
                .getIntOr(TOTVW.MOD_ID + ":benediction_of_the_verdant_mountains", 0) > 0;
    }
    private static void processRevivalThroughRunestone(ServerLevel level, Player player, List<CompoundTag> souls, CompoundTag stack) {
        CompoundTag attachments = stack.getCompoundOrEmpty("fabric:attachments").copy();
        int count = attachments.getIntOr(TOTVW.MOD_ID + ":wolf_benediction", 0);
        RandomSource random = level.getRandom();
        Inventory inv = player.getInventory();

        attachments.remove(TOTVW.MOD_ID + ":wolf_benediction");
        attachments.putInt(TOTVW.MOD_ID + ":wolf_benediction", count - 1);
        stack.put("fabric:attachments", attachments);

        souls.remove(stack);
        souls.add(stack);
        player.setAttached(VWAttachments.player.PLAYER_WOLF_SOULS, souls);

        float chance = 0.05f + ((souls.size() - 3) * 0.05f);
        if (souls.size() > 3 && random.nextFloat() < 0.33f + chance) {
            SoulRunestonePlate.processAndSummonSouls(player, level, souls);

            int slot = inv.findSlotMatchingItem(new ItemStack(VWItems.SOUL_RUNESTONE_PLATE));
            inv.removeItem(slot, 1);

            int randomAmount = random.nextIntBetweenInclusive(1, 4);
            List<ItemStack> fragments = new ArrayList<>(List.of(
                    new ItemStack(VWItems.SOUL_RUNESTONE_FRAGMENT_3),
                    new ItemStack(VWItems.SOUL_RUNESTONE_FRAGMENT_1),
                    new ItemStack(VWItems.SOUL_RUNESTONE_FRAGMENT_4),
                    new ItemStack(VWItems.SOUL_RUNESTONE_FRAGMENT_2),
                    new ItemStack(VWItems.VERIXIUM_POWDER, Math.clamp(randomAmount - 1, 0, 4))
            ));

            for (int i = 0; i < randomAmount; i++) {
                if (!fragments.isEmpty()) {
                    ItemStack fragment = fragments.get(random.nextIntBetweenInclusive(0, fragments.size() - 1));

                    inv.add(fragment);
                    fragments.remove(fragment);
                }
            }
        }

        if (player.level().isClientSide()) {
            player.makeSound(SoundEvents.ANVIL_BREAK);
        }

        applyBenedictionBlessings(player);
        level.broadcastEntityEvent(player, (byte) 35);
    }
    private static void applyBenedictionBlessings(Player player) {
        player.removeAllEffects();
        player.setHealth(player.getMaxHealth() * 0.5f);

        addEffect(player, MobEffects.RESISTANCE, 20 * 3, 255);
        addEffect(player, VWEffects.BLESSING_OF_THE_VERDANT_WIND, 20 * 10, 2);
        addEffect(player, MobEffects.ABSORPTION, 20 * 10, 2);
    }

    private static void atrocityProcessor(LivingEntity victim, DamageSource damageSource, boolean death) {
        if (victim == null) return;

        if (death && victim instanceof Player player) {
            if (!(player.level() instanceof ServerLevel)) return;
            player.removeAttached(VWAttachments.player.PLAYER_WOLF_ATROCITY_COUNT);
            player.removeAttached(VWAttachments.player.PLAYER_VILLAGER_ATROCITY_COUNT);

            player.removeAttached(VWAttachments.player.PLAYER_RECEIVED_ENCHANTMENTS_HANDBOOK);
            player.removeAttached(VWAttachments.player.PLAYER_RECEIVED_EFFECTS_HANDBOOK);
            player.removeAttached(VWAttachments.player.PLAYER_RECEIVED_ITEMS_HANDBOOK);
            player.removeAttached(VWAttachments.player.PLAYER_RECEIVED_FEATURES_HANDBOOK);
            return;
        }

        Entity attacker = damageSource.getEntity();
        if (!(attacker instanceof Player player)) return;
        Level level = player.level();

        float multiplier = setDifficultyBasedValue(level, 0.5f, 0.75f, 1.0f, 2.0f);

        int maybeAddMore = level.getRandom().nextIntBetweenInclusive(0, 3);
        int deduction = Mth.ceil(3 * multiplier) + maybeAddMore;
        int finalDeduction = death ? deduction * 4 : deduction;

        ServerPlayer serverPlayer = (ServerPlayer) player;

        if (victim instanceof Wolf wolf) {
            AttachmentType<Integer> WOLF_COUNTER = VWAttachments.player.PLAYER_WOLF_ATROCITY_COUNT;
            boolean maybeForgive = wolf.getOwner() != null && wolf.getOwner().is(player) && level.getRandom().nextBoolean();
            if (maybeForgive) return;

            int current = player.getAttachedOrElse(WOLF_COUNTER, 0);
            player.setAttached(WOLF_COUNTER, current + finalDeduction);

            showAtrocityCounter(serverPlayer, wolf, player.getAttachedOrElse(WOLF_COUNTER, 0));
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            AttachmentType<Integer> VILLAGER_COUNTER = VWAttachments.player.PLAYER_VILLAGER_ATROCITY_COUNT;

            int current = player.getAttachedOrElse(VILLAGER_COUNTER, 0);
            player.setAttached(VILLAGER_COUNTER, current + finalDeduction);

            showAtrocityCounter(serverPlayer, victim, player.getAttachedOrElse(VILLAGER_COUNTER, 0));
        }
    }

    private static void showAtrocityCounter(Player player, LivingEntity victim, int count) {
        if (!player.getAttachedOrElse(VWPlayerPrefs.SHOW_ATROCITY_COUNTER, VWConfig.get().CLIENT_SHOW_ATROCITY_COUNTER)) return;
        if (victim instanceof Wolf) {
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Wolf atrocity count: " + count);
        } else if (victim instanceof Villager || victim instanceof WanderingTrader) {
            sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, true, "Villager atrocity count: " + count);
        }
    }


    private VWGlobalEntityBehaviors() {}
}
