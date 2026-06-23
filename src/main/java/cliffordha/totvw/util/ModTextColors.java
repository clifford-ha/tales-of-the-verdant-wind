package cliffordha.totvw.util;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

// mixin
public final class ModTextColors {

    private static final Map<String, Integer> KEY_COLORS = new HashMap<>();

    private ModTextColors() {}

    public static void register(String translationKey, int rgb) {
        KEY_COLORS.put(translationKey, rgb);
    }

    @Nullable
    public static Integer getColor(String translationKey) {
        return KEY_COLORS.get(translationKey);
    }
}