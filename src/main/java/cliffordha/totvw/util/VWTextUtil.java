package cliffordha.totvw.util;

import cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor;
import cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextColor.*;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.BOLD;
import static cliffordha.totvw.item.scatteredpages.ScatteredPageTextStyle.ITALIC;

public class VWTextUtil {
    // TEXT FORMATTING UTIL

    public static String iText(int value, int max, boolean invert) {
        return getString(value, max, invert);
    }
    public static String iText(float value, float max) {
        return getString(value, max, false);
    }
    public static String iText(float value, float max, boolean invert) {
        return getString(value, max, invert);
    }

    @NonNull
    private static String getString(float value, float max, boolean invert) {
        if (value < max * 0.2f) {
            return cText(invert ? RED : DARK_AQUA, "" + value);
        } else if (value < max * 0.4f) {
            return cText(invert ? GOLD : GREEN, "" + value);
        } else if (value < max * 0.6f) {
            return cText(YELLOW, "" + value);
        } else if (value < max * 0.8f) {
            return cText(invert ? GREEN : GOLD, "" + value);
        } else if (value < max) {
            return cText(invert ? DARK_AQUA : RED, "" + value);
        } else {
            return cText(invert ? AQUA : DARK_RED, "" + (double) value);
        }
    }

    /** colors text **/
    public static String cText(ScatteredPageTextColor color, String text) {
        if (color == null) return text;
        return color.getColor() + text + "§r";
    }

    /** a test-dependent text value
     * note: be careful when using ServerLevel tests **/
    public static String tText(boolean test, String isTrue, String isFalse) {
        return test ? isTrue : isFalse;
    }
    public static String tText(boolean test, boolean test2, String isTrue, String isFalse) {
        return tText(test, tText(test2, isTrue, isFalse));
    }
    public static String tText(boolean test, boolean test2, String isTrue, String isFalse, boolean nextLine) {
        if (nextLine) {
            String finalText = tText(test, test2, isTrue, isFalse);
            return finalText.isEmpty() ? "": finalText + VWTextUtil.nextLine;
        } else {
            return tText(test, test2, isTrue, isFalse);
        }
    }
    public static String tText(boolean test, String isTrue) {
        return test ? isTrue : "";
    }
    public static String tText(boolean test, String isTrue, boolean nextLine) {
        if (nextLine) {
            String finalText = tText(test, isTrue);
            return finalText.isEmpty() ? "": finalText + VWTextUtil.nextLine;
        }
        return test ? isTrue : "";
    }

    /** like a docx, format text **/
    public static String fText(ScatteredPageTextStyle formatter, String text) {
        return formatter.getMarker() + text + "§r";
    }

    /** date, what else **/
    public static String dText(int day, int month, int year) {
        String cDay = day < 10 ? "0" + day : String.valueOf(day);
        String cMonth = month < 10 ? "0" + month : String.valueOf(month);
        return cText(DARK_GRAY, fText(ITALIC, cDay + "/" + cMonth + "/" + year)) + nextLine;
    }

    /** convert and iterate every letter from the input text and turn it into a block **/
    public static String bText(String text) {
        return "▌".repeat(text.length());
    }

    /** a set of predefined text **/
    public static String pText(int p) {
        String predefinedText;
        switch (p) {
            case 1 -> predefinedText = "Some contents are intentionally omitted";
            case 2 -> predefinedText = "The text trails and ends here...";
            case 3 -> predefinedText = "Scribbled gibberish";
            case 4 -> predefinedText = "Some contents have faded";
            default -> predefinedText = "Error: Invalid Predefined Text or Null";
        }
        return cText(DARK_GRAY, "[" + predefinedText + "]") + nextParagraph;
    }
    public static String nText(String text) {
        return cText(DARK_GRAY, fText(ITALIC, "[" + text + "]"));
    }

    public static final String nextLine = " §f§f§f§r\n";

    /** why... **/
    public static final String nextParagraph = " \n §f§f§f§r \n";

    public static final String addSeparator = nextParagraph + nextParagraph;

    public static String addTitle(String title) {
        return fText(BOLD, title);
    }

    /** purely made for separating *pages visually, rip brain **/
    public static String[] addPage(String text) {
        final int lengthBound = 700;
        int charCount = text.length();
        List<String> pages = new ArrayList<>();
        int start = 0;

        while (start < charCount) {
            while (start < charCount && Character.isWhitespace(text.charAt(start))) {
                start++;
            }

            if (start >= charCount) {
                break;
            }

            int end = Math.min(start + lengthBound, charCount);

            if (end < charCount) {
                int split = end;

                while (split > start && !Character.isWhitespace(text.charAt(split - 1))) {
                    split--;
                }

                if (split > start) {
                    end = split;
                }
            }

            pages.add(text.substring(start, end).trim());
            start = end;
        }

        return pages.toArray(new String[0]);
    }
    public static String iterateList(List<String> list, boolean vertical) {
        if (list.isEmpty()) return "";

        List<String> sorted = list.stream().sorted().toList();
        String separator = vertical ? "\n" : ", ";

        return switch (sorted.size()) {
            case 1 -> sorted.getFirst();
            case 2 -> sorted.getFirst() + " and " + sorted.getLast();
            default -> String.join(separator, sorted.subList(0, sorted.size() - 1))
                    + separator + "and " + sorted.getLast();
        };
    }
    public static String iterateDecoratedList(List<String> list) {
        if (list.isEmpty()) return "  • None";
        return list.stream()
                .sorted()
                .map(s -> "  • " + s)
                .collect(Collectors.joining("\n"));
    }
    public static String intToRoman(int num) {
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] symbols = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

        StringBuilder roman = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            // Repeat the symbol while the number is greater than or equal to the value
            while (num >= values[i]) {
                roman.append(symbols[i]);
                num -= values[i];
            }
        }

        return roman.toString();
    }
}
