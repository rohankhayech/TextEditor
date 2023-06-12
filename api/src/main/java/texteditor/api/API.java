package texteditor.api;

import java.util.Locale;

/**
 * The API allows plugins and scripts to access and modify the edited text within the text editor.
 * 
 * @author Rohan Khayech
 */
public interface API {
    /**
     * @return The application's current locale.
     */
    Locale getLocale();
    
    /**
     * @return A string containing the full contents of the edited text.
     */
    String getText();

    /**
     * Replaces the full contents of the edited text with the specified text.
     * @param text The text to set.
     */
    void setText(String text);

    /**
     * @return An integer representing the position of the caret within the edited text (in characters).
     */
    int getCaretPosition();

    /**
     * @return All characters after the caret position in the edited text.
     */
    String getAfter();

    /**
     * Returns the specified number of characters after the caret postion.
     * 
     * @param numChars The number of characters to return.
     * @return A string containing the specified number of characters before the caret postion.
     */
    String getAfter(int numChars);

    /**
     * @return All characters before the caret position in the edited text.
     */
    String getBefore();

    /**
     * Returns the specified number of characters before the caret position.
     * @param numChars The number of characters to return.
     * @return A string containing the the specified number of characters after the caret postion.
     */
    String getBefore(int numChars);

    /**
     * Inserts the specified text after the caret position,
     * and moves the caret to the end of the inserted text.
     * @param text The string of text to insert.
     */
    void insertText(String text);

    /**
     * Inserts the specified text at the start of the current line.
     * @param text The string of text to insert.
     */
    void insertTextAtSOL(String text);

    /**
     * Removes the specified text at the start of the current line, if it exists.
     * @param text The string of text to remove.
     */
    void removeTextAtSOL(String text);

    /**
     * Removes the specified text before the caret position, if it exists.
     * 
     * @param text The string of text to remove.
     */
    void removeTextBeforeCaret(String text);

    /**
     * Replaces the characters behind the caret position up to the specified length
     * with the specified text.
     * 
     * @param text   The string of text to replace.
     * @param length The length of characters to replace.
     */
    void replaceText(String text, int length);

    /**
     * Highlights the text between the specified indexes.
     * @param start The start position of text to highlight.
     * @param end The start position of text to highlight.
     */
    void highlightText(int start, int end);

    /**
     * Registers a callback function that is called when the user modifies the edited text.
     * @param callback The function to be called.
     */
    void registerModifyCallback(ModifyEventHandler callback);

    /**
     * Registers a callback function that is called when the user presses a function key.
     * 
     * @param callback The function to be called.
     */
    void registerFunctionKeyCallback(FunctionKeyHandler callback);

    /**
     * Adds a user-selectable option with the given title,
     * and registers a callback function that is called when the option is selected.
     * 
     * @param name The localised display name for the option.
     * @param callback The function to be called.
     */
    void addOption(String name, OptionEventHandler callback);

    /**
     * Prompts the user for input and returns the input text.
     * @param title The title to display to the user.
     * @param prompt The prompt to display to the user.
     * @return The input text, or {@code null} if the user cancelled the input prompt.
     */
    String getUserInput(String title, String prompt);
}
