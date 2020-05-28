package scheduler.model;

import java.util.Locale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface Country {

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
