/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.DataObject;

/**
 *
 * @author lerwi
 */
public interface DataObjectReferenceModel<T extends DataObject> {
    T getDataObject();
    ReadOnlyProperty<T> dataObjectProperty();
    int getPrimaryKey();
    ReadOnlyIntegerProperty primaryKeyProperty();
}
