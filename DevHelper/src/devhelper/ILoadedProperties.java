package devhelper;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Locale;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public interface ILoadedProperties extends Map<String, String> {
    Locale getLocale();
    void list(PrintStream out);
    void list(PrintWriter out);
    String get(String key, String defaultValue);
}
