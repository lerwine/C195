package scheduler.model;

import java.util.List;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T>
 */
public interface SelectStatementTable<T extends IDataRow> {

    String getTableName();

    List<SelectStatementColumn> getColumns();

    StringBuilder toSelectSql();
}
