package pl.itgolo.libs.updategradle.Extensions;

/**
 * The type Greeting plugin extension.
 */
public class GreetingPluginExtension {
    /**
     * The Message.
     */
    String message = "Hello from GreetingPlugin";

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
