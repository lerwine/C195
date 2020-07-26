package scheduler.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class LogHelper {

    private static final Pattern STRING_ENCODE = Pattern.compile("[\"\\u0000-\\u0019\\u007f-\\uffff\\\\]");
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getNumberInstance();

    public static String toLambdaSourceClass(Logger logger, String parentMethod, String target) {
        return String.format("%s.lambda$%s#%s", logger.getName(), parentMethod, target);
    }

    public static String toLambdaSourceClass(Logger logger, String nestedSourceClass, String parentMethod, String target) {
        return String.format("%s$%s.lambda$%s#%s", logger.getName(), nestedSourceClass, parentMethod, target);
    }

    /**
     * Ensures that the {@link Handler}s are configured to emit according to the {@link Level} of the {@link Logger}.
     *
     * @param logger The target {@link Logger}.
     */
    public static void ensureLoggerHandlerLevels(Logger logger) {
        Level level = logger.getLevel();
        int i = level.intValue();
        do {
            for (Handler h : logger.getHandlers()) {
                if (h.getLevel().intValue() > i) {
                    h.setLevel(level);
                }
            }
            if (logger.getUseParentHandlers()) {
                logger = logger.getParent();
            } else {
                break;
            }
        } while (null != logger);
    }

    /**
     * Sets the {@link Level} for the {@link Logger} and ensures that its {@link Handler}s are configured to emit accordingly.
     * <p>
     * Example:
     * <p>
     * <code>private static final Logger LOG = scheduler.util.AlertHelper.setLoggerAndHandlerLevels(Logger.getLogger(MyClas.class.getName()), Level.FINE);</code>
     *
     * @param logger The {@link Logger} to configure.
     * @param level The log level.
     * @return The {@link Logger} with the {@link Level} configured.
     */
    public static Logger setLoggerAndHandlerLevels(Logger logger, Level level) {
        logger.setLevel(level);
        ensureLoggerHandlerLevels(logger);
        return logger;
    }

    public static String iterableToLogText(Iterable<?> iterable) {
        if (null == iterable) {
            return "null";
        }
        return iteratorToLogText(iterable.iterator());
    }

    public static String iteratorToLogText(Iterator<?> iterator) {
        if (null == iterator) {
            return "null";
        }

        if (iterator.hasNext()) {
            String nl = System.lineSeparator();
            StringBuilder sb = new StringBuilder("{").append(nl).append("  ").append(toLogText(iterator.next()));
            int count = 1;
            while (iterator.hasNext()) {
                count++;
                sb.append(",").append(nl).append("  ").append(toLogText(iterator.next()));
                if (count == 255) {
                    sb.append(",").append(nl).append("  ...").append(nl).append("}");
                    return sb.toString();
                }
            }
            return sb.append(nl).append("}; count=").append(count).toString();
        }
        return "{ }; count=0";
    }

    public static <T extends Enum<T>> String toLogText(T value) {
        if (null == value) {
            return "null";
        }
        return String.format("%s.%s", value.getClass().getTypeName(), value.name());
    }

    public static String toLogText(Object obj) {
        if (null == obj) {
            return "null";
        }
        if (obj instanceof Number) {
            return NUMBER_FORMAT.format(obj);
        }
        if (obj instanceof Enum) {
            return String.format("%s.%s", obj.getClass().getTypeName(), ((Enum<?>) obj).name());
        }
        if (obj instanceof Boolean) {
            return ((Boolean) obj) ? "true" : "false";
        }
        if (obj instanceof Optional) {
            Optional<?> opt = (Optional<?>) obj;
            if (opt.isPresent()) {
                return String.format("Optional.of(%s)", toLogText(opt.get()));
            }
            return "Optional.empty()";
        }

        if (obj instanceof String) {
            String value = (String) obj;
            if (value.isEmpty()) {
                return "\"\"";
            }

            StringBuffer sb = new StringBuffer();
            sb.append('"');
            Matcher matcher = STRING_ENCODE.matcher(value);
            while (matcher.find()) {
                String r = matcher.group(0);
                switch (r) {
                    case "\r":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\r"));
                        break;
                    case "\n":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\n"));
                        break;
                    case "\t":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\t"));
                        break;
                    case "\b":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\b"));
                        break;
                    case "\f":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\f"));
                        break;
                    case "\"":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\\""));
                        break;
                    case "\\":
                        matcher.appendReplacement(sb, Matcher.quoteReplacement("\\\\"));
                        break;
                    default:
                        matcher.appendReplacement(sb, Matcher.quoteReplacement(String.format("\\u%04x", matcher.group(0).codePointAt(0))));
                        break;
                }
            }
            matcher.appendTail(sb);
            return sb.append('"').toString();
        }
        return Objects.toString(obj);
    }

    public static boolean logWarnings(Connection connection, Logger logger) throws SQLException {
        SQLWarning sqlWarning = connection.getWarnings();
        if (null == sqlWarning) {
            return false;
        }
        do {
            logger.log(Level.WARNING, "SQL warning", sqlWarning);
        } while (null != (sqlWarning = sqlWarning.getNextWarning()));
        return true;
    }

    private LogHelper() {
    }

}
