package cliffordha.totvw.entity.skills;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.PlayerPrefs;

import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.util.VWUtil.*;

public class RevivalByProxy {
    public static boolean revivePlayerIfPossible(LivingEntity entity, DamageSource damageSource, float damage) {
        boolean hasTotem = entity.getMainHandItem().is(Items.TOTEM_OF_UNDYING) || entity.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
        if (entity instanceof Player player && !hasTotem) {
            if (damageSource.is(DamageTypes.GENERIC_KILL)) return true;
            if (!player.getAttachedOrElse(PlayerPrefs.BENEDICTION_SHARE_STACK, VWConfig.get().SERVER_WOLF_SHARES_BENEDICTION_STACK)) return true;

            Level getLevel = player.level();
            ServerLevel level = (ServerLevel) getLevel;

            double distance = VWConfig.get().SERVER_WOLF_PLAYER_SCAN_DISTANCE * 16;
            AttachmentType<Integer> BENEDICTION_STACK = WolfAttachment.BENEDICTION;

            List<Wolf> wolves = level.getEntities(EntityTypes.WOLF, player.getBoundingBox().inflate(distance), wolf ->
                    wolf.getOwner() != null && wolf.getOwner().is(player));

            if (!wolves.isEmpty()) {
                List<Wolf> wolfWithStack = wolves.stream().filter(wolf -> wolf.getAttachedOrElse(BENEDICTION_STACK, 0) > 1).toList();
                if (!wolfWithStack.isEmpty()) {
                    Wolf mainWolf = wolfWithStack.getFirst();
                    getTeleportToWolf(player, mainWolf, false, mainWolf);

                    if (player.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, VWConfig.get().SERVER_WOLF_TP_ALL)) {
                        for (Wolf wolf : wolves) {
                            getTeleportToWolf(player, wolf, true, mainWolf);
                        }
                        for (Wolf otherMainWolf : wolfWithStack) {
                            getTeleportToWolf(player, otherMainWolf, true, mainWolf);
                        }
                    }

                    VerdantWindBlessing.applyBenedictionEffects(player, true);

                    int benediction = mainWolf.getAttachedOrElse(BENEDICTION_STACK, 0);
                    mainWolf.setAttached(BENEDICTION_STACK, benediction - 1);

                    String name = mainWolf.getPlainTextName();
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
            } else {
                // Last check to revive player if no wolves with stack found
                return checkWolfSouls(player, level, damageSource);
            }
        }
        return true;
    }
    private static boolean checkWolfSouls(Player player, ServerLevel level, DamageSource damageSource) {
        if (!player.getInventory().contains(new ItemStack(VWItems.SOUL_RUNESTONE_PLATE))) return true;

        List<CompoundTag> souls = player.getAttachedOrElse(PlayerAttachment.WOLF_SOULS, List.of());
        if (souls.isEmpty()) return true;

        CompoundTag stack = souls.stream().filter(soul -> soul.getCompoundOrEmpty("fabric:attachments")
                .getIntOr(TOTVW.MOD_ID + ":wolf_benediction", 0) > 1)
                .findFirst()
                .orElse(new CompoundTag());

        if (stack.isEmpty()) return true;

        if (wolfHasBenedictionEnchantment(stack)) {
            processRevivalThroughRunestone(level, player, souls, stack, damageSource);
            return false;
        }
        return true;
    }
    private static void getTeleportToWolf(Player player, Wolf wolf, boolean tpAll, Wolf mainWolf) {
        if (!player.canTeleport(player.level(), wolf.level())) return;
        if (!player.getAttachedOrElse(PlayerPrefs.BENEDICTION_TELEPORT_AFTER_SAVE, VWConfig.get().SERVER_TELEPORT_AFTER_SAVE)) return;
        if (player.distanceTo(wolf) < 16) return;

        BlockPos wolfPos = wolf.blockPosition();
        BlockPos playerPos = player.blockPosition();
        boolean tpMode = player.getAttachedOrElse(PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, VWConfig.get().SERVER_PLAYER_TP_METHOD) < 1;

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
    private static void untetherWolf(Wolf wolf) {
        wolf.unRide();
        wolf.dropLeash();
        wolf.setOrderedToSit(false);
    }
    private static boolean wolfHasBenedictionEnchantment(CompoundTag stack) {
        int equipment = stack.getCompoundOrEmpty("equipment")
                .getCompoundOrEmpty("body")
                .getCompoundOrEmpty("components")
                .getCompoundOrEmpty("minecraft:enchantments")
                .getIntOr(TOTVW.MOD_ID + ":benediction_of_the_verdant_mountains", 0);
        return equipment > 0;
    }
    private static void processRevivalThroughRunestone(ServerLevel level, Player player, List<CompoundTag> souls, CompoundTag stack, DamageSource source) {
        CompoundTag attachments = stack.getCompoundOrEmpty("fabric:attachments").copy();
        int count = attachments.getIntOr(TOTVW.MOD_ID + ":wolf_benediction", 0);
        RandomSource random = level.getRandom();
        Inventory inv = player.getInventory();

        attachments.remove(TOTVW.MOD_ID + ":wolf_benediction");
        attachments.putInt(TOTVW.MOD_ID + ":wolf_benediction", count - 1);
        stack.put("fabric:attachments", attachments);

        souls.remove(stack);
        souls.add(stack);
        player.setAttached(PlayerAttachment.WOLF_SOULS, souls);

        float chance = 0.05f + ((souls.size() - 3) * 0.05f);
        boolean checkDMGSource= source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL);
        if (!checkDMGSource && souls.size() > 3 && random.nextFloat() < (0.33f + chance)) {
            SoulRunestonePlate.processAndSummonSouls(player, level, souls);

            int slot = inv.findSlotMatchingItem(new ItemStack(VWItems.SOUL_RUNESTONE_PLATE));
            inv.removeItem(slot, 1);

            int randomAmount = random.nextIntBetweenInclusive(1, 3);
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
        VerdantWindBlessing.applyBenedictionEffects(player, true);
        level.broadcastEntityEvent(player, (byte) 35);
    }
}
