package pl.itgolo.libs.updategradle.Actions;

import org.apache.commons.net.ftp.FTP;
import org.codehaus.plexus.util.FileUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pl.itgolo.libs.updategradle.Abstract.PluginFunctionalTest;
import pl.itgolo.libs.updategradle.Services.FTPService;
import pl.itgolo.libs.updategradle.Services.LogService;
import pl.itgolo.libs.updategradle.Services.ProcessService;

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
 * The type Download new version test.
 */
public class LaunchUpdateAppSilentTest extends PluginFunctionalTest {

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
        appDir = testProjectDir.newFolder("appSilent");
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

    @Test
    public void updateByExternalApplication() throws IOException, InterruptedException {
        File appExternal = testProjectDir.newFolder("appExternalSilent");
        LogService.launch(appExternal, true);
        LogService.logsAsyncFromFileToConsole(300, "SUCCESS ASYNC TEST");
        LogService.log("Run test updateByExternalApplication");
        GradleRunner.create().withProjectDir(new File(".")).withArguments("publishAppExternal", "--stacktrace").withDebug(true).build();
        LogService.log("Build ExternalApp");
        File srcDir = new File("build/output/externalApp");
        FileUtils.copyDirectoryStructure(srcDir, appExternal);
        Assert.assertTrue(new File(appExternal, "externalApp.jar").exists());
                ftpService.delete(remoteDirTest);
        DeployApp deployApp = new DeployApp(
                appExternal.getCanonicalPath(),
                remoteUrlTest,
                "1.0.0.0z",
                10,
                false,
                remoteDirTest,
                env.getProperty("TEST_FTP_HOST"),
                Integer.parseInt(env.getProperty("TEST_FTP_PORT")),
                env.getProperty("TEST_FTP_USER"),
                env.getProperty("TEST_FTP_PASSWORD"),
                true
        );
        deployApp.deploy();
        LogService.log("END application external deploy");
        GradleRunner.create().withProjectDir(new File(".")).withArguments("copyCompileLibs", "--stacktrace").withDebug(true).build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("createJarFile", "--stacktrace").withDebug(true).build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("copyAppFiles", "--stacktrace").withDebug(true).build();
        LogService.log("END build plugin update");
        FileUtils.copyDirectoryStructure(new File("build/output/app"), new File(appExternal, "app/update"));
        Assert.assertTrue(Files.deleteIfExists(new File(appExternal, "test.txt").toPath()));
        Assert.assertTrue(new File(appExternal, "app/update/updategradle.jar").exists());
        String externalAppJarPath = new File(appExternal, "externalApp.jar").getCanonicalPath();
        Map<String, String> argsLaunchAppJarAfterUpdate = new HashMap<>();
        argsLaunchAppJarAfterUpdate.put("--debug", "");
        argsLaunchAppJarAfterUpdate.put("--appDir", appExternal.getCanonicalPath());
        Map<String, String> argsLaunchAppJar = new HashMap<>();
        argsLaunchAppJar.put("--test", "");
        argsLaunchAppJar.put("--silent", "");
        argsLaunchAppJar.put("--debug", "");
        argsLaunchAppJar.put("--urlDirUpdatePlugin", remoteUrlTest + "/noContainsFile");
        argsLaunchAppJar.put("--urlDirApp", remoteUrlTest);
        argsLaunchAppJar.put("--appDir", appExternal.getCanonicalPath());
        argsLaunchAppJar.put("--commandReturnAfterUpdated", ProcessService.toStringRunJar(externalAppJarPath, argsLaunchAppJarAfterUpdate));
        String commandLaunchAppJar = ProcessService.toStringRunJar(externalAppJarPath, argsLaunchAppJar);
        LogService.log("Run process externalApp.jar with automation update");
        ProcessService.exec(commandLaunchAppJar);
       // LogService.waitLogsAsyncFromFileToConsole();
        LogService.log("Sleep 120 seconds");
        Thread.sleep(120000);
        Assert.assertTrue(new File(appExternal,"saveFile.txt").exists());
        Assert.assertTrue(new File(appExternal, "test.txt").exists());
    }
}