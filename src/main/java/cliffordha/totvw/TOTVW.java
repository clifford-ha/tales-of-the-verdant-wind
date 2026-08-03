package cliffordha.totvw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TOTVW {
    public static final String MOD_ID = "tales-of-the-verdant-wind";
    public static final String MOD_NAME_LONG = "Tales of the Verdant Wind";
    public static final String MOD_NAME = "TOTVW";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static final Logger ID_LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean IN_DEVELOPMENT = TalesOfTheVerdantWind.IN_DEVELOPMENT;

    public static void sendStat(String stat) {
        ID_LOGGER.info(stat);
    }

    public static void sendClassRegisterLog(String className) {
        TOTVW.LOGGER.info("{} registered!", className);
    }
    public static void sendInfo(String info) {
        TOTVW.LOGGER.info(info);
    }
}
