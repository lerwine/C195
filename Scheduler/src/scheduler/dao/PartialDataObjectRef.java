package scheduler.dao;

import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
@FunctionalInterface
interface PartialDataObjectRef<T extends DataObject> {

    /**
     *
     * @return
     */
    int getPrimaryKey();

    default T getPartial() {
        return null;
    }

    public static <T extends DataObject> PartialDataObjectRef<T> of(int primaryKey) {
        return () -> primaryKey;
    }

    public static <T extends DataObject> PartialDataObjectRef<T> partial(T dao) {
        Objects.requireNonNull(dao);
        return new PartialDataObjectRef<T>() {
            @Override
            public int getPrimaryKey() {
                return dao.getPrimaryKey();
            }

            @Override
            public T getPartial() {
                return dao;
            }
        };
    }

}
