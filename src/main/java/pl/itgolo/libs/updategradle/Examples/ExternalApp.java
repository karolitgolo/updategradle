package pl.itgolo.libs.updategradle.Examples;

import pl.itgolo.libs.updategradle.Actions.LaunchUpdateApp;
import pl.itgolo.libs.updategradle.Services.ArgsService;
import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Map;

/**
 * IDE Editor: IntelliJ IDEA
 * <p>
 * Date: 05.12.2017
 * Time: 13:48
 * Project name: updategradle
 *
 * @author Karol Golec karol.rebigo@gmail.com
 */
public class ExternalApp {


    /**
     * The entry point of application.
     *
     * @param args the input arguments for tests: --urlDirUpdatePlugin, --urlDirApp, --commandReturnAfterUpdated, --update, --appDir
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ArgsService argsService = new ArgsService(args);
        if (argsService.hasArg("--debug")){
            LogService.setEnable(true);
            LogService.setAppDir(new File(argsService.getValueArg("--appDir")));
        }
        LogService.log("Run externalApp.jar");
        LogService.log("Run externalApp.jar exist --update: " + argsService.hasArg("--update"));
        for (Map.Entry<String, Object> entry : argsService.toMap().entrySet()) {
            LogService.log("Run externalApp.jar arg: " + entry.getKey() + ", val: " + entry.getValue());
        }
        if (!argsService.hasArg("--update")) {
            LogService.log("External app without --update argument");
            File saveFile = new File(argsService.getValueArg("--appDir") + "/saveFile.txt");
            Files.write(Paths.get(saveFile.getCanonicalPath()), ("content file" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
        } else {
            URL urlDirUpdatePlugin = new URL(argsService.getValueArg("--urlDirUpdatePlugin"));
            File appDir = new File(argsService.getValueArg("--appDir"));
            String appCurrentVersion = "1.0.0.0";
            String urlDirApp = argsService.getValueArg("--urlDirApp");
            String commandReturnAfterUpdated = argsService.getValueArg("--commandReturnAfterUpdated");
            Boolean silent = true;
            LogService.log("External app --update urlDirUpdatePlugin: " + urlDirUpdatePlugin);
            LogService.log("External app --update urlDirUpdatePlugin: " + urlDirUpdatePlugin);
            LogService.log("External app --update urlDirApp: " + urlDirApp);
            LogService.log("External app --update commandReturnAfterUpdated: " + commandReturnAfterUpdated);
            LogService.log("External app --update appDir: " + appDir);
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            LaunchUpdateApp launchUpdateApp = new LaunchUpdateApp(
                    urlDirUpdatePlugin, appDir, appCurrentVersion,
                    urlDirApp, commandReturnAfterUpdated, silent, pid);
            launchUpdateApp.setTimeoutWaitClose(120);
            launchUpdateApp.setDebug(argsService.hasArg("--debug"));
            launchUpdateApp.launch();
            LogService.log("External app with --update argument simulate close application with 10 seconds");
            Thread.sleep(10000);
            LogService.log("External app STOP simulate");
            System.exit(0);
        }

    }
}
