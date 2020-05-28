package scheduler.model.ui;

import java.time.ZoneId;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.ICountryDAO;
import scheduler.dao.filter.DaoFilter;
import scheduler.view.ModelFilter;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CityModel extends FxRecordModel<CityDAO> implements CityItem<CityDAO> {

    private static final Factory FACTORY = new Factory();

    public static final Factory getFactory() {
        return FACTORY;
    }

    public CityModel(CityDAO dao) {
        super(dao);

    }

    @Override
    public boolean isValid() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#isValid
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#validProperty
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#nameProperty
    }

    @Override
    public CountryItem<? extends ICountryDAO> getCountry() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#getCountry
    }

    @Override
    public ReadOnlyProperty<? extends CountryItem<? extends ICountryDAO>> countryProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#countryProperty
    }

    @Override
    public String getCountryName() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#getCountryName
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#countryNameProperty
    }

    @Override
    public ZoneId getZoneId() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#getZoneId
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#zoneIdProperty
    }

    @Override
    public String getTimeZoneDisplay() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#getTimeZoneDisplay
    }

    @Override
    public ReadOnlyStringProperty timeZoneDisplayProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#timeZoneDisplayProperty
    }

    @Override
    public String getLanguage() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#getLanguage
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#languageProperty
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CityModel#getName
    }

    public final static class Factory extends FxRecordModel.ModelFactory<CityDAO, CityModel> {

        // Singleton
        private Factory() {
            if (null != FACTORY) {
                throw new IllegalStateException();
            }
        }

        @Override
        public DaoFactory<CityDAO> getDaoFactory() {
            return CityDAO.getFactory();
        }

        @Override
        public CityModel createNew(CityDAO dao) {
            return new CityModel(dao);
        }

        @Override
        public CityDAO updateDAO(CityModel item) {
            CityDAO dao = item.getDataObject();
            if (dao.getRowState() == DataRowState.DELETED) {
                throw new IllegalArgumentException("City has been deleted");
            }
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#isValid
        }

        @Override
        protected void updateItemProperties(CityModel item, CityDAO dao) {
            throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement scheduler.model.ui.CountryModel#isValid
        }

        @Override
        public ModelFilter<CityDAO, CityModel, ? extends DaoFilter<CityDAO>> getAllItemsFilter() {
            return new ModelFilter<CityDAO, CityModel, DaoFilter<CityDAO>>() {
                private final String headingText = AppResources.getResourceString(RESOURCEKEY_ALLCITIES);
                private final DaoFilter<CityDAO> daoFilter = DaoFilter.all(AppResources.getResourceString(RESOURCEKEY_READINGFROMDB),
                        AppResources.getResourceString(RESOURCEKEY_LOADINGCITIES));

                @Override
                public String getHeadingText() {
                    return headingText;
                }

                @Override
                public DaoFilter<CityDAO> getDaoFilter() {
                    return daoFilter;
                }

                @Override
                public boolean test(CityModel t) {
                    return null != t;
                }

            };
        }

        @Override
        public ModelFilter<CityDAO, CityModel, ? extends DaoFilter<CityDAO>> getDefaultFilter() {
            return getAllItemsFilter();
        }

    }
}
