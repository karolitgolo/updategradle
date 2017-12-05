package pl.itgolo.libs.updategradle.Actions;

import pl.itgolo.libs.updategradle.Services.ArgsService;
import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * The type Update silent.
 */
public class UpdateSilent {

    private ArgsService argsService;

    /**
     * Instantiates a new Update silent.
     *
     * @param argsService the args service
     */
    public UpdateSilent(ArgsService argsService) {
        this.argsService = argsService;
    }

    /**
     * Update boolean.
     *
     * @return the boolean
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public Boolean update() throws IOException, InterruptedException {
        File appDir = new File(argsService.getValueArg("--appDir"));
        LogService.log("UpdateSilent with arg --appDir: "+ argsService.getValueArg("--appDir"));
        LogService.log("UpdateSilent with arg --remoteUrl: "+ argsService.getValueArg("--remoteUrl"));
        DownloadVersion downloadVersion = new DownloadVersion(appDir, new URL(argsService.getValueArg("--remoteUrl")));
        if (argsService.hasArg("--timeoutWaitClose")){
            LogService.log("UpdateSilent with arg --timeoutWaitClose: "+ argsService.getValueArg("--timeoutWaitClose"));
            downloadVersion.setTimeoutWaitClose(Integer.parseInt(argsService.getValueArg("--timeoutWaitClose")));
        } else {
            LogService.log("UpdateSilent without arg --timeoutWaitClose");
        }
        return downloadVersion.downloadNewVersion(argsService.getValueArg("--versionToCompare"));
    }
}
