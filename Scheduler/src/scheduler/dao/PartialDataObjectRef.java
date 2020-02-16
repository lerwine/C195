/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.util.Objects;

/**
 *
 * @author lerwi
 */
@FunctionalInterface
interface PartialDataObjectRef<T extends DataObject> {
    
    /**
     *
     * @return
        */
       int getPrimaryKey();

       default T getPartial() { return null; }

    public static <T extends DataObject> PartialDataObjectRef<T> of(int primaryKey) {
        return () -> primaryKey;
    }

    public static <T extends DataObject> PartialDataObjectRef<T> partial(T dao) {
        Objects.requireNonNull(dao);
        return new PartialDataObjectRef() {
            @Override
            public int getPrimaryKey() { return dao.getPrimaryKey(); }
            @Override
            public T getPartial() { return dao; }
        };
    }

}
