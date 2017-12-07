package pl.itgolo.libs.updategradle.Screens.Abstracts;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The type Presenter.
 */
public abstract class Presenter {

    /**
     * Instantiates a new Presenter.
     */
    public Presenter() {
        Platform.runLater(this::setOnShown);
    }

    protected void taskSync(Runnable taskSync){
        if (Platform.isFxApplicationThread()){
            taskSync.run();
        } else {
            Platform.runLater(()->{
                taskSync.run();
            });
        }
    }
    /**
     * Task async and sync.
     *
     * @param taskAsync the task async
     * @param taskSync  the task sync
     */
    protected void taskAsyncAndSync(Runnable taskAsync, Runnable taskSync) {
        SimpleBooleanProperty asyncCompleted = new SimpleBooleanProperty(false);
        asyncCompleted.addListener((v, o, n)->{
            if (n){
                if (Platform.isFxApplicationThread()){
                    taskSync.run();
                } else {
                    Platform.runLater(()->{
                        taskSync.run();
                    });
                }
            }
        });
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(()->{
            taskAsync.run();
            asyncCompleted.set(true);
        });
        executor.shutdown();
    }

    /**
     * Set on shown.
     */
    protected void setOnShown(){

    }
}
