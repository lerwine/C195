/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
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
