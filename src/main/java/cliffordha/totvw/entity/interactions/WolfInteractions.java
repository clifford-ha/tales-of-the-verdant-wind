package cliffordha.totvw.entity.interactions;

import cliffordha.totvw.entity.skills.RunestoneEffects;
import cliffordha.totvw.item.custom.SoulRunestonePlate;
import cliffordha.totvw.networking.packets.OpenTetherBlacklistPayload;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.TetherData;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import cliffordha.totvw.tag.VWItemTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Prediction;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.ArrayList;
import java.util.List;

import static cliffordha.totvw.util.ExtUtil.*;

public class WolfInteractions {
    public static InteractionResult handleInteraction(ServerLevel level, Player player, Wolf wolf, ItemStack stack) {
        if (wolf.getOwner() == player) {
            return tamedInteraction(level, player, wolf, stack);
        } else {
            return wildInteraction(level, player, wolf, stack);
        }
    }
    
    private static InteractionResult wildInteraction(ServerLevel level, Player player, Wolf wolf, ItemStack stack) {
        String name = wolf.getPlainTextName();

        if (stack.is(Items.BONE) && !wolf.isAngry()) {
            int atrocityCount = PlayerAttachment.getVillagerAtrocityCount(player);
            if (atrocityCount > 20) {
                sendToChat(player, VWColors.DEFAULT_MUTED, false, "Something is preventing you from taming this wolf. Try again later.\nWolf atrocity count: " + atrocityCount);
                return InteractionResult.FAIL;
                
            } else {
                consumeItem(player, stack);
                if (level.getRandom().nextInt(3) == 0) {
                    wolf.tame(player);
                    wolf.getNavigation().stop();
                    wolf.setTarget(null);
                    wolf.setOrderedToSit(true);
                    level.broadcastEntityEvent(wolf, (byte) 7);
                } else {
                    level.broadcastEntityEvent(wolf, (byte) 6);
                }
            }
            return InteractionResult.SUCCESS_SERVER;
            
        } else if (stack.is(Items.TOTEM_OF_UNDYING) && VWEnchantments.getBenediction(wolf)) {
            int STACK = wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0);
            int STACK_LIMIT = 3;

            if (STACK > STACK_LIMIT) {
                wolf.setAttached(WolfAttachment.BENEDICTION, STACK_LIMIT);
                return InteractionResult.FAIL;
            }

            if (STACK < STACK_LIMIT) {
                wolf.setAttached(WolfAttachment.BENEDICTION, STACK + 1);
                level.playSound(null, wolf.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.NEUTRAL);
                sendParticles(VWParticles.BENEDICTION_TRIGGER, level, wolf.blockPosition(), 12, 0.5);
                sendToChat(player, VWColors.VERDANT_WIND, true, wolf.getPlainTextName() + " Benediction stack " + wolf.getAttachedOrElse(WolfAttachment.BENEDICTION, 0));
                consumeItem(player, stack);
                return InteractionResult.SUCCESS;
            } else {
                sendToChat(player, true, name + " already reached Benediction Stack limit!");
                return InteractionResult.PASS;
            }
        }
        else if (stack.is(VWItemTags.WOLF_ARMOR_ENCHANTABLE) && !wolf.isWearingBodyArmor()) {
            wolf.equipItemIfPossible(level, stack);
            consumeItem(player, stack);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    private static InteractionResult tamedInteraction(ServerLevel level, Player player, Wolf wolf, ItemStack stack) {
        VWAttachments.validateSharedID(wolf, player);
        String name = wolf.getPlainTextName();
        
        if (player.isShiftKeyDown() && Runestone.hasTether(wolf)) {
            if (player instanceof ServerPlayer serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, new OpenTetherBlacklistPayload(wolf.getId(), TetherData.of(wolf)));
            }
            return InteractionResult.SUCCESS;
        }

        if (stack.is(Items.SHEARS) && Runestone.hasRunestone(wolf)) {
            Runestone type = wolf.getAttachedOrElse(WolfAttachment.RUNESTONE_TYPE, Runestone.EMPTY);
            wolf.drop(Runestone.getStack(type), false, Prediction.SERVER_ONLY);
            wolf.removeAttached(WolfAttachment.RUNESTONE_TYPE);
            level.playSound(null, wolf.blockPosition(), SoundEvents.SHEEP_SHEAR, SoundSource.NEUTRAL);
            sendToChat(player, true, name + "'s runestone has been removed.");
            return InteractionResult.SUCCESS;

        } else if (stack.is(Items.COMPASS)) {
            wolf.removeAttached(WolfAttachment.RESPAWN_DIMENSION);
            wolf.removeAttached(WolfAttachment.RESPAWN_POINT);
            sendToChat(player, true, name + "'s return point has been removed.");
            return InteractionResult.SUCCESS;

        } else if (stack.is(ItemTags.BEDS)) {
            BlockPos pos = player.getAttachedOrElse(PlayerAttachment.RESPAWN_POINT, player.blockPosition());
            if (pos.getX() == 0 && pos.getZ() == 0) {
                sendToChat(player, true, "Cannot set a return point here");
                return InteractionResult.FAIL;
            } else {
                wolf.setAttached(WolfAttachment.RESPAWN_DIMENSION, player.getAttachedOrElse(PlayerAttachment.RESPAWN_DIMENSION, player.level().dimension()));
                wolf.setAttached(WolfAttachment.RESPAWN_POINT, pos);
                sendToChat(player, VWColors.VERDANT_WIND, true, name + " has been set to return to " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
                player.playSound(VWSounds.NOTIFY);
                return InteractionResult.SUCCESS;
            }

        } else if (stack.is(VWItemTags.RUNESTONE_PLATES)) {
            if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.FAIL;

            InteractionResult updateRunestone;
            if (stack.is(VWItems.SOUL_RUNESTONE_PLATE)) {
                if (player.getAttachedOrElse(PlayerAttachment.WOLF_ATROCITY_COUNT, 0) > 10) {
                    sendToChat(player, VWColors.BLOODLUST_MUTED, true, "The runestone did not respond...");
                    player.getCooldowns().addCooldown(stack, 60);
                    return InteractionResult.FAIL;
                }

                int soulCount = player.getAttachedOrElse(PlayerAttachment.WOLF_SOULS, List.of()).size();
                boolean limiter = VWEnchantments.getBenediction(player) || player.isCreative();
                int applyLimit = limiter ? 12 : 5;
                if (soulCount < applyLimit) {
                    List<CompoundTag> souls = new ArrayList<>(player.getAttachedOrElse(PlayerAttachment.WOLF_SOULS, List.of()));

                    wolf.extinguishFire();
                    wolf.stopRiding();
                    wolf.setOrderedToSit(false);
                    wolf.removeAttached(RunestoneEffects.TETHERED_ENTITIES);

                    TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, wolf.registryAccess());
                    wolf.save(output);
                    CompoundTag tag = output.buildResult();
                    tag.remove("Pos");

                    souls.add(tag);
                    player.setAttached(PlayerAttachment.WOLF_SOULS, souls);
                    wolf.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);

                    sendToChat(player, VWColors.VERDANT_WIND, true, name + "'s soul has been stored within you.");

                    SoulRunestonePlate.processAdditional(player, souls.size(), false);
                    ItemStack plate = new ItemStack(VWItems.SOUL_RUNESTONE_PLATE);
                    player.getCooldowns().addCooldown(plate, 10);
                    return InteractionResult.SUCCESS;

                } else {
                    sendToChat(player, VWColors.BLOODLUST, "You can only store up to " + applyLimit + " wolf souls.");
                    return InteractionResult.FAIL;
                }

            } else if (stack.is(VWItems.TETHER_RUNESTONE_PLATE)) {
                updateRunestone = Runestone.updateRunestone(wolf, player, Runestone.TETHER, stack);
                return updateRunestone;

            } else if (stack.is(VWItems.GENESIS_RUNESTONE_PLATE)) {
                return InteractionResult.PASS;

            } else if (stack.is(VWItems.HAVOC_RUNESTONE_PLATE)) {
                updateRunestone = Runestone.updateRunestone(wolf, player, Runestone.HAVOC, stack);
                return updateRunestone;
                
            } else if (stack.is(VWItems.EFFLORESCENCE_RUNESTONE_PLATE)) {
                updateRunestone = Runestone.updateRunestone(wolf, player, Runestone.EFFLORESCENCE, stack);

                return updateRunestone;
            }
        }
        return InteractionResult.PASS;
    }
}
