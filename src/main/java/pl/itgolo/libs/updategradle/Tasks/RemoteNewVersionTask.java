package pl.itgolo.libs.updategradle.Tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import pl.itgolo.libs.updategradle.Actions.RemoteNewVersion;
import pl.itgolo.libs.updategradle.Extensions.UpdatePluginExtension;

/**
 * The type Remote new version task.
 */
public class RemoteNewVersionTask extends DefaultTask {

    /**
     * The Update plugin extension.
     */
    UpdatePluginExtension updatePluginExtension = (UpdatePluginExtension) getProject().getExtensions().getByName("UpdatePlugin");

    /**
     * Action.
     */
    @TaskAction
    void action(){
        String updateJsonUrlFile = String.format("%1$s/update.json", updatePluginExtension.urlApp);
        RemoteNewVersion remoteNewVersion = new RemoteNewVersion(updateJsonUrlFile, updatePluginExtension.remoteNewVersionTimeout);
        String remoteVersion = remoteNewVersion.getVersion();
        System.out.println("Remote application version: " + remoteVersion);
    }
}
