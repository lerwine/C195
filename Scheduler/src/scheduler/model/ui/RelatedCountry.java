package scheduler.model.ui;

import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.Country;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry extends RelatedModel<ICountryDAO> implements CountryItem<ICountryDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedCountry.class.getName());

    private final ReadOnlyJavaBeanObjectProperty<Locale> locale;
    private final ReadOnlyStringBindingProperty name;
    private final ReadOnlyStringBindingProperty language;
    private final ReadOnlyBooleanProperty valid;

    public RelatedCountry(ICountryDAO rowData) {
        super(rowData);
        try {
            locale = ReadOnlyJavaBeanObjectPropertyBuilder.<Locale>create().bean(rowData).name(CountryDAO.PROP_LOCALE).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        name = new ReadOnlyStringBindingProperty(this, PROP_NAME, () -> {
            return CountryProperties.getCountryDisplayText(locale.get());
        }, locale);
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, () -> CountryProperties.getLanguageDisplayText(locale.get()), locale);
        valid = new ReadOnlyBooleanBindingProperty(this, PROP_VALID,
                Bindings.createBooleanBinding(() -> {
                    return Values.isNotNullWhiteSpaceOrEmpty(name.get());
                }, name)
                        .and(language.isNotEmpty()));
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
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
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
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        return ToStringPropertyBuilder.create(this)
                .addNumber(primaryKeyProperty())
                .addString(name)
                .addLocale(locale);
    }

}
