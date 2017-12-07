package pl.itgolo.libs.updategradle.Screens;

import com.airhacks.afterburner.injection.Injector;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.itgolo.libs.updategradle.Actions.LaunchMainApp;
import pl.itgolo.libs.updategradle.Screens.Update.UpdateView;
import pl.itgolo.libs.updategradle.Services.ArgsService;
import pl.itgolo.libs.updategradle.Services.LogService;

/**
 * The type Main.
 */
public class Main extends Application {

    public static ArgsService argsService;

    public static Boolean updated = false;

    @Override
    public void start(Stage stage) throws Exception {
        UpdateView updateView = new UpdateView();
        Scene scene = new Scene(updateView.getView());
        stage.setTitle("Aktualizacja");
        final String uri = getClass().getResource("main.css").toExternalForm();
        scene.getStylesheets().add(uri);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        if (argsService != null){
            LogService.log("Launch ExternalApp after updated from updategradle GUI with arg --updated: " + updated.toString());
            LaunchMainApp launchMainApp = new LaunchMainApp(argsService, updated);
            launchMainApp.launch();
        }
        Injector.forgetAll();
    }

    /**
     * Main.
     *
     * @param args the args
     */
    public static void main(String[] args){
        launch(args);
    }
}
