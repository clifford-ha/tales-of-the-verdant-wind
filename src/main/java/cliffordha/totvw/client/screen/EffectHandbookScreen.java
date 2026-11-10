package cliffordha.totvw.client.screen;

import cliffordha.totvw.TOTVW;
import cliffordha.totvw.registry.VWColors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;

import java.util.function.Supplier;

public class EffectHandbookScreen extends ScrollableTextScreen {
    private static final Identifier BLESSING = TOTVW.registerID("textures/mob_effect/blessing_of_the_verdant_wind.png");
    private static final Identifier AMPLIFIED_MIGHT = TOTVW.registerID("textures/mob_effect/amplified_might.png");
    private static final Identifier BLOODLUST = TOTVW.registerID("textures/mob_effect/bloodlust.png");
    private static final Identifier PARALYZE = TOTVW.registerID("textures/mob_effect/paralyze.png");
    private static final Identifier HAVOC = TOTVW.registerID("textures/mob_effect/havoc.png");
    private static final Identifier WIND_VEIL = TOTVW.registerID("textures/mob_effect/wind_veil.png");

    private static final int SIZE = 18;
    private static float FADE = 0.0f;

    private static final String[] effectNames = {"Blessing of the Verdant Wind", "Amplified Might", "Bloodlust", "Paralyze", "Havoc", "Wind Veil"};
    private final int effect;
    private final Screen parent;
    public static void create(Screen parent, Supplier<String> info, int effect) {
        Minecraft.getInstance().setScreenAndShow(new EffectHandbookScreen(parent, effectNames[effect], info, effect));
    }

    public EffectHandbookScreen(Screen callback, String pageTitle, Supplier<String> info, int type) {
        super(Component.literal(pageTitle), pageTitle, info);
        this.effect = type;
        this.parent = callback;
    }

    @Override
    public void tick() {
        float sine = Mth.sin(Util.getMillis() * (Math.PI * 2.0 / 5000.0));
        float t = sine * 0.5f + 0.5f;
        FADE = Mth.lerp(t, 0.3f, 0.7f);
        super.tick();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        Identifier ICON;
        int color;
        if (effect == 0) {
            ICON = BLESSING;
            color = VWColors.VERDANT_WIND;
        } else if (effect == 1) {
            ICON = AMPLIFIED_MIGHT;
            color = VWColors.MIGHT;
        } else if (effect == 2) {
            ICON = BLOODLUST;
            color = VWColors.BLOODLUST;
        } else if (effect == 3) {
            ICON = PARALYZE;
            color = VWColors.PARALYZE;
        } else if (effect == 4) {
            ICON = HAVOC;
            color = VWColors.RUNESTONE_HAVOC;
        } else {
            ICON = WIND_VEIL;
            color = VWColors.VERDANT_WIND;
        }
        int finalized = Mth.ceil(this.height * 0.5);
        int xpos = Mth.ceil(this.width * 0.5) - Mth.ceil(finalized * 0.5);
        int ypos = Mth.ceil(this.height * 0.5) - finalized / 2;
        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                ICON,
                xpos, ypos,
                0.0F, 0.0F,
                finalized, finalized,
                SIZE, SIZE,
                SIZE, SIZE
        );
        RandomSource random = RandomSource.create();
        color = (effect == 4 && Minecraft.getInstance().hasShiftDown() && random.nextFloat() < 0.03f) ? 0x00000000 : color;

        graphics.fillGradient(0, 0, this.width, this.height, ARGB.color(FADE, color), 0x00000000);
        graphics.fillGradient(0, 0, this.width, this.height, 0x00000000, ARGB.color(FADE, color));

        if (!Minecraft.getInstance().hasShiftDown()) {
            super.extractRenderState(graphics, mouseX, mouseY, a);

        } else {
            if (effect == 4 && random.nextFloat() < 0.13f) return;
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED,
                    ICON,
                    xpos, ypos,
                    0.0F, 0.0F,
                    finalized, finalized,
                    SIZE, SIZE,
                    SIZE, SIZE
            );
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreenAndShow(parent);
    }
}
