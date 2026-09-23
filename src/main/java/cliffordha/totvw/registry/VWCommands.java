package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.entity.PlayerAttachment;
import cliffordha.totvw.registry.attachments.entity.WolfAttachment;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import oshi.util.tuples.Pair;

import java.util.*;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class VWCommands {
    private static final AttachmentType<List<Pair<String, UUID>>> TRUST_DATA = PlayerAttachment.TRUSTED_PLAYERS;
    private static final AttachmentType<List<Pair<String, UUID>>> WOLF_TRUST_DATA = WolfAttachment.TRUSTED_PLAYERS;
    private static final AttachmentType<List<UUID>> AGGRESSOR_LIST = WolfAttachment.AGGRESSOR_LIST;

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("trust_data")
                    .then(Commands.literal("query")
                    .executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        int data = player.getAttachedOrElse(TRUST_DATA, List.of()).size();
                        if (data > 0) {
                            sendSuccess(context, false, "There are currently " + data + " player" + (data > 1 ? "s" : "") + " you trust.");
                            return data;
                        } else {
                            sendFail(context, VWColors.GRAY, "You don't have any trusted players in your list.");
                            return 0;
                        }
                    }
                    ))));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("trust_data")
                    .then(Commands.literal("remove")
                    .then(Commands.argument("entity", IntegerArgumentType.integer(1))
                    .executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        int index = context.getArgument("entity", Integer.class);
                        List<Pair<String, UUID>> data = new ArrayList<>(player.getAttachedOrElse(TRUST_DATA, List.of()));

                        if (!data.isEmpty()) {
                            if (index < 1 || index > data.size()) {
                                sendFail(context, "The index you provided is out of range. (1-" + data.size() + ")");
                                return 0;
                            }

                            Pair<String, UUID> removed = data.get(index - 1);
                            sendSuccess(context, false, "You removed " + removed.getA() + " from your list of trusted players.");
                            data.remove(index - 1);
                            player.setAttached(TRUST_DATA, data);
                            return data.size();
                        } else {
                            sendFail(context, "No trust data to remove.");
                            return 0;
                        }
                    }
                    )))));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("trust_data")
                    .then(Commands.literal("remove")
                    .then(Commands.literal("all")
                    .executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        int data = player.getAttachedOrElse(TRUST_DATA, List.of()).size();
                        if (data > 0) {
                            player.removeAttached(TRUST_DATA);
                            sendSuccess(context, false, "You removed your list of trusted players (" + data + ").");
                            return data;
                        } else {
                            sendFail(context, "No trust data to remove.");
                            return 0;
                        }
                    }
                    )))));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("trust_data")
                    .then(Commands.literal("wolf")
                    .then(Commands.literal("removeAggressorList")
                    .executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        ServerLevel level = player.level();
                        List<Wolf> wolves = level.getEntities(
                                EntityTypes.WOLF,
                                player.getBoundingBox().inflate(16),
                                t -> t.getOwner() == player);

                        if (wolves.isEmpty()) {
                            sendFail(context, "No nearby tamed wolves to update!");
                            return 0;
                        }

                        for (Wolf wolf : wolves) {
                            wolf.removeAttached(AGGRESSOR_LIST);
                        }

                        String w = wolves.size() > 1 ? "nearby wolves" : wolves.getFirst().getPlainTextName();
                        sendSuccess(context, false, "Removed the aggressor list of " + w + ".");
                        return wolves.size();
                    }
                    )))));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("trust_data")
                    .then(Commands.literal("wolf")
                    .then(Commands.literal("sync")
                    .executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }
                        ServerLevel level = player.level();

                        List<Pair<String, UUID>> trustData = player.getAttachedOrElse(TRUST_DATA, List.of());

                        List<Wolf> wolves = level.getEntities(
                                EntityTypes.WOLF,
                                player.getBoundingBox().inflate(16),
                                t -> t.getOwner() == player);

                        if (wolves.isEmpty()) {
                            sendFail(context, "No nearby wolves to sync to!");
                            return 0;
                        }

                        int aggressors = 0;
                        for (Wolf wolf : wolves) {
                            if (!trustData.isEmpty()) {
                                wolf.setAttached(WOLF_TRUST_DATA, trustData);

                                AttachmentType<List<UUID>> AGGRESSOR_LIST = WolfAttachment.AGGRESSOR_LIST;
                                List<UUID> aggressorList = new ArrayList<>(wolf.getAttachedOrElse(AGGRESSOR_LIST, List.of()));

                                for (Pair<String, UUID> pair : trustData) {
                                    if (aggressorList.contains(pair.getB())) {
                                        aggressors++;
                                        aggressorList.remove(pair.getB());
                                        wolf.setAttached(AGGRESSOR_LIST, aggressorList);
                                    }
                                }
                            } else {
                                wolf.removeAttached(WOLF_TRUST_DATA);
                            }
                        }

                        String wolfCount = wolves.size() > 1 ? "nearby wolves" : wolves.getFirst().getPlainTextName();
                        String removedAggressors = aggressors > 0 ? " and removed " + aggressors + " aggressor" + (aggressors > 1 ? "s" : "") + "." : ".";
                        String count = "Synced " + trustData.size() + " trusted players to " + wolfCount + removedAggressors;

                        String finalize = trustData.isEmpty() ? "Removed old data from " + wolfCount : count;

                        sendSuccess(context, false, finalize);
                        return wolves.size();
                    }
                    )))));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("enchantments_handbook").executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }
                        int getStat = player.getAttachedOrElse(PlayerAttachment.RECEIVED_ENCHANTMENTS_HANDBOOK, 0);
                        if (getStat < 1) {
                            giveOrDropHandbook(player, 0);
                        } else {
                            if (player.isCreative() || player.isSpectator()) {
                                giveOrDropHandbook(player, 0);
                            } else {
                                handbookCopy(context);
                            }
                        }
                        return getStat;
                    }
                    )));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("effects_handbook").executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }
                        int getStat = player.getAttachedOrElse(PlayerAttachment.RECEIVED_EFFECTS_HANDBOOK, 0);
                        if (getStat < 1) {
                            giveOrDropHandbook(player, 1);
                        } else {
                            if (player.isCreative() || player.isSpectator()) {
                                giveOrDropHandbook(player, 1);
                            } else {
                                handbookCopy(context);
                            }
                        }
                        return getStat;
                    }
                    )));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("items_handbook").executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        int getStat = player.getAttachedOrElse(PlayerAttachment.RECEIVED_ITEMS_HANDBOOK, 0);
                        if (getStat < 1) {
                            giveOrDropHandbook(player, 2);
                        } else {
                            if (player.isCreative() || player.isSpectator()) {
                                giveOrDropHandbook(player, 2);
                            } else {
                                handbookCopy(context);
                            }
                        }
                        return getStat;
                    }
                    )));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("features_handbook").executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        int getStat = player.getAttachedOrElse(PlayerAttachment.RECEIVED_FEATURES_HANDBOOK, 0);
                        if (getStat < 1) {
                            giveOrDropHandbook(player, 3);
                        } else {
                            if (player.isCreative() || player.isSpectator()) {
                                giveOrDropHandbook(player, 3);
                            } else {
                                handbookCopy(context);
                            }
                        }
                        return getStat;
                    }
                    )));

            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("get_atrocity_count").executes(context -> {
                                ServerPlayer player = getPlayer(context);
                                if (player == null) {
                                    failed(context);
                                    return 0;
                                }

                                int villager = player.getAttachedOrElse(PlayerAttachment.VILLAGER_ATROCITY_COUNT, 0);
                                int wolf = player.getAttachedOrElse(PlayerAttachment.WOLF_ATROCITY_COUNT, 0);
                                if ((villager + wolf) < 1) {
                                    context.getSource().sendSystemMessage(Component.literal("You don't have any atrocity count."));
                                } else {
                                    context.getSource().sendSuccess(() -> Component.literal("Wolf: " + wolf + "  |  Villager: " + villager), true);
                                }
                                return 1;
                            }
                    )));
        });


        if (!TOTVW.IN_DEVELOPMENT) return;
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("place-village-pools")
                    .then(Commands.argument("root", IntegerArgumentType.integer(0, 1))
                    .then(Commands.argument("type", BoolArgumentType.bool())
                    .then(Commands.argument("folder", StringArgumentType.string())
                            .executes(context -> {
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
                    ))))
            );
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("tame_nearby_wolves").executes(context -> {
                        ServerPlayer player = getPlayer(context);
                        if (player == null) {
                            failed(context);
                            return 0;
                        }

                        ServerLevel level = player.level();
                        List<Wolf> wolves = level.getEntities(EntityTypes.WOLF,
                                player.getBoundingBox().inflate(32),
                                wolf -> wolf.isTame() && wolf.getUUID() != player.getUUID());

                        if (wolves.isEmpty()) {
                            context.getSource().sendFailure(Component.literal("No nearby wolves to tame!"));
                            return 0;
                        }

                        for (Wolf wolf : wolves) {
                            wolf.setOwner(player);
                        }
                        context.getSource().sendSuccess(() -> Component.literal("Tamed " + wolves.size() + " nearby wolves."), true);
                        return wolves.size();
                    }
                    )));
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("release_tamed_wolves").executes(context -> {
                                ServerPlayer player = getPlayer(context);
                                if (player == null) {
                                    failed(context);
                                    return 0;
                                }

                                ServerLevel level = player.level();
                                List<Wolf> wolves = level.getEntities(EntityTypes.WOLF,
                                        player.getBoundingBox().inflate(32),
                                        wolf -> wolf.isTame() && wolf.getUUID() == player.getUUID());

                                if (wolves.isEmpty()) {
                                    context.getSource().sendFailure(Component.literal("No nearby wolves to un-tame!"));
                                    return 0;
                                }

                                for (Wolf wolf : wolves) {
                                    wolf.setOwner(null);
                                    wolf.setTame(false, true);
                                }
                                context.getSource().sendSuccess(() -> Component.literal("Un-tamed " + wolves.size() + " nearby wolves."), true);
                                return wolves.size();
                            }
                    )));
        });
    }

    private static void giveOrDropHandbook(ServerPlayer player, int toGive) {
        ItemStack mainHand = player.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack handbook;
        AttachmentType<Integer> handbookType;
        String handbookName;

        switch (toGive) {
            case 0 -> {
                handbook = new ItemStack(VWItems.Pages.ENCHANTMENTS_HANDBOOK);
                handbookType = PlayerAttachment.RECEIVED_ENCHANTMENTS_HANDBOOK;
                handbookName = "Enchantments";
            }
            case 1 -> {
                handbook = new ItemStack(VWItems.Pages.EFFECTS_HANDBOOK);
                handbookType = PlayerAttachment.RECEIVED_EFFECTS_HANDBOOK;
                handbookName = "Effects";
            }
            case 2 -> {
                handbook = new ItemStack(VWItems.Pages.ITEMS_HANDBOOK);
                handbookType = PlayerAttachment.RECEIVED_ITEMS_HANDBOOK;
                handbookName = "Items";
            }
            default -> {
                handbook = new ItemStack(VWItems.Pages.FEATURES_HANDBOOK);
                handbookType = PlayerAttachment.RECEIVED_FEATURES_HANDBOOK;
                handbookName = "Features";
            }
        }

        Inventory inv = player.getInventory();
        boolean hasItem = inv.contains(handbook);
        if (hasItem) {
            sendToChat(player, false, "You already have the " + handbookName + " Handbook!");
        } else if (mainHand.isEmpty()) {
            player.setItemSlot(EquipmentSlot.MAINHAND, handbook);
        } else {
            int slot = inv.getFreeSlot();
            if (slot < 1) {
                player.spawnAtLocation(player.level(), handbook);
            } else {
                inv.add(slot, handbook);
            }
        }

        player.setAttached(handbookType, 1);
    }

    private static int placeTaigaVillagePools(CommandSourceStack source, int type, String folder) {
        ServerPlayer player = getPlayer(source);
        if (player == null) {
            failed(source);
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

        int finalPlaced = 0;
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
                //totalPlaced++;
                placedAny = true;
            }

            if (placedAny) {
                sendSuccess(source, false, entry.getKey().identifier().getPath() + " — " + poolTemplates.size() + " piece(s)");
                zCursor += POOL_SPACING;
            }
        }

        sendSuccess(source, false, "Placed " + finalPlaced + " structure(s) from " + taigaPools.size() + " taiga village template pool(s).");
        return finalPlaced;
    }

    private static ServerPlayer getPlayer(CommandContext<CommandSourceStack> context) {
        ServerPlayer player;
        try {
            player = context.getSource().getPlayerOrException();
        } catch (Exception e) {
            context.getSource().sendFailure(Component.literal("This command must be run by a player."));
            return null;
        }
        return player;
    }
    private static ServerPlayer getPlayer(CommandSourceStack source) {
        ServerPlayer player;
        try {
            player = source.getPlayerOrException();
        } catch (Exception e) {
            source.sendFailure(Component.literal("This command must be run by a player."));
            return null;
        }
        return player;
    }
    private static void handbookCopy(CommandContext<CommandSourceStack> c) {
        sendSuccess(c, false, "You can request another copy after your next respawn.");
    }
    private static void sendSuccess(CommandContext<CommandSourceStack> c, boolean broadcast, String msg) {
        c.getSource().sendSuccess(() -> Component.literal(msg), broadcast);
    }
    private static void sendSuccess(CommandSourceStack s, boolean broadcast, String msg) {
        s.sendSuccess(() -> Component.literal(msg), broadcast);
    }
    private static void sendFail(CommandContext<CommandSourceStack> c, String msg) {
        c.getSource().sendFailure(Component.literal(msg));
    }
    private static void sendFail(CommandContext<CommandSourceStack> c, int color, String msg) {
        c.getSource().sendFailure(Component.literal(msg).withColor(color));
    }
    private static void failed(CommandContext<CommandSourceStack> c) {
        failed(c.getSource());
    }
    private static void failed(CommandSourceStack c) {
        c.sendFailure(Component.literal("Failed to execute command."));
    }
}