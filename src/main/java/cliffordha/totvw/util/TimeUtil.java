package cliffordha.totvw.util;

public class TimeUtil {
    public static final int SEC = 20;
    public static final int MIN = SEC * 60;
    public static int sec(int sec) {
        return sec * SEC;
    }
    public static int min(int min) {
        return min * MIN;
    }
    public static int duration(int min, int sec) {
        return min(min) + sec(sec);
    }
}
