package cliffordha.totvw.util;

public class VWTimer {
    public static int tickSeconds(int sec) {
        return sec * 20;
    }
    public static int tickMinutes(int min) {
        return min * tickSeconds(60);
    }
    public static int tickDuration(int min, int sec) {
        return tickMinutes(min) + tickSeconds(sec);
    }
    public static int minToSec(int min) {
        return min * 60;
    }
}
