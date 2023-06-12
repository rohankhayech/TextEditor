package texteditor.app;

import java.util.Locale;
import javafx.application.Application;
import javafx.stage.Stage;

import texteditor.app.controller.APIProvider;
import texteditor.app.controller.FileIO;
import texteditor.app.controller.PluginManager;
import texteditor.app.view.GUI;
import texteditor.app.view.KeyPressHandler;

/**
 * The TextEditor application allows basic editing of text with plugin/script
 * support and customisable key mappings. The main class of the application.
 * 
 * @author Rohan Khayech
 */
public class TextEditor extends Application {

    /** The current locale. */
    private Locale locale;
    /** The plugin manager. */
    private PluginManager pluginManager;
    /** The keypress handler. */
    private KeyPressHandler kpHandler;
    /** The File I/O controller. */
    private FileIO fileIO;
    /** The main GUI. */
    private GUI ui;
    /** The API provider. */
    private APIProvider api;
 
    /**
     * Main line of the application.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /** Starts the text editor application. */
    @Override
    public void start(Stage stage) {
        // Get the locale based on locale parameter, or use default locale if not specified.
        String localeString = getParameters().getNamed().get("locale");
        if (localeString != null) {
            locale = Locale.forLanguageTag(localeString);
        } else {
            locale = Locale.getDefault();
        }

        // Construct controller objects.
        api = new APIProvider(locale);
        fileIO = new FileIO();
        pluginManager = new PluginManager(locale, api, fileIO);
        kpHandler = new KeyPressHandler(api);

        // Display the GUI.
        ui = new GUI(stage,locale,fileIO,pluginManager,api,kpHandler);
        api.setUI(ui);
        ui.display();
    }
}
