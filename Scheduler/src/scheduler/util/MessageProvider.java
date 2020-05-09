package scheduler.util;

import java.util.Objects;
import scheduler.AppResourceKeys;
import scheduler.AppResources;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface MessageProvider {

    public static MessageProvider of(String title, String heading, String content) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(heading);
        Objects.requireNonNull(content);
        return new MessageProvider() {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getHeaderText() {
                return heading;
            }

            @Override
            public String getContentText() {
                return content;
            }

        };
    }

    public static MessageProvider of(String title, String content) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(content);
        return new MessageProvider() {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getContentText() {
                return content;
            }

        };
    }

    public static MessageProvider of(String content) {
        Objects.requireNonNull(content);
        return () -> content;
    }

    public static MessageProvider withLogMessage(String title, String heading, String content, String logMessage) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(heading);
        Objects.requireNonNull(content);
        Objects.requireNonNull(logMessage);
        return new MessageProvider() {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getHeaderText() {
                return heading;
            }

            @Override
            public String getContentText() {
                return content;
            }

            @Override
            public String getLogMessage() {
                return logMessage;
            }

        };
    }

    public static MessageProvider withLogMessage(String title, String content, String logMessage) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(content);
        Objects.requireNonNull(logMessage);
        return new MessageProvider() {
            @Override
            public String getTitle() {
                return title;
            }

            @Override
            public String getContentText() {
                return content;
            }

            @Override
            public String getLogMessage() {
                return logMessage;
            }

        };
    }

    public static MessageProvider withLogMessage(String content, String logMessage) {
        Objects.requireNonNull(content);
        Objects.requireNonNull(logMessage);
        return new MessageProvider() {
            @Override
            public String getContentText() {
                return content;
            }

            @Override
            public String getLogMessage() {
                return logMessage;
            }

        };
    }

    default String getTitle() {
        return AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_UNEXPECTEDERRORTITLE);
    }

    default String getHeaderText() {
        return "";
    }

    default String getLogMessage() {
        String title = getTitle();
        String heading = getHeaderText();
        String content = getContentText();
        if (null == title || title.trim().isEmpty()) {
            if (null == heading || heading.trim().isEmpty()) {
                return content;
            }
            if (null == content || content.trim().isEmpty()) {
                return heading;
            }
            return String.format("%s:%n%s", heading, content);
        }

        if (null == heading || heading.trim().isEmpty()) {
            if (null == content || content.trim().isEmpty()) {
                return title;
            }
            return String.format("%s:%n%s", title, content);
        }
        if (null == content || content.trim().isEmpty()) {
            return String.format("%s:%n%s", title, heading);
        }
        return String.format("%s:%n%s%n%s", title, heading, content);
    }

    String getContentText();

}
