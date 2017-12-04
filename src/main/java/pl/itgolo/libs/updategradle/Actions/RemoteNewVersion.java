package pl.itgolo.libs.updategradle.Actions;

import com.google.gson.Gson;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Remote new version.
 */
public class RemoteNewVersion {

    private String updateJsonUrlFile;
    private Integer remoteNewVersionTimeout;

    /**
     * Instantiates a new Remote new version.
     *
     * @param updateJsonUrlFile       the update json url file
     * @param remoteNewVersionTimeout the remote new version timeout
     */
    public RemoteNewVersion(String updateJsonUrlFile, Integer remoteNewVersionTimeout) {
        this.updateJsonUrlFile = updateJsonUrlFile;
        this.remoteNewVersionTimeout = remoteNewVersionTimeout;
    }

    /**
     * Has new version boolean.
     *
     * @param versionToCompare the version to compare
     * @return the boolean
     */
    public Boolean hasNewVersion(String versionToCompare){
        DefaultArtifactVersion remoteVersion = new DefaultArtifactVersion(getVersion());
        DefaultArtifactVersion appVersion = new DefaultArtifactVersion(versionToCompare);
        return appVersion.compareTo(remoteVersion) < 0;
    }

    /**
     * Gets version.
     *
     * @return the version
     */
    @SuppressWarnings("unchecked")
    public String getVersion() {
        String json = getJson();
        if (json == null){
            return null;
        }
        Gson gson = new Gson();
        Map<String, String> map = new HashMap<>();
        map = (Map<String, String>) gson.fromJson(json, map.getClass());
        return map.get("version");
    }


    private String getJson() {
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(updateJsonUrlFile);
            URLConnection urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(this.remoteNewVersionTimeout * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                InputStreamReader in = new InputStreamReader(urlConn.getInputStream(), Charset.forName("UTF-8"));
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
                in.close();
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
    }
}
