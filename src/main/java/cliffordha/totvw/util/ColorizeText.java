package cliffordha.totvw.util;

import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ColorizeText {
    private static Map<String, Integer> KEY_COLORS = new HashMap<>();
    private ColorizeText() {}

    public static void register(int rgb, List<String> keys) {
        for (String key : keys) {
            KEY_COLORS.put(key, rgb);
        }
    }
    public static void register(int rgb, String key) {
        KEY_COLORS.put(key, rgb);
    }

    @Nullable
    public static Integer getColor(String translationKey) {
        return KEY_COLORS.get(translationKey);
    }
}