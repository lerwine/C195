package scheduler.view.country;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.Country;
import scheduler.dao.DataObjectImpl;

public final class CountryReferenceModelImpl extends DataObjectImpl.DataObjectReferenceModelImpl<Country> implements CountryReferenceModel<Country> {

    private final ReadOnlyStringWrapper name;

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    public CountryReferenceModelImpl(Country dao) {
        super(dao);
        name = new ReadOnlyStringWrapper(this, "name", dao.getName());
    }

}
