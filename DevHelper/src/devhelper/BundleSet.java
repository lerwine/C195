package devhelper;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Pair;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class BundleSet {
    public static final Pattern PATTERN_BUNDLENAME = Pattern.compile("(^.+)_([a-z]{2,3}(?:_[a-z\\d]+)*)(\\.properties)$", Pattern.CASE_INSENSITIVE);

    BundleSet(File rootPath, ObservableList<BundleSet> list) {
        ArrayList<File> loadFailures = new ArrayList<>();
        ArrayList<File> invalidTags = new ArrayList<>();
//        rootPath.listFiles(filter)
    }
    
    static class BundleName {
        final String baseName;
        final String suffix;
        final String extension;
        final Locale locale;
        final String tag;
        BundleName(File file) {
            Matcher m = PATTERN_BUNDLENAME.matcher(file.getName());
            String t;
            if (m.find()) {
                baseName = m.group(0);
                suffix = m.group(1);
                extension = m.group(3);
                locale = Locale.forLanguageTag(m.group(1).replace("_", "-"));
                t = locale.toLanguageTag();
                tag = (t.equals("und")) ? "" : t;
            } else {
                t = file.getName();
                int i = t.lastIndexOf(".");
                if (i < 1) {
                    baseName = t;
                    extension = "";
                } else {
                    baseName = t.substring(0, i);
                    extension = t.substring(i);
                }
                suffix = tag = "";
                locale = null;
            }
        }
    }
    static class BundleLoader implements FileFilter {
        private final File rootDirectory;
        private final ObservableList<BundleSet> bundleSets;
        private final ObservableList<BundleName> loadFailures;
        
        BundleLoader(File rootDirectory) {
            this.rootDirectory = rootDirectory;
            bundleSets = FXCollections.observableArrayList();
            loadFailures = FXCollections.observableArrayList();
        }

        void load(File directory) {
            ArrayList<BundleName> invalidTags = new ArrayList<>();
            HashMap<String, ArrayList<BundleName>> byBaseName = new HashMap<>();
            Arrays.stream(directory.listFiles(this)).map((t) -> new BundleName(t)).forEach((t) -> {
                if (t.tag.isEmpty())
                    invalidTags.add(t);
                else {
                    if (byBaseName.containsKey(t.baseName))
                        byBaseName.get(t.baseName).add(t);
                    else {
                        ArrayList<BundleName> list = new ArrayList<>();
                        list.add(t);
                        byBaseName.put(t.baseName, list);
                    }
                }
            });
        }
        
        @Override
        public boolean accept(File pathname) {
            if (!pathname.isFile())
                return false;
            String n = pathname.getName();
            throw new UnsupportedOperationException();
//            return n.length() > tooShort && baseName.equalsIgnoreCase(n.substring(0, baseLen)) &&
//                    PATTERN_BUNDLENAME.matcher(n.substring(baseLen)).find();
        }
    }
}
