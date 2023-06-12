package texteditor.app.view;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import org.python.core.PyException;

import texteditor.api.Plugin;
import texteditor.app.ParseException;
import texteditor.app.controller.APIProvider;
import texteditor.app.controller.FileIO;
import texteditor.app.controller.PluginManager;
import texteditor.app.model.Script;

/**
 * Main GUI for the application.
 * 
 * @author Rohan Khayech
 * @author David Cooper
 */
public class GUI
{
    /** Text area containing the editable text. */
    private TextArea textArea = new TextArea();
    /** Main tool bar of the application. */
    private ToolBar toolbar;
    /** Dialog used to select file encoding. */
    private Dialog<String> encodingDialog;
    /** The resource bundle containing the application's localised strings. */
    private ResourceBundle bundle;
    /** Reference to the plugin manager. */
    private PluginManager pluginManager;
    /** Reference to the api provider. */
    private APIProvider api;
    /** Reference to the keypress handler. */
    private KeyPressHandler kpHandler;
    /** Reference to the File I/O controller. */
    private FileIO fileIO;
    /** Reference to the application's stage. */
    private Stage stage;

    /**
     * Constructs a new GUI object.
     * @param stage The JavaFX stage.
     * @param locale The current locale.
     * @param fileio Reference to the File I/O controller.
     * @param pluginManager Reference to the plugin manager.
     * @param kpHandler Reference to the keypress handler.
     */
    public GUI(Stage stage, Locale locale, FileIO fileio, PluginManager pluginManager, APIProvider api, KeyPressHandler kpHandler) {
        this.stage = stage;
        this.fileIO = fileio;
        this.pluginManager = pluginManager;
        this.api = api;
        this.kpHandler = kpHandler;

        // Get the resource bundle for the specified locale.
        this.bundle = ResourceBundle.getBundle("bundle", locale);
    }
    
    /**
     * Displays the GUI.
     */
    public void display()
    {
        // Setup Window.
        stage.setTitle(bundle.getString("text_editor"));
        stage.setMinWidth(800);

        // Create toolbar
        Button openButton = new Button(bundle.getString("open"));
        Button saveButton = new Button(bundle.getString("save"));
        Button pluginsButton = new Button(bundle.getString("plugins"));
        Button scriptsButton = new Button(bundle.getString("scripts"));
        toolbar = new ToolBar(openButton,saveButton,pluginsButton,scriptsButton);

        // Subtle user experience tweaks
        toolbar.setFocusTraversable(false);
        toolbar.getItems().forEach(btn -> btn.setFocusTraversable(false));
        textArea.setStyle("-fx-font-family: 'monospace'"); // Set the font
        
        // Add the main parts of the UI to the window.
        BorderPane mainBox = new BorderPane();
        mainBox.setTop(toolbar);
        mainBox.setCenter(textArea);
        Scene scene = new Scene(mainBox);        
        
        // Setup Button event handlers.
        openButton.setOnAction(event -> openFile());
        saveButton.setOnAction(event -> saveFile());
        pluginsButton.setOnAction(event -> showPluginsDialog());
        scriptsButton.setOnAction(event -> showScriptsDialog());

        // Notify text modification handlers when text modified.
        textArea.textProperty().addListener((object, oldValue, newValue) -> {
            //This must run after caret position is updated to allow API calls to get the correct postion.
            //Hence using Platform.runLater to ensure caret is updated.
            Platform.runLater(()->api.notifyModifyEvent());
        });

        // Set up global keypress handler
        // Load key mappings.
        try {
            kpHandler.loadKeyMappings();
        } catch (IOException e) { // Errors loading the keymap file.
            new Alert(Alert.AlertType.ERROR, String.format(bundle.getString("error_loading_keymaps")+" %s", e.getMessage()), ButtonType.CLOSE).showAndWait();
        } catch (ParseException e) { // Errors parsing the keymap file.
            new Alert(Alert.AlertType.ERROR, String.format(bundle.getString("error_parsing_keymaps")), ButtonType.CLOSE).showAndWait();
        }

        // Set key event handler.
        scene.setOnKeyPressed(keyEvent -> {
            kpHandler.handleKeyEvent(keyEvent);
        });
        
        // Show window.
        stage.setScene(scene);
        stage.sizeToScene();
        stage.show();
    }
    
    /**
     * @return The TextArea object containing the editable text.
     */
    public TextArea getTextArea() {
        return textArea;
    }

    /**
     * @return The ToolBar object holding the application's main buttons.
     */
    public ToolBar getToolBar() {
        return toolbar;
    }
    
    /** Displays the plugin list dialog. */
    private void showPluginsDialog()
    {        
        Button loadButton = new Button(bundle.getString("load"));
        ToolBar toolBar = new ToolBar(loadButton);
        
        // Set the button press handler.
        loadButton.setOnAction(event -> loadPlugin());
        
        // Setup the list to display the updated list of loaded plugins.
        ListView<Plugin> listView = new ListView<>(pluginManager.getPlugins());
        
        // Set up and display dialog
        BorderPane box = new BorderPane();
        box.setTop(toolBar);
        box.setCenter(listView);
        
        var dialog = new Dialog<>();
        dialog.setTitle(bundle.getString("plugins"));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    /** Displays the script list dialog. */
    private void showScriptsDialog() {
        Button loadButton = new Button(bundle.getString("load"));
        ToolBar toolBar = new ToolBar(loadButton);

        // Set the button press handler
        loadButton.setOnAction(event -> loadScript());

        // Setup the list to display the updated list of loaded scripts.
        ListView<Script> listView = new ListView<>(pluginManager.getScripts());

        // Set up and display dialog
        BorderPane box = new BorderPane();
        box.setTop(toolBar);
        box.setCenter(listView);

        var dialog = new Dialog<>();
        dialog.setTitle(bundle.getString("scripts"));
        dialog.getDialogPane().setContent(box);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.showAndWait();
    }

    /**
     * Internal method for displaying the encoding dialog and retrieving the name of
     * the chosen encoding.
     * 
     * @return The chosen encoding method. 
     */
    private String getEncoding() {
        if (encodingDialog == null) {
            var encodingComboBox = new ComboBox<String>();
            var content = new FlowPane();
            encodingDialog = new Dialog<>();
            encodingDialog.setTitle(bundle.getString("select_encoding"));
            encodingDialog.getDialogPane().setContent(content);
            encodingDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
            encodingDialog.setResultConverter(btn -> (btn == ButtonType.OK) ? encodingComboBox.getValue() : null);

            content.getChildren().setAll(new Label(bundle.getString("encoding")), encodingComboBox);

            encodingComboBox.getItems().setAll("UTF-8", "UTF-16", "UTF-32");
            encodingComboBox.setValue("UTF-8");
        }
        return encodingDialog.showAndWait().orElse(null);
    }

    /**
     * Prompts the user to select an encoding and a file to load.
     */
    private void openFile() {
        // Prompt the user to select an encoding.
        String encoding = getEncoding();
        if (encoding != null) {
            // Prompt the user to select a filename.
            FileChooser fileDialog = new FileChooser();
            fileDialog.setInitialDirectory(new File(System.getProperty("user.dir") + "/.."));
            fileDialog.setTitle(bundle.getString("open"));

            File file = fileDialog.showOpenDialog(stage);
            if (file != null) {
                try {
                    // Load the file.
                    String contents = fileIO.load(file, encoding);
                    // Set the editable text to the contents of the file.
                    textArea.setText(contents);
                } catch (IOException e) { // Errors while loading the file.
                    new Alert(Alert.AlertType.ERROR,
                            String.format(bundle.getString("error_loading")+" %s: %s", e.getClass().getName(), e.getMessage()),
                            ButtonType.CLOSE).showAndWait();
                }
            }
        }
    }

    /**
     * Prompts the user to select an encoding and select a filename to save the file as.
     */
    private void saveFile() {
        // Prompt the user to select an encoding.
        String encoding = getEncoding();
        if (encoding != null) {
            // Prompt the user to select a filename.
            FileChooser fileDialog = new FileChooser();
            fileDialog.setInitialDirectory(new File(System.getProperty("user.dir") + "/.."));
            fileDialog.setTitle(bundle.getString("save"));

            File file = fileDialog.showSaveDialog(stage);
            if (file != null) {
                try {
                    // Save the edited text to file using the specified encoding.
                    fileIO.save(file, textArea.getText(), encoding);
                } catch (IOException e) { // Errors while writing the file.
                    new Alert(Alert.AlertType.ERROR,
                            String.format(bundle.getString("error_saving")+" %s", e.getMessage()),
                            ButtonType.CLOSE).showAndWait();
                }
            }
        }
    }

    /**
     * Prompts the user to select a script file to load into the application.
     */
    private void loadScript() {
        // Prompt the user to select a script file.
        FileChooser fileDialog = new FileChooser();
        fileDialog.setInitialDirectory(new File(System.getProperty("user.dir") + "/../scripts"));
        fileDialog.setTitle(bundle.getString("open"));

        File file = fileDialog.showOpenDialog(stage);
        if (file != null) {
            try {
                // Load and run the script.
                pluginManager.loadScript(file);
            } catch (IOException e) { // Errors while loading the script file.
                new Alert(Alert.AlertType.ERROR, String.format(bundle.getString("error_loading")+" %s",e.getMessage()), ButtonType.CLOSE).showAndWait();
            } catch (PyException e) { // External errors while running the script.
                new Alert(Alert.AlertType.ERROR, String.format(bundle.getString("error_running_script")+" %s",e.toString()), ButtonType.CLOSE).showAndWait();
            }
        }
    }

    /**
     * Prompts the user to enter a class name of a plugin to load.
     */
    private void loadPlugin() {
        // Prompts the user for a class name.
        var dialog = new TextInputDialog();
        dialog.setTitle(bundle.getString("add_plugin"));
        dialog.setHeaderText(bundle.getString("plugin_prompt"));

        String className = (String) dialog.showAndWait().orElse(null);
        if (className != null) {
            try {
                // Load the plugin.
                pluginManager.loadPlugin(className);
            } catch (IllegalArgumentException e) { // Errors loading invalid plugins.
                new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.CLOSE).showAndWait();
            }
        } 
    }
}