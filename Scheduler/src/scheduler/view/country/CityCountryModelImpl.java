package scheduler.view.country;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.view.model.RelatedItemModel;
import scheduler.dao.CountryElement;

public final class CityCountryModelImpl extends RelatedItemModel<CountryElement> implements CityCountryModel<CountryElement> {

    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public CityCountryModelImpl(CountryElement dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
    }

}
