package scheduler.view.city;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.dao.CityElement;
import scheduler.dao.CountryElement;
import scheduler.observables.ChildPropertyWrapper;
import scheduler.view.country.CityCountryModel;
import scheduler.view.country.CityCountryModelImpl;
import scheduler.view.country.CityOptionModel;
import scheduler.view.model.RelatedItemModel;

public class RelatedCityModel extends RelatedItemModel<CityElement> implements CityModel<CityElement> {

    private final ReadOnlyStringWrapper name;
    private final ReadOnlyObjectWrapper<CityCountryModel<? extends CountryElement>> country;
    private final ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryName;
    private final ObjectProperty<CityOptionModel> optionModel;

    public RelatedCityModel(CityElement dao) {
        super(dao);
        optionModel = new SimpleObjectProperty<>(CityOptionModel.getCityOption(dao.getName()));
        if (null == optionModel.get())
            throw new IllegalArgumentException("Data access object does not map to an option model");
        name = new ReadOnlyStringWrapper(this, "name");
        name.bind(optionModel.get().nameProperty());
        CountryElement c = dao.getCountry();
        country = new ReadOnlyObjectWrapper<>(this, "country", (null == c) ? null : new CityCountryModelImpl(c));
        countryName = new ChildPropertyWrapper<>(this, "countryName", country, (t) -> t.nameProperty());
        optionModel.addListener(this::onOptionModelChanged);
    }

    @SuppressWarnings("unchecked")
    private void onOptionModelChanged(Observable observable) {
        name.unbind();
        CityOptionModel model = ((SimpleObjectProperty<CityOptionModel>)observable).get();
        if (null != model)
            name.bind(model.nameProperty());
    }
    
    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CityOptionModel getOptionModel() {
        return optionModel.get();
    }

    public void setOptionModel(CityOptionModel value) {
        optionModel.set(value);
    }

    public ObjectProperty<CityOptionModel> optionModelProperty() {
        return optionModel;
    }

    @Override
    public CityCountryModel<? extends CountryElement> getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityCountryModel<? extends CountryElement>> countryProperty() {
        return country.getReadOnlyProperty();
    }

    @Override
    public String getCountryName() {
        return countryName.get();
    }

    @Override
    public ChildPropertyWrapper<String, CityCountryModel<? extends CountryElement>> countryNameProperty() {
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
