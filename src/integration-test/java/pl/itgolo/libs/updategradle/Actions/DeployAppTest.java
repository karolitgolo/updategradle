package pl.itgolo.libs.updategradle.Actions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Properties;

/**
 * The type Deploy app test.
 */
public class DeployAppTest {

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
     * The Env.
     */
    Properties env;

    /**
     * Create structure files.
     *
     * @throws IOException the io exception
     */
    @Before
    public void createStructureFiles() throws IOException {
        env = new Properties();
        env.load(new FileInputStream("env.properties"));
        appDir = tempFolder.newFolder("app");
        File resDir = tempFolder.newFolder("app", "resources");
        File res2Dir = tempFolder.newFolder("app", "resources2");
        File dataDir = tempFolder.newFolder("app", "data");
        File myAppExe = new File(appDir, "myApp.exe");
        File myResTxt = new File(resDir, "myRes.txt");
        File myRes2Txt = new File(res2Dir, "myRes2.txt");

        Files.write(Paths.get(myAppExe.getCanonicalPath()), ("content file" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myResTxt.getCanonicalPath()), "content file".getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myRes2Txt.getCanonicalPath()), ("content file 2" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));

    }

    /**
     * Gets relative paths from app directory.
     *
     * @throws IOException the io exception
     */
    @Test
    public void getRelativePathsFromAppDirectory() throws IOException {
        DeployApp deployApp = new DeployApp(
                appDir.getCanonicalPath(),
                env.getProperty("TEST_URL_APP"),
                "1.0.0.0a",
                10,
                true,
                env.getProperty("TEST_REMOTE_DIR_APP"),
                env.getProperty("TEST_FTP_HOST"),
                Integer.parseInt(env.getProperty("TEST_FTP_PORT")),
                env.getProperty("TEST_FTP_USER"),
                env.getProperty("TEST_FTP_PASSWORD")
        );
        deployApp.deploy();
    }
}