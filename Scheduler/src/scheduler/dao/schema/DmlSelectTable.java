package scheduler.dao.schema;

import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface DmlSelectTable {
    DbTable getDbTable();
    String getName();
    String getAlias();
    Map<String, DmlSelectQueryBuilder.JoinedTable> getJoins();
    boolean isReadOnly();
    DmlSelectQueryBuilder.JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo);
    DmlSelectQueryBuilder.JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, DbColumn ...toAdd);
    DmlSelectQueryBuilder.JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, Stream<DbColumn> toAdd);
    DmlSelectQueryBuilder.JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias);
    DmlSelectQueryBuilder.JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias, DbColumn ...toAdd);
    DmlSelectQueryBuilder.JoinedTable join(DbColumn joinFrom, TableJoinType type, DbColumn joinTo, String alias, Stream<DbColumn> toAdd);
    boolean add(DbColumn column, String alias);
    boolean add(DbColumn column);
    boolean addAll(DbColumn[] columns);
    boolean addAll(Stream<DbColumn> columns);
    boolean addCountOf(DbColumn column, String alias);
    boolean containsColumn(DbColumn column);
    DmlSelectQueryBuilder getBuilder();
}
