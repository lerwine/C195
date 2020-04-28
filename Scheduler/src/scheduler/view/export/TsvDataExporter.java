package scheduler.view.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <K> Type of object that identifies a column to be exported
 * @param <V> Type of object that represents a row to be exported.
 */
public final class TsvDataExporter<K, V> extends TabularDataExporter<K, V> {
    public static final String RECORD_SEPARATOR = "\r\n";
    public static final String COLUMN_SEPARATOR = "\t";
    public static final String LINE_BREAK = "\n";
    public static final Pattern MUST_QUOTE = Pattern.compile("[\\r\\n\\t\"';:]");
    public static final Pattern MUST_IGNORE = Pattern.compile("[\\u0000-\\u0008\\u000B\\u000C-\\u0019\\u00FE]");
    public static final Pattern ENCODE = Pattern.compile("(\\r\\n?)|(\")|([\\u0000-\\u0008\\u000B\\u000C-\\u0019\\u00FE])");
    
    public static final String encode(String data) {
        if (null == data || data.isEmpty())
            return "";
        if (MUST_QUOTE.matcher(data).find()) {
            StringBuffer sb = new StringBuffer();
            Matcher matcher = ENCODE.matcher(data);
            while (matcher.find()) {
                if (null != matcher.group(1))
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\n"));
                else if (null != matcher.group(2))
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\"\""));
                else
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(""));
            }
            matcher.appendTail(sb);
            return sb.insert(0, "\"").append("\"").toString();
        } else {
            Matcher matcher = MUST_IGNORE.matcher(data);
            if (matcher.find()) {
                StringBuffer sb = new StringBuffer();
                do {
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(""));
                } while (matcher.find());
                matcher.appendTail(sb);
                return sb.toString();
            }
        }
        return data;
    }

    public TsvDataExporter(TabularDataReader<K, V> dataReader) {
        super(dataReader);
    }
    
    @Override
    protected final void exportHeaderCell(String data, int colIndex, Writer writer) throws IOException {
        if (colIndex > 0)
            writer.write(COLUMN_SEPARATOR);
        writer.write(encode(data));
    }

    @Override
    protected final void exportDataCell(String data, int colIndex, Writer writer) throws IOException {
        if (colIndex > 0)
            writer.write(COLUMN_SEPARATOR);
        writer.write(encode(data));
    }

    @Override
    protected final void exportDataRow(Iterator<K> columns, V item, Writer writer) throws IOException {
        writer.write(RECORD_SEPARATOR);
        super.exportDataRow(columns, item, writer);
    }

}
