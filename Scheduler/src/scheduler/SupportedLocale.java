package scheduler;

import java.util.Locale;

/**
 * Defines languages supported by the application. Each element defines a language tag, where the lower-case element name is is the ISO 639 two-letter language code and is
 * constructed with the ISO 3166 country code.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum SupportedLocale {
    EN("US"),
    DE("DE"),
    ES("GT"),
    HI("IN");

    private final Locale locale;

    private SupportedLocale(String defaultCountryCode) {
        Locale current = Locale.getDefault();
        Locale prospective = Locale.forLanguageTag(String.format("%s-%s", name().toLowerCase(), defaultCountryCode));
        locale = (prospective.getLanguage().equals(current.getLanguage())) ? current : prospective;
    }

    public Locale getLocale() {
        return locale;
    }

    @Override
    public String toString() {
        return locale.toLanguageTag();
    }

}
