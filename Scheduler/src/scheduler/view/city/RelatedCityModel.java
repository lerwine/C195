package scheduler.view.city;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.view.model.RelatedItemModel;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;
import scheduler.model.ui.CountryDbItem;
import scheduler.model.ui.CityDbItem;

public class RelatedCityModel extends RelatedItemModel<CityRowData> implements CityDbItem<CityRowData> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<CountryDbItem<? extends CountryRowData>> country;
    private final ChildPropertyWrapper<String, CountryDbItem<? extends CountryRowData>> countryName;
//    private final ObjectProperty<CityOptionModel> optionModel;

    public RelatedCityModel(CityRowData dao) {
        super(dao);
//        optionModel = new SimpleObjectProperty<>(CityOptionModel.getCityOption(dao.getName()));
//        if (null == optionModel.get())
//            throw new IllegalArgumentException("Data access object does not map to an option model");
        name = new ReadOnlyStringWrapper(this, "name");
        // CURRENT: Initialize name
//        name.bind(optionModel.get().nameProperty());
        CountryRowData c = dao.getCountry();
        country = new ReadOnlyObjectWrapper<>(this, "country", (null == c) ? null : new CityCountryModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
//        optionModel.addListener(this::onOptionModelChanged);
    }

//    @SuppressWarnings("unchecked")
//    private void onOptionModelChanged(Observable observable) {
//        name.unbind();
//        CityOptionModel model = ((SimpleObjectProperty<CityOptionModel>)observable).get();
//        if (null != model)
//            name.bind(model.nameProperty());
//    }
    
    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

//    @Override
//    public CityOptionModel getOptionModel() {
//        return optionModel.get();
//    }
//
//    public void setOptionModel(CityOptionModel value) {
//        optionModel.set(value);
//    }
//
//    public ObjectProperty<CityOptionModel> optionModelProperty() {
//        return optionModel;
//    }

    @Override
    public CountryDbItem<? extends CountryRowData> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryDbItem<? extends CountryRowData>> countryProperty() {
        return country.getReadOnlyProperty();
    }

    /**
     * @deprecated Use {@link #getName()}
     */
    public String getCountryName() {
        return countryName.get();
    }

    /**
     * @deprecated Use {@link #nameProperty()}
     */
    public ChildPropertyWrapper<String, CountryDbItem<? extends CountryRowData>> countryNameProperty() {
        return countryName;
    }

    @Override
    public String toString() {
        return name.get();
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof CityModelImpl) {
            final CityModelImpl other = (CityModelImpl) obj;
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
}
