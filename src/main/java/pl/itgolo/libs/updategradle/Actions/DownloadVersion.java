package pl.itgolo.libs.updategradle.Actions;

import com.google.gson.Gson;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
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

   public static SimpleStringProperty oldVersionProperty = new SimpleStringProperty("");
    public static  SimpleStringProperty newVersionProperty = new SimpleStringProperty("");
    public static  SimpleStringProperty relativeFilePathProperty = new SimpleStringProperty("");
    public static SimpleDoubleProperty progressFileProperty = new SimpleDoubleProperty(0.0);
    public static  SimpleDoubleProperty progressAllProperty = new SimpleDoubleProperty(0.0);

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
            LogService.log("Version to compare ExternalApp: " + versionToCompare);
            oldVersionProperty.set(versionToCompare);
            String newVersion = remoteNewVersion.getVersion();
            LogService.log("Detect newVersion ExternalApp: " + newVersion);
            return download(newVersion);
        } else {
            LogService.log("Has not new version versionToCompare: " + versionToCompare);
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
            newVersionProperty.set(version);
            LogService.log("Exist remote structure externalApp: " + version);
            Map<String, String> structureRemote = buildStructureRemote();
            LogService.log("Build remote structure externalApp");
            Map<String, String> structure = buildStructure();
            LogService.log("Build structure externalApp");
            updateFiles(structure, structureRemote);
            LogService.log("Downloaded new files of externalApp");
            return true;
        } else {
            LogService.log("Not exist remote structure externalApp: " + version);
        }
        return false;
    }

    private void updateFiles(Map<String, String> structure, Map<String, String> structureRemote) throws IOException, InterruptedException {
        int allFiles = structureRemote.size();
        int count = 0;
        for (Map.Entry<String, String> entry : structureRemote.entrySet()) {
            count++;
            setProgressProperty(progressAllProperty, allFiles, count);
            String relativeFileRemote = entry.getKey();
            String md5Remote = entry.getValue();
            if (isDirectoryRemote(md5Remote)){
                Files.createDirectories(Paths.get(dirApp.getCanonicalPath() + "/" + relativeFileRemote));
            } else if (isFileRemote(md5Remote)) {
                if (mustUpdateFile(structure, relativeFileRemote, md5Remote)){
                    relativeFilePathProperty.set(relativeFileRemote);
                    LogService.log("Download new version file: " + relativeFileRemote);
                    downloadFile(relativeFileRemote);
                }
            }
        }
    }

    private void setProgressProperty(SimpleDoubleProperty doubleProperty, int fileWeight, final double count) throws InterruptedException {
        Long nowProgress = Math.round((count / fileWeight)* 100.0);
        if (!nowProgress.equals(progressFileProperty.get())) {
            doubleProperty.set(new Double(nowProgress/100.0));
        }
    }

    private void downloadFile(String relativeFileRemote) throws IOException, InterruptedException {
        String urlFileRemote = urlApp.toString() + String.format("/files/%1$s/%2$s", version, relativeFileRemote);
        File file = new File(dirApp, relativeFileRemote);
        Files.createDirectories(Paths.get(file.getParent()));
        Integer fileWeight = getFileWeightRemote(urlFileRemote).intValue();
        BufferedInputStream in = null;
        FileOutputStream fout = null;
        try {
            in = new BufferedInputStream(new URL(urlFileRemote).openStream());
            fout = new FileOutputStream(file);
            final byte data[] = new byte[1024];
            int count;
            double countBytes = 0.0;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fout.write(data, 0, count);
                countBytes +=count;
                setProgressProperty(progressFileProperty,fileWeight, countBytes);
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



    private Long getFileWeightRemote(String urlFileRemote) throws IOException, InterruptedException {
        URL url = new URL(urlFileRemote);
        HttpURLConnection conn = DeployApp.buildHttpUrlConnection(url);
        return conn.getContentLengthLong();
    }

    private Boolean mustUpdateFile(Map<String, String> structure, String relativeFileRemote, String md5Remote) {
        LogService.log("Check new version file: " + relativeFileRemote);
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
