/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.country;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.Country;
import scheduler.view.DataObjectReferenceModel;

/**
 * Models a country data access object.
 * @author lerwi
 * @param <T> The type of {@link Country} data access object.
 */
public interface CountryReferenceModel<T extends Country> extends DataObjectReferenceModel<T> {
    String getName();
    ReadOnlyProperty<String> nameProperty();
}
