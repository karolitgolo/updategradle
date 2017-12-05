package pl.itgolo.libs.updategradle.Services;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;

/**
 * The type Log service.
 */
public class LogService {

    /**
     * The Log file.
     */
    static File logFile;

    static File appDir;

    private static boolean enable = false;

    /**
     * Log.
     *
     * @param message the message
     */
    public static void log(String message){
        if (!enable){
            return;
        }
        if (appDir == null){
            logFile = new File(String.format("app/update/logs_%1$s.txt", DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd")));
        } else {
            logFile = new File(appDir, String.format("app/update/logs/logs_%1$s.txt",DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd")));
        }
        try {
            Files.createDirectories(Paths.get(logFile.getParent()));
            if (!logFile.exists()){
                logFile.createNewFile();
            }
            message = String.format("[%1$s] - %2$s%3$s", DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"), message, System.lineSeparator());
            Files.write(Paths.get(logFile.getCanonicalPath()), message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter for property 'appDir'.
     *
     * @param appDir Value to set for property 'appDir'.
     */
    public static void setAppDir(File appDir) {
        LogService.appDir = appDir;
    }

    public static void setEnable(boolean enable) {
        LogService.enable = enable;
    }
}
