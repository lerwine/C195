package scheduler.model;

import java.util.List;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface SelectStatementTable<T extends IDataRow> {

    String getTableName();

    List<SelectStatementColumn> getColumns();

    StringBuilder toSelectSql();
}
