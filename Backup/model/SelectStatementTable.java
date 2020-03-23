package scheduler.model;

import java.util.List;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T>
 */
public interface SelectStatementTable<T extends IDataRow> {

    String getTableName();

    List<SelectStatementColumn> getColumns();

    StringBuilder toSelectSql();
}
