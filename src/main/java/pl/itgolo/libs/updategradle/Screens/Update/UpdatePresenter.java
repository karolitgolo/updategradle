package pl.itgolo.libs.updategradle.Screens.Update;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import pl.itgolo.libs.updategradle.Screens.Abstracts.Presenter;
import pl.itgolo.libs.updategradle.Screens.Main;
import pl.itgolo.libs.updategradle.Screens.Update.Download.DownloadView;
import pl.itgolo.libs.updategradle.Screens.Update.Wait.WaitView;
import pl.itgolo.libs.updategradle.Services.LogService;
import pl.itgolo.libs.updategradle.Services.SilentService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Update presenter.
 */
public class UpdatePresenter extends Presenter implements Initializable {

    /**
     * The Wait view.
     */
    WaitView waitView;

    DownloadView downloadView;

    /**
     * The Screen box.
     */
    @FXML
    Pane screenBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        waitView = new WaitView();
        downloadView = new DownloadView();
    }

    /**
     * Wait closed caller app.
     *
     * @throws Exception the exception
     */
    void waitClosedCallerApp() throws Exception {
        LogService.log("updategradle GUI wait for closed ExternalApp");
        SilentService.waitExitProcessMainApp(Main.argsService);
    }

    @Override
    protected void setOnShown() {
        screenBox.getChildren().add(waitView.getView());
        taskAsyncAndSync(()->{
            try {
                waitClosedCallerApp();
            } catch (Exception e) {
                e.printStackTrace();
                LogService.log(e);
            }
        },()->{
            screenBox.getChildren().remove(waitView.getView());
            downloadApp();
        });
    }

    private void downloadApp() {
        screenBox.getChildren().add(downloadView.getView());
        taskAsyncAndSync(()->{
            try {
                Thread.sleep(17000);
            } catch (Exception e) {
                e.printStackTrace();
                LogService.log(e);
            }
        },()->{
            Platform.exit();
        });
    }
}