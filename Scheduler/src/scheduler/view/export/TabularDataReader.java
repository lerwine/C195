package scheduler.view.export;

import java.util.Collection;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <K> Type of object that identifies a column to be exported
 * @param <V> Type of object that represents a row to be exported.
 */
public interface TabularDataReader<K, V> {
    Collection<K> getColumns();
    String getHeaderText(K column);
    String getColumnText(V item, K column);
}
