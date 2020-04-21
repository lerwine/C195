package scheduler.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class DB {

    public static String resultStringOrDefault(ResultSet rs, String columnLabel, String defaultValue) throws SQLException {
        String result = rs.getString(columnLabel);
        return (rs.wasNull()) ? defaultValue : result;
    }

    public static short resultShortOrDefault(ResultSet rs, String columnLabel, short defaultValue) throws SQLException {
        short result = rs.getShort(columnLabel);
        return (rs.wasNull()) ? defaultValue : result;
    }

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
    
}
