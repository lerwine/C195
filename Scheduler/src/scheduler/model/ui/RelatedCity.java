package scheduler.model.ui;

import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.City;
import scheduler.model.CityProperties;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity extends RelatedModel<ICityDAO> implements CityItem<ICityDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedCity.class.getName());

    private final ReadOnlyJavaBeanStringProperty name;
    private final ReadOnlyJavaBeanObjectProperty<TimeZone> timeZone;
    private final ReadOnlyStringProperty timeZoneDisplay;
    private final ReadOnlyJavaBeanObjectProperty<ICountryDAO> countryDAO;
    private final ReadOnlyObjectBindingProperty<CountryItem<? extends ICountryDAO>> country;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty language;

    public RelatedCity(ICityDAO dao) {
        super(dao);
        try {
            name = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(dao).name(PROP_NAME).build();
            timeZone = ReadOnlyJavaBeanObjectPropertyBuilder.<TimeZone>create().bean(dao).name(PROP_TIMEZONE).build();
            countryDAO = ReadOnlyJavaBeanObjectPropertyBuilder.<ICountryDAO>create().bean(dao).name(PROP_COUNTRY).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        country = new ReadOnlyObjectBindingProperty<>(this, PROP_COUNTRY, () -> CountryItem.createModel(countryDAO.get()), countryDAO);
        timeZoneDisplay = new ReadOnlyStringBindingProperty(this, PROP_TIMEZONEDISPLAY, () -> {
            return CityProperties.getTimeZoneDisplayText(timeZone.get());
        }, timeZone);
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(country, CountryProperties.PROP_NAME));
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(country, CountryItem.PROP_LANGUAGE));
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
    public CountryItem<? extends ICountryDAO> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryItem<? extends ICountryDAO>> countryProperty() {
        return country;
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName;
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
    public TimeZone getTimeZone() {
        return timeZone.get();
    }

    @Override
    public ReadOnlyObjectProperty<TimeZone> timeZoneProperty() {
        return timeZone;
    }

    @Override
    public String getTimeZoneDisplay() {
        return timeZoneDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty timeZoneDisplayProperty() {
        return timeZoneDisplay;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof City && ModelHelper.areSameRecord(this, (City) obj);
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
                .addDataObject(country)
                .addTimeZone(timeZone);
    }

}
