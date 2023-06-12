package texteditor;

import java.text.Normalizer;
import java.util.Locale;
import java.util.ResourceBundle;

import texteditor.api.API;
import texteditor.api.Plugin;

/**
 * The find plugin creates a new button labelled "Find". When pressed, or when
 * the F3 key is pressed, the plugin finds and highlights the user specified string in the edited text.
 * 
 * @author Rohan Khayech
 */
public class FindPlugin extends Plugin {

    /** Plugin display name. */
    public static final String NAME = "Find";

    /** API endpoint. */
    private API api;
    /** The current locale. */
    private Locale locale;
    /** The resource bundle containing the application's localised strings. */
    private ResourceBundle bundle;

    /** Constructs a new FindPlugin object. */
    public FindPlugin() {}

    /**
     * Starts the plugin.
     * @param api Reference to the API.
     */
    @Override
    public void start(API api) {
        this.api = api;
        locale = api.getLocale();
        bundle = ResourceBundle.getBundle("find_bundle", locale);

        // Add find menu option
        api.addOption(bundle.getString("find"),()->{
            findString();
        });
        
        // Add function key shortcut
        api.registerFunctionKeyCallback(keyNum ->{
            if (keyNum == 3) {
                findString();
            }
        });
    }

    /**
     * @return The plugin's display name.
     */
    @Override
    public String getName() {
        return NAME;
    }

    /**
     * Prompts the user for a search term and highlights the first occurence of the term after the caret postion.
     */
    private void findString() {

        // Get search term by user
        String term = api.getUserInput(bundle.getString("find"),bundle.getString("find_prompt"));
        if (term != null) {
            String text = api.getAfter();

            // Normalize terms
            term = Normalizer.normalize(term.toLowerCase(), Normalizer.Form.NFKC);
            text = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFKC);

            // Find term in text and highlight if exists.
            int index = text.indexOf(term);
            if (index != -1) {
                api.highlightText(api.getCaretPosition()+index,api.getCaretPosition()+index+term.length());
            }
        }
    }
    
}
