package pl.itgolo.libs.updategradle.Actions;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

/**
 * The type Download update app.
 */
public class DownloadUpdateApp {

    /**
     * The Dir update app.
     */
    File dirUpdateApp;

    /**
     * The Url update app.
     */
    URL urlUpdateApp;


    /**
     * Instantiates a new Download update app.
     *
     * @param dirApp       the dir app
     * @param urlUpdateApp the url update app
     */
    public DownloadUpdateApp(File dirApp, URL urlUpdateApp) {
        this.dirUpdateApp = new File(dirApp, "app/update");
        this.urlUpdateApp = urlUpdateApp;
    }

    /**
     * Download new version boolean.
     *
     * @return the boolean
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public Boolean downloadNewVersion() throws IOException, InterruptedException {
        Files.createDirectories(dirUpdateApp.toPath());
        DownloadVersion downloadVersion = new DownloadVersion(dirUpdateApp, urlUpdateApp);
        return downloadVersion.downloadNewVersion("0.0.0.0");
    }
}
