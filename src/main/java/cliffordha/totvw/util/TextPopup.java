package cliffordha.totvw.util;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.attachments.PlayerPrefs;
import com.mojang.math.Transformation;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Brightness;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.level.storage.TagValueInput;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class TextPopup {
    private static final AttachmentType<Integer> LIFETIME = AttachmentRegistry.create(TOTVW.registerID("text_display_lifetime"));
    private static final AttachmentType<Integer> TEXT_DISPLAY = AttachmentRegistry.create(
            TOTVW.registerID("text_display"),
            builder -> builder.persistent(Codec.INT).initializer(() -> 0)
    );

    private static int lifetime() {
        return ExtUtil.random.nextIntBetweenInclusive(17, 33);
    }
    private static final double RISE_PER_TICK = 0.04;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> unloadPrevious(server, null));
        ServerLevelEvents.UNLOAD.register((server, level) -> unloadPrevious(null, level));
        if (!VWConfig.get().SERVER_SHOW_EFFECT_INDICATORS) return;
        ServerTickEvents.END_SERVER_TICK.register(server -> server.getAllLevels().forEach(
                level -> level.getEntities(EntityTypes.TEXT_DISPLAY, test -> test.hasAttached(TEXT_DISPLAY)).forEach(
                        entity -> {
                            int lifetime = entity.getAttachedOrElse(LIFETIME, 0);
                            if (lifetime <= 0) {
                                entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
                                return;
                            }
                            entity.setAttached(LIFETIME, lifetime - 1);
                            double speed = entity.getText().getString().contains("^") ? 0.4 : 0;
                            entity.setPos(entity.getX(), entity.getY() + RISE_PER_TICK + speed, entity.getZ());
                        }
                )
        ));
    }
    private static void unloadPrevious(@Nullable MinecraftServer server, ServerLevel serverLevel) {
        if (serverLevel != null) {
            serverLevel.getEntities(EntityTypes.TEXT_DISPLAY, test -> test.hasAttached(TEXT_DISPLAY)).forEach(
                    entity -> entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK)
            );
        }
        if (server != null) {
            server.getAllLevels().forEach(level -> level.getEntities(EntityTypes.TEXT_DISPLAY, test -> test.hasAttached(TEXT_DISPLAY)).forEach(
                    entity -> entity.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK)
            ));
        }
    }

    public static void showNum(Wolf caller, LivingEntity target, int value, int rgb, float size) {
        showWolfMain(caller, target, value + "", rgb, size, true);
    }
    public static void showBold(Wolf caller, LivingEntity target, String value, int rgb, float size) {
        showWolfMain(caller, target, value, rgb, size, true);
    }
    public static void showBold(Wolf caller, LivingEntity target, String value, float size) {
        showWolfMain(caller, target, value, VWColors.DEFAULT, size, true);
    }
    public static void showBold(Wolf caller, String value, float size) {
        showWolfMain(caller, caller, value, VWColors.DEFAULT, size, true);
    }
    public static void showBold(Wolf caller, String value) {
        showWolfMain(caller, caller, value, VWColors.DEFAULT, 1.0f, true);
    }
    public static void show(Wolf caller, LivingEntity target, String value, int rgb, float size) {
        showWolfMain(caller, target, value, rgb, size, false);
    }
    public static void show(Wolf caller, LivingEntity target, String value, float size) {
        showWolfMain(caller, target, value, VWColors.DEFAULT, size, false);
    }
    public static void show(Wolf caller, LivingEntity target, String value) {
        showWolfMain(caller, target, value, VWColors.DEFAULT, 1.0f, false);
    }
    public static void show(Wolf caller, String value, float size) {
        showWolfMain(caller, caller, value, VWColors.DEFAULT, size, false);
    }
    public static void show(Wolf caller, LivingEntity target, String value, int rgb) {
        showWolfMain(caller, target, value, rgb, 1.0f, false);
    }
    public static void show(Wolf caller, String value) {
        showWolfMain(caller, caller, value, VWColors.DEFAULT, 1.0f, false);
    }

    public static void show(LivingEntity target, String value, int rgb, float size) {
        showOtherMain(target, value, rgb, size, false);
    }
    public static void show(LivingEntity target, String value, int rgb) {
        showOtherMain(target, value, rgb, 1.0f, false);
    }
    public static void showBold(LivingEntity target, String value, int rgb, float size) {
        showOtherMain(target, value, rgb, size, true);
    }
    public static void showBold(LivingEntity target, String value, float size) {
        showOtherMain(target, value, VWColors.DEFAULT, size, true);
    }

    private static CompoundTag template() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", "minecraft:text_display");
        tag.putInt("teleport_duration", 3);
        tag.putInt("background", 0);
        tag.putBoolean("shadow", true);
        return tag;
    }
    private static void showWolfMain(Wolf caller, LivingEntity target, String name, int rgb, float size, boolean bold) {
        if (!(caller.getOwner() instanceof LivingEntity owner)) return;
        if (caller.distanceTo(owner) > 32) return;
        if (!owner.getAttachedOrElse(PlayerPrefs.SHOW_WOLF_EFFECT_INDICATORS, VWConfig.get().SYNCED_SHOW_WOLF_EFFECT_INDICATORS)) return;
        showMain(target, name, rgb, size, bold);
    }
    private static void showOtherMain(LivingEntity target, String name, int rgb, float size, boolean bold) {
        showMain(target, name, rgb, size, bold);
    }
    private static void showMain(LivingEntity target, String name, int rgb, float size, boolean bold) {
        if (!VWConfig.get().SERVER_SHOW_EFFECT_INDICATORS) return;
        if (!(target.level() instanceof ServerLevel level)) return;

        TagValueInput input = (TagValueInput) TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), template());
        Optional<Entity> created = EntityType.create(input, level, new EntitySpawnRequest(EntitySpawnReason.LOAD, false));

        created.ifPresent(e -> {
            if (!(e instanceof Display.TextDisplay text)) return;

            Component textValue = Component.literal(name).withColor(rgb);
            if (bold) {
                textValue = textValue.copy().withStyle(ChatFormatting.BOLD);
            }
            text.setText(textValue);

            text.setTransformation(new Transformation(null, null, new Vector3f(size, size, size), null));
            text.setBrightnessOverride(new Brightness(15, 15));
            text.setBillboardConstraints(Display.BillboardConstraints.CENTER);
            text.setGlowingTag(true);

            double d1 = ExtUtil.random.nextDouble() * (target.getBbWidth() * 1.3D) - (target.getBbWidth());
            double d2 = ExtUtil.random.nextDouble() + ExtUtil.random.nextDouble() * (target.getBbWidth() * 1.3D) - (target.getBbWidth());
            double ypos = target.getY() + (target.getBbHeight() * Math.clamp(level.getRandom().nextDouble(), 0.4, 0.8));
            ypos = name.contains("^") ? target.getY() - 2 : ypos;

            text.setPos(target.getX() + d2, ypos, target.getZ() + d1);

            text.setAttached(LIFETIME, lifetime());
            text.setAttached(TEXT_DISPLAY, 0);

            level.addFreshEntity(text);
        });
    }
}