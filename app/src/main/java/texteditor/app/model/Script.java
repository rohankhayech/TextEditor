package texteditor.app.model;

/**
 * Data structure for storing script names and passing to Python scripts.
 * 
 * @author Rohan Khayech
 */
public class Script {
    /** The script's user-readable name. */
    private String name;

    /**
     * Constructs a String object with the given name.
     * @param name The default name of the script (eg. the filename).
     */
    public Script(String name) { this.name = name; }

    /** @return The script's user-readable name. */
    public String getName() { return name; }

    /**
     * Sets the script's user-readable name.
     * @param name The script's user-readable name.
     */
    public void setName(String name) { this.name = name; }

    /** @return The script's user-readable name. */
    @Override
    public String toString() { return getName(); }
}
