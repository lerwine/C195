package scheduler.view;

import scheduler.dao.DataObjectImpl;

/**
 * Java FX object model for a {@link DataObjectImpl} object.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The type of {@link DataObjectImpl} to be used for data access operations.
 */
public abstract class ItemModel<T extends DataObjectImpl> extends DataObjectImpl.DataObjectModel<T> implements ChildModel<T> {

    /**
     * Initializes a new ModelBase object.
     *
     * @param dao The {@link DataObjectImpl} to be used for data access operations.
     */
    protected ItemModel(T dao) {
        super(dao);
    }

//    public void saveChanges(Connection connection) throws SQLException {
//        dataObject.saveChanges(connection);
//        newItem.set(false);
//        createDate.set(DB.fromUtcTimestamp(dataObject.getCreateDate()));
//        createdBy.set(dataObject.getCreatedBy());
//        lastModifiedDate.set(DB.fromUtcTimestamp(dataObject.getLastModifiedDate()));
//        lastModifiedBy.set(dataObject.getLastModifiedBy());
//    }
    @Override
    public boolean equals(Object obj) {
        if (null != obj && getClass().isAssignableFrom(obj.getClass())) {
            DataObjectImpl other = (DataObjectImpl) obj;
            return getPrimaryKey() == other.getPrimaryKey();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getPrimaryKey();
    }

}
