package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.ICityDAO;
import scheduler.dao.ICountryDAO;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity extends RelatedModel<ICityDAO> implements CityItem<ICityDAO> {

    public RelatedCity(ICityDAO dao) {
        super(dao);
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#nameProperty
    }

    @Override
    public CountryItem<? extends ICountryDAO> getCountry() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#getCountry
    }

    @Override
    public ReadOnlyProperty<? extends CountryItem<? extends ICountryDAO>> countryProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#countryProperty
    }

    @Override
    public String getCountryName() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#getCountryName
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#countryNameProperty
    }

    @Override
    public ZoneId getZoneId() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#getZoneId
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#zoneIdProperty
    }

    @Override
    public String getTimeZoneDisplay() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#getTimeZoneDisplay
    }

    @Override
    public ReadOnlyStringProperty timeZoneDisplayProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#timeZoneDisplayProperty
    }

    @Override
    public String getLanguage() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#getLanguage
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#languageProperty
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.RelatedCity#getName
    }

}
