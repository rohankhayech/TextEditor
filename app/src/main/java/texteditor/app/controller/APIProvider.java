package texteditor.app.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;

import texteditor.api.API;
import texteditor.api.FunctionKeyHandler;
import texteditor.api.ModifyEventHandler;
import texteditor.api.OptionEventHandler;
import texteditor.app.view.GUI;

/**
 * The core application's implementation of the API.
 * 
 * @author Rohan Khayech
 */
public class APIProvider implements API {

    /** Back-reference to the UI. */
    private GUI ui;
    /** The current locale. */
    private Locale locale;
    /** The text area containing the editable text. */
    private TextArea textArea;
    /** List of handlers for text modification. */
    private List<ModifyEventHandler> modifyEventHandlers = new LinkedList<>();
    /** List of handlers for function key presses. */
    private List<FunctionKeyHandler> functionKeyHandlers = new LinkedList<>();
    
    /** 
     * Constructs an instance of the API implementation.
     * @param locale The current locale.
     */
    public APIProvider(Locale locale) {
        this.locale = locale;
    }

    /**
     * Sets the UI back reference.
     * @param ui Back-reference to the UI.
     */
    public void setUI(GUI ui) {
        this.ui = ui;
        this.textArea = ui.getTextArea();
    }

    /**
     * @return The application's current locale.
     */
    @Override
    public Locale getLocale() {
        return locale;
    }

    /**
     * @return A string containing the full contents of the edited text.
     */
    @Override
    public String getText() {
        return textArea.getText();
    }

    /**
     * Replaces the full contents of the edited text with the specified text.
     * 
     * @param text The text to set.
     */
    @Override
    public void setText(String text) {
        textArea.setText(text);
    }

    /**
     * @return An integer representing the position of the caret within the edited text (in characters).
     */
    @Override
    public int getCaretPosition() {
        return textArea.getCaretPosition();
    }

    /**
     * @return All characters after the caret position in the edited text.
     */
    @Override
    public String getAfter() {
        if (getCaretPosition() < textArea.getLength()) {
            return textArea.getText(getCaretPosition(),textArea.getLength());
        } else {
            return "";
        }
    }

    /**
     * Returns the specified number of characters after the caret postion.
     * 
     * @param numChars The number of characters to return.
     * @return A string containing the specified number of characters after the caret postion.
     */
    @Override
    public String getAfter(int numChars) {
        if (numChars > 0) {
            int end = getCaretPosition()+numChars;
            if (end <= textArea.getLength()) {
                return textArea.getText(getCaretPosition(), end);
            } else {
                return getAfter();
            }
        } else {
            return "";
        }
    }

    /**
     * @return All characters before the caret position in the edited text.
     */
    @Override
    public String getBefore() {
        if (getCaretPosition() > 0) {
            return textArea.getText(0,getCaretPosition());
        } else {
            return "";
        }
    }

    /**
     * Returns the specified number of characters before the caret position.
     * 
     * @param numChars The number of characters to return.
     * @return A string containing the the specified number of characters before the caret postion.
     */
    @Override
    public String getBefore(int numChars) {
        if (numChars > 0) {
            int start = getCaretPosition()-numChars;
            if (start > 0) {
                return textArea.getText(start,getCaretPosition());
                
            } else {
                return getBefore();
            }
        } else {
            return "";
        }
    }

    /**
     * Inserts the specified text after the caret position, and moves the caret to
     * the end of the inserted text.
     * @param text The string of text to replace.
     */
    @Override
    public void insertText(String text) {
        int caret = getCaretPosition();
        textArea.insertText(caret, text);
    }

    /**
     * Inserts the specified text at the start of the current line.
     * @param text The string of text to insert.
     */
    @Override
    public void insertTextAtSOL(String text) {
        int caret = getCaretPosition();
        
        textArea.insertText(getSOL(), text);
        
        //Move caret appropriately
        int newCaret = caret + text.length();
        textArea.selectRange(newCaret, newCaret);
    }

    /**
     * Removes the specified text at the start of the current line, if it exists.
     * @param text The string of text to remove.
     */
    @Override
    public void removeTextAtSOL(String text) {
        int sol = getSOL();
        int caret = getCaretPosition();

        if (sol+text.length()<=textArea.getLength()) {
            if (textArea.getText(sol, sol+text.length()).equals(text)) {
                textArea.replaceText(sol, sol+text.length(),"");

                int newCaret;
                if (caret-text.length() > sol) {
                    newCaret = caret-text.length();
                } else {
                    newCaret = sol;
                }
                textArea.selectRange(newCaret, newCaret);
            }
        }
    }

    /**
     * Removes the specified text before the caret position, if it exists.
     * @param text The string of text to remove.
     */
    @Override
    public void removeTextBeforeCaret(String text) {
        int caret = getCaretPosition();
        int start = caret-text.length();
        if (start>=0) {
            if (textArea.getText(start, caret).equals(text)) {
                textArea.replaceText(start, caret, "");
            }
        }
    }

    /**
     * Replaces the characters behind the caret position up to the specified length with the specified text.
     * @param text The string of text to replace.
     * @param length The length of characters to replace.
     */
    @Override
    public void replaceText(String text, int length) {
        int start = getCaretPosition()-length;
        if (start >= 0) {
            textArea.replaceText(start, getCaretPosition(), text);
        } else {
            textArea.replaceText(0, getCaretPosition(), text);
        }
    }

    /**
     * Highlights the text between the specified indexes.
     * 
     * @param start The start position of text to highlight.
     * @param end   The start position of text to highlight.
     */
    @Override
    public void highlightText(int start, int end) {
        textArea.selectRange(start, end);   
    }

    /**
     * Registers a callback function that is called when the user modifies the edited text.
     * @param callback The function to be called.
     */
    @Override
    public void registerModifyCallback(ModifyEventHandler callback) {
        modifyEventHandlers.add(callback);
    }

    /**
     * Registers a callback function that is called when the user presses a function key.
     * 
     * @param callback The function to be called.
     */
    @Override
    public void registerFunctionKeyCallback(FunctionKeyHandler callback) {
        functionKeyHandlers.add(callback);
    }

    /**
     * Adds a user-selectable option with the given title,
     * and registers a callback function that is called when the option is selected.
     * 
     * @param name The localised display name for the option.
     * @param callback The function to be called.
     */
    @Override
    public void addOption(String name, OptionEventHandler callback) {
        Button button = new Button(name);
        button.setOnAction(event -> callback.onOptionSelected());
        ui.getToolBar().getItems().add(button);
    }

    /**
     * Prompts the user for input and returns the input text.
     * @param title The title to display to the user.
     * @param prompt The prompt to display to the user.
     * @return The input text, or {@code null} if the user cancelled the input prompt.
     */
    @Override
    public String getUserInput(String title, String prompt) {
        var dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(prompt);

        return dialog.showAndWait().orElse(null);
    }

    /**
     * Notifies all function key press handlers that the specified function key was pressed.
     * 
     * @param keyNum The number of the function key that was pressed.
     */
    public void notifyFunctionKeyPress(int keyNum) {
        for (FunctionKeyHandler handler : functionKeyHandlers) {
            handler.onKeyPressed(keyNum);
        }
    }

    /**
     * Notifies all modify event handlers that the edited text was modified.
     */
    public void notifyModifyEvent() {
        for (ModifyEventHandler handler : modifyEventHandlers) {
            handler.onTextModified();
        }
    }

    /**
     * Gets the index of the start of the current line, by finding the last {@code \n} character 
     * or the start of file if one is not found.
     * @return The index of the start of the current line, or 0 if on the first line.
     */
    private int getSOL() {
        String text = getBefore();
        return text.lastIndexOf("\n")+1;
    }
}