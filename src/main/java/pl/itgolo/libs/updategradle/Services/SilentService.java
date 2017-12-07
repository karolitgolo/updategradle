package pl.itgolo.libs.updategradle.Services;

import pl.itgolo.libs.updategradle.Actions.LaunchMainApp;
import pl.itgolo.libs.updategradle.Actions.UpdateSilent;

import java.io.IOException;

/**
 * The type Silent service.
 */
public class SilentService {
    /**
     * Update.
     *
     * @param argsService the args service
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public void update(ArgsService argsService) throws IOException, InterruptedException {
        if (argsService.hasArg("--pid")){
            waitExitProcessMainApp(argsService);
        }
        UpdateSilent updateSilent = new UpdateSilent(argsService);
        Boolean updated = updateSilent.update();
        if (updated){
            LogService.log("updategradle.jar updated externalApp files");
        } else {
            LogService.log("updategradle.jar not updated externalApp files");
        }
        LogService.log("Launch updated externalApp.jar without arg --test");
        LaunchMainApp launchMainApp = new LaunchMainApp(argsService, updated);
        launchMainApp.launch();
    }

    public static void waitExitProcessMainApp(ArgsService argsService) throws IOException, InterruptedException {
        if (argsService == null){
            return;
        }
        String pid = argsService.getValueArg("--pid");
        LogService.log("ExternalApp.jar has PID: "+ pid);
        if (argsService.hasArg("--timeoutWaitClose")){
            Integer timeout = Integer.parseInt(argsService.getValueArg("--timeoutWaitClose"));
            for (int i=0 ; i< timeout ; i+=5){
                LogService.log(String.format("Wait %1$s of %2$s seconds updategradle.jar for closed externalApp.jar", i, timeout));
                if (!ProcessService.isStillAlive(pid)){
                    return;
                }
                Thread.sleep(5000);
            }
        }
        if (ProcessService.isStillAlive(pid)){
            LogService.log("updategradle.jar can not closed externalApp.jar without timeout");
            throw new IOException("updategradle.jar can not closed externalApp.jar without timeout");
        }
    }
}
