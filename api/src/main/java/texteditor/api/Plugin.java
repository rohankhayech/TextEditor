package texteditor.api;

/**
 * Abstract class defining a plugin with a start method and a display name.
 * 
 * @author Rohan Khayech
 */
public abstract class Plugin {
    /**
     * Starts the plugin.
     * @param api Reference to the API.
     */
    public abstract void start(API api);
    
    /**
     * @return The plugin's display name.
     */
    public abstract String getName();

    /**
     * @return A user-readable string representation of the plugin.
     */
    @Override
    public String toString() {
        return getName();
    }
}
