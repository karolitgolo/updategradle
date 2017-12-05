package pl.itgolo.libs.updategradle.Actions;

import pl.itgolo.libs.updategradle.Services.ArgsService;

import java.io.IOException;
import java.io.InputStream;

/**
 * The type Launch main app.
 */
public class LaunchMainApp {
    private ArgsService argsService;
    private Boolean updated;
    /**
     * The Command return main app.
     */
    String commandReturnMainApp;

    /**
     * Instantiates a new Launch main app.
     *
     * @param argsService the args service
     * @param updated     the updated
     */
    public LaunchMainApp(ArgsService argsService, Boolean updated) {
        this.argsService = argsService;
        this.updated = updated;
        commandReturnMainApp = argsService.getValueArg("--commandReturnMainApp");
    }

    /**
     * Launch.
     *
     * @throws InterruptedException the interrupted exception
     * @throws IOException          the io exception
     */
    public void launch() throws InterruptedException, IOException {
        Process proc = Runtime.getRuntime().exec(commandReturnMainApp + " --updated=" + updated.toString());
        proc.waitFor();
        InputStream in = proc.getInputStream();
        InputStream err = proc.getErrorStream();
        byte b[]=new byte[in.available()];
        in.read(b,0,b.length);
        System.out.println(new String(b));
        byte c[]=new byte[err.available()];
        err.read(c,0,c.length);
        System.out.println(new String(c));
        System.exit(0);
    }
}
