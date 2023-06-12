package texteditor.app.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.python.core.PyException;
import org.python.util.*;

import texteditor.api.Plugin;
import texteditor.app.model.Script;

/** 
 * The plugins class handles loading and storing plugins and scripts.
 * 
 * @author Rohan Khayech
 */
public class PluginManager {

    /** The current locale. */
    private Locale locale;
    /** The resource bundle containing the application's localised strings. */
    private ResourceBundle bundle;
    /** Reference to the API. */
    private APIProvider api;
    /** Reference to the FileIO controller. */
    private FileIO fileio;
    /** List of loaded plugins. */
    private ObservableList<Plugin> plugins = FXCollections.observableList(new LinkedList<>());
    /** List of loaded scripts. */
    private ObservableList<Script> scripts = FXCollections.observableList(new LinkedList<>());

    /**
     * Constructs a new Plugin Manager.
     * @param locale The current locale.
     * @param api Reference to the API.
     * @param fileio Reference to the FileIO controller.
     */
    public PluginManager(Locale locale, APIProvider api, FileIO fileio) {
        this.api = api;
        this.fileio = fileio;
        this.locale = locale;
        this.bundle = ResourceBundle.getBundle("bundle", this.locale);
    }

    /**
     * @return The list of loaded plugins.
     */
    public ObservableList<Plugin> getPlugins() {
        return plugins;
    }

    /**
     * @return The list of loaded scripts.
     */
    public ObservableList<Script> getScripts() {
        return scripts;
    }

    /**
     * Loads a plugin with the specified class name.
     * @param className The fully qualified name of the plugin class to load.
     * @throws IllegalArgumentException If there is an error loading the plugin.
     */
    public void loadPlugin(String className) throws IllegalArgumentException {
        try {
            Class<?> cls = Class.forName(className);
            Plugin plugin = (Plugin) cls.getConstructor().newInstance();
            plugins.add(plugin);
            plugin.start(api);
        } catch (ReflectiveOperationException | ClassCastException e) {
            throw new IllegalArgumentException(bundle.getString("error_loading_plugin"));
        }
    }

    /**
     * Loads and runs a python script from file.
     * @param file The filename of the script to load.
     * @throws IOException If there was an error loading the script.
     * @throws PyException If there was an error running the script.
     */
    public void loadScript(File file) throws IOException, PyException {

        // Set filename as default script name.
        Script script = new Script(file.getName());
        
        String code = fileio.load(file,"UTF-8");

        // Initialise the interpreter
        PythonInterpreter interpreter = new PythonInterpreter();
        // Bind the API to the script environment
        interpreter.set("api", api);
        // Bind the script object to the script environment, to allow script to report its name.
        interpreter.set("script",script);
        // Run the script
        interpreter.exec(code);

        // Close the interpreter once finished
        interpreter.close();

        // Add the script to the list.
        scripts.add(script);
    }
        
}
