package texteditor.api;

/** 
 * Event handler for function key presses.
 * 
 * @author Rohan Khayech
 */
public interface FunctionKeyHandler {

    /**
     * Called when the specified function key is pressed.
     * @param keyNum The function key pressed.
     */
    void onKeyPressed(int keyNum);
    
}
