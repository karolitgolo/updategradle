package pl.itgolo.libs.updategradle.Actions;

import com.google.gson.Gson;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The type Generator structure.
 */
public class GeneratorStructure {
    /**
     * The Temp dir.
     */
    File tempDir;
    /**
     * The Dir release unpack app files.
     */
    String dirReleaseUnpackAppFiles;

    /**
     * Instantiates a new Generator structure.
     *
     * @param dirReleaseUnpackAppFiles the dir release unpack app files
     * @throws IOException the io exception
     */
    public GeneratorStructure(String dirReleaseUnpackAppFiles) throws IOException {
        this.dirReleaseUnpackAppFiles = dirReleaseUnpackAppFiles;
        tempDir = Files.createTempDirectory("upload").toFile();
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
        File structureJson = new File(tempDir, "structure.json");
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
            if (relativePath.length() > 0) {
                relativePath = relativePath.replaceAll("\\\\", "/");
                if (relativePath.startsWith("/")) {
                    relativePath = relativePath.substring(1);
                }
                relativePaths.put(relativePath, getMd5File(file));
            }
        }
        return relativePaths;
    }

    /**
     * Gets md 5 file.
     *
     * @param file the file
     * @return the md 5 file
     * @throws IOException the io exception
     */
    public String getMd5File(File file) throws IOException {
        if (file.isFile()) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                md.update(Files.readAllBytes(file.toPath()));
                byte[] digest = md.digest();
                return DatatypeConverter.printHexBinary(digest).toUpperCase();
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
        return "";
    }

}
