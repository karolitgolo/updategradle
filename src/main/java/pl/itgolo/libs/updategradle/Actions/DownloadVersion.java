package pl.itgolo.libs.updategradle.Actions;

import com.google.gson.Gson;
import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Download version.
 */
public class DownloadVersion {

    /**
     * The Dir app.
     */
    File dirApp;

    /**
     * The Url app.
     */
    URL urlApp;


    /**
     * The Version.
     */
    String version;
    private int timeoutWaitClose;


    /**
     * Instantiates a new Download version.
     *
     * @param dirApp the dir app
     * @param urlApp the url app
     */
    public DownloadVersion(File dirApp, URL urlApp) {
        this.dirApp = dirApp;
        this.urlApp = urlApp;
        this.timeoutWaitClose = 120;
    }

    /**
     * Download new version boolean.
     *
     * @param versionToCompare the version to compare
     * @return the boolean
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public Boolean downloadNewVersion(String versionToCompare) throws IOException, InterruptedException {
        RemoteNewVersion remoteNewVersion = new RemoteNewVersion(this.urlApp.toString() + "/update.json", 25);
        if (remoteNewVersion.hasNewVersion(versionToCompare)){
            String newVersion = remoteNewVersion.getVersion();
            LogService.log("DownloadVersion newVersion: " + newVersion);
            return download(newVersion);
        } else {
            LogService.log("DownloadVersion has not new version versionToCompare: " + versionToCompare);
        }
        return false;
    }

    /**
     * Download boolean.
     *
     * @param version the version
     * @return the boolean
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public Boolean download(String version) throws IOException, InterruptedException {
        this.version = version;
        if (existVersion()){
            LogService.log("DownloadVersion exist structure: " + version);
            Map<String, String> structureRemote = buildStructureRemote();
            LogService.log("DownloadVersion build remote structure");
            Map<String, String> structure = buildStructure();
            LogService.log("DownloadVersion build structure");
            updateFiles(structure, structureRemote);
            LogService.log("DownloadVersion Updated files");
            return true;
        } else {
            LogService.log("DownloadVersion not exist structure: " + version);
        }
        return false;
    }

    private void updateFiles(Map<String, String> structure, Map<String, String> structureRemote) throws IOException {
        for (Map.Entry<String, String> entry : structureRemote.entrySet()) {
            String relativeFileRemote = entry.getKey();
            String md5Remote = entry.getValue();
            if (isDirectoryRemote(md5Remote)){
                Files.createDirectories(Paths.get(dirApp.getCanonicalPath() + "/" + relativeFileRemote));
            } else if (isFileRemote(md5Remote)) {
                if (mustUpdateFile(structure, relativeFileRemote, md5Remote)){
                    downloadFile(relativeFileRemote);
                }
            }
        }
    }

    private void downloadFile(String relativeFileRemote) throws IOException {
       // LogService.log("DownloadVersion downloadFile relativeFileRemote: " + relativeFileRemote);
        String urlFileRemote = urlApp.toString() + String.format("/files/%1$s/%2$s", version, relativeFileRemote);
        File file = new File(dirApp, relativeFileRemote);
        Files.createDirectories(Paths.get(file.getParent()));
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlFileRemote).openStream());
            fout = new FileOutputStream(file);
            final byte data[] = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
            }
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            if (in != null) {
                in.close();
            }
            if (fout != null) {
                fout.close();
            }
        }
    }



    private Boolean mustUpdateFile(Map<String, String> structure, String relativeFileRemote, String md5Remote) {
        for (Map.Entry<String, String> entry : structure.entrySet()) {
            String relativeFile = entry.getKey();
            String md5 = entry.getValue();
            if (relativeFileRemote.equals(relativeFile) && !md5Remote.equals(md5)){
                return true;
            } else if (relativeFileRemote.equals(relativeFile) && md5Remote.equals(md5)){
                return false;
            }
        }
        return true;
    }

    private Boolean isFileRemote(String md5) {
        return md5.matches("\\p{XDigit}+");
    }

    private Boolean isDirectoryRemote(String md5) {
        return md5.isEmpty();
    }

    private Map<String,String> buildStructure() throws IOException, InterruptedException {
        GeneratorStructure generatorStructure = new GeneratorStructure(dirApp.getCanonicalPath());
        generatorStructure.setTimeoutWaitClose(this.timeoutWaitClose);
        return generatorStructure.toMap();
    }

    @SuppressWarnings("unchecked")
    private Map<String,String> buildStructureRemote() {
        String structureJson = RemoteNewVersion.getJson(this.urlApp.toString() + String.format("/files/%1$s-structure.json", version), 25);
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        return  (Map<String,String>) gson.fromJson(structureJson, map.getClass());
    }

    /**
     * Exist version boolean.
     *
     * @return the boolean
     * @throws MalformedURLException the malformed url exception
     */
    Boolean existVersion() throws MalformedURLException {
        URL urlStructureJson = new URL(this.urlApp.toString() + String.format("/files/%1$s-structure.json", version));
        try {
            HttpURLConnection conn = (HttpURLConnection) urlStructureJson.openConnection();
            Integer codeResp = conn.getResponseCode();
            return codeResp.equals(200);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sets timeout wait close.
     *
     * @param timeoutWaitClose the timeout wait close
     */
    public void setTimeoutWaitClose(int timeoutWaitClose) {
        this.timeoutWaitClose = timeoutWaitClose;
    }
}
