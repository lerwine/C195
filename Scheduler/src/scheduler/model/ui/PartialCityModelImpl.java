package scheduler.model.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.PartialCityDAO;
import scheduler.dao.PartialCountryDAO;
import scheduler.model.City;
import scheduler.model.CountryProperties;
import scheduler.model.ModelHelper;
import scheduler.observables.property.ReadOnlyObjectBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PartialCityModelImpl extends RelatedModel<PartialCityDAO> implements PartialCityModel<PartialCityDAO> {

    private static final Logger LOG = Logger.getLogger(PartialCityModelImpl.class.getName());

    private final ReadOnlyJavaBeanStringProperty name;
    private final ReadOnlyJavaBeanObjectProperty<PartialCountryDAO> countryDAO;
    private final ReadOnlyObjectBindingProperty<PartialCountryModel<? extends PartialCountryDAO>> country;
    private final ReadOnlyStringBindingProperty countryName;
    private final ReadOnlyStringBindingProperty language;

    public PartialCityModelImpl(PartialCityDAO dao) {
        super(dao);
        try {
            name = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(dao).name(PROP_NAME).build();
            countryDAO = ReadOnlyJavaBeanObjectPropertyBuilder.<PartialCountryDAO>create().bean(dao).name(PROP_COUNTRY).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        country = new ReadOnlyObjectBindingProperty<>(this, PROP_COUNTRY, () -> PartialCountryModel.createModel(countryDAO.get()), countryDAO);
        countryName = new ReadOnlyStringBindingProperty(this, PROP_COUNTRYNAME, Bindings.selectString(country, CountryProperties.PROP_NAME));
        language = new ReadOnlyStringBindingProperty(this, PROP_LANGUAGE, Bindings.selectString(country, PartialCountryModel.PROP_LANGUAGE));
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
    public PartialCountryModel<? extends PartialCountryDAO> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<PartialCountryModel<? extends PartialCountryDAO>> countryProperty() {
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
                .addDataObject(country);
    }

}
