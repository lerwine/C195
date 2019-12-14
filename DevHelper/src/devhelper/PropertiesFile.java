/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 *
 * @author Leonard T. Erwine
 */
public class PropertiesFile {
    private static final Pattern PATTERN_BASE_AND_LANG = Pattern.compile("^((?:\\\\.|[^\\\\_]+)+)_(.+)", Pattern.CASE_INSENSITIVE);

    private final ReadOnlyObjectProperty<File> source;

    public File getSource() { return source.get(); }

    public ReadOnlyObjectProperty<File> sourceProperty() { return source; }
    
    private final ReadOnlyObjectProperty<Optional<Locale>> locale;

    public Optional<Locale> getLocale() { return locale.get(); }

    public ReadOnlyObjectProperty<Optional<Locale>> localeProperty() { return locale; }
    
    private final ReadOnlyStringWrapper baseName;

    public String getBaseName() { return baseName.get(); }

    public ReadOnlyStringProperty baseNameProperty() { return baseName.getReadOnlyProperty(); }
    
    private final ReadOnlyStringWrapper validationMessage;

    public String getValidationMessage() { return validationMessage.get(); }

    public ReadOnlyStringProperty validationMessageProperty() { return validationMessage.getReadOnlyProperty(); }
    
    private final ReadOnlyBooleanWrapper valid;

    public boolean isValid() { return valid.get(); }

    public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectProperty<Properties> properties;

    public Properties getProperties() { return properties.get(); }

    public ReadOnlyObjectProperty propertiesProperty() { return properties; }
    
    private final ReadOnlyBooleanWrapper critical;

    public boolean isCritical() { return critical.get(); }

    public ReadOnlyBooleanProperty criticalProperty() { return critical.getReadOnlyProperty(); }
    
    public static PropertiesFile load(File source, Set<String> expectedKeys, boolean force) {
        Properties properties = new Properties();
        String validationMessage = "";
        Locale locale = null;
        String baseName = source.getName();
        if (!source.isAbsolute())
            source = source.getAbsoluteFile();
        if (source.exists()) {
            if (!source.isFile())
                validationMessage = "Source is not a file";
        } else {
            File parent = source.getParentFile();
            if (parent == null || !parent.exists())
                validationMessage = "Parent directory does not exist";
            else if (!parent.isDirectory())
                validationMessage = "Parent is not a directory";
        }
        int index = baseName.lastIndexOf('.');
        if (index < 1) {
            if (validationMessage.isEmpty() && !force)
                validationMessage = "Source is not a properties file";
        } else {
            if ((index == baseName.length() - 1 || !baseName.substring(index + 1).equals("properties")) && !force)
                validationMessage = "Source is not a properties file";
            baseName = baseName.substring(0, index);
            Matcher m = PATTERN_BASE_AND_LANG.matcher(source.getName());
            if (m.matches()) {
                locale = Locale.forLanguageTag(m.group(2));
                baseName = m.group(1);
            }
        }
        if (validationMessage.isEmpty() && source.exists()) {
            try {
                FileInputStream stream = new FileInputStream(source);
                try { properties.load(stream); }
                finally { stream.close(); }
            } catch (IOException ex) {
                Logger.getLogger(PropertiesFile.class.getName()).log(Level.SEVERE, null, ex);
                validationMessage = "Unable to load properties";
            }
        }
        PropertiesFile result = new PropertiesFile(source, baseName, properties,
                (locale == null) ? Optional.empty() : Optional.of(locale), validationMessage);
        result.revalidate(expectedKeys);
        return result;
    }
    
    public PropertiesFile(File directory, String baseName, Locale locale) {
        if (baseName.trim().isEmpty() || baseName.endsWith("_") || PATTERN_BASE_AND_LANG.matcher(baseName).matches())
            throw new IllegalArgumentException("Invalid base name");
        if (!directory.isAbsolute())
            directory = directory.getAbsoluteFile();
        if (directory.exists()) {
            valid = new ReadOnlyBooleanWrapper(directory.isDirectory());
            this.validationMessage = new ReadOnlyStringWrapper((valid.get()) ? "" : "Parent is not a directory");
        } else {
            valid = new ReadOnlyBooleanWrapper(false);
            this.validationMessage = new ReadOnlyStringWrapper("Parent directory does not exist");
        }
        String path = directory.getAbsolutePath();
        if (locale != null) {
            source = new ReadOnlyObjectWrapper<>(new File((path.endsWith(File.pathSeparator)) ? String.format("%s%s_%s.properties", path, baseName, locale.toLanguageTag()) :
                    String.format("%s%s%s_%s.properties", path, File.pathSeparator, baseName, locale.toLanguageTag())));
            this.locale = new ReadOnlyObjectWrapper<>(Optional.of(locale));
        } else {
            source = new ReadOnlyObjectWrapper<>(new File((path.endsWith(File.pathSeparator)) ? String.format("%s%s.properties", path, baseName) :
                    String.format("%s%s%s.properties", path, File.pathSeparator, baseName)));
            this.locale = new ReadOnlyObjectWrapper<>(Optional.empty());
        }
        
        this.properties = new ReadOnlyObjectWrapper<>(new Properties());
        this.baseName = new ReadOnlyStringWrapper(baseName);
        this.critical = new ReadOnlyBooleanWrapper(!valid.get());
    }
    
    private PropertiesFile(File source, String baseName, Properties properties, Optional<Locale> locale, String validationMessage) {
        this.properties = new ReadOnlyObjectWrapper<>(properties);
        this.baseName = new ReadOnlyStringWrapper(baseName);
        this.locale = new ReadOnlyObjectWrapper<>(locale);
        this.source = new ReadOnlyObjectWrapper<>(source);
        this.validationMessage = new ReadOnlyStringWrapper((validationMessage == null) ? "" : validationMessage.trim());
        this.valid = new ReadOnlyBooleanWrapper(this.validationMessage.get().isEmpty());
        this.critical = new ReadOnlyBooleanWrapper(!valid.get());
    }

    @Override
    public int hashCode() { return Objects.hashCode(this.source); }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj != null && getClass() == obj.getClass() &&
            Objects.equals(this.source, ((PropertiesFile) obj).source));
    }
    
    public boolean revalidate(Set<String> expectedKeys) {
        if (critical.get())
            return false;
        Set<String> existing = properties.get().stringPropertyNames();
        ArrayList<String> missingKeys = new ArrayList<>();
        expectedKeys.stream().filter((String k) -> !existing.contains(k)).forEach((String k) -> { missingKeys.add(k); });
        ArrayList<String> extraKeys = new ArrayList<>();
        existing.stream().filter((String k) -> !expectedKeys.contains(k)).forEach((String k) -> { extraKeys.add(k); });
        if (missingKeys.isEmpty()) {
            if (extraKeys.isEmpty()) {
                validationMessage.set("");
                valid.set(true);
                return true;
            }
            validationMessage.set(String.format("Extra keys: %s", extraKeys.toString()));
        } else if (extraKeys.isEmpty())
            validationMessage.set(String.format("Missing keys: %s", missingKeys.toString()));
        else
            validationMessage.set(String.format("Missing keys: %s; Extra keys: %s", missingKeys.toString(), extraKeys.toString()));
        valid.set(false);
        return false;
    }
    
    public static FilenameFilter getPropertiesFileFilter(String baseName) {
        if (baseName == null || baseName.isEmpty())
            return (File dir, String name) -> {
                int ext = name.lastIndexOf('.');
                return ext > 0 && ext == name.length() - 11
                        && name.substring(ext + 1).toLowerCase().equals("properties");
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

}
