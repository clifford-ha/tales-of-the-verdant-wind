package cliffordha.totvw;

import net.minecraft.server.level.ServerLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TOTVW {
    public static final String MOD_ID = "tales-of-the-verdant-wind";
    public static final String MOD_NAME = "Tales of the Verdant Wind";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void sendClassRegisterLog(String className) {
        TOTVW.LOGGER.info("{} registered!", className);
    }
    public static void sendInfo(String info) {
        TOTVW.LOGGER.info(info);
    }
    public static void sendWarning(String error) {
        TOTVW.LOGGER.warn(error);
    }
    public static void sendWarning(String error, Object info) {
        TOTVW.LOGGER.warn("{}{}", error, info);
    }
    
    private static final int secTick = 20;
    private static final int minTick = secTick * 60;



    public static int setTime(int min,  int sec) {return ((min * minTick) + (sec * secTick));}
    public static boolean getGameTime(ServerLevel world, int min, int sec) {
        long gameTime = world.getGameTime();
        int finalTotal = ((min * minTick) + (sec * secTick));
        if (finalTotal <= 0) return false;
        return gameTime % finalTotal == 0;
    }
}
