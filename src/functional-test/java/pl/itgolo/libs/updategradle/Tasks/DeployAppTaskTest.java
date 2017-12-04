package pl.itgolo.libs.updategradle.Tasks;

import org.apache.commons.net.ftp.FTP;
import org.gradle.testkit.runner.BuildResult;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import pl.itgolo.libs.updategradle.Abstract.PluginFunctionalTest;
import pl.itgolo.libs.updategradle.Services.FTPService;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Greeting plugin test.
 */
public class DeployAppTaskTest extends PluginFunctionalTest {

    /**
     * The Temp folder.
     */
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();


    FTPService ftpService;
    /**
     * The App dir.
     */
    File appDir;


    /**
     * Sets .
     *
     * @throws IOException        the io exception
     * @throws URISyntaxException the uri syntax exception
     */
    @Before
    @Override
    public void setup() throws IOException, URISyntaxException {
        super.setup();
        ftpService = new FTPService(env.getProperty("TEST_FTP_HOST"), Integer.parseInt(env.getProperty("TEST_FTP_PORT")), env.getProperty("TEST_FTP_USER"), env.getProperty("TEST_FTP_PASSWORD"), true, FTP.BINARY_FILE_TYPE);
        appDir = tempFolder.newFolder("app");
        File resDir = tempFolder.newFolder("app", "resources");
        File res2Dir = tempFolder.newFolder("app", "resources2");
        File dataDir = tempFolder.newFolder("app", "data");
        File myAppExe = new File(appDir, "deployAppTask.exe");
        File myResTxt = new File(resDir, "myRes.txt");
        File myRes2Txt = new File(res2Dir, "myRes2.txt");
        Files.write(Paths.get(myAppExe.getCanonicalPath()), ("content file" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myResTxt.getCanonicalPath()), "content file".getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myRes2Txt.getCanonicalPath()), ("content file 2" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void extensionGreetingPluginWithPropertyInBuildGradleFile() throws Exception {
        String remoteDirTest = env.getProperty("TEST_REMOTE_DIR_APP") + "/tests/functional/deployAppTask";
        ftpService.delete(remoteDirTest);
        Map<Object, Object> templateProps = new HashMap<>();
        propsToMap("env.properties", templateProps);
        templateProps.put("dirReleaseUnpackAppFiles", appDir.getCanonicalPath().replaceAll("\\\\", "\\\\\\\\"));
        templateProps.put("TEST_REMOTE_DIR_APP", env.getProperty("TEST_REMOTE_DIR_APP") + "/tests/functional/deployAppTask");
        templateProps.put("urlApp", env.getProperty("TEST_URL_APP") + "/functional/deployAppTask");
        templateProps.put("forceUpload", "true");
        BuildResult result = createGradleRunner("src/functional-test/resources/pl/itgolo/libs/updategradle/templateBuild.gradle", templateProps)
                .withArguments("deployApp", "--stacktrace","--refresh-dependencies")
                .withDebug(true)
                .build();
        BuildResult result2 = createGradleRunner("src/functional-test/resources/pl/itgolo/libs/updategradle/templateBuild.gradle", templateProps)
                .withArguments("deployApp", "--stacktrace","--refresh-dependencies")
                .withDebug(true)
                .build();
        Assert.assertTrue(result2.getOutput().contains("SUCCESS DEPLOY"));
    }

    @Test
    public void sendApplicationWithThisSomeVersion() throws IOException {
        String remoteDirTest = env.getProperty("TEST_REMOTE_DIR_APP") + "/tests/functional/deployAppTask";
        ftpService.delete(remoteDirTest);
        Map<Object, Object> templateProps = new HashMap<>();
        propsToMap("env.properties", templateProps);
        templateProps.put("dirReleaseUnpackAppFiles", appDir.getCanonicalPath().replaceAll("\\\\", "\\\\\\\\"));
        templateProps.put("TEST_REMOTE_DIR_APP", env.getProperty("TEST_REMOTE_DIR_APP") + "/tests/functional/deployAppTask");
        templateProps.put("urlApp", env.getProperty("TEST_URL_APP") + "/functional/deployAppTask");
        templateProps.put("forceUpload", "false");
        BuildResult result = createGradleRunner("src/functional-test/resources/pl/itgolo/libs/updategradle/templateBuild.gradle", templateProps)
                .withArguments("deployApp", "--stacktrace","--refresh-dependencies")
                .withDebug(true)
                .build();
        try {
            createGradleRunner("src/functional-test/resources/pl/itgolo/libs/updategradle/templateBuild.gradle", templateProps)
                    .withArguments("deployApp", "--stacktrace","--refresh-dependencies")
                    .withDebug(true)
                    .build();
        } catch (Exception e) {
            Assert.assertTrue(e.getMessage().contains("New version is less or equal from remote version"));
        }
    }
}