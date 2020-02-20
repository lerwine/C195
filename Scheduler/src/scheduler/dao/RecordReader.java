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
