package cliffordha.totvw.item.custom;

import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWParticles;
import cliffordha.totvw.registry.attachments.VWAttachments;
import cliffordha.totvw.tag.VWBiomeTags;
import cliffordha.totvw.util.VWUtil;
import cliffordha.totvw.worldgen.dimension.VWDimensions;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Set;

public class GenesisRunestonePlate extends Item {
    public GenesisRunestonePlate(Properties properties) {
        super(properties);
    }

    private static final Component GENESIS = Component.literal("§lGenesis Runestone Plate§r").withColor(VWColors.RUNESTONE_GENESIS);
    private static boolean ACTIVE = false;

    @Override
    public Component getName(ItemStack itemStack) {
        return ACTIVE ? GENESIS : super.getName(itemStack);
    }

    @Override
    public void inventoryTick(ItemStack itemStack, ServerLevel level, Entity owner, @Nullable EquipmentSlot slot) {
        ACTIVE = owner instanceof Player player && VWUtil.isInBiome(player, VWBiomeTags.IS_VERDANT_BIOMES);
        super.inventoryTick(itemStack, level, owner, slot);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide() && !player.getCooldowns().isOnCooldown(new ItemStack(this))) {
            player.getCooldowns().addCooldown(new ItemStack(this), 100);
            if (!player.level().dimension().equals(VWDimensions.NOLAYAN_LEVEL_KEY) && !VWUtil.isInBiome(player, BiomeTags.IS_OVERWORLD)) {
                VWUtil.sendToChat(player, false, "You must be in the Overworld to establish connection");
                return InteractionResult.FAIL;
            }
            player.teleport(getPortalDestination((player)));
            return InteractionResult.SUCCESS;
        }
        return super.use(level, player, hand);
    }

    public TeleportTransition getPortalDestination(Entity entity) {
        if (!(entity instanceof ServerPlayer player)) return null;
        ServerLevel currentLevel = player.level();

        VWUtil.sendParticles(VWParticles.BENEDICTION_TRIGGER_PARTICLE, currentLevel, player.blockPosition(), 32, 1.5);
        ResourceKey<Level> currentDimension = currentLevel.dimension();
        boolean isLeavingCustomDim = currentDimension == VWDimensions.NOLAYAN_LEVEL_KEY;

        LevelData.RespawnData respawnData = currentLevel.getRespawnData();
        ResourceKey<Level> targetDimensionKey = isLeavingCustomDim ? respawnData.dimension() : VWDimensions.NOLAYAN_LEVEL_KEY;

        ServerLevel targetLevel = currentLevel.getServer().getLevel(targetDimensionKey);
        if (targetLevel == null) {
            return null;
        }

        BlockPos nolayanPos = player.getAttachedOrElse(VWAttachments.LAST_NOLAYAN_POS, ServerLevel.END_SPAWN_POINT.above(14));
        BlockPos overworldPos = player.getAttachedOrElse(VWAttachments.LAST_OVERWORLD_POS, respawnData.pos());

        BlockPos baseSpawnPos = isLeavingCustomDim ? overworldPos : nolayanPos;

        Vec3 exactSpawnPos = Vec3.atBottomCenterOf(baseSpawnPos);
        float yRot, xRot;
        Set<Relative> relatives;

        if (isLeavingCustomDim) {
            // Save current Nolayan pos before leaving
            player.setAttached(VWAttachments.LAST_NOLAYAN_POS, player.blockPosition().above());

            //exactSpawnPos = Vec3.atBottomCenterOf(entity.adjustSpawnLocation(targetLevel, baseSpawnPos));
            exactSpawnPos = Vec3.atBottomCenterOf(baseSpawnPos);
            yRot = respawnData.yaw();
            xRot = respawnData.pitch();
            relatives = Relative.union(Relative.DELTA, Relative.ROTATION);
        } else {
            yRot = Direction.NORTH.toYRot();
            xRot = 0f;
            relatives = Relative.union(Relative.DELTA, Set.of(Relative.X_ROT));

            BlockPos platformCenter = BlockPos.containing(exactSpawnPos).below();
            BlockPos portalPos = platformCenter.north();

            boolean createPlatform = !targetLevel.getBlockState(portalPos.above()).is(Blocks.AIR)
                    && !targetLevel.getBlockState(portalPos).is(Blocks.AIR);

            boolean firstEntrance = !entity.getAttachedOrElse(VWAttachments.HAS_ENTERED_NOLAYAN, false);

            if (firstEntrance && createPlatform) {
                targetLevel.setBlockAndUpdate(portalPos, Blocks.AIR.defaultBlockState());
                targetLevel.setBlockAndUpdate(portalPos.above(), Blocks.AIR.defaultBlockState());
                targetLevel.setBlockAndUpdate(portalPos.below(), Blocks.GRASS_BLOCK.defaultBlockState());
            }

            exactSpawnPos = exactSpawnPos.subtract(0, 1, 0);

            // Save current overworld pos before leaving
            player.setAttached(VWAttachments.LAST_OVERWORLD_POS, player.blockPosition());
        }

        return new TeleportTransition(
                targetLevel,
                exactSpawnPos,
                Vec3.ZERO,
                yRot,
                xRot,
                relatives,
                TeleportTransition.PLAY_PORTAL_SOUND
                        .then(TeleportTransition.PLACE_PORTAL_TICKET)
        );
    }
}
