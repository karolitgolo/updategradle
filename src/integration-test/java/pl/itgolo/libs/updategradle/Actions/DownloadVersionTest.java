package pl.itgolo.libs.updategradle.Actions;

import org.apache.commons.net.ftp.FTP;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import pl.itgolo.libs.updategradle.Services.FTPService;
import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Properties;

/**
 * The type Download new version test.
 */
public class DownloadVersionTest {

    /**
     * The Temp folder.
     */
    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    /**
     * The Env.
     */
    protected Properties env;

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
    public void setup() throws IOException {
        env = new Properties();
        File envFile = new File("env.properties");
        env.load(new FileInputStream(envFile));
        ftpService = new FTPService(env.getProperty("TEST_FTP_HOST"), Integer.parseInt(env.getProperty("TEST_FTP_PORT")), env.getProperty("TEST_FTP_USER"), env.getProperty("TEST_FTP_PASSWORD"), true, FTP.BINARY_FILE_TYPE);
        remoteDirTest = env.getProperty("TEST_REMOTE_DIR_APP") + "/tests/integration/downloadNewVersion";
        remoteUrlTest = env.getProperty("TEST_URL_APP") + "/integration/downloadNewVersion";
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
        LogService.setAppDir(appDir);
    }

    /**
     * Check exist update json file in remote.
     *
     * @throws MalformedURLException the malformed url exception
     */
    @Test
    public void checkExistUpdateJsonFileInRemoteBadVersionToDownload() throws IOException, InterruptedException {
        ftpService.delete(remoteDirTest);
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
                true
        );
        deployApp.deploy();
        DownloadVersion downloadVersion = new DownloadVersion(appDir, new URL(remoteUrlTest));
        Assert.assertFalse(downloadVersion.download("1.0.0.0"));
    }

    @Test
    public void updateApplication() throws IOException, InterruptedException {
        ftpService.delete(remoteDirTest);
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
                true
        );
        deployApp.deploy();
        File myAppExe = new File(appDir, "myApp.exe");
        Files.write(Paths.get(myAppExe.getCanonicalPath()), ("content old file").getBytes(StandardCharsets.UTF_8));
        final String md5Old = GeneratorStructure.getMd5File(myAppExe);
        DownloadVersion downloadVersion = new DownloadVersion(appDir, new URL(remoteUrlTest));
        Assert.assertTrue(downloadVersion.download("1.0.0.0a"));
        Assert.assertNotEquals(md5Old, GeneratorStructure.getMd5File(myAppExe));
    }

    @Test
    public void updateApplicationNewVersion() throws IOException, InterruptedException {
        ftpService.delete(remoteDirTest);
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
                true
        );
        deployApp.deploy();
        File myAppExe = new File(appDir, "myApp.exe");
        Files.write(Paths.get(myAppExe.getCanonicalPath()), ("content old file").getBytes(StandardCharsets.UTF_8));
        final String md5Old = GeneratorStructure.getMd5File(myAppExe);
        DownloadVersion downloadVersion = new DownloadVersion(appDir, new URL(remoteUrlTest));
        Assert.assertTrue(downloadVersion.downloadNewVersion("1.0.0.0"));
        Assert.assertNotEquals(md5Old, GeneratorStructure.getMd5File(myAppExe));
    }
}