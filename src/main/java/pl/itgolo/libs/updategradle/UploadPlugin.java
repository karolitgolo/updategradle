package pl.itgolo.libs.updategradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import pl.itgolo.libs.updategradle.Extensions.UpdatePluginExtension;
import pl.itgolo.libs.updategradle.Tasks.DeployAppTask;
import pl.itgolo.libs.updategradle.Tasks.GeneratorStructureTask;
import pl.itgolo.libs.updategradle.Tasks.RemoteNewVersionTask;

/**
 * The type Upload plugin.
 */
public class UploadPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("UpdatePlugin", UpdatePluginExtension.class);
        project.getTasks().create("deployApp", DeployAppTask.class);
        project.getTasks().create("generateStructure", GeneratorStructureTask.class);
        project.getTasks().create("remoteVersion", RemoteNewVersionTask.class);

    }
}
