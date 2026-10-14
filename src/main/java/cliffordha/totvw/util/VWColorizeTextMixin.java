package cliffordha.totvw.util;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// mixin
public final class VWColorizeTextMixin {

    private static final Map<String, Integer> KEY_COLORS = new HashMap<>();

    private VWColorizeTextMixin() {}

    public static void register(int rgb, List<String> keys) {
        for (String key : keys) {
            KEY_COLORS.put(key, rgb);
        }
    }

    @Nullable
    public static Integer getColor(String translationKey) {
        return KEY_COLORS.get(translationKey);
    }
}