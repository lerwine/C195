package scheduler.model.fx;

import java.util.Locale;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CountryDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.model.Country;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface PartialCountryModel<T extends PartialCountryDAO> extends Country, PartialEntityModel<T> {

    /**
     * The name of the 'language' property.
     */
    public static final String PROP_LANGUAGE = "language";

    @SuppressWarnings("unchecked")
    public static PartialCountryModel<? extends PartialCountryDAO> createModel(PartialCountryDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof CountryDAO) {
            return ((CountryDAO) t).cachedModel(true);
        }

        return new PartialCountryModelImpl((CountryDAO.Partial) t);
    }

    ReadOnlyStringProperty nameProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    ReadOnlyObjectProperty<Locale> localeProperty();

    @Override
    T dataObject();

}
