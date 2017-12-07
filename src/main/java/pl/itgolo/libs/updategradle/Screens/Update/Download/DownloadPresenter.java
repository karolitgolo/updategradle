package pl.itgolo.libs.updategradle.Screens.Update.Download;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import pl.itgolo.libs.updategradle.Actions.DownloadVersion;
import pl.itgolo.libs.updategradle.Actions.LaunchMainApp;
import pl.itgolo.libs.updategradle.Actions.UpdateSilent;
import pl.itgolo.libs.updategradle.Screens.Abstracts.Presenter;
import pl.itgolo.libs.updategradle.Screens.Main;
import pl.itgolo.libs.updategradle.Services.LogService;

import javax.inject.Inject;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Download presenter.
 */
public class DownloadPresenter extends Presenter implements Initializable {

    @Inject
    private String appTitle;

    @FXML
    Label appExternalTitle;

    /**
     * The Progress file.
     */
    @FXML
    ProgressBar progressFile;

    /**
     * The Progress all.
     */
    @FXML
    ProgressBar progressAll;

    /**
     * The Relative file path.
     */
    @FXML
    Label relativeFilePath;
    /**
     * The Old version.
     */
    @FXML
    Label oldVersion;
    /**
     * The New version.
     */
    @FXML
    Label newVersion;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LogService.log("updategradle GUI initialize download presenter");
        appExternalTitle.setText(appTitle);
        if (Main.argsService != null){
            if (Main.argsService.hasArg("--appTitle")){
                appExternalTitle.setText(Main.argsService.getValueArg("--appTitle"));
            }
        }
        DownloadVersion.oldVersionProperty.addListener((v, o, n) ->{
            taskSync(()->{
                oldVersion.setText(n);
            });
        });
        DownloadVersion.newVersionProperty.addListener((v, o, n) ->{
            taskSync(()->{
                newVersion.setText(n);
            });
        });
        DownloadVersion.relativeFilePathProperty.addListener((v, o, n) ->{
            taskSync(()->{
                relativeFilePath.setText(n);
            });
        });
        DownloadVersion.progressFileProperty.addListener((v, o, n) ->{
            taskSync(()->{
                progressFile.setProgress(n.doubleValue());
            });
        });
        DownloadVersion.progressAllProperty.addListener((v, o, n) ->{
            taskSync(()->{
                progressAll.setProgress(n.doubleValue());
            });
        });
        //DownloadVersion.newVersionProperty.bind(newVersion.textProperty());
        //DownloadVersion.relativeFilePathProperty.bindBidirectional(relativeFilePath.textProperty());
    }

    /**
     * Set on shown.
     */
    @Override
    protected void setOnShown() {

        LogService.log("updategradle GUI shown download presenter");
        taskAsyncAndSync(()->{
            try {
                UpdateSilent updateSilent = new UpdateSilent(Main.argsService);
                Boolean updated = updateSilent.update();
                Thread.sleep(5000);
                if (updated){
                    LogService.log("updategradle GUI updated ExternalApp");
                } else {
                    LogService.log("updategradle GUI not updated ExternalApp");
                }
                LaunchMainApp launchMainApp = new LaunchMainApp(Main.argsService, updated);
                launchMainApp.launch();

            } catch (Exception e) {
                e.printStackTrace();
                LogService.log(e);
            }
            System.out.println("NEW VALUE FROM PROPERTY");
        }, ()->{
            Platform.exit();
        });
    }
}
