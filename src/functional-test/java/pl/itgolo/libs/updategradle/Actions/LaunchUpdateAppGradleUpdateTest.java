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
public class LaunchUpdateAppGradleUpdateTest extends PluginFunctionalTest {

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
        appDir = testProjectDir.newFolder("appGradleUpdate");
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
    public void connectionExternalAppWithGradleUpdate() throws IOException, InterruptedException {
        File tempAppExternal = testProjectDir.newFolder("AppExternalGradleUpdate");
        LogService.launch(tempAppExternal, true);
        LogService.logsAsyncFromFileToConsole(3000, "SUCCESS ASYNC TEST");
        LogService.log("Build ExternalApp.jar");
        GradleRunner.create().withProjectDir(new File(".")).withArguments("publishAppExternal", "--stacktrace").build();
        LogService.log("Copy ExternalApp.jar to temp directory with name AppExternal");
        File externalAppBuildDir = new File("build/output/externalApp");
        FileUtils.copyDirectoryStructure(externalAppBuildDir, tempAppExternal);
        Assert.assertTrue(new File(tempAppExternal, "externalApp.jar").exists());
        ftpService.delete(remoteDirTest);
        LogService.log("Deploy AppExternal to FTP");
        DeployApp deployExternalApp = new DeployApp(
                tempAppExternal.getCanonicalPath(),
                remoteUrlTest,
                "99999.0.0.0z",
                10,
                false,
                remoteDirTest,
                env.getProperty("TEST_FTP_HOST"),
                Integer.parseInt(env.getProperty("TEST_FTP_PORT")),
                env.getProperty("TEST_FTP_USER"),
                env.getProperty("TEST_FTP_PASSWORD"),
                true
        );
        deployExternalApp.deploy();
        LogService.log("Deployed AppExternal to FTP");
        LogService.log("Build updategradle.jar");
        GradleRunner.create().withProjectDir(new File(".")).withArguments("copyCompileLibs", "--stacktrace").build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("createJarFile", "--stacktrace").build();
        GradleRunner.create().withProjectDir(new File(".")).withArguments("copyAppFiles", "--stacktrace").build();
        LogService.log("Copy updategradle.jar to temp directory with name AppExternal");
        FileUtils.copyDirectoryStructure(new File("build/output/app"), new File(tempAppExternal, "app/update"));
        String externalAppJarPath = new File(tempAppExternal, "externalApp.jar").getCanonicalPath();
        Map<String, String> argsLaunchAppJarAfterUpdate = new HashMap<>();
        argsLaunchAppJarAfterUpdate.put("--debug", "");
        argsLaunchAppJarAfterUpdate.put("--appDir", tempAppExternal.getCanonicalPath());
        Map<String, String> argsLaunchAppJar = new HashMap<>();
        argsLaunchAppJar.put("--test", "");
        argsLaunchAppJar.put("--silent", "");
        argsLaunchAppJar.put("--debug", "");
        argsLaunchAppJar.put("--urlDirUpdatePlugin", remoteUrlTest + "/noContainsFile");
        argsLaunchAppJar.put("--urlDirApp", remoteUrlTest);
        argsLaunchAppJar.put("--appDir", tempAppExternal.getCanonicalPath());
        argsLaunchAppJar.put("--commandReturnAfterUpdated", ProcessService.toStringRunJar(externalAppJarPath, argsLaunchAppJarAfterUpdate));
        String commandLaunchAppJar = ProcessService.toStringRunJar(externalAppJarPath, argsLaunchAppJar);
        LogService.log("Run process externalApp.jar with automation update");
        ProcessService.exec(commandLaunchAppJar);
        LogService.waitLogsAsyncFromFileToConsole();
        Assert.assertTrue(new File(tempAppExternal,"saveFile.txt").exists());
    }

}