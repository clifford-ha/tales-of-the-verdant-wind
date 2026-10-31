package cliffordha.totvw.entity.skills;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import cliffordha.totvw.registry.VWItems;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cliffordha.totvw.util.ExtUtil.*;

public class RevivalByProxy {
    public static boolean allowPlayerDeath(LivingEntity entity, DamageSource damageSource, float damage) {
        if (!(entity instanceof ServerPlayer player)) return true;
        ServerLevel level = player.level();

        boolean hasTotem = player.getMainHandItem().is(Items.TOTEM_OF_UNDYING) || player.getOffhandItem().is(Items.TOTEM_OF_UNDYING);
        boolean hasSoulRunestone = player.getInventory().contains(new ItemStack(VWItems.SOUL_RUNESTONE_PLATE));

        if (hasTotem && damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            if (hasSoulRunestone && PlayerAttachment.hasWolfSoul(player)) {
                return checkWolfSouls(player, level, damageSource);
            }
        }

        if (!hasTotem || (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD) && !PlayerAttachment.hasWolfSoul(player))) {
            if (damageSource.is(DamageTypes.GENERIC_KILL)) return true;
            if (!player.getAttachedOrElse(PlayerPrefs.BENEDICTION_SHARE_STACK, VWConfig.get().SYNCED_WOLF_SHARES_BENEDICTION_STACK)) return true;
            Wolf wolf = null;

            for (ServerLevel serverLevel : level.getServer().getAllLevels()) {
                for (Entity e : serverLevel.getAllEntities()) {
                    if (e instanceof Wolf mainWolf && WolfAttachment.benedictionStack(mainWolf) > 0 && mainWolf.getOwner() == player) {
                        wolf = mainWolf;
                        break;
                    }
                }
            }
            if (wolf == null) {
                return checkWolfSouls(player, level, damageSource);

            } else {
                if (damageSource.is(DamageTypes.FELL_OUT_OF_WORLD)) {
                    validateLocationBeforeRevival(level, player);
                }
                teleportToWolfOrReturnPoint(level, player, wolf);

                VerdantWindBlessing.applyBenedictionEffects(player, true);

                int benediction = WolfAttachment.benedictionStack(wolf);
                wolf.setAttached(WolfAttachment.BENEDICTION, benediction - 1);

                String name = wolf.getPlainTextName();
                benediction = WolfAttachment.benedictionStack(wolf);
                if (benediction <= 0) {
                    sendToChat(wolf, VWColors.BLOODLUST_MUTED, name + " used up all Benediction stacks");
                } else {
                    sendToChat(wolf, VWColors.VERDANT_WIND_MUTED, "A Benediction stack has been shared by " + name + ". " + benediction + " remaining.");
                }

                wolf.makeSound(new SoundEvent(Identifier.withDefaultNamespace("entity.wolf.whine"), Optional.of(16.0f)));
                level.broadcastEntityEvent(player, (byte) 35);
                return false;
            }
        } else {
                // Last check to revive player if no wolves with stack found
            return checkWolfSouls(player, level, damageSource);
        }
    }
    private static boolean checkWolfSouls(ServerPlayer player, ServerLevel level, DamageSource damageSource) {
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
    private static void teleportToWolfOrReturnPoint(ServerLevel level, ServerPlayer player, @Nullable Wolf wolf) {
        if (wolf != null) {
            int distance = (level.getServer().getPlayerList().getSimulationDistance() * 16) * 2;
            List<Wolf> wolves1 = wolf.level().getEntitiesOfClass(Wolf.class, scanArea(player, distance), other -> other.getOwner() == player);
            List<Wolf> wolves = level.getEntitiesOfClass(Wolf.class, scanArea(player, distance), other -> other.getOwner() == player);

            if (isSameDimension(player, wolf) && player.canTeleport(player.level(), wolf.level())) {
                tryTeleport(player, wolf);
                performTeleportAll(level, wolf, wolves, player, true);
                performTeleportAll(level, wolf, wolves1, player, true);

            } else {
                processTeleports(level, player, wolf, wolves1, wolves);
            }

            for (ServerLevel otherLevel : level.getServer().getAllLevels()) {
                if (otherLevel == level) continue;
                if (otherLevel == wolf.level()) continue;
                otherLevel.getAllEntities().forEach(entity -> {
                    if (entity instanceof Wolf otherWolf && otherWolf.getOwner() == player) {
                        if (player.getAttachedOrElse(PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, VWConfig.get().SYNCED_PLAYER_TP_METHOD) < 1) return;
                        otherWolf.teleport(teleporter(otherLevel, player));
                        addEffect(otherWolf, VWEffects.WIND_VEIL, 3, 0);
                        untetherWolf(otherWolf);
                        otherWolf.teleportToAroundBlockPos(player.blockPosition());
                    }
                });
            }
            return;
        }

        ServerPlayer.RespawnConfig config = player.getRespawnConfig();
        LevelData.RespawnData data = config == null ? level.getRespawnData() : config.respawnData();

        ServerLevel target = level.getServer().getLevel(data.dimension());
        if (target == null) return;
        BlockPos pos = data.pos();

        player.teleport(new TeleportTransition(
                target,
                Vec3.atBottomCenterOf(pos),
                Vec3.ZERO,
                target.getRespawnData().yaw(),
                target.getRespawnData().pitch(),
                TeleportTransition.PLACE_PORTAL_TICKET
        ));
        addEffect(player, VWEffects.WIND_VEIL, 3, 0);
    }

    private static void processTeleports(ServerLevel level, ServerPlayer player, @NotNull Wolf wolf, List<Wolf> wolves1, List<Wolf> wolves) {
        if (player.getAttachedOrElse(PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, VWConfig.get().SYNCED_PLAYER_TP_METHOD) < 1) {
            player.teleport(teleporter(level, wolf));
            addEffect(player, VWEffects.WIND_VEIL, 3, 0);

        } else {
            wolf.teleport(teleporter(level, player));
            addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
        }
        untetherWolf(wolf);
        performTeleportAll(level, wolf, wolves, player, false);
        performTeleportAll(level, wolf, wolves1, player, false);
    }

    private static void performTeleportAll(ServerLevel level, Wolf main, List<Wolf> wolves, Player player, boolean sameDimension) {
        if (!player.getAttachedOrElse(PlayerPrefs.BENEDICTION_WOLF_TP_ALL, VWConfig.get().SYNCED_WOLF_TP_ALL)) return;
        if (wolves.isEmpty()) return;

        for (Wolf other : wolves) {
            if (other == main) continue;

            if (sameDimension) {
                other.teleportToAroundBlockPos(player.blockPosition());
            } else {
                other.teleport(teleporter(level, player));
            }
            addEffect(other, VWEffects.WIND_VEIL, 3, 0);
            untetherWolf(other);
        }
    }

    private static TeleportTransition teleporter(ServerLevel level, LivingEntity entity) {
        ServerLevel newLevel = entity.level() instanceof ServerLevel serverLevel ? serverLevel : level;
        return new TeleportTransition(
                newLevel,
                new Vec3(entity.getX(), entity.getY(), entity.getZ()),
                Vec3.ZERO,
                newLevel.getRespawnData().yaw(),
                newLevel.getRespawnData().pitch(),
                TeleportTransition.PLACE_PORTAL_TICKET
        );
    }

    private static void tryTeleport(Player player, Wolf wolf) {
        BlockPos playerPos = player.blockPosition();
        BlockPos wolfPos = wolf.blockPosition();
        RevivalByProxy.untetherWolf(wolf);
        if (player.getAttachedOrElse(PlayerPrefs.BENEDICTION_PLAYER_TP_METHOD, VWConfig.get().SYNCED_PLAYER_TP_METHOD) < 1) {
            player.teleportTo(wolfPos.getX(), wolfPos.getY(), wolfPos.getZ());
            addEffect(player, VWEffects.WIND_VEIL, 3, 0);
        } else {
            wolf.teleportToAroundBlockPos(playerPos);
            addEffect(wolf, VWEffects.WIND_VEIL, 3, 0);
        }
    }

    public static void untetherWolf(Wolf wolf) {
        wolf.unRide();
        wolf.dropLeash();
        wolf.setOrderedToSit(false);
    }
    public static boolean validLevel(LivingEntity entity) {
        BlockPos pos = entity.blockPosition();
        return !(pos.getY() < -63 && !isDimension(entity, Level.END)) || (pos.getY() < -32 && isDimension(entity, Level.END));
    }
    private static boolean wolfHasBenedictionEnchantment(CompoundTag stack) {
        int equipment = stack.getCompoundOrEmpty("equipment")
                .getCompoundOrEmpty("body")
                .getCompoundOrEmpty("components")
                .getCompoundOrEmpty("minecraft:enchantments")
                .getIntOr(TOTVW.MOD_ID + ":benediction_of_the_verdant_mountains", 0);
        return equipment > 0;
    }
    private static void processRevivalThroughRunestone(ServerLevel level, ServerPlayer player, List<CompoundTag> souls, CompoundTag stack, DamageSource source) {
        CompoundTag attachments = stack.getCompoundOrEmpty("fabric:attachments").copy();
        int count = attachments.getIntOr(TOTVW.MOD_ID + ":wolf_benediction", 0);
        RandomSource random = level.getRandom();
        Inventory inv = player.getInventory();

        attachments.putInt(TOTVW.MOD_ID + ":wolf_benediction", count - 1);
        stack.put("fabric:attachments", attachments);

        souls.remove(stack);
        souls.add(stack);
        player.setAttached(PlayerAttachment.WOLF_SOULS, souls);

        float chance = 0.05f + ((souls.size() - 3) * 0.05f);
        boolean checkDMGSource= source.is(DamageTypes.FELL_OUT_OF_WORLD) || source.is(DamageTypes.GENERIC_KILL);
        if (source.is(DamageTypes.FELL_OUT_OF_WORLD)) {
            validateLocationBeforeRevival(level, player);
        }
        if (!checkDMGSource && souls.size() > 3 && random.nextFloat() < (0.33f + chance)) {
            SoulRunestonePlate.loadWolves(level, player, souls);

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
    private static void validateLocationBeforeRevival(ServerLevel level, ServerPlayer player) {
        if (player.blockPosition().getY() > -64) return;

        ServerLevel serverLevel;
        BlockPos pos;
        ServerPlayer.RespawnConfig data = player.getRespawnConfig();
        LevelData.RespawnData respawnData;
        if (data == null) {
            respawnData = level.getRespawnData();
            serverLevel = level;
            pos = level.getRespawnData().pos();
        } else {
            respawnData = data.respawnData();
            serverLevel = level.getServer().getLevel(data.respawnData().dimension());
            pos = respawnData.pos();
        }

        if (serverLevel == null) {
            TOTVW.sendError("Failed to validate player location at " + player.blockPosition() + " in dimension " + player.level().dimension() + ".");
            return;
        }

        player.teleport(
                new TeleportTransition(
                        serverLevel,
                        Vec3.atBottomCenterOf(pos),
                        Vec3.ZERO,
                        respawnData.yaw(),
                        respawnData.pitch(),
                        TeleportTransition.PLACE_PORTAL_TICKET.then(TeleportTransition.PLAY_PORTAL_SOUND)
                )
        );
        sendToChat(player, VWColors.BLOODLUST_MUTED, "Player is out of bounds! Attempted to return to a valid location.");
    }
}
