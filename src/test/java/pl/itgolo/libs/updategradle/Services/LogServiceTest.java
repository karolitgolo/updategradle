package pl.itgolo.libs.updategradle.Services;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * IDE Editor: IntelliJ IDEA
 * <p>
 * Date: 06.12.2017
 * Time: 22:01
 * Project name: updategradle
 *
 * @author Karol Golec karol.itgolo@gmail.com
 */
public class LogServiceTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    @Test
    public void checkWriteCorrectClassNameAndMethodNameToLogFile() throws IOException {
        File appDir = tempFolder.newFolder("app");
        LogService.launch(appDir);
        LogService.log("test message");
        String content = new String(Files.readAllBytes(Paths.get(LogService.getLogFile().getCanonicalPath())));
        System.out.println(content);
        Assert.assertTrue(content.contains("test message"));
        Assert.assertTrue(content.contains("checkWriteCorrectClassNameAndMethodNameToLogFile"));
        Assert.assertTrue(content.contains("LogServiceTest"));
    }

}