package texteditor.app.model;

import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;

/**
 * Data structure used to store key combinations and their mapped actions and strings.
 * 
 * @author Rohan Khayech
 */
public class KeyMapping {
    /**
     * Enum representing the possible actions a key combo can trigger.
     */
    public static enum KeymapAction {
        /** Insert the specified string. */
        INSERT, 
        /** Delete the specified string.  */
        DELETE
    }

    /**
     * Enum representing the possible positions for a string to be inserted/deleted from.
     */
    public static enum KeymapPosition {
        /** Insert/delete the string at the start of the current line. */
        SOL, 
        /** Insert/delete the string at the current caret position. */
        CARET
    }
    
    /** The key combination. */
    private final KeyCodeCombination keyCombo;

    /** The action to take when the key combo is pressed. */
    private final KeymapAction action;

    /** The string to insert/delete when the key combo is pressed. */
    private final String string;

    /** The position to insert/delete the string when the key combo is pressed. */
    private final KeymapPosition pos;

    /**
     * Constructs a new KeyMapping object.
     * 
     * @param keyCombo The key combination.
     * @param action   The action to take when the key combo is pressed.
     * @param string   The string to insert/delete when the key combo is pressed.
     * @param pos      The position to insert/delete the string when the key combo
     *                 is pressed.
     */
    public KeyMapping(final KeyCodeCombination keyCombo, final KeymapAction action, final String string, final KeymapPosition pos) {
        this.keyCombo = keyCombo;
        this.action = action;
        this.string = string;
        this.pos = pos;
    }

    /**
     * @return The key combination.
     */
    public KeyCodeCombination getKeyCombo() {
        return keyCombo;
    }

    /**
     * @return The action to take when the key combo is pressed.
     */
    public KeymapAction getAction() {
        return action;
    }

    /**
     * @return The string to insert/delete when the key combo is pressed.
     */
    public String getString() {
        return string;
    }

    /**
     * @return The position to insert/delete the string when the key combo is pressed.
     */
    public KeymapPosition getPosition() {
        return pos;
    }

    /**
     * Checks if the pressed key combination matches this KeyMapping's key combination.
     * @param keyEvent The keypress event.
     * @return {@code true} if the key combination matches, {@code false} otherwise.
     */
    public boolean matches(KeyEvent keyEvent) {
        return keyCombo.match(keyEvent);
    }
}
