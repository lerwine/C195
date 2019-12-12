/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import com.sun.javaws.exceptions.InvalidArgumentException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.util.Pair;

/**
 *
 * @author Leonard T. Erwine
 */
public class LoadedPropertySet extends Properties {
    private static final Pattern PATTERN_BASE_AND_LANG = Pattern.compile("^([^\\\\\\._]+)_([^\\.]+((?=\\.+.[^\\.]+\\.)\\.+[^\\.]+)*)\\.properties",
            Pattern.CASE_INSENSITIVE);
    
    private static final Pattern PATTERN_IMPORT = Pattern.compile("\\\\((?=\\r)\\r\\n?|.)|\\r\\n?|\\n");
    
    private final Optional<Locale> locale;
    private final File source;
    private final String baseName;
    
    public Optional<Locale> getLocale() { return locale; }
    
    public File getSource() { return source; }
    
    public String getBaseName() { return baseName; }
    
    public static Pair<String, String> getBaseAndLang(String name) {
        Matcher m = PATTERN_BASE_AND_LANG.matcher(name);
        if (!m.matches())
            return new Pair<>(name, null);
        return new Pair<>(m.group(1), m.group(2));
    }
    
    public static FilenameFilter getPropertiesFileFilter(String baseName) {
        if (baseName == null || baseName.isEmpty())
            return (File dir, String name) -> {
                int ext = name.lastIndexOf('.');
                return ext > 0 && ext == name.length() - 11 && name.substring(ext + 1).toLowerCase().equals("properties");
            };
        
        return getPropertiesFileFilter(baseName, false);
    }
    
    public static FilenameFilter getPropertiesFileFilter(String baseName, boolean caseSensitive) {
        if (caseSensitive)
            return (File dir, String name) -> {
                int ext = name.lastIndexOf('.');
                if (ext < 0 || ext != name.length() - 11 || !name.substring(ext + 1).toLowerCase().equals("properties"))
                    return false;
                Matcher m = PATTERN_BASE_AND_LANG.matcher(name);
                return m.matches() && m.group(1).equals(baseName);
            };
        return (File dir, String name) -> {
            int ext = name.lastIndexOf('.');
            if (ext < 0 || ext != name.length() - 11 || !name.substring(ext + 1).toLowerCase().equals("properties"))
                return false;
            Matcher m = PATTERN_BASE_AND_LANG.matcher(name);
            return m.matches() && m.group(1).toLowerCase().equals(baseName);
        };
    }
    
    public Stream<String> getStringPropertyKeys() {
        Stream.Builder<String> builder = Stream.builder();
        Enumeration<Object> e = keys();
        while (e.hasMoreElements()) {
            Object k = e.nextElement();
            if (k instanceof String && get(k) instanceof String)
                builder.accept((String)k);
        }
        return builder.build();
    }
    
    public String exportValues() {
        return getStringPropertyKeys().map((String k) -> (String)get(k)).reduce(null, (String t, String u) -> {
            if (u.isEmpty())
                return (t == null) ? "" : t + "\n";
            String[] lines = u.split("\\r\\n?|\\n");
            int e = lines.length - 1;
            String[] a;
            for (int i = 0; i < e; i++) {
                a = lines[i].split("\\\\");
                lines[i] = ((a.length == 0) ? lines[i] : String.join("\\\\", a)) + "\\";
            }
            a = lines[e].split("\\\\");
            if (a.length > 0)
                lines[e] = String.join("\\\\", a);
            if (t == null)
                return (e == 0) ? a[0] : String.join("\r\n", lines);
            return t + "\n" + ((e == 0) ? a[0] : String.join("\r\n", lines));
        });
    }
    
    public void ImportValues(Stream<String> keys, String source) {
        if (source == null) {
            long expected;
            if (keys != null && (expected = keys.count()) != 0)
                throw new IllegalArgumentException(String.format("Expected %d values; actual: 0", expected));
            return;
        }
        
        Iterator<String> iterator;
        int index = 0;
        Matcher m;
        String k;
        if (source.isEmpty() || !(m = PATTERN_IMPORT.matcher(source)).matches()) {
            if (keys == null || !(iterator = keys.iterator()).hasNext())
                throw new IllegalArgumentException("Expected 0 values; actual: 1");
            k = iterator.next();
            while (iterator.hasNext()) {
                index++;
                iterator.next();
            }
            
            if (index != 0)
                throw new IllegalArgumentException(String.format("Expected %d values; actual: 1", index + 1));
            put(k, source);
            return;
        }
        
        HashMap<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
    }
    
    public LoadedPropertySet(File directory, String baseName, Locale locale) throws IOException {
        super();
        if (!(directory.exists() && directory.isDirectory()))
            throw new IllegalArgumentException("Directory does not exist");
        if (baseName.indexOf('.') > -1 || baseName.indexOf('_') > -1 || baseName.indexOf('\\') > -1)
            throw new IllegalArgumentException("Invalid base name");
        if (!directory.isAbsolute())
            directory = directory.getAbsoluteFile();
        String languageTag;
        if (locale == null || (languageTag = locale.toLanguageTag()).trim().isEmpty() || languageTag.endsWith("."))
            throw new IllegalArgumentException("Invalid locale");
        source = new File(String.format("%s%s%s_%s.properties", directory.getAbsolutePath(), File.separator, baseName, languageTag));
        this.locale = Optional.of(locale);
        this.baseName = baseName;
        if (!source.exists())
            return;
        
        // Loading properties file from the classpath
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(source.getAbsolutePath());
        if(iStream == null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error loading %s", source);
            throw new IllegalArgumentException(String.format("File \"%s\" not loaded.", source));
        }
        try { load(iStream); }
        finally { iStream.close(); }
    }
    
    public LoadedPropertySet(File source) throws IOException, Exception {
        super();
        
        this.source = source;
        Matcher m = PATTERN_BASE_AND_LANG.matcher(source.getName());
        Locale l;
        if (m.matches() && (l = Locale.forLanguageTag(m.group(2))) != null) {
            baseName = m.group(1);
            locale = Optional.of(l);
        } else {
            String n = source.getName();
            int index = n.lastIndexOf('.');
            baseName = (index > 0) ? n.substring(0, index) : n;
            locale = Optional.empty();
        }
        
        if (!source.exists())
            return;
        
        // Loading properties file from the classpath
        InputStream iStream = getClass().getClassLoader().getResourceAsStream(source.getAbsolutePath());
        if(iStream == null) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "Error loading %s", source);
            throw new Exception(String.format("File \"%s\" not loaded.", source));
        }
        try { load(iStream); }
        finally { iStream.close(); }
    }
}
