package pl.itgolo.libs.updategradle.Tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import pl.itgolo.libs.updategradle.Actions.DeployApp;
import pl.itgolo.libs.updategradle.Extensions.UpdatePluginExtension;

import java.io.File;
import java.io.IOException;

/**
 * The type Deploy app task.
 */
public class DeployAppTask extends DefaultTask {

    /**
     * The Update plugin extension.
     */
    UpdatePluginExtension updatePluginExtension = (UpdatePluginExtension) getProject().getExtensions().getByName("UpdatePlugin");

    /**
     * Action.
     *
     * @throws IOException the io exception
     */
    @TaskAction
    void action() throws IOException {
        DeployApp deployApp = new DeployApp(
                updatePluginExtension.dirReleaseUnpackAppFiles,
                updatePluginExtension.urlApp,
                updatePluginExtension.newVersion,
                updatePluginExtension.remoteNewVersionTimeout,
                updatePluginExtension.forceUpload,
                updatePluginExtension.remoteDirApp,
                updatePluginExtension.ftpHost,
                updatePluginExtension.ftpPort,
                updatePluginExtension.ftpUser,
                updatePluginExtension.ftpPassword
        );
        deployApp.deploy();
        System.out.println("Deployed app files: " + new File(updatePluginExtension.dirReleaseUnpackAppFiles).getCanonicalPath());
    }
}
