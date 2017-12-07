package pl.itgolo.libs.updategradle.Examples;

import pl.itgolo.libs.updategradle.Actions.LaunchUpdateApp;
import pl.itgolo.libs.updategradle.Services.ArgsService;
import pl.itgolo.libs.updategradle.Services.LogService;
import pl.itgolo.libs.updategradle.Services.ProcessService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

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
        LogService.launch(argsService.hasArg("--debug"), new File(argsService.getValueArg("--appDir")));
        if (argsService.hasArg("--test")) {
            LogService.log("Launched externalApp.jar with mode test");
            LogService.log("Running updategradle.jar with mode test");
            URL urlDirUpdatePlugin = new URL(argsService.getValueArg("--urlDirUpdatePlugin"));
            LogService.log("Running updategradle.jar with arg --appDir" + argsService.getValueArg("--appDir"));
            File appDir = new File(argsService.getValueArg("--appDir"));
            String appCurrentVersion = "1.0.0.0";
            String urlDirApp = argsService.getValueArg("--urlDirApp");
            String commandReturnAfterUpdated = argsService.getValueArg("--commandReturnAfterUpdated");
            Boolean silent =argsService.hasArg("--silent");
            String pid = ProcessService.getPid();
            String appTitle = argsService.getValueArg("--appTitle");
            LogService.log("Running updategradle.jar with arg --appTitle: " + appTitle);
            LaunchUpdateApp launchUpdateApp = new LaunchUpdateApp(appTitle,
                    urlDirUpdatePlugin, appDir, appCurrentVersion,
                    urlDirApp, commandReturnAfterUpdated, silent, pid);
            launchUpdateApp.setTimeoutWaitClose(120);
            launchUpdateApp.setDebug(argsService.hasArg("--debug"));
            launchUpdateApp.setTest(argsService.hasArg("--test"));
            launchUpdateApp.launch();
            LogService.log("Called launch updategradle.jar with mode test");
            LogService.log("Wait for close externalApp.jar with mode test");
            Thread.sleep(6000);
            LogService.log("Closed externalApp.jar with mode test");
            System.exit(0);
        } else {
            LogService.log("Launched externalApp.jar without mode test");
            LogService.log("Create saveFile.txt file in ExternalApp directory");
            File saveFile = new File(argsService.getValueArg("--appDir") + "/saveFile.txt");
            Files.write(Paths.get(saveFile.getCanonicalPath()), ("content file" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
            LogService.log("SUCCESS ASYNC TEST");
        }

    }
}
