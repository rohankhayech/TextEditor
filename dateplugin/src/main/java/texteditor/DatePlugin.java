package texteditor;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;

import texteditor.api.API;
import texteditor.api.Plugin;

/**
 * The date plugin creates a new button labelled "Date". When pressed, the current
 * date and time (for the correct locale) are inserted at the caret position.
 * 
 * @author Rohan Khayech
 */
public class DatePlugin extends Plugin {

    /** Plugin display name. */
    public static final String NAME = "Date";

    /** API endpoint. */
    private API api;
    /** The current locale. */
    private Locale locale;
    /** The resource bundle containing the plugin's localised strings. */
    private ResourceBundle bundle;

    /** Constructs a new DatePlugin object. */
    public DatePlugin() {}

    /**
     * Starts the plugin.
     * @param api Reference to the API.
     */
    @Override
    public void start(API api) {
        this.api = api;

        // Get the locale and resource bundle.
        locale = api.getLocale();
        bundle = ResourceBundle.getBundle("date_bundle", locale);

        // Add UI option for inserting the current date.
        api.addOption(bundle.getString("insert_date"),()->{
            insertDate();
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
     * Inserts a localized string representing the current date and time at the caret position.
     */
    private void insertDate() {
        // Format date string according to locale.
        DateTimeFormatter dtf = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG).withLocale(locale);
        api.insertText(dtf.format(ZonedDateTime.now()));
    }
    
}
