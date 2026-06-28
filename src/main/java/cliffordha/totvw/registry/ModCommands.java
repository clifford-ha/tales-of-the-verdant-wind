package cliffordha.totvw.registry;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.StringRepresentableArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ModCommands {
    private static final int MIN_Y = -64;
    private static final int MAX_Y = 48;

    private static final int SMALL_VEINS_PER_CHUNK = 8;
    private static final int LARGE_VEINS_PER_CHUNK = 2;

    private static final int SMALL_VEIN_SIZE = 12;
    private static final int LARGE_VEIN_SIZE = 24;

    public static void registerModCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(
                    Commands.literal("totvw")
                            .then(Commands.literal("repopulate-verixium")
                            .then(Commands.argument("chunkRadius", IntegerArgumentType.integer(0, 32))
                                    .executes(context -> {
                                        int radius = IntegerArgumentType.getInteger(context, "chunkRadius");
                                        return repopulateVerixium(context.getSource(), radius);
                                    }))
            ));
            dispatcher.register(
                    Commands.literal("totvw")
                            .then(Commands.literal("place-village-pools")
                                    .then(Commands.argument("root", IntegerArgumentType.integer(0, 1))
                                    .then(Commands.argument("type", BoolArgumentType.bool())
                                    .then(Commands.argument("folder", StringArgumentType.string())
                                    .executes(
                                            context -> {
                                                boolean type = context.getArgument("type", Boolean.class);
                                                String folder;
                                                if (!type) {
                                                    folder = StringArgumentType.getString(context, "folder");
                                                } else {
                                                    folder = "zombie/" + StringArgumentType.getString(context, "folder");
                                                }

                                                int root = IntegerArgumentType.getInteger(context, "root");
                                                return placeTaigaVillagePools(context.getSource(), root, folder);
                                            })
                                    ))
                            )));
        });
    }

    private static int placeTaigaVillagePools(CommandSourceStack source, int type, String folder) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0;
        }

        ServerLevel level = player.level();
        StructureTemplateManager templateManager = source.getServer().getStructureManager();
        Registry<StructureTemplatePool> poolRegistry = level.registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
        BlockPos origin = player.blockPosition().above(1);

        final int TEMPLATE_SPACING = 8;
        final int POOL_SPACING = 8;
        String root;
        String rootFolder;
        if (type == 0) {
            root = "village/taiga/";
            rootFolder = "minecraft";
        } else {
            root = "village/verdant/";
            rootFolder = "tales-of-the-verdant-wind";
        }

        List<Map.Entry<ResourceKey<StructureTemplatePool>, StructureTemplatePool>> taigaPools = poolRegistry.entrySet()
                .stream()
                .filter(e -> e.getKey().identifier().getNamespace().equals(rootFolder)
                        && e.getKey().identifier().getPath().startsWith(root + folder))
                .sorted(Comparator.comparing(e -> e.getKey().identifier().getPath()))
                .toList();

        int totalPlaced = 0;
        int zCursor = 0;

        for (Map.Entry<ResourceKey<StructureTemplatePool>, StructureTemplatePool> entry : taigaPools) {
            List<StructurePoolElement> poolTemplates = entry.getValue().templates;
            int xCursor = 0;
            boolean placedAny = false;

            for (StructurePoolElement element : poolTemplates) {
                if (!(element instanceof SinglePoolElement single)) continue;

                StructureTemplate template = single.getTemplate(templateManager);
                BlockPos pos = origin.offset(xCursor, -1, zCursor);
                StructurePlaceSettings settings = new StructurePlaceSettings();
                template.placeInWorld(level, pos, pos, settings, level.getRandom(), 2);

                Vec3i size = template.getSize();
                xCursor += Math.max(size.getX(), size.getZ()) + TEMPLATE_SPACING;
                totalPlaced++;
                placedAny = true;
            }

            if (placedAny) {
                source.sendSuccess(() -> Component.literal(
                        "  " + entry.getKey().identifier().getPath() + " — " + poolTemplates.size() + " piece(s)"
                ), false);
                zCursor += POOL_SPACING;
            }
        }

        final int finalPlaced = totalPlaced;
        source.sendSuccess(() -> Component.literal(
                "Placed " + finalPlaced + " structure(s) from " + taigaPools.size() + " taiga village template pool(s)."
        ), true);
        return totalPlaced;
    }

    private static int repopulateVerixium(CommandSourceStack source, int chunkRadius) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception exception) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return 0; }

        ServerLevel level = player.level();
        ChunkPos centerChunk = player.chunkPosition();

        int processedChunks = 0;
        int placedBlocks = 0;

        for (int chunkX = centerChunk.x() - chunkRadius; chunkX <= centerChunk.x() + chunkRadius; chunkX++) {
            for (int chunkZ = centerChunk.z() - chunkRadius; chunkZ <= centerChunk.z() + chunkRadius; chunkZ++) {
                if (!level.hasChunk(chunkX, chunkZ)) {
                    continue;
                }

                long chunkSeed = ((long) chunkX & 0xffffffffL) | (((long) chunkZ & 0xffffffffL) << 32);
                RandomSource random = RandomSource.create(level.getSeed() ^ chunkSeed ^ 0x5DEECE66DL);

                placedBlocks += repopulateChunk(level, chunkX, chunkZ, random);
                processedChunks++;
            }
        }

        int finalProcessedChunks = processedChunks;
        int finalPlacedBlocks = placedBlocks;

        source.sendSuccess(
                () -> Component.literal(
                        "Repopulated " + finalProcessedChunks + " chunks and placed " + finalPlacedBlocks + " Verixium ore blocks."
                ),
                true
        );

        return placedBlocks;
    }

    private static int repopulateChunk(ServerLevel level, int chunkX, int chunkZ, RandomSource random) {
        int placedBlocks = 0;

        for (int i = 0; i < LARGE_VEINS_PER_CHUNK; i++) {
            placedBlocks += tryPlaceVein(level, chunkX, chunkZ, random, LARGE_VEIN_SIZE);
        }

        for (int i = 0; i < SMALL_VEINS_PER_CHUNK; i++) {
            placedBlocks += tryPlaceVein(level, chunkX, chunkZ, random, SMALL_VEIN_SIZE);
        }

        return placedBlocks;
    }

    private static int tryPlaceVein(ServerLevel level, int chunkX, int chunkZ, RandomSource random, int veinSize) {
        int startX = chunkX * 16 + random.nextInt(16);
        int startY = MIN_Y + random.nextInt(MAX_Y - MIN_Y + 1);
        int startZ = chunkZ * 16 + random.nextInt(16);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(startX, startY, startZ);
        int placedBlocks = 0;

        for (int i = 0; i < veinSize; i++) {
            mutablePos.set(
                    startX + random.nextInt(7) - 3,
                    startY + random.nextInt(5) - 2,
                    startZ + random.nextInt(7) - 3
            );

            if (!level.isInWorldBounds(mutablePos)) {
                continue;
            }

            BlockState currentState = level.getBlockState(mutablePos);

            if (!canReplaceWithVerixiumOre(currentState)) {
                continue;
            }

            BlockState oreState = getOreForTarget(currentState);

            level.setBlock(mutablePos, oreState, Block.UPDATE_CLIENTS);
            placedBlocks++;
        }

        return placedBlocks;
    }

    private static boolean canReplaceWithVerixiumOre(BlockState state) {
        return state.is(Blocks.STONE)
                || state.is(Blocks.DEEPSLATE)
                || state.is(Blocks.TUFF);
    }

    private static BlockState getOreForTarget(BlockState targetState) {
        if (targetState.is(Blocks.DEEPSLATE)) {
            return ModBlocks.VERIXIUM_DEEPSLATE_ORE.defaultBlockState();
        } else if (targetState.is(Blocks.STONE)) {
            return ModBlocks.VERIXIUM_STONE_ORE.defaultBlockState();
        }
        return null;
    }
}