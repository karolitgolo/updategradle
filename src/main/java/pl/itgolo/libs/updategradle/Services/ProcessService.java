package pl.itgolo.libs.updategradle.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.Map;

/**
 * The type Process service.
 */
public class ProcessService {

    /**
     * Is still alive boolean.
     *
     * @param pidStr the pid str
     * @return the boolean
     */
    public static boolean isStillAlive(String pidStr) {
        String OS = System.getProperty("os.name").toLowerCase();
        String command = null;
        if (OS.indexOf("win") >= 0) {
            command = "cmd /c tasklist /FI \"PID eq " + pidStr + "\"";
        } else if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0) {
            command = "ps -p " + pidStr;
        } else {
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

    /**
     * To string run jar string.
     *
     * @param appJarPath the app jar path
     * @param args       the args
     * @return the string
     */
    public static String toStringRunJar(String appJarPath, Map<String, String> args) {
        String command = String.format("java -jar \"%1$s\"", appJarPath);
        for (Map.Entry<String, String> entry : args.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (!value.isEmpty()){
                command += String.format(" %1$s=\"%2$s\"", key, value);
            } else {
                command += String.format(" %1$s", key);
            }
        }
        return command;
    }

    public static void exec(String command) throws IOException {
        Process proc = Runtime.getRuntime().exec(command);
    }

    public static void execAndWait(String command) throws IOException, InterruptedException {
        Process proc = Runtime.getRuntime().exec(command);
        proc.waitFor();
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();
        byte b[]=new byte[in.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));
        byte c[]=new byte[err.available()];
        err.read(c,0,c.length);
        if (!new String(c).isEmpty()){
            throw new IOException(new String(c));
        }
        System.exit(0);
    }

    public static String getPid() {
        return ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
    }
}
