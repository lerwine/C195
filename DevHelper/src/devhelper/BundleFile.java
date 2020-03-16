/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author lerwi
 */
public class BundleFile extends HashMap<String, String> implements ILoadedProperties {
    private boolean changed;
    private final Properties backingProperties;
    private final ReadOnlyLoadedProperties readOnlyProperties;
    private final Locale locale;
    private static final Logger LOG = Logger.getLogger(BundleFile.class.getName());

    public boolean isChanged() {
        return changed;
    }

    @Override
    public Locale getLocale() {
        return locale;
    }

    @Override
    public void list(PrintStream out) {
        out.println("-- listing properties --");
        this.entrySet().forEach((t) -> {
            String key = t.getKey();
            String val = t.getValue();
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        });
    }

    @Override
    public void list(PrintWriter out) {
        out.println("-- listing properties --");
        this.entrySet().forEach((t) -> {
            String key = t.getKey();
            String val = t.getValue();
            if (val.length() > 40) {
                val = val.substring(0, 37) + "...";
            }
            out.println(key + "=" + val);
        });
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        super.replaceAll(function);
        checkChange();
    }

    @Override
    public String replace(String key, String value) {
        String result = super.replace(key, value);
        checkChange();
        return result;
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        boolean result = super.replace(key, oldValue, newValue);
        checkChange();
        return result;
    }

    @Override
    public void clear() {
        super.clear();
        checkChange();
    }

    @Override
    public String remove(Object key) {
        String result = super.remove(key);
        checkChange();
        return result;
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        super.putAll(m);
        checkChange();
    }

    @Override
    public String put(String key, String value) {
        String result = super.put(key, value);
        checkChange();
        return result;
    }

    private synchronized void checkChange() {
        changed = size() != backingProperties.size() || keySet().stream().anyMatch((t) -> !(backingProperties.containsKey(t) && get(t).equals(backingProperties.get(t))));
    }
    
    @Override
    public synchronized String get(String key, String defaultValue) {
        return (containsKey(key)) ? get(key) : defaultValue;
    }
    
    @Override
    public synchronized boolean equals(Object o) {
        return null != o && o instanceof BundleFile && locale.toLanguageTag().equalsIgnoreCase(((BundleFile)o).locale.toLanguageTag());
    }

    @Override
    public synchronized int hashCode() {
        return locale.toLanguageTag().hashCode();
    }

    @Override
    public synchronized String toString() {
        return locale.toLanguageTag();
    }
    
    public ReadOnlyLoadedProperties getReadOnlyMap() {
        return readOnlyProperties;
    }
    
    public BundleFile(Locale locale) {
        readOnlyProperties = new ReadOnlyLoadedProperties(this);
        backingProperties = new Properties();
        this.locale = Objects.requireNonNull(locale);
        assert !locale.toLanguageTag().equalsIgnoreCase("und") : "Language could not be determined";
    }
    
    public String calculateFileName(String baseName) {
        return String.format("%s_%s.properties", baseName, locale.toLanguageTag().replace("-", "_"));
    }
    
    public boolean tryLoad(File baseName) {
        File directory = baseName.getParentFile();
        if (null == directory || !(directory.exists() && directory.isDirectory()))
            return false;
        File file = Paths.get(directory.toString(), calculateFileName(baseName.getName())).toFile();
        if (!(file.exists() && file.isFile()))
            return false;
        try (InputStream iStream = Files.newInputStream(file.toPath())) {
            backingProperties.load(iStream);
        } catch (IOException ex) {
            LOG.log(Level.INFO, String.format("Error loading resource \"%s\"", file.toString()), ex);
            return false;
        }
        reset();
        return true;
    }
    
    public void load(File baseName) throws IOException {
        File directory = baseName.getParentFile();
        if (null == directory)
            throw new IOException("Base name cannot be root directory");
        if (!(directory.exists() && directory.isDirectory()))
            throw new IOException("Directory not found");
        File file = Paths.get(directory.toString(), calculateFileName(baseName.getName())).toFile();
        if (!(file.exists() && file.isFile()))
            throw new IOException("File not found");
        try (InputStream iStream = Files.newInputStream(file.toPath())) {
            backingProperties.load(iStream);
        }
        reset();
    }
    
    public void save(File baseName) throws IOException {
        File directory = baseName.getParentFile();
        if (null == directory)
            throw new IOException("Base name cannot be root directory");
        if (!(directory.exists() && directory.isDirectory()))
            throw new IOException("Directory not found");
        Properties newProperties = new Properties();
        keySet().stream().sorted().forEach((t) -> {
            newProperties.setProperty(t, get(t));
        });
        try (OutputStream outputStream = Files.newOutputStream(Paths.get(directory.toString(), calculateFileName(baseName.getName())),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            newProperties.store(outputStream, null);
        }
        backingProperties.clear();
        newProperties.keySet().forEach((t) -> backingProperties.put(t, newProperties.get(t)));
        changed = false;
    }

    private void reset() {
        clear();
        backingProperties.keySet().forEach((t) -> {
            if (t instanceof String)
                put((String)t, backingProperties.getProperty((String)t));
        });
        changed = false;
    }
    
}
