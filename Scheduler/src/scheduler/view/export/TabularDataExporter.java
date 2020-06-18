package scheduler.view.export;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <K> Type of object that identifies a column to be exported
 * @param <V> Type of object that represents a row to be exported.
 */
public abstract class TabularDataExporter<K, V> {

    private final TabularDataReader<K, V> dataReader;

    protected TabularDataExporter(TabularDataReader<K, V> dataReader) {
        this.dataReader = dataReader;
    }

    protected abstract void exportHeaderCell(String data, int colIndex, Writer writer) throws IOException;

    protected abstract void exportDataCell(String data, int colIndex, Writer writer) throws IOException;

    protected void exportHeader(Iterator<K> columns, Writer writer) throws IOException {
        int colIndex = -1;
        while (columns.hasNext()) {
            String text = dataReader.getHeaderText(columns.next());
            exportHeaderCell((null == text) ? "" : text, ++colIndex, writer);
        }
    }

    protected void exportDataRow(Iterator<K> columns, V item, Writer writer) throws IOException {
        int colIndex = -1;
        while (columns.hasNext()) {
            String text = dataReader.getColumnText(item, columns.next());
            exportDataCell((null == text) ? "" : text, ++colIndex, writer);
        }
    }

    protected void exportRows(Collection<K> columns, Iterator<V> rows, Writer writer) throws IOException {
        while (rows.hasNext()) {
            V item = rows.next();
            if (null != item) {
                exportDataRow(columns.iterator(), item, writer);
            }
        }
    }

    public void export(Writer writer, Iterable<V> rows) throws IOException {
        Collection<K> columns = new ArrayList<>();
        Iterator<K> iterator = dataReader.getColumns().iterator();
        while (iterator.hasNext()) {
            columns.add(iterator.next());
        }
        columns = Collections.unmodifiableCollection(columns);
        exportHeader(columns.iterator(), writer);
        exportRows(columns, rows.iterator(), writer);
    }
}
