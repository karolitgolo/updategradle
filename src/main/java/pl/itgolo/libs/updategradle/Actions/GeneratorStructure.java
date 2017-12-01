package pl.itgolo.libs.updategradle.Actions;

import com.google.gson.Gson;
import org.gradle.internal.impldep.org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Generator structure.
 */
public class GeneratorStructure {

    /**
     * The Dir release unpack app files.
     */
    String dirReleaseUnpackAppFiles;

    /**
     * Instantiates a new Generator structure.
     *
     * @param dirReleaseUnpackAppFiles the dir release unpack app files
     */
    public GeneratorStructure(String dirReleaseUnpackAppFiles) {
        this.dirReleaseUnpackAppFiles = dirReleaseUnpackAppFiles;
    }

    /**
     * To json string.
     *
     * @return the string
     * @throws IOException the io exception
     */
    public String toJson() throws IOException {
        Map<String, String> relativePaths = toMap();
        return new Gson().toJson(relativePaths);
    }

    /**
     * To file file.
     *
     * @return the file
     * @throws IOException the io exception
     */
    public File toFile() throws IOException {
        String json = toJson();
        File structureJson = new File("app/temp/updategradle/structure.json");
        Files.createDirectories(Paths.get(structureJson.getParent()));
        Files.write(Paths.get(structureJson.getCanonicalPath()), json.getBytes(StandardCharsets.UTF_8));
        return structureJson;
    }

    /**
     * To map map.
     *
     * @return the map
     * @throws IOException the io exception
     */
    public Map<String, String> toMap() throws IOException {
        File dirApp = new File(this.dirReleaseUnpackAppFiles);
        if (!dirApp.exists()) {
            throw new IOException("Dir with release unpack app files not found: " + this.dirReleaseUnpackAppFiles);
        }
        if (!dirApp.isDirectory()) {
            throw new IOException("Not found directory: " + this.dirReleaseUnpackAppFiles);
        }
        Map<String, String> relativePaths = new HashMap<>();
        List<Path> paths = Files.walk(Paths.get(dirApp.getCanonicalPath())).collect(Collectors.toList());
        for (Path path : paths) {
            File file = path.toFile();
            String relativePath = file.getCanonicalPath().replace(dirApp.getCanonicalPath(), "");
            if (relativePath.length()>0){
                relativePath = relativePath.replaceAll("\\\\", "/");
                if (relativePath.startsWith("/")){
                    relativePath = relativePath.substring(1);
                }
                relativePaths.put(relativePath, getMd5File(file));
            }
        }
        return relativePaths;
    }

    private String getMd5File(File file) throws IOException {
        if (file.isFile()){
            return DigestUtils.md5Hex(new FileInputStream(file));
        }
        return "";
    }
}
