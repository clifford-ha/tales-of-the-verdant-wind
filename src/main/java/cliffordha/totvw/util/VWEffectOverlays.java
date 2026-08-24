package cliffordha.totvw.util;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.VWConfig;
import cliffordha.totvw.registry.VWColors;
import cliffordha.totvw.registry.VWEffects;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Player;

public final class VWEffectOverlays {
    private static final float BLOODLUST_PULSE_SPEED_MS = (float) (Math.PI * 2.0 / 2000.0);

    private VWEffectOverlays() {}

    public static void register() {
        HudElementRegistry.addFirst(
                register("bloodlust_overlay"),
                VWEffectOverlays::bloodlustOverlay
        );
        HudElementRegistry.addFirst(
                register("paralyze_overlay"),
                VWEffectOverlays::paralyzeOverlay
        );
    }
    private static int getWidth() {return Minecraft.getInstance().getWindow().getGuiScaledWidth();}
    private static int getHeight() {return Minecraft.getInstance().getWindow().getGuiScaledHeight();}

    private static void paralyzeOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (overlaysDisabled()) return;
        if (player() == null) return;
        if (notSurvival()) return;

        if (player().hasEffect(VWEffects.PARALYZE)) {
            graphics.fill(0, 0, getWidth(), getHeight(), ARGB.color(0.75f, 0x000000));
        }
    }

    private static void bloodlustOverlay(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!allowBloodlustOverlay()) return;
        if (player() == null) return;
        if (notSurvival()) return;

        if (player().hasEffect(VWEffects.BLOODLUST)) {
            var playerHealth = player().getHealth();
            float alpha = getAlpha(playerHealth);
            int a = (int) (alpha * 255) & 0xFF;

            graphics.fill(0, 0, getWidth(), getHeight(), ARGB.color(a, VWColors.BLOODLUST_EFFECT));
        }
    }

    private static float getAlpha(float playerHealth) {
        var playerMaxHealth = player().getMaxHealth();

        float max;
        if (playerHealth <= playerMaxHealth * 0.2) {
            max = 0.55f;
        } else if (playerHealth <= playerMaxHealth * 0.35) {
            max = 0.40f;
        } else if (playerHealth <= playerMaxHealth * 0.5) {
            max = 0.25f;
        } else if (playerHealth <= playerMaxHealth * 0.75) {
            max = 0.15f;
        } else {
            max = 0.2f;
        }

        float sine = Mth.sin(Util.getMillis() * BLOODLUST_PULSE_SPEED_MS);
        float t = sine * 0.5f + 0.5f;
        return Mth.lerp(t, 0, max);
    }
    private static boolean overlaysDisabled() {
        return !VWConfig.get().CLIENT_ALLOW_EFFECT_OVERLAYS;
    }
    private static boolean allowBloodlustOverlay() {
        if (overlaysDisabled()) return false;
        return VWConfig.get().CLIENT_BLOODLUST_EFFECT_OVERLAY;
    }
    private static Player player() {
        return Minecraft.getInstance().player;
    }
    private static boolean notSurvival() {
        return player().isCreative() || player().isSpectator();
    }

    private static Identifier register(String name) {
        return Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, name);
    }
}