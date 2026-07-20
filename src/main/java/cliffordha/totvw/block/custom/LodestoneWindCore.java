package cliffordha.totvw.block.custom;

import cliffordha.totvw.registry.*;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.Nullable;

import java.util.List;

import static cliffordha.totvw.entity.skill.VWSkillProcessor.notifyFromPlayer;
import static cliffordha.totvw.util.VWGlobalUtil.addEffect;

public class LodestoneWindCore extends Block {
    public static final MapCodec<LodestoneWindCore> CODEC = simpleCodec(LodestoneWindCore::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    public static final IntegerProperty MAX_ACTIVE_USE = IntegerProperty.create("max_active_use", 0, 600);

    @Override
    protected MapCodec<? extends Block> codec() {
        return CODEC;
    }

    public LodestoneWindCore(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        ItemStack itemStack = player.getMainHandItem();

        if (itemStack.is(VWItems.VERIXIUM_POWDER)) {
            BlockState change = state.cycle(ACTIVE);
            level.setBlockAndUpdate(pos, change);
            level.playSound(null, pos, VWSounds.NOTIFY, SoundSource.BLOCKS, 1.0F, 1.0F);
            String currentState = level.getBlockState(pos).getValue(ACTIVE) ? "activated" : "deactivated";
            notifyFromPlayer(player, VWColors.VERDANT_WIND, true, "Lodestone Wind Core has been " + currentState);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ACTIVE, false).setValue(MAX_ACTIVE_USE, 0).setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ACTIVE, MAX_ACTIVE_USE);
    }

    private static AABB scanner(BlockPos pos) {
        return new AABB(pos.getX() - 7, pos.getY() - 7, pos.getZ() - 7, pos.getX() + 7, pos.getY() + 7, pos.getZ() + 7);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (level.getGameTime() % 20 == 0) {
            if (state.getValue(ACTIVE) && state.getValue(MAX_ACTIVE_USE) > 0) {
                AABB test = scanner(pos);
                List<Wolf> wolves = level.getEntities(EntityType.WOLF, test, t -> t.isAlive() && t.distanceToSqr(pos.getX(), pos.getY(), pos.getZ()) < 8);
                if (!wolves.isEmpty()) {
                    for (Wolf wolf : wolves) {
                        addEffect(wolf, MobEffects.STRENGTH, 120, 0);
                        float randomPos = level.getRandom().nextFloat();
                        wolf.level().addParticle(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE, pos.getX() + randomPos, pos.getY() + 1, pos.getZ() + randomPos, 0.0D, 0.0D, 0.0D);
                    }
                }
                List<Monster> monsters = level.getEntitiesOfClass(Monster.class, test);
                if (!monsters.isEmpty()) {
                    for (Monster monster : monsters) {
                        if (!monster.getAttachedOrElse(VWAttachments.ENTITY_HAS_VERDANT_OMEN, false)) {
                            monster.setAttached(VWAttachments.ENTITY_HAS_VERDANT_OMEN, true);
                            VWParticleEffects.triggerMightParalyzeParticles(monster, 4);
                            int remaining = state.getValue(MAX_ACTIVE_USE);
                            state.setValue(MAX_ACTIVE_USE, remaining - 3);
                        }
                    }
                }
                List<Player> players = level.getEntitiesOfClass(Player.class, test);
                if (!players.isEmpty()) {
                    for (Player player : players) {
                        notifyFromPlayer(player, VWColors.VERDANT_WIND, true, "Remaining uses: " + state.getValue(MAX_ACTIVE_USE));
                    }
                }
            }
        }
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE) && level.getRandom().nextDouble() == 0.33) {
            AABB test = scanner(pos);
            List<Monster> monsters = level.getEntitiesOfClass(Monster.class, test);
            for (Monster monster : monsters) {
                addEffect(monster, MobEffects.WEAKNESS, 120, 0);
                int remaining = state.getValue(MAX_ACTIVE_USE);
                state.setValue(MAX_ACTIVE_USE, remaining - 5);
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ACTIVE)) {
            float randomPos = level.getRandom().nextFloat();
            level.addParticle(VWParticles.VERIXIUM_POWDER_RAIN_PARTICLE, pos.getX() + randomPos, pos.getY() + 1, pos.getZ() + randomPos, 0.0D, 0.0D, 0.0D);
        }
    }
}
