package pl.itgolo.libs.updategradle.Actions;

import org.apache.commons.net.ftp.FTP;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.itgolo.libs.updategradle.Abstract.PluginFunctionalTest;
import pl.itgolo.libs.updategradle.Services.FTPService;
import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;

/**
 * The type Download new version test.
 */
public class LaunchUpdateAppTest extends PluginFunctionalTest {

    /**
     * The Ftp service.
     */
    FTPService ftpService;

    /**
     * The App dir.
     */
    File appDir;

    String remoteDirTest;

    String remoteUrlTest;

    /**
     * Sets .
     *
     * @throws IOException the io exception
     */
    @Before
    public void setup() throws IOException, URISyntaxException {
        super.setup();
        ftpService = new FTPService(env.getProperty("TEST_FTP_HOST"), Integer.parseInt(env.getProperty("TEST_FTP_PORT")), env.getProperty("TEST_FTP_USER"), env.getProperty("TEST_FTP_PASSWORD"), true, FTP.BINARY_FILE_TYPE);
        remoteDirTest = env.getProperty("TEST_REMOTE_DIR_APP") + "/tests/functional/LaunchUpdateApp";
        remoteUrlTest = env.getProperty("TEST_URL_APP") + "/functional/LaunchUpdateApp";
        appDir = testProjectDir.newFolder("app");
        File resDir = testProjectDir.newFolder("app", "resources");
        File res2Dir = testProjectDir.newFolder("app", "resources2");
        File dataDir = testProjectDir.newFolder("app", "data");
        File myAppExe = new File(appDir, "myApp.exe");
        File myResTxt = new File(resDir, "myRes.txt");
        File myRes2Txt = new File(res2Dir, "myRes2.txt");
        Files.write(Paths.get(myAppExe.getCanonicalPath()), ("content file" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myResTxt.getCanonicalPath()), "content file".getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myRes2Txt.getCanonicalPath()), ("content file 2" + Calendar.getInstance().toString()).getBytes(StandardCharsets.UTF_8));
    }
    /**
     * Check exist update json file in remote.
     *
     * @throws MalformedURLException the malformed url exception
     */
    @Test
    public void downloadUpdateApp() throws IOException, InterruptedException {
        ftpService.delete(remoteDirTest);
        LogService.launch(appDir);
        DeployApp deployApp = new DeployApp(
                appDir.getCanonicalPath(),
                remoteUrlTest,
                "1.0.0.0a",
                10,
                false,
                remoteDirTest,
                env.getProperty("TEST_FTP_HOST"),
                Integer.parseInt(env.getProperty("TEST_FTP_PORT")),
                env.getProperty("TEST_FTP_USER"),
                env.getProperty("TEST_FTP_PASSWORD"),
                false
        );
        deployApp.deploy();
        //GradleRunner.create().withProjectDir(new File(".")).withArguments("clean", "--stacktrace").withDebug(true).build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("copyCompileLibs", "--stacktrace").withDebug(true).build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("createJarFile", "--stacktrace").withDebug(true).build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("copyAppFiles", "--stacktrace").withDebug(true).build();
        DeployApp deployApp2 = new DeployApp(
                new File("build/output/app").getCanonicalPath(),
                remoteUrlTest + "/updateApp",
                "1.0.0.0a",
                10,
                false,
                remoteDirTest + "/updateApp",
                env.getProperty("TEST_FTP_HOST"),
                Integer.parseInt(env.getProperty("TEST_FTP_PORT")),
                env.getProperty("TEST_FTP_USER"),
                env.getProperty("TEST_FTP_PASSWORD"),
                false
        );
        deployApp2.deploy();
        File appMainDir = testProjectDir.newFolder("appTest");
        LaunchUpdateApp launchUpdateApp = new LaunchUpdateApp(
                "AppTitle", new URL(remoteUrlTest + "/updateApp"),
                appMainDir,
                "1.0.0.0",
                remoteUrlTest,
                "notepad",
                true,
                null);
        launchUpdateApp.setTest(true);
        launchUpdateApp.launch();
        File myAppExe = new File(appDir, "myApp.exe");
        Assert.assertTrue(myAppExe.exists());
    }
}