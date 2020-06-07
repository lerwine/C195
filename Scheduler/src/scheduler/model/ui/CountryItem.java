package scheduler.model.ui;

import java.util.Locale;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.Country;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> Type of object for database access.
 */
public interface CountryItem<T extends ICountryDAO> extends Country, FxDbModel<T> {

    /**
     * The name of the 'language' property.
     */
    public static final String PROP_LANGUAGE = "language";

    public static CountryItem<? extends ICountryDAO> createModel(ICountryDAO t) {
        if (null == t) {
            return null;
        }
        if (t instanceof CountryDAO) {
            return new CountryModel((CountryDAO) t);
        }

        return new RelatedCountry(t);
    }

    ReadOnlyStringProperty nameProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    ReadOnlyObjectProperty<Locale> localeProperty();

    @Override
    T dataObject();

}
