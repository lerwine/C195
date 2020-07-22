package scheduler.model;

import java.util.Locale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryProperties {

    public static final int MAX_LENGTH_NAME = 50;

    /**
     * The name of the 'name' property.
     */
    public static final String PROP_NAME = "name";

    /**
     * The name of the 'locale' property.
     */
    public static final String PROP_LOCALE = "locale";

    /**
     * Gets the name of the current country.
     *
     * @return The name of the current country.
     */
    String getName();

    /**
     * Gets the {@link Locale} for the current country.
     *
     * @return The {@link Locale} for the current country.
     */
    Locale getLocale();

}
