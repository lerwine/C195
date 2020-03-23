package devhelper;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class ReadOnlyLoadedProperties implements ILoadedProperties {

    private final BundleFile backingProperties;
    
    @Override
    public Locale getLocale() {
        return backingProperties.getLocale();
    }

//    @Override
//    public String calculateFileName() {
//        return backingProperties.calculateFileName();
//    }

    @Override
    public void list(PrintStream out) {
        backingProperties.list(out);
    }

    @Override
    public void list(PrintWriter out) {
        backingProperties.list(out);
    }

    @Override
    public String get(String key, String defaultValue) {
        return backingProperties.get(key, defaultValue);
    }

    @Override
    public int size() {
        return backingProperties.size();
    }

    @Override
    public boolean isEmpty() {
        return backingProperties.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return backingProperties.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return backingProperties.containsValue(value);
    }

    @Override
    public String get(Object key) {
        return backingProperties.get(key);
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException("Properties are read-only.");
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException("Properties are read-only.");
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException("Properties are read-only.");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Properties are read-only.");
    }

    @Override
    public Set<String> keySet() {
        return backingProperties.keySet();
    }

    @Override
    public Collection<String> values() {
        return backingProperties.values();
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return backingProperties.entrySet();
    }

    public ReadOnlyLoadedProperties(BundleFile properties) {
        backingProperties = Objects.requireNonNull(properties);
    }
    
}
