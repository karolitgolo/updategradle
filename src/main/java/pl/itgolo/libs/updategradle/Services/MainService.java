package pl.itgolo.libs.updategradle.Services;

import javafx.application.Application;
import pl.itgolo.libs.updategradle.Screens.Main;

import java.io.File;
import java.io.IOException;

/**
 * The type Main service.
 */
public class MainService {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        ArgsService argsService = new ArgsService(args);
        LogService.launch(argsService.hasArg("--debug"), new File(argsService.getValueArg("--appDir")));
        if (argsService.hasArg("--test")){
            LogService.log("Launched updategradle.jar with mode test");
        }
        if (argsService.hasArg("--debug")){
            LogService.log("Launched updategradle.jar with mode debug");
        }
        if (argsService.hasArg("--silent")){
            LogService.log("Launched updategradle.jar with mode silent");
            SilentService silentService = new SilentService();
            silentService.update(argsService);
        } else {
            LogService.log("Launched updategradle.jar with mode GUI");
            try {
                Main.argsService = argsService;
                Application.launch(Main.class, args);
            } catch (Exception e) {
                LogService.log(e);
            }
        }
    }
}
