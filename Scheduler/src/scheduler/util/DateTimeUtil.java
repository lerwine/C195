package scheduler.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DateTimeUtil {

    public static ZonedDateTime toUTCZonedDateTime(Timestamp timestamp) {
        return timestamp.toLocalDateTime().atZone(ZoneId.of("UTC"));
    }

    public static ZonedDateTime toZonedDateTime(Timestamp timestamp, ZoneId zoneId) {
        return toUTCZonedDateTime(timestamp).withZoneSameInstant((null == zoneId) ? ZoneId.systemDefault() : zoneId);
    }

    public static ZonedDateTime toZonedDateTime(Timestamp timestamp) {
        return toZonedDateTime(timestamp, null);
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp, ZoneId zoneId) {
        return toZonedDateTime(timestamp, zoneId).toLocalDateTime();
    }

    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        return toLocalDateTime(timestamp, null);
    }

    public static Timestamp toUtcTimestamp(ZonedDateTime dateTime) {
        return Timestamp.valueOf(dateTime.withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
    }

    public static Timestamp toUtcTimestamp(LocalDateTime dateTime, ZoneId zoneId) {
        return toUtcTimestamp(dateTime.atZone((null == zoneId) ? ZoneId.systemDefault() : zoneId));
    }

    public static Timestamp toUtcTimestamp(LocalDateTime dateTime) {
        return toUtcTimestamp(dateTime, null);
    }

    public static String getTimeZoneDisplayText(TimeZone timeZone) {
        if (null == timeZone) {
            return "";
        }

        int u = timeZone.getRawOffset();
        boolean n = (u < 0);
        if (n) {
            u *= -1;
        }
        int s = u / 1000;
        u -= (s * 1000);
        int m = s / 60;
        s -= (m * 60);
        int h = m / 60;
        m -= (h * 60);
        String p;
        if (u > 0) {
            p = String.format("%s%02d:%02d:%02d.%d", (n) ? "-" : "+", h, m, s, u);
        } else if (s > 0) {
            p = String.format("%s%02d:%02d:%02d", (n) ? "-" : "+", h, m, s);
        } else {
            p = String.format("%s%02d:%02d", (n) ? "-" : "+", h, m);
        }

        String d = timeZone.getDisplayName();
        String i = timeZone.getID();
        if (i.equalsIgnoreCase(p)) {
            return (d.isEmpty() || d.equalsIgnoreCase(i)) ? i : String.format("%s (%s)", d, i);
        }
        if (d.isEmpty() || d.equalsIgnoreCase(i)) {
            return String.format("%s (%s)", i, p);
        }
        return String.format("%s (%s %s)", d, i, p);
    }

}
