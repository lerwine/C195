package scheduler.view.export;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <K> Type of object that identifies a column to be exported
 * @param <V> Type of object that represents a row to be exported.
 */
public final class HtmlDataExporter<K, V> extends TabularDataExporter<K, V> {
    public static final Pattern CDATA_RECODE = Pattern.compile("\\]\\]>");
    public static final Pattern ENTITY_ENCODE = Pattern.compile("\r\n?|\n|[\\u0000-\\u0008\\u000B\\u000C\\u000E-\\u0019&<>\\u00fe-\\uffff]");
    private final String title;
    
    public static final String toEntityEncoded(String text) {
        if (null == text || text.isEmpty())
            return text;
        Matcher matcher = ENTITY_ENCODE.matcher(text);
        if (matcher.find()) {
            return toEntityEncoded(matcher);
        }
        return text;
    }

    private static String toEntityEncoded(Matcher matcher) {
        StringBuffer sb = new StringBuffer();
        do {
            switch (matcher.group(1)) {
                case "&":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("&amp;"));
                    break;
                case "<":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("&lt;"));
                    break;
                case ">":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("&gt;"));
                    break;
                case "\r\n":
                case "\r":
                case "\n":
                    matcher.appendReplacement(sb, Matcher.quoteReplacement("\n<br />"));
                    break;
                default:
                    matcher.appendReplacement(sb, Matcher.quoteReplacement(String.format("&#%d;", matcher.group(1).codePointAt(0))));
                    break;
            }
        } while (matcher.find());
        matcher.appendTail(sb);
        return sb.insert(0, "<![CDATA[").append("]]>").toString();
    }
    
    public static final String toCDataSection(String text) {
        if (null == text || text.isEmpty())
            return "<![CDATA[]]>";
        Matcher matcher = CDATA_RECODE.matcher(text);
        if (matcher.find()) {
            StringBuffer sb = new StringBuffer();
            do {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("]]]]><![CDATA[>"));
            } while (matcher.find());
            matcher.appendTail(sb);
            return sb.insert(0, "<![CDATA[").append("]]>").toString();
        }
        return text;
    }
    
    public static final String encode(String text) {
        if (null == text || text.isEmpty())
            return "";
        if (text.contains("\t"))
            return toCDataSection(text);
        
        Matcher matcher = ENTITY_ENCODE.matcher(text);
        if (matcher.find()) {
            String a = toEntityEncoded(matcher);
            String b = toCDataSection(text);
            return (a.length() < b.length()) ? a : b;
        }
        return text;
    }

    public HtmlDataExporter(String title, TabularDataReader<K, V> dataReader) {
        super(dataReader);
        this.title = title;
    }
    
    @Override
    protected final void exportHeader(Iterator<K> columns, Writer writer) throws IOException {
        writer.write(String.format("%n\t\t<thead>%n\t\t\t<tr>"));
        super.exportHeader(columns, writer);
        writer.write(String.format("%n\t\t\t</tr>%n\t\t</thead>"));
    }

    @Override
    protected final void exportHeaderCell(String data, int colIndex, Writer writer) throws IOException {
        writer.write(String.format("%n\t\t\t\t<th>"));
        String text = encode(data);
        if (!text.isEmpty())
            writer.write(text);
        writer.write("</th>");
    }

    @Override
    protected final void exportRows(Collection<K> columns, Iterator<V> rows, Writer writer) throws IOException {
        writer.write(String.format("%n\t\t<tbody>"));
        super.exportRows(columns, rows, writer);
        writer.write(String.format("%n\t\t</tbody>"));
    }

    @Override
    protected final void exportDataRow(Iterator<K> columns, V item, Writer writer) throws IOException {
        writer.write(String.format("%n\t\t\t<tr>"));
        super.exportDataRow(columns, item, writer);
        writer.write(String.format("%n\t\t\t</tr>"));
    }

    @Override
    protected final void exportDataCell(String data, int colIndex, Writer writer) throws IOException {
        writer.write(String.format("%n\t\t\t\t<td>"));
        String text = encode(data);
        if (!text.isEmpty())
            writer.write(text);
        writer.write("</td>");
    }

    @Override
    public void export(Writer writer, Iterable<V> rows) throws IOException {
        writer.write(String.format("<!DOCTYPE html>%n<html lang=\"en\">%n<head>%n\t<meta charset=\"UTF-8\">%n" +
            "\t<meta name=\"viewport\" content=\"width=900, initial-scale=1.0\">%n\t<title>%s</title>%n</head>%n<body>%n\t<table>",
                toEntityEncoded(title)));
        super.export(writer, rows);
        writer.write(String.format("%n\t</table>%n</body>%n</html>"));
    }

}
