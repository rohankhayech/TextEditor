package texteditor.app.view;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import texteditor.app.controller.APIProvider;
import texteditor.app.model.KeyMapping;
import texteditor.app.model.KeyMapping.KeymapAction;
import texteditor.app.model.KeyMapping.KeymapPosition;
import texteditor.app.KeymapParser;
import texteditor.app.ParseException;

/**
 * Class responsible for handling keypress events and loading custom key mappings from file.
 * 
 * @author Rohan Khayech
 */
public class KeyPressHandler {

    /** The name of the file where custom key mappings are stored. */
    private static final String KEYMAP_FILENAME = "keymap";

    /** Back reference to the API. */
    private APIProvider api;

    /** List of custom key mappings. */
    private List<KeyMapping> keymaps = new LinkedList<>();

    /**
     * Constructs a new KeyPressHandler object.
     * 
     * @param api Back reference to the API.
     */
    public KeyPressHandler(APIProvider api) {
        this.api = api;
    }

    /**
     * Invokes the DSL parser to load and set the custom key mappings from the default file.
     * @throws IOException If the file could not be found or there were errors reading the file.
     * @throws ParseException If there are any errors while parsing the key mappings.
     */
    public void loadKeyMappings() throws IOException, ParseException {
        keymaps.addAll(KeymapParser.parse(KEYMAP_FILENAME));
    }

    /**
     * Handles the given key press event.
     * @param keyEvent The key press event to handle.
     */
    public void handleKeyEvent(KeyEvent keyEvent) {
        handleFunctionKeyPresses(keyEvent);
        handleKeyComboPresses(keyEvent);
    }

    /**
     * Handles function key press events and notifies any function key handlers.
     * @param keyEvent The key press event to handle.
     */
    private void handleFunctionKeyPresses(KeyEvent keyEvent) {
        // Check if the pressed key is a valid function key.
        KeyCode key = keyEvent.getCode();
        if (key.isFunctionKey()) {
            int keyNum;
            switch (key) {
                case F1: keyNum = 1; break;
                case F2: keyNum = 2; break;
                case F3: keyNum = 3; break;
                case F4: keyNum = 4; break;
                case F5: keyNum = 5; break;
                case F6: keyNum = 6; break;
                case F7: keyNum = 7; break;
                case F8: keyNum = 8; break;
                case F9: keyNum = 9; break;
                case F10: keyNum = 10; break;
                case F11: keyNum = 11; break;
                case F12: keyNum = 11; break;
                default: keyNum = 0;
            }
            if (keyNum != 0) {
                // Notify all function keypress handlers that the specified key was pressed.
                api.notifyFunctionKeyPress(keyNum);
            }
        }
    }
    
    /**
     * Handles key combination presses.
     * @param keyEvent The key press event to handle.
     */
    private void handleKeyComboPresses(KeyEvent keyEvent) {
        // Check all custom key mappings.
        for (KeyMapping keymap : keymaps) {
            // Check if the key event matches the key combination.
            if (keymap.matches(keyEvent)) {
                
                // Perform the mapped action.
                KeymapAction action = keymap.getAction();
                KeymapPosition pos = keymap.getPosition();
                String string = keymap.getString();

                if (action == KeymapAction.INSERT) {
                    if (pos == KeymapPosition.CARET) {
                        api.insertText(string);
                    } else if (pos == KeymapPosition.SOL) {
                        api.insertTextAtSOL(string);
                    }
                } else if (action == KeymapAction.DELETE) {
                    if (pos == KeymapPosition.CARET) {
                        api.removeTextBeforeCaret(string);
                    } else if (pos == KeymapPosition.SOL) {
                        api.removeTextAtSOL(string);
                    }
                }
            }
        }
    }
}
