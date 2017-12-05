package pl.itgolo.libs.updategradle.Services;

import pl.itgolo.libs.updategradle.Actions.LaunchMainApp;
import pl.itgolo.libs.updategradle.Actions.UpdateSilent;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * The type Main service.
 */
public class MainService {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        /**
         * the args: --silent, --versionToCompare=1.0.0.0
         *             --appDir=C:\appDir, --remoteUrl=http://host/appDir
         *             --commandReturnMainApp="java -jar C:\appDir\myApp.jar --arg"
         *             --timeoutWaitClose --pid=12345
         */
        ArgsService argsService = new ArgsService(args);
        if (argsService.hasArg("--debug")){
            LogService.setEnable(true);
            LogService.setAppDir(new File(argsService.getValueArg("--appDir")));
        }
        Boolean updated = false;
        for (Map.Entry<String, Object> entry : argsService.toMap().entrySet()) {
            LogService.log("MainService arg: " + entry.getKey() + ", val: " + entry.getValue());
        }
        if (argsService.hasArg("--pid")){
            waitExitProcessMainApp(argsService);
        }
        if (argsService.hasArg("--silent")){
            UpdateSilent updateSilent = new UpdateSilent(argsService);
            updated = updateSilent.update();
            LogService.log("MainService updated: "+ updated);
        } else {
            LogService.log("MainService not set --silent");
        }
        if (!argsService.hasArg("--test")){
            LogService.log("MainService without arg --test: "+ updated);
            LaunchMainApp launchMainApp = new LaunchMainApp(argsService, updated);
            launchMainApp.launch();
        } else {
            LogService.log("MainService with arg --test: "+ updated);
        }
    }

    private static void waitExitProcessMainApp(ArgsService argsService) throws IOException, InterruptedException {
        String pid = argsService.getValueArg("--pid");
        LogService.log("MainService has pid: "+ pid);
        if (argsService.hasArg("--timeoutWaitClose")){
            LogService.log("MainService has timeoutWaitClose: "+ pid);
            Integer timeout = Integer.parseInt(argsService.getValueArg("--timeoutWaitClose"));
            LogService.log("MainService timeout: "+ timeout);

            for (int i=0 ; i< timeout ; i+=5){
                if (!ProcessService.isStillAllive(pid)){
                    return;
                }
                LogService.log("Wait for pid close: "+ pid);
                Thread.sleep(5000);
            }
        }
        if (ProcessService.isStillAllive(pid)){
            LogService.log("PID is not close: "+ pid);
            throw new IOException("PID is not close: "+ pid);
        }
    }

}
