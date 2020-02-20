/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import scheduler.util.ThrowableFunction;
import scheduler.view.ItemModel;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface RecordReader<T extends DataObjectImpl> extends ThrowableFunction<Connection, List<T>, SQLException> {
    String getWhereClause();
    String getLoadingMessage();
    DataObjectImpl.Factory<T, ? extends ItemModel<T>> getFactory();
}
