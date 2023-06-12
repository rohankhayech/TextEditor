package texteditor.api;

/**
 * Event handler for user-selectable options.
 * 
 * @author Rohan Khayech
 */
public interface OptionEventHandler {

    /**
     * Called when the option is selected by the user.
     */
    void onOptionSelected();
}
