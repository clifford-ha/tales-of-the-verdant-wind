package cliffordha.totvw.item.custom;

import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.util.VWUtil;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cliffordha.totvw.util.VWUtil.*;

public class SoulRunestonePlate extends Item {
    private static final AttachmentType<List<CompoundTag>> WOLF_SOULS = VWAttachments.player.PLAYER_WOLF_SOULS;
    private static final AttachmentType<Integer> WOLF_SOULS_COUNTER = VWAttachments.player.PLAYER_WOLF_SOULS_COUNTER;

    public SoulRunestonePlate(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!player.isCrouching()) return InteractionResult.PASS;

        boolean notOnGround = player.isFallFlying() || level.getBlockState(player.blockPosition().below()).isAir();
        if (player.isInLiquid() || notOnGround) {
            String errorGround = "You can only summon when on ground!";
            sendToChat(player, false, errorGround);
            return InteractionResult.FAIL;
        }
        List<CompoundTag> souls = player.getAttachedOrElse(WOLF_SOULS_COUNTER, 0) > 0 ? player.getAttachedOrElse(WOLF_SOULS, List.of()) : List.of();

        if (!player.hasAttached(WOLF_SOULS) || souls.isEmpty()) {
            sendToChat(player, false, "You currently have no wolf souls to summon!");
            return InteractionResult.FAIL;
        } else {
            if (player.getAttachedOrElse(VWAttachments.player.PLAYER_WOLF_ATROCITY_COUNT, 0) > 10) {
                sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, "The runestone rejected your summoning request...");
                return InteractionResult.FAIL;
            }
            if (!level.isClientSide()) {
                ServerLevel serverLevel = (ServerLevel) level;
                processAndSummonSouls(player, serverLevel, souls);

                List<String> nameList = getNameForWolves(souls);
                String names;
                if (nameList.size() > 2) {
                    names = nameList.stream().limit(nameList.size() - 1).collect(Collectors.joining(", ")) + ", and " + nameList.getLast();
                } else {
                    names = nameList.stream().reduce((a, b) -> a + " and " + b).orElse("");
                    if (nameList.size() == 1) names = nameList.getFirst();
                }
                String message = nameList.size() > 5 ? "Summoned " + souls.size() + " wolves." : "Summoned " + names + ".";
                sendToChat(player, false, message);

                processAdditional(player, souls.size(), true);
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return InteractionResult.FAIL;
    }

    public static void processAndSummonSouls(Player player, ServerLevel serverLevel, List<CompoundTag> souls) {
        souls.forEach(soul -> {
            Level level = player.level();
            ListTag setPosition = getPosition(player);
            soul.put("Pos", setPosition);

            TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), soul);
            Wolf wolf = EntityType.WOLF.spawn(serverLevel, player.blockPosition(), EntitySpawnReason.MOB_SUMMONED);
            if (wolf != null) {
                wolf.load(input);

                if (wolf.getHealth() < 2.0f) wolf.setHealth(4.0f);
                wolf.removeAllEffects();
                wolf.teleportToAroundBlockPos(player.blockPosition());
                VWUtil.addHiddenEffect(wolf, MobEffects.RESISTANCE, 6, 254);
            }
        });
        player.removeAttached(WOLF_SOULS);
        player.setAttached(WOLF_SOULS_COUNTER, 0);
    }

    public static void processAdditional(Player player, int souls, boolean isSummoned) {
        Level level = player.level();
        if (level.isClientSide()) return;

        ServerLevel serverLevel = (ServerLevel) level;

        for (int i = 0; i < 16; i++) {
            double xz = serverLevel.getRandom().nextIntBetweenInclusive(0, 2);
            double y = serverLevel.getRandom().nextIntBetweenInclusive(0, 3);
            serverLevel.sendParticles(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE, player.getX(), player.getY(), player.getZ(), 3, xz, y, xz, 0);
        }
        serverLevel.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);

        if (player.isCreative() || player.isSpectator()) return;
        boolean ownerHasBenediction = entityEnchantmentLVL(player, EquipmentSlot.CHEST, VWEnchantments.BENEDICTION_OF_THE_VERDANT_MOUNTAINS) > 0;
        int getLimit = ownerHasBenediction ? 12 : 5;
        if (isSummoned) {
            if (souls > getLimit) addOrStackEffect(player, MobEffects.WEAKNESS, 60 * souls, 1);
        } else {
            if (souls > 3 && level.getRandom().nextFloat() < 0.6f) {
                int multiplier = souls - 3;
                player.hurtServer(serverLevel, level.damageSources().starve(), 1.3f * multiplier);
            }
        }

    }
    private static List<String> getNameForWolves(List<CompoundTag> tag) {
        List<String> namesList = new ArrayList<>();
        for (CompoundTag soul : tag) {
            if (soul.getString("CustomName").isPresent()) {
                namesList.add(soul.getString("CustomName").get());
            } else {
                namesList.add("Wolf");
            }
        }
        return namesList;
    }
    private static ListTag getPosition(Player player) {
        ListTag pos = new ListTag();
        pos.add(DoubleTag.valueOf(player.getX()));
        pos.add(DoubleTag.valueOf(player.getY()));
        pos.add(DoubleTag.valueOf(player.getZ()));
        return pos;
    }
}
