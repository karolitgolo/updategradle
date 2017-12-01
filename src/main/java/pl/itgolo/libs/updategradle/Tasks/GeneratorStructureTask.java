package pl.itgolo.libs.updategradle.Tasks;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import pl.itgolo.libs.updategradle.Actions.GeneratorStructure;
import pl.itgolo.libs.updategradle.Extensions.UpdatePluginExtension;

import java.io.File;
import java.io.IOException;

/**
 * The type Generator structure task.
 */
public class GeneratorStructureTask extends DefaultTask {

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
        GeneratorStructure generatorStructure = new GeneratorStructure(updatePluginExtension.dirReleaseUnpackAppFiles);
        File structureJsonFile = generatorStructure.toFile();
        System.out.println("Generated structure JSON file: " + structureJsonFile.getCanonicalPath());
    }
}
