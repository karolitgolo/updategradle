package pl.itgolo.libs.updategradle.Actions;

import pl.itgolo.libs.updategradle.Services.LogService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * The type Launch update app.
 */
public class LaunchUpdateApp {

    /**
     * The Main dir app.
     */
    File mainDirApp;
    private String versionToCompare;
    private String urlApp;
    private String commandReturnMainApp;
    private Boolean silent;
    private String pid;

    private String appTitle;
    /**
     * The Url app update.
     */
    URL urlAppUpdate;
    private Boolean test;
    private int timeoutWaitClose;
    private Boolean debug;

    /**
     * Instantiates a new Launch update app.
     *
     * @param appTitle             the app title
     * @param urlAppUpdate         the url app update
     * @param mainDirApp           the main dir app
     * @param versionToCompare     the version to compare
     * @param urlApp               the url app
     * @param commandReturnMainApp the command return main app
     * @param silent               the silent
     * @param pid                  the pid
     */
    public LaunchUpdateApp(String appTitle, URL urlAppUpdate, File mainDirApp, String versionToCompare, String urlApp, String commandReturnMainApp, Boolean silent, String pid) {
        this.appTitle = appTitle;
        this.urlAppUpdate = urlAppUpdate;
        this.mainDirApp = mainDirApp;
        this.versionToCompare = versionToCompare;
        this.urlApp = urlApp;
        this.commandReturnMainApp = commandReturnMainApp;
        this.silent = silent;
        this.pid = pid;
        this.timeoutWaitClose = 120;
        this.test = false;
        this.debug = false;
    }

    /**
     * Launch.
     *
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public void launch() throws IOException, InterruptedException {
        DownloadUpdateApp downloadUpdateApp = new DownloadUpdateApp(mainDirApp, urlAppUpdate);
        Boolean downloaded= downloadUpdateApp.downloadNewVersion();
        LogService.log("LaunchUpdateApp: Downloaded updategradle: " + downloaded.toString());
        if (existUpdateApp()){
            LogService.log("LaunchUpdateApp: existUpdateApp= true");
            String command = buildCommandRunUpdateApp();
            LogService.log("LaunchUpdateApp: command: " + command);
            Process proc = Runtime.getRuntime().exec(command);
            if (pid==null){
                proc.waitFor();
                InputStream in = proc.getInputStream();
                InputStream err = proc.getErrorStream();
                byte b[]=new byte[in.available()];
                in.read(b,0,b.length);
                in.close();
                byte c[]=new byte[err.available()];
                err.read(c,0,c.length);
                err.close();
                String error = new String(c);
                LogService.log("LaunchUpdateApp: result info: "+ new String(b));
                if (!error.isEmpty()){
                    LogService.log("LaunchUpdateApp: result error: "+error);
                    throw new IOException("Error from return process running updategradle.jar: \n" + error);
                }
            }
        }
    }

    private String buildCommandRunUpdateApp() throws IOException {
        String pathUpdateApp = new File(mainDirApp.getCanonicalPath() + "/app/update/updategradle.jar").getCanonicalPath();
        String command = String.format("java -jar %1$s --versionToCompare=%2$s --appDir=%3$s " +
                        "--remoteUrl=%4$s --commandReturnMainApp=%5$s --timeoutWaitClose=%6$s --appTitle=%7$s",
                pathUpdateApp,
                versionToCompare,
                "\"" + mainDirApp.getCanonicalPath() + "\"",
                urlApp,
                 "\"" + commandReturnMainApp.replaceAll("\"", "\\\\\"") + "\"",
                timeoutWaitClose,
                appTitle);
        if (silent){
            command += " --silent";
        }
        if (test){
            command += " --test";
        }
        if (pid !=null){
            command += " --pid="+pid;
        }
        if (debug){
            command += " --debug";
        }
        return command;
    }

    private Boolean existUpdateApp() {
        File updateApp = new File(mainDirApp, "/app/update/updategradle.jar");
        return updateApp.exists();
    }

    /**
     * Sets test.
     *
     * @param test the test
     */
    public void setTest(Boolean test) {
        this.test = test;
    }

    /**
     * Sets timeout wait close.
     *
     * @param timeoutWaitClose the timeout wait close
     */
    public void setTimeoutWaitClose(int timeoutWaitClose) {
        this.timeoutWaitClose = timeoutWaitClose;
    }

    /**
     * Sets debug.
     *
     * @param debug the debug
     */
    public void setDebug(Boolean debug) {
        this.debug = debug;
    }
}
