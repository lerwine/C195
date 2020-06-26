package scheduler.dao;

import java.util.Locale;
import scheduler.model.Country;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface ICountryDAO extends DbObject, Country {

    // FIXME: 0 - Replace with validator
    public static <T extends ICountryDAO> T assertValidCountry(T target) {
        if (target.getRowState() == DataRowState.DELETED) {
            throw new IllegalArgumentException("Country has already been deleted");
        }

        String name = target.getName();

        if (name.isEmpty()) {
            throw new IllegalStateException("Country name not defined");
        }

        if (name.length() > MAX_LENGTH_NAME) {
            throw new IllegalStateException("Name too long");
        }

        Locale locale = target.getLocale();

        if (null == locale) {
            throw new IllegalStateException("Locale not defined");
        }

        if (locale.getDisplayCountry().isEmpty()) {
            throw new IllegalStateException("Locale does not specify a country");
        }
        if (locale.getDisplayLanguage().isEmpty()) {
            throw new IllegalStateException("Locale does not specify a language");
        }

        return target;
    }

}
