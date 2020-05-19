package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import scheduler.dao.CountryDAO;
import scheduler.dao.ICountryDAO;
import scheduler.model.Country;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.view.country.CountryModel;
import scheduler.view.country.RelatedCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CountryItem extends Country, FxModel {

    public static CountryItem createItem(ICountryDAO dao) {
        if (null == dao) {
            return null;
        }
        if (dao instanceof CountryDAO) {
            return new CountryModel((CountryDAO) dao);
        }
        PredefinedCountry p = dao.getPredefinedData();
        if (null != p && p.getDataObject() == dao) {
            return p;
        }
        return new RelatedCountry(dao);
    }

    ReadOnlyStringProperty nameProperty();

    ZoneId getZoneId();

    ReadOnlyObjectProperty<ZoneId> zoneIdProperty();

    String getLanguage();

    ReadOnlyStringProperty languageProperty();

    ReadOnlyObjectProperty<PredefinedCountry> predefinedDataProperty();

    @Override
    ICountryDAO getDataObject();

    @Override
    ReadOnlyObjectProperty<? extends ICountryDAO> dataObjectProperty();

}
