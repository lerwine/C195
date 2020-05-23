package scheduler.model.ui;

import java.time.ZoneId;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import static scheduler.AppResourceKeys.RESOURCEKEY_ALLCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_LOADINGCITIES;
import static scheduler.AppResourceKeys.RESOURCEKEY_READINGFROMDB;
import scheduler.AppResources;
import scheduler.dao.CityDAO;
import scheduler.dao.DataAccessObject.DaoFactory;
import scheduler.dao.DataRowState;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.predefined.PredefinedCountry;
import scheduler.observables.DerivedBooleanProperty;
import scheduler.observables.NestedObjectProperty;
import scheduler.observables.NestedStringProperty;
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

    private final SimpleObjectProperty<PredefinedCity> predefinedData;
    private final NestedStringProperty<PredefinedCity> name;
    private final NestedObjectProperty<PredefinedCity, PredefinedCountry> country;
    private final NestedStringProperty<PredefinedCountry> countryName;
    private final NestedStringProperty<PredefinedCountry> language;
    private final NestedObjectProperty<PredefinedCity, ZoneId> zoneId;
    private final DerivedBooleanProperty<PredefinedCity> valid;

    public CityModel(CityDAO dao) {
        super(dao);
        predefinedData = new SimpleObjectProperty<>(this, "predefinedData", dao.getPredefinedData());
        name = new NestedStringProperty<>(this, "name", predefinedData, (t) -> t.nameProperty());
        country = new NestedObjectProperty<>(this, "country", predefinedData, (t) -> t.countryProperty());
        countryName = new NestedStringProperty<>(this, "countryName", country, (t) -> t.nameProperty());
        language = new NestedStringProperty<>(this, "language", country, (t) -> t.languageProperty());
        zoneId = new NestedObjectProperty<>(this, "zoneId", predefinedData, (t) -> t.zoneIdProperty());
        valid = new DerivedBooleanProperty<>(this, "valid", predefinedData, Objects::nonNull);
    }

    @Override
    public String getName() {
        return nameProperty().get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyStringProperty();
    }

    @Override
    public PredefinedCountry getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<PredefinedCountry> countryProperty() {
        return country.getReadOnlyObjectProperty();
    }

    public String getCountryName() {
        return countryNameProperty().get();
    }

    @Override
    public ReadOnlyStringProperty countryNameProperty() {
        return countryName.getReadOnlyStringProperty();
    }

    @Override
    public ZoneId getZoneId() {
        return zoneId.get();
    }

    @Override
    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() {
        return zoneId.getReadOnlyObjectProperty();
    }

    @Override
    public String getLanguage() {
        return language.get();
    }

    @Override
    public ReadOnlyStringProperty languageProperty() {
        return language.getReadOnlyStringProperty();
    }

    @Override
    public PredefinedCity getPredefinedData() {
        return predefinedData.get();
    }

    public void setPredefinedData(PredefinedCity value) {
        predefinedData.set(value);
    }

    @Override
    public ObjectProperty<PredefinedCity> predefinedDataProperty() {
        return predefinedData;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid.getReadOnlyBooleanProperty();
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public int hashCode() {
        if (isNewRow()) {
            int hash = 7;
            hash = 23 * hash + Objects.hashCode(this.name);
            hash = 23 * hash + Objects.hashCode(this.country);
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CityModel) {
            final CityModel other = (CityModel) obj;
            if (isNewRow()) {
                return Objects.equals(name.get(), other.name.get()) && Objects.equals(country.get(), other.country.get());
            }
            return !other.isNewRow() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
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
            dao.setPredefinedData(item.getPredefinedData());
            return dao;
        }

        @Override
        protected void updateItemProperties(CityModel item, CityDAO dao) {
            item.setPredefinedData(dao.getPredefinedData());
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
