package pl.itgolo.libs.updategradle.Extensions;

/**
 * The type Update plugin extension.
 */
public class UpdatePluginExtension {
    /**
     * The New version.
     */
    public String newVersion = "1.0.0.0a";
    /**
     * The Url app.
     */
    public String urlApp = "http://apps.itgolo.pl/updategradle";
    /**
     * The Remote new version timeout.
     */
    public Integer remoteNewVersionTimeout = 10;
    /**
     * The Dir release unpack app files.
     */
    public String dirReleaseUnpackAppFiles = "app/temp/updategradle/app";
    /**
     * The Force upload.
     */
    public Boolean forceUpload = false;
    /**
     * The Remote dir app.
     */
    public String remoteDirApp = "/";
    /**
     * The Ftp host.
     */
    public String ftpHost = "";
    /**
     * The Ftp port.
     */
    public Integer ftpPort = 21;
    /**
     * The Ftp user.
     */
    public String ftpUser = "";
    /**
     * The Ftp password.
     */
    public String ftpPassword = "";

    /**
     * The Validate update.
     */
    public Boolean validateUpdate = true;
}
