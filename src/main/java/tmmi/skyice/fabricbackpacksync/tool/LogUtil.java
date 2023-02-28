package tmmi.skyice.fabricbackpacksync.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    private LogUtil() {
    }
    public static final Logger LOGGER = LoggerFactory.getLogger("BackPackSync");

    public  static  Logger getLogger() {
        return LOGGER;
    }

}
