package pl.itgolo.libs.updategradle.Actions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * The type Generator structure test.
 */
public class GeneratorStructureTest {

    /**
     * The Temp folder.
     */
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * The App dir.
     */
    File appDir;

    /**
     * Create structure files.
     *
     * @throws IOException the io exception
     */
    @Before
    public void createStructureFiles() throws IOException {
        appDir = tempFolder.newFolder("app");
        File resDir = tempFolder.newFolder("app", "resources");
        File dataDir = tempFolder.newFolder("app", "data");
        File myAppExe = new File(appDir, "myApp.exe");
        File myResTxt = new File(resDir, "myRes.txt");

        Files.write(Paths.get(myAppExe.getCanonicalPath()), "content file".getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myResTxt.getCanonicalPath()), "content file".getBytes(StandardCharsets.UTF_8));
        LogService.setAppDir(appDir);
    }

    /**
     * Gets relative paths from app directory.
     *
     * @throws IOException the io exception
     */
    @Test
    public void getRelativePathsFromAppDirectory() throws IOException, InterruptedException {
        GeneratorStructure generatorStructure = new GeneratorStructure(appDir.getCanonicalPath());
        File structureJsonFile = generatorStructure.toFile();
    }
}