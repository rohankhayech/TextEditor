package texteditor.api;

/**
 * Event handler for text modification.
 * 
 * @author Rohan Khayech
 */
public interface ModifyEventHandler {

    /**
     * Called when the edited text is modified by the user.
     */
    void onTextModified();
}
