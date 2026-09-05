package cliffordha.totvw.registry;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.attachments.VWAttachments;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static cliffordha.totvw.util.VWUtil.sendToChat;

public class VWCommands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("enchantments_handbook").executes(context -> {
                        ServerPlayer player;
                        try {
                            player = context.getSource().getPlayerOrException();
                        } catch (Exception e) {
                            context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                            return 0;
                        }
                        int getStat = player.getAttachedOrElse(VWAttachments.player.PLAYER_RECEIVED_ENCHANTMENTS_HANDBOOK, 0);
                        if (getStat < 1) {
                            giveOrDropHandbook(player, 0);
                        } else {
                            if (player.isCreative() || player.isSpectator()) {
                                giveOrDropHandbook(player, 0);
                            } else {
                                context.getSource().sendSuccess(() -> Component.literal("You can request another copy after your next respawn."), true);
                            }
                        }
                        return getStat;
                    }
                    ))
            );
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("effects_handbook").executes(context -> {
                        ServerPlayer player;
                        try {
                            player = context.getSource().getPlayerOrException();
                        } catch (Exception e) {
                            context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                            return 0;
                        }
                        int getStat = player.getAttachedOrElse(VWAttachments.player.PLAYER_RECEIVED_EFFECTS_HANDBOOK, 0);
                        if (getStat < 1) {
                            giveOrDropHandbook(player, 1);
                        } else {
                            if (player.isCreative() || player.isSpectator()) {
                                giveOrDropHandbook(player, 1);
                            } else {
                                context.getSource().sendSuccess(() -> Component.literal("You can request another copy after your next respawn."), true);
                            }
                        }
                        return getStat;
                    }
                    ))
            );
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("items_handbook").executes(context -> {
                                ServerPlayer player;
                                try {
                                    player = context.getSource().getPlayerOrException();
                                } catch (Exception e) {
                                    context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                                    return 0;
                                }
                                int getStat = player.getAttachedOrElse(VWAttachments.player.PLAYER_RECEIVED_ITEMS_HANDBOOK, 0);
                                if (getStat < 1) {
                                    giveOrDropHandbook(player, 2);
                                } else {
                                    if (player.isCreative() || player.isSpectator()) {
                                        giveOrDropHandbook(player, 2);
                                    } else {
                                        context.getSource().sendSuccess(() -> Component.literal("You can request another copy after your next respawn."), true);
                                    }
                                }
                                return getStat;
                            }
                    ))
            );
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("features_handbook").executes(context -> {
                                ServerPlayer player;
                                try {
                                    player = context.getSource().getPlayerOrException();
                                } catch (Exception e) {
                                    context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                                    return 0;
                                }
                                int getStat = player.getAttachedOrElse(VWAttachments.player.PLAYER_RECEIVED_FEATURES_HANDBOOK, 0);
                                if (getStat < 1) {
                                    giveOrDropHandbook(player, 3);
                                } else {
                                    if (player.isCreative() || player.isSpectator()) {
                                        giveOrDropHandbook(player, 3);
                                    } else {
                                        context.getSource().sendSuccess(() -> Component.literal("You can request another copy after your next respawn."), true);
                                    }
                                }
                                return getStat;
                            }
                    ))
            );
            dispatcher.register(Commands.literal("totvw")
                    .then(Commands.literal("get_atrocity_count").executes(context -> {
                                ServerPlayer player;
                                try {
                                    player = context.getSource().getPlayerOrException();
                                } catch (Exception e) {
                                    context.getSource().sendFailure(Component.literal("This command must be run by a player."));
                                    return 0;
                                }
                                int villager = player.getAttachedOrElse(VWAttachments.player.PLAYER_VILLAGER_ATROCITY_COUNT, 0);
                                int wolf = player.getAttachedOrElse(VWAttachments.player.PLAYER_WOLF_ATROCITY_COUNT, 0);
                                if ((villager + wolf) < 1) {
                                    context.getSource().sendSystemMessage(Component.literal("You don't have any atrocity count."));
                                } else {
                                    context.getSource().sendSuccess(() -> Component.literal("Wolf: " + wolf + "  |  Villager: " + villager), true);
                                }
                                return 1;
                            }
                    ))
            );
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
                        ServerPlayer player;
                        try {
                            player = context.getSource().getPlayerOrException();
                        } catch (Exception e) {
                            context.getSource().sendFailure(Component.literal("This command must be run by a player."));
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
                handbookType = VWAttachments.player.PLAYER_RECEIVED_ENCHANTMENTS_HANDBOOK;
                handbookName = "Enchantments";
            }
            case 1 -> {
                handbook = new ItemStack(VWItems.Pages.EFFECTS_HANDBOOK);
                handbookType = VWAttachments.player.PLAYER_RECEIVED_EFFECTS_HANDBOOK;
                handbookName = "Effects";
            }
            case 2 -> {
                handbook = new ItemStack(VWItems.Pages.ITEMS_HANDBOOK);
                handbookType = VWAttachments.player.PLAYER_RECEIVED_ITEMS_HANDBOOK;
                handbookName = "Items";
            }
            default -> {
                handbook = new ItemStack(VWItems.Pages.FEATURES_HANDBOOK);
                handbookType = VWAttachments.player.PLAYER_RECEIVED_FEATURES_HANDBOOK;
                handbookName = "Features";
            }
        }

        boolean hasItem = player.getInventory().contains(handbook);
        if (hasItem) {
            sendToChat(player, false, "You already have the " + handbookName + " Handbook!");
        } else if (mainHand.isEmpty()) {
            player.setItemSlot(EquipmentSlot.MAINHAND, handbook);
        } else {
            int slot = player.getInventory().getFreeSlot();
            if (slot < 1) {
                player.spawnAtLocation(player.level(), handbook);
            } else {
                player.getInventory().add(slot, handbook);
            }
        }

        player.setAttached(handbookType, 1);
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
                //totalPlaced++;
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
}