package pl.itgolo.libs.updategradle.Actions;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * IDE Editor: IntelliJ IDEA
 * <p>
 * Date: 04.12.2017
 * Time: 10:09
 * Project name: updategradle
 *
 * @author Karol Golec <karol.rebigo@gmail.com>
 */
public class GeneratorStructureTest {

    @Rule
    public final TemporaryFolder tempFolder = new TemporaryFolder();

    File tempDir;

    @Before
    public void setup() throws IOException {
        tempDir = tempFolder.newFolder("temp");
    }

    @Test
    public void getCorrectMD5OfFile() throws IOException {
        File myFile = new File(tempDir, "myApp.exe");
        File myFile2 = new File(tempDir, "myApp2.exe");
        Files.write(Paths.get(myFile.getCanonicalPath()), "content file".getBytes(StandardCharsets.UTF_8));
        Files.write(Paths.get(myFile2.getCanonicalPath()), "content file 2".getBytes(StandardCharsets.UTF_8));

        GeneratorStructure generatorStructure = new GeneratorStructure(myFile.getCanonicalPath());
        String md5 = generatorStructure.getMd5File(myFile);
        String md52 = generatorStructure.getMd5File(myFile2);
        Assert.assertTrue(md5.matches("\\p{XDigit}+"));
        Assert.assertNotEquals(md5, md52);
    }
}