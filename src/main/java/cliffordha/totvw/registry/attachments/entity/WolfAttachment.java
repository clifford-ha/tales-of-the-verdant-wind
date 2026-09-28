package cliffordha.totvw.registry.attachments.entity;

import cliffordha.totvw.registry.attachments.Runestone;
import cliffordha.totvw.registry.attachments.VWAttachments;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.wolf.Wolf;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cliffordha.totvw.registry.attachments.AttachmentUtil.*;

public class WolfAttachment {
    private static final String WOLF = "wolf_";
    
    public static final AttachmentType<Boolean> IS_VERDANT_TYPE = registerBool(WOLF + "is_verdant_type", true);
    public static final AttachmentType<Boolean> IS_VILLAGE_GUARD = registerBool(WOLF + "is_village_guard", true);

    public static final AttachmentType<Integer> TIMER_AIR_SUPPLY = registerInt(WOLF + "timer_air_supply", true);
    public static final AttachmentType<Integer> NOTIFY_AIR_SUPPLY = registerInt(WOLF + "notify_air_supply", false);

    public static final AttachmentType<Integer> TRY_SAVE_POINTS = registerInt(WOLF + "try_save_points", true);
    public static final AttachmentType<Integer> TRY_SAVE_STATUS = registerInt(WOLF + "try_save_status", true);

    public static final AttachmentType<Integer> CD_BLESSING_OF_THE_VERDANT_WIND = registerInt(WOLF + "cd_blessing_of_the_verdant_wind", true);
    public static final AttachmentType<Integer> CD_BLOODLUST_SKILL_PARALYZE = registerInt(WOLF + "cd_bloodlust_skill_paralyze", true);
    public static final AttachmentType<Integer> CD_MIGHT_SKILL_RUPTURE = registerInt(WOLF + "cd_might_skill_rupture", true);
    public static final AttachmentType<Integer> CD_IGNORE_HIGH_DAMAGE = registerInt(WOLF + "cd_ignored_insurmountable_damage", true);

    public static final AttachmentType<Integer> NOTIFY_MIGHT_SKILL_RUPTURE = registerInt(WOLF + "notify_might_skill_rupture", false);
    public static final AttachmentType<Integer> NOTIFY_BLOODLUST_SKILL_PARALYZE = registerInt(WOLF + "notify_bloodlust_skill_paralyze", false);
    public static final AttachmentType<Integer> NOTIFY_BLESSING_OF_THE_VERDANT_WIND = registerInt(WOLF + "notify_blessing_of_the_verdant_wind", false);

    public static final AttachmentType<Integer> BENEDICTION = registerInt(WOLF + "benediction", true);

    public static final AttachmentType<UUID> SOUL_ID = registerUUID(WOLF + "soul_id", true);
    public static final AttachmentType<UUID> FAMILY_ID = registerUUID(WOLF + "family_id", true);

    public static final AttachmentType<BlockPos> RESPAWN_POINT = registerBlockPos(WOLF + "respawn_point", true);
    public static final AttachmentType<List<Pair<String, UUID>>> AGGRESSOR_LIST = registerListPair(WOLF + "aggressor_list", true);
    public static final AttachmentType<List<Pair<String, UUID>>> TRUSTED_PLAYERS = registerListPair(WOLF + "trusted_players", true);
    public static final AttachmentType<List<String>> TETHERED_ENTITY_TYPES = registerStringList(WOLF + "tethered_entity_types", false);

    public static final AttachmentType<Runestone> RUNESTONE_TYPE = registerRunestone(WOLF + "runestone_type", false);
    public static final AttachmentType<Integer> ATTACK_CYCLE = registerInt(WOLF + "attack_cycle", false);


    public static boolean isFamilyRelated(Wolf wolf, Wolf baby) {
        UUID wolfID = wolf.getAttachedOrElse(WolfAttachment.FAMILY_ID, EMPTY_UUID);
        UUID babyID = baby.getAttachedOrElse(WolfAttachment.FAMILY_ID, EMPTY_UUID);
        if (wolfID.equals(EMPTY_UUID)) return false;
        if (babyID.equals(EMPTY_UUID)) return false;

        // Somehow using the == does not work... or I'm just high when I tested it
        return wolfID.equals(babyID);
    }
    public static boolean isPlayerToBeAttacked(Wolf wolf, LivingEntity target) {
        List<Pair<String, UUID>> TRUSTED = getTrustedPlayers(wolf);
        List<Pair<String, UUID>> check = TRUSTED.stream().filter(data -> data.getB().equals(VWAttachments.getWolfPlayerSharedId(target))).toList();

        return !check.isEmpty();
    }
    public static List<Pair<String, UUID>> getTrustedPlayers(Wolf wolf) {
        return wolf.getAttachedOrElse(TRUSTED_PLAYERS, List.of());
    }
    public static List<Pair<String, UUID>> getAggressors(Wolf wolf) {
        return wolf.getAttachedOrElse(AGGRESSOR_LIST, List.of());
    }
    public static boolean isListedAggressor(Wolf wolf, UUID aggressor) {
        List<Pair<String, UUID>> aggressors = getAggressors(wolf);
        if (aggressors.isEmpty()) return false;

        List<Pair<String, UUID>> check = aggressors.stream().filter(data -> data.getB().equals(aggressor)).toList();
        return !check.isEmpty();
    }
    public static Pair<String, UUID> getNameAndUUID(LivingEntity entity) {
        return new Pair<>(entity.getPlainTextName(), VWAttachments.getWolfPlayerSharedId(entity));
    }
    public static void addPlayerToAggressors(Wolf wolf, LivingEntity entity) {
        List<Pair<String, UUID>> oldPlayerData = WolfAttachment.getAggressors(wolf);
        List<Pair<String, UUID>> newPlayerData = new ArrayList<>(oldPlayerData);

        Pair<String, UUID> mobData = getNameAndUUID(entity);

        boolean hasPriorData = oldPlayerData.stream().anyMatch(data -> data.getB() == mobData.getB());
        if (oldPlayerData.size() >= 6 && !hasPriorData) {
            return;
        }
        if (hasPriorData) {
            var index = oldPlayerData.stream().findFirst().filter(data -> data.getB() == mobData.getB()).map(oldPlayerData::indexOf).orElse(null);
            if (index == null) return;

            Pair<String, UUID> data = oldPlayerData.get(index);

            if (!data.getA().equals(mobData.getA()) && data.getB() == mobData.getB()) {
                Pair<String, UUID> update = new Pair<>(mobData.getA(), mobData.getB());

                newPlayerData.set(newPlayerData.indexOf(data), update);
                wolf.setAttached(WolfAttachment.AGGRESSOR_LIST, List.copyOf(newPlayerData));
            }
        } else {
            newPlayerData.add(mobData);
            wolf.setAttached(WolfAttachment.AGGRESSOR_LIST, List.copyOf(newPlayerData));
        }
    }
}
