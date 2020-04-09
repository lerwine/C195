package scheduler.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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

    public static LocalDateTime fromUtcTimestamp(Timestamp timestamp) {
        Objects.requireNonNull(timestamp, "Timestamp object cannot be null");
        return timestamp.toLocalDateTime().atZone(ZoneId.of("UTC")).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     *
     * @param dateTime
     * @return
     */
    public static Timestamp toUtcTimestamp(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "Date/Time object cannot be null");
        return Timestamp.valueOf(dateTime.atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime());
    }
}
