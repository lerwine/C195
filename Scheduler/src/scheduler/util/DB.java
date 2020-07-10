package scheduler.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DB {

    static final Pattern PATTERN_WC = Pattern.compile("([%_])|([^%_a-zA-Z\\d]+)");
    static final Pattern PATTERN_REPL = Pattern.compile("[%\\_]");

    public static String resultStringOrDefault(ResultSet rs, String columnLabel, String defaultValue) throws SQLException {
        String result = rs.getString(columnLabel);
        return (rs.wasNull()) ? defaultValue : result;
    }

    public static short resultShortOrDefault(ResultSet rs, String columnLabel, short defaultValue) throws SQLException {
        short result = rs.getShort(columnLabel);
        return (rs.wasNull()) ? defaultValue : result;
    }

    public static Pattern wcToPattern(String source) {
        if (null == source || source.isEmpty()) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_WC.matcher(source);
        while (matcher.find()) {
            if (null != matcher.group(2)) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("\\" + Pattern.quote(matcher.group(2))));
            } else if (matcher.group(1).equals("*")) {
                matcher.appendReplacement(sb, Matcher.quoteReplacement(".*?"));
            } else {
                matcher.appendReplacement(sb, Matcher.quoteReplacement("."));
            }
        }
        matcher.appendTail(sb);
        return Pattern.compile(sb.toString(), Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    }

    public static String escapeWC(String source) {
        if (null == source || source.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        Matcher matcher = PATTERN_REPL.matcher(source);
        while (matcher.find()) {
            matcher.appendReplacement(sb, Matcher.quoteReplacement("\\" + matcher.group()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
