package pl.itgolo.libs.updategradle.Services;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.codehaus.plexus.util.ExceptionUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Log service.
 */
public class LogService {

    /**
     * The Log file.
     */
    static File logFile;

    /**
     * The App dir.
     */
    static File appDir;

    /**
     * The Last logs from file.
     */
    static SimpleStringProperty lastLogsFromFile = new SimpleStringProperty("");
    /**
     * The Finish logs async from file to console.
     */
    static SimpleBooleanProperty finishLogsAsyncFromFileToConsole = new SimpleBooleanProperty(false);
    private static boolean enable = false;

    /**
     * Log.
     *
     * @param throwable the throwable
     */
    public static void log(Throwable throwable){
        log(throwable.getMessage() + System.lineSeparator() + ExceptionUtils.getStackTrace(throwable));
    }

    /**
     * Log.
     *
     * @param message the message
     */
    public static void log(String message){
        if (!enable){
            return;
        }

        try {

            if (!logFile.exists()){
                logFile.createNewFile();
            }
            // item log:
            // [2017-12-02 12:02:00] - pl.itgolo.libs.updategradle.MyClass::myMethodInClass
            // message: this log message
            String messageLineFirst = String.format("[%1$s] (%2$s::%3$s)%4$s",
                    DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd HH:mm:ss"),
                    getCallerClassName(),
                    getCallerMethodName(),
                    " "
            );
            String messageLineSecond = String.format("%1$s%2$s",
                    message,
                    ""//System.lineSeparator()
            );
            message = messageLineFirst+messageLineSecond + System.lineSeparator();
            Files.write(Paths.get(logFile.getCanonicalPath()), message.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
//            messageLineFirst = ConsoleService.setColorCyan(messageLineFirst);
//            messageLineSecond = ConsoleService.setColorYellow(messageLineSecond);
//            System.out.println(messageLineFirst + messageLineSecond);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets app dir.
     *
     * @param appDir the app dir
     */
    public static void setAppDir(File appDir) {
        LogService.appDir = appDir;
    }

    /**
     * Sets enable.
     *
     * @param enable the enable
     * @throws IOException the io exception
     */
    public static void setEnable(boolean enable) throws IOException {
        if (appDir == null){
            logFile = new File(String.format("app/update/logs_%1$s.txt", DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd")));
        } else {
            logFile = new File(appDir, String.format("app/update/logs/logs_%1$s.txt",DateFormatUtils.format(Calendar.getInstance(), "yyyy-MM-dd")));
        }
        if (!logFile.getParentFile().exists()){
            logFile.getParentFile().mkdirs();
        }

        if (!logFile.exists()){
            logFile.createNewFile();
        }
        LogService.enable = enable;
    }

    /**
     * Launch.
     *
     * @param appDir the app dir
     * @param clear  the clear
     * @throws IOException the io exception
     */
    public static void launch(File appDir, Boolean clear) throws IOException {
        LogService.appDir = appDir;
        setEnable(true);
        if (clear){
            clearLogs();
        }
    }

    /**
     * Launch.
     *
     * @param appDir the app dir
     * @throws IOException the io exception
     */
    public static void launch(File appDir) throws IOException {
        LogService.appDir = appDir;
        setEnable(true);
    }

    /**
     * Launch.
     *
     * @param enable the enable
     * @param appDir the app dir
     * @throws IOException the io exception
     */
    public static void launch(boolean enable, File appDir) throws IOException {
        if (enable){
            LogService.appDir = appDir;
            setEnable(true);
        }
    }

    /**
     * Gets caller class name.
     *
     * @return the caller class name
     */
    public static String getCallerClassName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogService.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getClassName();
            }
        }
        return null;
    }

    /**
     * Gets caller method name.
     *
     * @return the caller method name
     */
    public static String getCallerMethodName() {
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        for (int i=1; i<stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(LogService.class.getName()) && ste.getClassName().indexOf("java.lang.Thread")!=0) {
                return ste.getMethodName();
            }
        }
        return null;
    }

    /**
     * Get log file file.
     *
     * @return the file
     */
    public static File getLogFile(){
        return logFile;
    }

    /**
     * Config by args.
     *
     * @param argsService the args service
     * @throws IOException the io exception
     */
    public static void configByArgs(ArgsService argsService) throws IOException {
        if (argsService.hasArg("--debug")){
            LogService.setEnable(true);
            LogService.setAppDir(new File(argsService.getValueArg("--appDir")));
            for (Map.Entry<String, Object> entry : argsService.toMap().entrySet()) {
                LogService.log("MainService arg: " + entry.getKey() + ", val: " + entry.getValue());
            }
        }
    }

    /**
     * Logs async from file to console.
     *
     * @param timeout                      the timeout
     * @param interruptAsyncByStringInLogs the interrupt async by string in logs
     * @throws IOException the io exception
     */
    public static void logsAsyncFromFileToConsole(Integer timeout, String interruptAsyncByStringInLogs) throws IOException {


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(()->{
            try {
                for (Integer i =0 ; i<10 ; i++){
                    if (logFile.exists()){
                        break;
                    }
                    Thread.sleep(1000);
                }
                for(Integer i = 0 ; i<timeout * 5 ; i++){
                    if (logFile.exists()){
                        String content = new String(Files.readAllBytes(logFile.toPath()));
                        if(lastLogsFromFile.get().isEmpty() && !content.isEmpty()){
                            System.out.println(ConsoleService.setColorCyan(content.trim()));
                            lastLogsFromFile.set(content);
                        } else if (!lastLogsFromFile.get().isEmpty()){
                            String log = content.replace(lastLogsFromFile.get(), "");
                            if (!log.isEmpty()){
                                System.out.println(ConsoleService.setColorCyan(log.trim()));
                            }
                            lastLogsFromFile.set(content);
                        }
                        if (content.contains(interruptAsyncByStringInLogs)){
                            break;
                        }
                    }
                    Thread.sleep(200);
                }
                finishLogsAsyncFromFileToConsole.set(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        executorService.shutdown();
    }


    /**
     * Clear logs.
     *
     * @throws IOException the io exception
     */
    public static void clearLogs() throws IOException {
        if (logFile != null){
            Files.deleteIfExists(logFile.toPath());
        }
    }

    /**
     * Wait logs async from file to console.
     *
     * @throws InterruptedException the interrupted exception
     */
    public static void waitLogsAsyncFromFileToConsole() throws InterruptedException {
        while(!finishLogsAsyncFromFileToConsole.get()){
            Thread.sleep(200);
           }
    }
}
