package scheduler.view.country;

import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.view.model.RelatedItemModel;
import scheduler.dao.CountryElement;

public final class CityCountryModelImpl extends RelatedItemModel<CountryElement> implements CityCountryModel<CountryElement> {

    private final ReadOnlyStringWrapper name;
    private final ObjectProperty<CountryOptionModel> optionModel;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CountryOptionModel getOptionModel() {
        return optionModel.get();
    }

    public void setOptionModel(CountryOptionModel value) {
        optionModel.set(value);
    }

    public ObjectProperty<CountryOptionModel> optionModelProperty() {
        return optionModel;
    }
    
    public CityCountryModelImpl(CountryElement dao) {
        super(dao);
        optionModel = new SimpleObjectProperty<>(CountryOptionModel.getCountryOption(dao.getName()));
        if (null == optionModel.get())
            throw new IllegalArgumentException("Data access object does not map to an option model");
        name = new ReadOnlyStringWrapper(this, "name");
        name.bind(optionModel.get().nameProperty());
        optionModel.addListener(this::onOptionModelChanged);
    }

    @SuppressWarnings("unchecked")
    private void onOptionModelChanged(Observable observable) {
        name.unbind();
        CountryOptionModel model = ((SimpleObjectProperty<CountryOptionModel>)observable).get();
        if (null != model)
            name.bind(model.nameProperty());
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
        if (null != obj && obj instanceof CountryModel) {
            final CountryModel other = (CountryModel) obj;
            return !other.isNewItem() && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }
    
}
