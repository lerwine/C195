package scheduler.model.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanIntegerPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.dao.DbObject;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public abstract class RelatedModel<T extends DbObject> implements FxDbModel<T> {

    private static final Logger LOG = Logger.getLogger(RelatedModel.class.getName());

    private final T dataObject;
    private ReadOnlyJavaBeanIntegerProperty primaryKey;
    private ReadOnlyJavaBeanObjectProperty<DataRowState> rowState;

    protected RelatedModel(T dao) {
        dataObject = dao;
        try {
            primaryKey = ReadOnlyJavaBeanIntegerPropertyBuilder.create().bean(dao).name(DataAccessObject.PROP_PRIMARYKEY).build();
            rowState = ReadOnlyJavaBeanObjectPropertyBuilder.<DataRowState>create().bean(dao).name(DataAccessObject.PROP_ROWSTATE).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
    }

    @Override
    public T dataObject() {
        return dataObject;
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey;
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyObjectProperty<DataRowState> rowStateProperty() {
        return rowState;
    }

}
