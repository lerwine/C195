
package devhelper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import javafx.util.Pair;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class ObsoleteBundleSet implements Map<String, String> {
    public final Pattern PATTERN_BUNDLENAMESUFFIX = Pattern.compile("^_([a-z]{2,3}(?:_[a-z\\d]+)*)\\.properties$", Pattern.CASE_INSENSITIVE);
    private File directory;
    private String baseName;
    private boolean changed;
    private final Values values = new Values();
    private final EntrySet entrySet = new EntrySet();
    private final ReadOnlyListWrapper<ResourceProperty> leftProperties = new ReadOnlyListWrapper<>();
    private final ObservableSet<String> keySet;
    private final ObservableSet<String> readOnlyKeySet;
    private final ObservableSet<String> languageTags;
    private final ObservableSet<String> readOnlyLanguageTags;
    private final ObservableSet<BundleFile> files;
    
    BundleFile current;
    private Map<String, String> currentMap;
    
    public boolean isCurrentChanged() {
        return null != current && current.isChanged();
    }
    
    public File getDirectory() {
        return directory;
    }

    public String getBaseName() {
        return baseName;
    }

    public synchronized String getCurrentLanguageTag() {
        return (null == current) ? "" : current.getLocale().toLanguageTag();
    }
    
    public synchronized boolean setCurrentLanguageTag(String tag) {
        Optional<BundleFile> p = files.stream().filter((t) -> t.getLocale().toLanguageTag().equalsIgnoreCase(tag)).findFirst();
        if (p.isPresent()) {
            currentMap = current = p.get();
            return true;
        }
        return false;
    }

    public ObservableSet<String> getLanguageTags() {
        return readOnlyLanguageTags;
    }
    
    public synchronized Locale getCurrentLocale() {
        return (null == current) ? null : current.getLocale();
    }
    
    public Stream<Locale> getAllLocales() {
        return files.stream().map((t) -> t.getLocale());
    }

    public synchronized boolean addLanguage(Locale locale) {
        String tag = locale.toLanguageTag();
        if (tag.equalsIgnoreCase("und"))
            throw new IllegalArgumentException();
        if (languageTags.contains(tag))
            return false;
        currentMap = current = new BundleFile(locale);
        files.add(current);
        languageTags.add(tag);
        changed = true;
        return true;
    }
    
    public synchronized boolean removeLanguage(Locale locale) {
        String tag = locale.toLanguageTag();
        Optional<BundleFile> p = files.stream().filter((t) -> t.getLocale().toLanguageTag().equalsIgnoreCase(tag)).findFirst();
        if (p.isPresent()) {
            BundleFile lp = p.get();
            files.remove(lp);
            languageTags.remove(tag);
            if (files.isEmpty()) {
                current = null;
                currentMap = Collections.EMPTY_MAP;
            } else {
                String[] obsoleteKeys = p.get().keySet().stream().filter((t) -> files.stream().allMatch((c) -> !c.containsKey(t))).toArray(String[]::new);
                for (String k : obsoleteKeys)
                    keySet.remove(k);
                if (current == lp) {
                    currentMap = current = files.iterator().next();
                }
            }
            changed = true;
            return true;
        }
        return false;
    }

    public ObsoleteBundleSet() {
        keySet = FXCollections.observableSet();
        readOnlyKeySet = FXCollections.unmodifiableObservableSet(keySet);
        languageTags = FXCollections.observableSet();
        readOnlyLanguageTags = FXCollections.unmodifiableObservableSet(languageTags);
        files = FXCollections.observableSet();
        directory = null;
        baseName = "";
        current = null;
        currentMap = Collections.EMPTY_MAP;
    }
    
    public synchronized boolean load(String basePath) {
        File file = (new File(basePath)).getAbsoluteFile();
        String name = file.getName();
        if (file.exists()) {
            if (file.isDirectory())
                throw new IllegalArgumentException("Base path cannot be a directory");
            file = file.getParentFile();
            if (file == null || !file.isDirectory())
                throw new IllegalArgumentException("Parent directory not found");
        } else {
            file = file.getParentFile();
            if (null == file)
                throw new IllegalArgumentException("Base path cannot be a root directory");
            if (!file.exists())
                throw new IllegalArgumentException("Parent directory not found");
        }
        BundleLoader loader = new BundleLoader(name);
        BundleFile[] results = loader.load(file);
        Alert alert;
        if (loader.invalidTags.isEmpty()) {
            if (!loader.loadFailures.isEmpty()) {
                if (loader.loadFailures.size() == 1) {
                    alert = new Alert(Alert.AlertType.ERROR, String.format("Unable to load file %s", loader.loadFailures.get(0).toString()),
                            ButtonType.OK);
                    alert.setTitle("File load failure");
                }
                else {
                    StringBuilder sb = new StringBuilder("Unable to load the following files:");
                    loader.loadFailures.forEach((t) -> sb.append("\n\t").append(t.toString()));
                    alert = new Alert(Alert.AlertType.ERROR, sb.toString(), ButtonType.OK);
                    alert.setTitle("File load failures");
                }
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                if (results.length == 0)
                    return false;
            }
        } else {
            if (loader.loadFailures.isEmpty()) {
                if (loader.invalidTags.size() == 1) {
                    alert = new Alert((results.length == 0) ? Alert.AlertType.ERROR : Alert.AlertType.WARNING,
                            String.format("File %s references unknown language tag \"%s\".",
                            loader.invalidTags.get(0).getValue().toString(), loader.invalidTags.get(0).getKey()),
                            ButtonType.OK);
                } else {
                    StringBuilder sb = new StringBuilder("The following files have unknown language tags:");
                    loader.invalidTags.forEach((t) -> sb.append("\n\t").append(t.getKey()).append(": ").append(t.getValue().toString()));
                    alert = new Alert((results.length == 0) ? Alert.AlertType.ERROR : Alert.AlertType.WARNING, sb.toString(), ButtonType.OK);
                }
                alert.setTitle("Invalid language tag");
            } else {
                StringBuilder sb;
                if (loader.loadFailures.size() == 1) {
                    sb = new StringBuilder("Unable to load file ").append(loader.loadFailures.get(0).toString());
                } else {
                    sb = new StringBuilder("Unable to load the following files:");
                    loader.loadFailures.forEach((t) -> sb.append("\n\t").append(t.toString()));
                }
                
                if (loader.invalidTags.size() == 1) {
                    sb.append("\n\nFile ").append(loader.invalidTags.get(0).getValue().toString()).append(" references unknown language tag \"")
                            .append(loader.invalidTags.get(0).getKey()).append("\".");
                } else {
                    sb.append("\n\nThe following files have unknown language tags:");
                    loader.invalidTags.forEach((t) -> sb.append("\n\t").append(t.getKey()).append(": ").append(t.getValue().toString()));
                }
                alert = new Alert(Alert.AlertType.ERROR, sb.toString(), ButtonType.OK);
                alert.setTitle("File load failures");
            }
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            if (results.length == 0)
                return false;
        }
        
        keySet.clear();
        languageTags.clear();
        files.clear();
        directory = file;
        baseName = name;
        loader.tags.forEach((t) -> languageTags.add(t));
        for (BundleFile p : results) {
            files.add(p);
            p.keySet().forEach((t) -> {
                if (!keySet.contains(t))
                    keySet.add(t);
            });
        }
        
        changed = false;
        return true;
    }

    @Override
    public int size() {
        return keySet.size();
    }

    @Override
    public boolean isEmpty() {
        return keySet.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return null != key && key instanceof String && keySet.contains((String)key);
    }

    @Override
    public synchronized boolean containsValue(Object value) {
        return currentMap.containsValue(value) || (null != value && value instanceof String && ((String)value).isEmpty() &&
                keySet.stream().anyMatch((t) -> !currentMap.containsKey(t)));
    }

    @Override
    public synchronized String get(Object key) {
        String value = currentMap.get(key);
        if (null != value)
            return value;
        return (null != key && key instanceof String && keySet.contains((String)key)) ? "" : null;
    }

    public synchronized String get(Object key, String languageTag) {
        Optional<BundleFile> p = files.stream().filter((t) -> t.getLocale().toLanguageTag().equalsIgnoreCase(languageTag)).findFirst();
        assert p.isPresent() : "Cannot find language tag";
        if (null != key && key instanceof String) {
            BundleFile lp = p.get();
            String value = lp.get((String)key);
            if (null != value)
                return value;
            if (keySet.contains((String)key))
                return "";
        }
        return null;
    }

    @Override
    public synchronized String put(String key, String value) {
        boolean wasChanged = changed;
        if (!Objects.requireNonNull(current, "No properties files have been added").containsKey(Objects.requireNonNull(key, "Key cannot be null"))) {
            if (keySet.contains(key)) {
                current.put(key, Objects.requireNonNull(value, "Value cannot be null"));
                return "";
            }
            keySet.add(key);
        }
        String result =  current.put(key, Objects.requireNonNull(value, "Value cannot be null"));
        if (current.isChanged())
            changed = true;
        else if (wasChanged)
            changed = files.stream().anyMatch((t) -> t.isChanged());
        return result;
    }
    
    public synchronized String put(String key, String value, String languageTag) {
        Optional<BundleFile> p = files.stream().filter((t) -> t.getLocale().toLanguageTag().equalsIgnoreCase(languageTag)).findFirst();
        assert p.isPresent() : "Cannot find language tag";
        BundleFile lp = p.get();
        if (!lp.containsKey(Objects.requireNonNull(key, "Key cannot be null"))) {
            if (keySet.contains(key)) {
                lp.put(key, Objects.requireNonNull(value, "Value cannot be null"));
                return "";
            }
            keySet.add(key);
        }
        // TODO: Update change field
        return lp.put(key, Objects.requireNonNull(value, "Value cannot be null"));
    }

    @Override
    public synchronized String remove(Object key) {
        if (null != key && key instanceof String && keySet.contains((String)key)) {
            String result = (currentMap.containsKey((String)key)) ? currentMap.remove(key) : "";
            files.forEach((t) -> t.remove(key));
            // TODO: Update change field
        }
        return null;
    }

    @Override
    public synchronized void putAll(Map<? extends String, ? extends String> m) {
        currentMap.putAll(m);
        m.keySet().forEach((t) -> {
            if (!keySet.contains(t))
                keySet.add(t);
        });
        // TODO: Update change field
    }

    @Override
    public synchronized void clear() {
        files.forEach((t) -> t.clear());
        keySet.clear();
        // TODO: Update change field
    }

    @Override
    public Set<String> keySet() {
        return readOnlyKeySet;
    }

    @Override
    public Collection<String> values() {
        return values;
    }

    @Override
    public Set<Map.Entry<String, String>> entrySet() {
        return entrySet;
    }
    
    class Values implements Collection<String> {

        @Override
        public int size() {
            return keySet.size();
        }

        @Override
        public boolean isEmpty() {
            return keySet.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return null != o && o instanceof String && currentMap.containsValue((String)o);
        }

        @Override
        public Iterator<String> iterator() {
            return currentMap.values().iterator();
        }

        @Override
        public Object[] toArray() {
            return currentMap.values().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            Object[] arr = toArray();
            if (null != a && a.length >= arr.length) {
                System.arraycopy(a, 0, arr, 0, arr.length);
                if (a.length > arr.length)
                    a[arr.length] = null;
            } else {
                a = (T[])(new Object[arr.length]);
                System.arraycopy(a, 0, arr, 0, arr.length);
            }
            return a;
        }

        @Override
        public boolean add(String e) {
            throw new UnsupportedOperationException("Collection is read-only");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Collection is read-only");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return currentMap.values().containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            throw new UnsupportedOperationException("Collection is read-only");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Collection is read-only");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Collection is read-only");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Collection is read-only");
        }
        
    }
    
    class EntrySet implements Set<Map.Entry<String, String>> {

        @Override
        public int size() {
            return keySet.size();
        }

        @Override
        public boolean isEmpty() {
            return keySet.isEmpty();
        }

        private Stream<Map.Entry<String, String>> getEntries() {
            return keySet.stream().map((String t) -> new Map.Entry<String, String>() {
                private final String key = t;
                private String value = (currentMap.containsKey(t)) ? currentMap.get(key) : "";
                @Override
                public String getKey() {
                    return key;
                }
                @Override                
                public String getValue() {
                    return value;
                }
                @Override
                public String setValue(String value) {
                    String result = put(key, value);
                    this.value = value;
                    return result;
                }
            });
        }
        
        @Override
        public boolean contains(Object o) {
            if (null != o && o instanceof Map.Entry) {
                Map.Entry e = (Map.Entry)o;
                Object k = e.getKey();
                if (null != k && k instanceof String) {
                    Object c = e.getValue();
                    if (null != c && c instanceof String) {
                        String v = get((String)k);
                        return (null == v) ? keySet.contains(k) && ((String)c).isEmpty() : v.equals(c);
                    }
                }
            }
            return false;
        }

        @Override
        public Iterator<Map.Entry<String, String>> iterator() {
            return getEntries().iterator();
        }

        @Override
        public Object[] toArray() {
            return getEntries().toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            Object[] arr = toArray();
            if (null != a && a.length >= arr.length) {
                System.arraycopy(a, 0, arr, 0, arr.length);
                if (a.length > arr.length)
                    a[arr.length] = null;
            } else {
                a = (T[])(new Object[arr.length]);
                System.arraycopy(a, 0, arr, 0, arr.length);
            }
            return a;
        }

        @Override
        public boolean add(Map.Entry<String, String> e) {
            throw new UnsupportedOperationException("Set is read-only");
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Set is read-only");
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return null != c && (c.isEmpty() || c.stream().allMatch((i) -> {
                if (null != i && i instanceof Map.Entry) {
                    Map.Entry entry = (Map.Entry)i;
                    Object key = entry.getKey();
                    if (null != key && key instanceof String) {
                        String value = currentMap.get((String)key);
                        if (null != value) {
                            Object obj = entry.getValue();
                            return null != obj && obj instanceof String && value.equals(obj);
                        }
                    }
                }
                return false;
            }));
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<String, String>> c) {
            throw new UnsupportedOperationException("Set is read-only");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Set is read-only");
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Set is read-only");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("Set is read-only");
        }
        
    }
    
    class BundleLoader implements FileFilter {
        private final String baseName;
        private final int baseLen;
        private final int tooShort;
        private ArrayList<String> tags;
        private ArrayList<File> loadFailures;
        private ArrayList<Pair<String, File>> invalidTags;
        
        BundleLoader(String baseName) {
            this.baseName = baseName;
            baseLen = baseName.length();
            tooShort = baseLen + 13;
        }

        synchronized BundleFile[] load(File directory) {
            tags = new ArrayList<>();
            loadFailures = new ArrayList<>();
            invalidTags = new ArrayList<>();
            return Arrays.stream(directory.listFiles(this)).map((t) -> {
                Matcher m = PATTERN_BUNDLENAMESUFFIX.matcher(t.getName().substring(baseLen));
                m.find();
                Locale l = Locale.forLanguageTag(m.group(1).replace("_", "-"));
                if (l.toLanguageTag().equalsIgnoreCase("und")) {
                    invalidTags.add(new Pair<>(m.group(1).replace("_", "-"), t));
                } else {
                    BundleFile properties = new BundleFile(l);
                    try {
                        properties.load(t);
                        tags.add(properties.getLocale().toLanguageTag());
                        return properties;
                    } catch (IOException ex) {
                        loadFailures.add(t);
                        Logger.getLogger(ObsoleteBundleSet.class.getName()).log(Level.SEVERE, String.format("Unexpected exception loading file: ", t.toString()), ex);
                    }
                }
                return null;
            }).filter((t) -> null != t).toArray(BundleFile[]::new);
        }
        
        @Override
        public boolean accept(File pathname) {
            if (!pathname.isFile())
                return false;
            String n = pathname.getName();
            return n.length() > tooShort && baseName.equalsIgnoreCase(n.substring(0, baseLen)) &&
                    PATTERN_BUNDLENAMESUFFIX.matcher(n.substring(baseLen)).find();
        }
    }
}
