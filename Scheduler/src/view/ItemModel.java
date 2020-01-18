/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.Objects;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.DataObject;
import scheduler.dao.DataObjectImpl;
import util.DB;

/**
 * Java FX object model for a {@link DataObjectImpl} object.
 * @author erwinel
 * @param <R> The type of {@link DataObjectImpl} to be used for data access operations.
 */
public abstract class ItemModel<R extends DataObjectImpl> implements ChildModel<R> {
    //<editor-fold defaultstate="collapsed" desc="Properties">
    
    //<editor-fold defaultstate="collapsed" desc="dataObject property">
    
    private final R dataObject;
    
    @Override
    public R getDataObject() { return dataObject; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="createDate property">
    
    private final ReadOnlyObjectWrapper<LocalDateTime> createDate;
    
    /**
     * Gets the date and time when the associated data row was created.
     * @return The date and time when the associated data row was created.
     */
    public LocalDateTime getCreateDate() { return createDate.get(); }
    
    /**
     * Gets the property that contains the date and time when the associated data row was created.
     * @return The property that contains the date and time when the associated data row was created.
     */
    public ReadOnlyObjectProperty<LocalDateTime> createDateProperty() { return createDate.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="createdBy property">
    
    private final ReadOnlyStringWrapper createdBy;
    
    /**
     * Gets the name of the person who created the associated data row.
     * @return The name of the person who created the associated data row.
     */
    public String getCreatedBy() { return createdBy.get(); }
    
    /**
     * Gets the property that contains the name of the person who created the associated data row.
     * @return The property that contains the name of the person who created the associated data row.
     */
    public ReadOnlyStringProperty createdByProperty() { return createdBy.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="lastModifiedDate property">
    
    private final ReadOnlyObjectWrapper<LocalDateTime> lastModifiedDate;
    
    /**
     * Gets the date and time when the associated data row was last modified.
     * @return The date and time when the associated data row was last modified.
     */
    public LocalDateTime getLastModifiedDate() { return lastModifiedDate.get(); }
    
    /**
     * Gets the property that contains the date and time when the associated data row was last modified.
     * @return The property that contains the date and time when the associated data row was last modified.
     */
    public ReadOnlyObjectProperty<LocalDateTime> lastModifiedDateProperty() { return lastModifiedDate.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="lastModifiedBy property">
    
    private final ReadOnlyStringWrapper lastModifiedBy;
    
    /**
     * Gets the name of the person who last modified the associated data row.
     * @return The name of the person who last modified the associated data row.
     */
    public String getLastModifiedBy() { return lastModifiedBy.get(); }
    
    /**
     * Gets the property that contains the name of the person who last modified the associated data row.
     * @return The property that contains the name of the person who last modified the associated data row.
     */
    public ReadOnlyStringProperty lastModifiedByProperty() { return lastModifiedBy.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="newItem property">
    
    private final ReadOnlyBooleanWrapper newItem;
    
    /**
     * Indicates whether this represents a new item that has not been saved to the database.
     * @return {@code true} if the current item has not been saved to the database; otherwise, {@code false}.
     */
    public boolean isNewItem() { return newItem.get(); }
    
    /**
     * Gets the property that indicates whether the current item has not yet been saved to the database.
     * @return The property that indicates whether the current item has not yet been saved to the database.
     */
    public ReadOnlyBooleanProperty newItemProperty() { return newItem.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //</editor-fold>

    /**
     * Initializes a new ModelBase object.
     * @param dao The {@link DataObjectImpl} to be used for data access operations.
     */
    protected ItemModel(R dao) {
        Objects.requireNonNull(dao, "Data access object cannot be null");
        assert dao.getRowState() != DataObject.ROWSTATE_DELETED : String.format("%s has been deleted", dao.getClass().getName());
        dataObject = dao;
        createDate = new ReadOnlyObjectWrapper<>(DB.fromUtcTimestamp(dao.getCreateDate()));
        createdBy = new ReadOnlyStringWrapper(dao.getCreatedBy());
        lastModifiedDate = new ReadOnlyObjectWrapper<>(DB.fromUtcTimestamp(dao.getLastModifiedDate()));
        lastModifiedBy = new ReadOnlyStringWrapper(dao.getLastModifiedBy());
        newItem = new ReadOnlyBooleanWrapper(dao.getRowState() == DataObject.ROWSTATE_NEW);
    }
    
    public abstract boolean delete(Connection connection);
    
    public abstract void saveChanges(Connection connection);
    
    public void refreshFromDAO() {
        createDate.set(DB.fromUtcTimestamp(dataObject.getCreateDate()));
        createdBy.set(dataObject.getCreatedBy());
        lastModifiedDate.set(DB.fromUtcTimestamp(dataObject.getLastModifiedDate()));
        lastModifiedBy.set(dataObject.getLastModifiedBy());
        newItem.set(dataObject.getRowState() == DataObject.ROWSTATE_NEW);
    }
}
