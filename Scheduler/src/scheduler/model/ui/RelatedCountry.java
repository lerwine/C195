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
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCountry extends RelatedModel<ICountryDAO> implements CountryItem<ICountryDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedCountry.class.getName());

    private final ReadOnlyJavaBeanStringProperty name;
    private final ReadOnlyStringProperty language;
    private final ReadOnlyJavaBeanObjectProperty<Locale> locale;
    private final ReadOnlyBooleanProperty valid;

    public RelatedCountry(ICountryDAO rowData) {
        super(rowData);
        try {
            name = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(CountryDAO.PROP_NAME).build();
            locale = ReadOnlyJavaBeanObjectPropertyBuilder.<Locale>create().bean(rowData).name(CountryDAO.PROP_LOCALE).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        language = new ReadOnlyStringBindingProperty(this, "language", () -> {
            Locale l = locale.get();
            return (null == l) ? "" : l.getDisplayLanguage();
        }, locale);
        valid = new ReadOnlyBooleanBindingProperty(this, "valid",
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(name.get()), name)
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
}
