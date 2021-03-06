package scheduler.model.fx;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import scheduler.dao.CountryDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.model.Country;
import scheduler.model.ModelHelper;
import scheduler.model.ModelHelper.CountryHelper;
import scheduler.observables.property.ReadOnlyStringBindingProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PartialCountryModelImpl extends PartialModel<PartialCountryDAO> implements PartialCountryModel<PartialCountryDAO> {

    private static final Logger LOG = Logger.getLogger(PartialCountryModelImpl.class.getName());

    private final ReadOnlyJavaBeanObjectProperty<Locale> locale;
    private final ReadOnlyStringBindingProperty name;
    private final ReadOnlyStringBindingProperty language;

    public PartialCountryModelImpl(CountryDAO.Partial rowData) {
        super(rowData);
        try {
            locale = ReadOnlyJavaBeanObjectPropertyBuilder.<Locale>create().bean(rowData).name(CountryDAO.PROP_LOCALE).build();
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Error creating property", ex);
        }
        name = new ReadOnlyStringBindingProperty(this, PROP_NAME, () -> {
            return CountryHelper.getCountryDisplayText(locale.get());
        }, locale);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, () -> CountryHelper.getLanguageDisplayText(locale.get()), locale);
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name;
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language;
    }

    @Override
    public Locale getLocale() {
        return locale.get();
    }

    @Override
    public ReadOnlyObjectProperty<Locale> localeProperty() {
        return locale;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof Country && ModelHelper.areSameRecord(this, (Country) obj);
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

    @Override
    public String toString() {
        return ModelHelper.CountryHelper.appendPartialModelProperties(this, new StringBuilder(PartialCountryModelImpl.class.getName()).append(" { ")).append("}").toString();
    }

}
