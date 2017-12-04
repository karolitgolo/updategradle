package pl.itgolo.libs.updategradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.DependencyResolutionListener;
import org.gradle.api.artifacts.ResolvableDependencies;
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

        addDependencies(project);

    }

    private void addDependencies(Project project) {
        project.getGradle().addListener(new DependencyResolutionListener() {
            @Override
            public void beforeResolve(ResolvableDependencies dependencies) {
                project.getBuildscript().getDependencies().add("classpath", project.getDependencies().create("commons-net:commons-net:3.6"));
                project.getBuildscript().getDependencies().add("classpath", project.getDependencies().create("org.apache.maven:maven-artifact:3.5.2"));
                project.getBuildscript().getDependencies().add("classpath", project.getDependencies().create("com.google.code.gson:gson:2.8.2"));
            }
            @Override
            public void afterResolve(ResolvableDependencies dependencies) {

            }
        });
    }
}
