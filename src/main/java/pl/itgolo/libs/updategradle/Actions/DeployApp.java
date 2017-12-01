package pl.itgolo.libs.updategradle.Actions;

import org.apache.commons.net.ftp.FTP;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import pl.itgolo.libs.updategradle.Services.FTPService;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * The type Deploy app.
 */
public class DeployApp {

    /**
     * The New version.
     */
    String newVersion;
    /**
     * The Url app.
     */
    String urlApp;
    /**
     * The Remote new version timeout.
     */
    Integer remoteNewVersionTimeout;
    /**
     * The Dir release unpack app files.
     */
    String dirReleaseUnpackAppFiles;
    /**
     * The Force upload.
     */
    Boolean forceUpload;
    /**
     * The Remote dir app.
     */
    String remoteDirApp;
    /**
     * The Generator structure.
     */
    GeneratorStructure generatorStructure;
    /**
     * The Ftp service.
     */
    FTPService ftpService;

    /**
     * Instantiates a new Deploy app.
     *
     * @param dirReleaseUnpackAppFiles the dir release unpack app files
     * @param urlApp                   the url app
     * @param newVersion               the new version
     * @param remoteNewVersionTimeout  the remote new version timeout
     * @param forceUpload              the force upload
     * @param remoteDirApp             the remote dir app
     * @param ftpHost                  the ftp host
     * @param ftpPort                  the ftp port
     * @param ftpUser                  the ftp user
     * @param ftpPassword              the ftp password
     * @throws IOException the io exception
     */
    public DeployApp(String dirReleaseUnpackAppFiles, String urlApp, String newVersion, Integer remoteNewVersionTimeout, Boolean forceUpload, String remoteDirApp, String ftpHost, Integer ftpPort, String ftpUser, String ftpPassword) throws IOException {
        this.newVersion = newVersion;
        this.urlApp = urlApp;
        this.remoteNewVersionTimeout = remoteNewVersionTimeout;
        this.dirReleaseUnpackAppFiles = dirReleaseUnpackAppFiles;
        this.forceUpload = forceUpload;
        this.generatorStructure = new GeneratorStructure(dirReleaseUnpackAppFiles);
        ftpService = new FTPService(ftpHost, ftpPort, ftpUser, ftpPassword, true, FTP.BINARY_FILE_TYPE);
        this.remoteDirApp = remoteDirApp;

    }

    /**
     * Deploy.
     *
     * @throws IOException the io exception
     */
    public void deploy() throws IOException {
        validateHasThisSomeVersion();
        sendFilesToFtp();
        File structureJsonFile = buildStructure();
        File updateJsonFile = buildUpdateJsonFile();
        ftpService.upload(structureJsonFile, String.format("%1$s/files/%2$s-structure.json", this.remoteDirApp, this.newVersion));
        ftpService.upload(updateJsonFile, String.format("%1$s/update.json", this.remoteDirApp));
        ftpService.closeFtpClient();
    }

    private void sendFilesToFtp() throws IOException {
        Map<String, String> relativePaths = this.generatorStructure.toMap();
        for (Map.Entry<String, String> entry : relativePaths.entrySet()) {
            String remotePathRelative = entry.getKey();
            File file = new File(this.dirReleaseUnpackAppFiles + "/" + entry.getKey());
            String remotePath = String.format("%1$s/files/%2$s/%3$s", this.remoteDirApp, this.newVersion, remotePathRelative);
            ftpService.upload(file, remotePath);
            validateUpload(file, remotePathRelative);
        }
    }

    private void validateUpload(File file, String remotePathRelative) throws IOException {
        if (file.isFile()) {
            String urlFile = String.format("%1$s/files/%2$s/%3$s", this.urlApp, this.newVersion, remotePathRelative);
            URL url = new URL(urlFile);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.getInputStream();
            Long weightRemote = conn.getContentLengthLong();
            Long weight = file.length();
            if (!weight.equals(weightRemote)) {
                throw new IOException("In validate upload file to FTP, file in server not equal length to file local");
            }
            conn.disconnect();
        }
    }

    private void validateHasThisSomeVersion() throws IOException {
        String updateJsonUrlFile = String.format("%1$s/update.json", urlApp);
        RemoteNewVersion remoteNewVersion = new RemoteNewVersion(updateJsonUrlFile, remoteNewVersionTimeout);
        String remoteVersionString = remoteNewVersion.getVersion();
        if (remoteVersionString != null) {
            DefaultArtifactVersion remoteVersion = new DefaultArtifactVersion(remoteVersionString);
            DefaultArtifactVersion newVersion = new DefaultArtifactVersion(this.newVersion);
            if ((newVersion.compareTo(remoteVersion) < 0 || newVersion.compareTo(remoteVersion) == 0) && !forceUpload) {
                throw new IOException("New version is less or equal from remote version. Try set `forceUpdate` to true.");
            }
        }
    }

    private File buildUpdateJsonFile() throws IOException {
        String json = "{\"version\": \"" + newVersion + "\"}";
        File updateJson = new File("app/temp/updategradle/update.json");
        Files.createDirectories(Paths.get(updateJson.getParent()));
        Files.write(Paths.get(updateJson.getCanonicalPath()), json.getBytes(StandardCharsets.UTF_8));
        return updateJson;
    }

    private File buildStructure() throws IOException {
        return this.generatorStructure.toFile();
    }
}
