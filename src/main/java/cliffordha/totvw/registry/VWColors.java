package cliffordha.totvw.registry;

public class VWColors {
    private VWColors() {}

    public static final int VERDANT_WIND = 0x00FFD0;
    public static final int VERDANT_WIND_MUTED = shadeColor(VERDANT_WIND, -0.25f);

    public static final int MIGHT = 0x6EC5FF;
    public static final int MIGHT_MUTED = shadeColor(MIGHT, -0.25f);

    public static final int BLOODLUST = 0xFF2E4D;
    public static final int BLOODLUST_MUTED = shadeColor(BLOODLUST, -0.25f);

    public static final int ARCTIC_WINDS = 0xADEBFA;
    public static final int ARCTIC_WINDS_MUTED = shadeColor(ARCTIC_WINDS, -0.25f);

    public static final int GNAWING = shadeColor(0x56992F, 0.2f);
    public static final int GNAWING_MUTED = shadeColor(GNAWING, -0.25f);

    public static final int IGNITION = 0xCF2D1A;
    public static final int IGNITION_MUTED = shadeColor(IGNITION, -0.25f);

    public static final int POISON = 0x992D56;
    public static final int POISON_MUTED = shadeColor(POISON, -0.25f);

    public static final int WITHERING = 0x711E7B;
    public static final int WITHERING_MUTED = shadeColor(WITHERING, -0.25f);

    public static final int HAVOC_PARTICLE = 0xFF2236;
    public static final int HAVOC_PARTICLE_MUTED = shadeColor(HAVOC_PARTICLE, -0.25f);

    public static final int PARALYZE = 0x365258;
    public static final int PARALYZE_MUTED = shadeColor(PARALYZE, -0.25f);

    public static final int GRAY = 0x607D8B;
    public static final int GRAY_MUTED = shadeColor(GRAY, -0.25f);

    public static final int DEFAULT = 0xDCFAFA;
    public static final int DEFAULT_MUTED = shadeColor(DEFAULT, -0.25f);

    public static final int RUNESTONE_GENESIS = 0x20DFDF;
    public static final int RUNESTONE_SOUL = 0x5BE5BC;
    public static final int RUNESTONE_EFFLORESCENCE = 0xF9D2D2;
    public static final int RUNESTONE_TETHER = 0x207FDF;
    public static final int RUNESTONE_HAVOC = 0x9D2828;

    public static final int INDICATOR_20 = 0xb93636;
    public static final int INDICATOR_40 = 0xB97232;
    public static final int INDICATOR_60 = 0xadb94f;
    public static final int INDICATOR_80 = 0x4bb94f;
    public static final int INDICATOR_100 = 0x46B5B9;

    public static final int BLOCK_VERDANT_WOOD = 0x19281B;
    public static final int BLOCK_VERDANT_LEAVES = 0x0B604B;
    public static final int BLOCK_VERDANT_LOG = 0x517360;

    public static int setColor(int color) {
        return (0xff << 24) | color;
    }
    public static int setColor(int color, int alpha) {
        return (alpha << 24) | color;
    }
    public static int shadeColor(int color, float factor) {
        factor = Math.clamp(factor, -1.0f, 1.0f);

        int alpha = (color >> 24) & 0xFF;
        int red   = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue  = color & 0xFF;

        int finalR, finalG, finalB;

        if (factor >= 0) {
            finalR = (int) (red   + (255 - red)   * factor);
            finalG = (int) (green + (255 - green) * factor);
            finalB = (int) (blue  + (255 - blue)  * factor);
        } else {
            float darkenAmount = Math.abs(factor);
            finalR = (int) (red   - (red   * darkenAmount));
            finalG = (int) (green - (green * darkenAmount));
            finalB = (int) (blue  - (blue  * darkenAmount));
        }

        return (alpha << 24) | (finalR << 16) | (finalG << 8) | finalB;
    }
}