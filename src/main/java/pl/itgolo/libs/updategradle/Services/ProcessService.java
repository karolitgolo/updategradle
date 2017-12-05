package pl.itgolo.libs.updategradle.Services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The type Process service.
 */
public class ProcessService {

    /**
     * Is still allive boolean.
     *
     * @param pidStr the pid str
     * @return the boolean
     */
    public static boolean isStillAllive(String pidStr) {
        String OS = System.getProperty("os.name").toLowerCase();
        String command = null;
        if (OS.indexOf("win") >= 0) {
            LogService.log("Check alive Windows mode. Pid: [{}]");
            command = "cmd /c tasklist /FI \"PID eq " + pidStr + "\"";
        } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0) {
            LogService.log("Check alive Linux/Unix mode. Pid: [{}]");
            command = "ps -p " + pidStr;
        } else {
            LogService.log("Unsuported OS: Check alive for Pid: [{}] return false");
            return false;
        }
        return isProcessIdRunning(pidStr, command);
    }

    private static boolean isProcessIdRunning(String pid, String command) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);
            InputStreamReader isReader = new InputStreamReader(pr.getInputStream());
            BufferedReader bReader = new BufferedReader(isReader);
            String strLine = null;
            while ((strLine= bReader.readLine()) != null) {
                if (strLine.contains(" " + pid + " ")) {
                    return true;
                }
            }
            return false;
        } catch (Exception ex) {
            LogService.log("Got exception using system command [{}].");
            return true;
        }
    }
}
