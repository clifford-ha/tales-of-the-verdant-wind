package cliffordha.totvw.item.custom;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.*;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cliffordha.totvw.util.VWUtil.*;

public class SoulRunestonePlate extends Item {
    private static final AttachmentType<List<CompoundTag>> WOLF_SOULS = PlayerAttachment.WOLF_SOULS;

    public SoulRunestonePlate(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (player.getCooldowns().isOnCooldown(new ItemStack(this))) return InteractionResult.FAIL;
        if (!player.isCrouching()) return InteractionResult.FAIL;

        boolean notOnGround = player.isFallFlying() || level.getBlockState(player.blockPosition().below()).isAir();
        if (player.isInLiquid() || notOnGround) {
            String errorGround = "You can only summon when on ground!";
            sendToChat(player, false, errorGround);
            player.getCooldowns().addCooldown(new ItemStack(this), 20);
            return InteractionResult.FAIL;
        }
        List<CompoundTag> souls = player.getAttachedOrElse(WOLF_SOULS, List.of());

        if (!player.hasAttached(WOLF_SOULS) || souls.isEmpty()) {
            sendToChat(player, false, "You currently have no wolf souls to summon!");
            player.getCooldowns().addCooldown(new ItemStack(this), 20);
            return InteractionResult.FAIL;
        } else {
            if (player.getAttachedOrElse(PlayerAttachment.WOLF_ATROCITY_COUNT, 0) > 10) {
                sendToChat(player, VWColors.BLOODLUST_EFFECT_MUTED, false, "The runestone rejected your summoning request...");
                player.getCooldowns().addCooldown(new ItemStack(this), 60);
                return InteractionResult.FAIL;
            }
            if ((player.level() instanceof ServerLevel serverLevel)) {
                processAndSummonSouls(player, serverLevel, souls);

                List<String> nameList = getNameForWolves(souls);
                int pass = 0;
                for (String name : nameList) {
                    if (name.equals("Wolf")) pass++;
                }

                String names;
                if (nameList.size() >= 2 && pass == nameList.size()) {
                    names = souls.size() + " wolves";
                } else {
                    if (nameList.size() > 2) {
                        names = nameList.stream().limit(nameList.size() - 1).collect(Collectors.joining(", ")) + ", and " + nameList.getLast();
                    } else {
                        names = nameList.stream().reduce((a, b) -> a + " and " + b).orElse("");
                        if (nameList.size() == 1) names = nameList.getFirst();
                    }
                }

                String message = nameList.size() > 5 ? "Summoned " + souls.size() + " wolves." : "Summoned " + names + ".";
                sendToChat(player, false, message);

                processAdditional(player, souls.size(), true);
                player.getCooldowns().addCooldown(new ItemStack(this), 30);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    public static void processAndSummonSouls(Player player, ServerLevel level, List<CompoundTag> souls) {
        int pass = 0;
        List<CompoundTag> catcher = new ArrayList<>();

        for (CompoundTag soul : souls) {
            ListTag setPosition = getPosition(player);
            soul.put("Pos", setPosition);

            TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), soul);
            Optional<Entity> wolfEntity = EntityType.create(input, level, new EntitySpawnRequest(EntitySpawnReason.LOAD, false));

            if (wolfEntity.isPresent()) {
                level.addFreshEntity(wolfEntity.get());
                Wolf wolf = (Wolf) wolfEntity.get();
                if (wolf.getHealth() < 2.0f) {
                    wolf.setHealth(4.0f);
                }
                wolf.removeAllEffects();
                wolf.teleportToAroundBlockPos(player.blockPosition());
                addHiddenEffect(wolf, VWEffects.WIND_VEIL, 20 * 3, 0);

                pass++;

            } else {
                catcher.add(soul);
            }
        }
        /*
        souls.forEach(soul -> {
            ListTag setPosition = getPosition(player);
            soul.put("Pos", setPosition);

            TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), soul);
            Optional<Entity> wolfEntity = EntityType.create(input, level, new EntitySpawnRequest(EntitySpawnReason.LOAD, true));

            if (wolfEntity.isPresent()) {
                level.addFreshEntity(wolfEntity.get());
                Wolf wolf = (Wolf) wolfEntity.get();
                if (wolf.getHealth() < 2.0f) {
                    wolf.setHealth(4.0f);
                }
                wolf.removeAllEffects();
                wolf.teleportToAroundBlockPos(player.blockPosition());
                addHiddenEffect(wolf, VWEffects.WIND_VEIL, 20 * 3, 0);

                check.add(1);
            }
        });*/

        if (pass == souls.size()) {
            player.removeAttached(WOLF_SOULS);
        } else {
            processAndSummonSouls(player, level, catcher);
            TOTVW.sendWarning(player.getPlainTextName() + " resummoned " + catcher.size() + " wolf souls due to a failure.");
        }
    }

    public static void processAdditional(Player player, int souls, boolean isSummoned) {
        if (!(player.level() instanceof ServerLevel level)) return;

        RandomSource random = level.getRandom();
        for (int i = 0; i < 16; i++) {
            double xz = random.nextIntBetweenInclusive(0, 2);
            double y = random.nextIntBetweenInclusive(0, 3);
            level.sendParticles(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE, player.getX(), player.getY(), player.getZ(), 3, xz, y, xz, 0);
        }
        level.playSound(null, player.blockPosition(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS);

        if (player.isCreative() || player.isSpectator()) return;
        boolean ownerHasBenediction = VWEnchantments.getBenediction(player);
        int getLimit = ownerHasBenediction ? 12 : 5;
        if (isSummoned) {
            if (souls > getLimit) addOrStackEffect(player, MobEffects.WEAKNESS, 60 * souls, 1, true);
        } else {
            if (souls > 3 && random.nextFloat() < 0.6f) {
                int multiplier = souls - 3;
                player.hurtServer(level, level.damageSources().starve(), 1.3f * multiplier);
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
