package cliffordha.totvw.util;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.config.TOTVWConfig;
import cliffordha.totvw.registry.ModColors;
import cliffordha.totvw.registry.ModEffects;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public final class ModEffectOverlays {
    private static final int RGB_BLOODLUST = ModColors.BLOODLUST_EFFECT;
    private static final float BLOODLUST_PULSE_SPEED_MS = (float) (Math.PI * 2.0 / 2000.0);

    private ModEffectOverlays() {}

    public static void register() {
        HudElementRegistry.addFirst(
                Identifier.fromNamespaceAndPath(TOTVW.MOD_ID, "bloodlust_effect_overlay"),
                ModEffectOverlays::bloodlustEffect
        );
    }

    private static void bloodlustEffect(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        if (!TOTVWConfig.get().bloodlustScreenOverlay) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.player.isCreative()) return;
        if (!mc.player.hasEffect(ModEffects.BLOODLUST)) return;

        var playerHealth = mc.player.getHealth();
        var playerMaxHealth = mc.player.getMaxHealth();

        float max;
        if (playerHealth <= playerMaxHealth * 0.2) {
            max = 0.7f;
        } else if (playerHealth <= playerMaxHealth * 0.35) {
            max = 0.55f;
        } else if (playerHealth <= playerMaxHealth * 0.5) {
            max = 0.35f;
        } else if (playerHealth <= playerMaxHealth * 0.75) {
            max = 0.25f;
        } else {
            max = 0.2f;
        }

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        float sine = Mth.sin(Util.getMillis() * BLOODLUST_PULSE_SPEED_MS);
        float t    = sine * 0.5f + 0.5f;
        float alpha = Mth.lerp(t, 0, max);
        int   a     = (int) (alpha * 255) & 0xFF;

        graphics.fill(0, 0, w, h, ARGB.color(a, RGB_BLOODLUST));
    }
}