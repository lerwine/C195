package scheduler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.util.Values;

/**
 * Parsed contents of the zone1970.tab file which maps ISO 3166 2-character country codes to time zones included within that country.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RegionTable extends AbstractSet<RegionTable.Row> {

    private static final String TAB_FILE_PATH = "scheduler/zone1970.tab";
    private static final Logger LOG = Logger.getLogger(RegionTable.class.getName());

    public static RegionTable INSTANCE = new RegionTable();

    public static Set<ZoneId> getZonesForCountry(String countryId) {
        Map<String, Set<ZoneId>> map = INSTANCE.countryZoneIdsMap;
        return (map.containsKey(countryId)) ? map.get(countryId) : Collections.emptySet();
    }

    public static Set<String> getCountriesForZoneId(String zoneId) {
        Map<String, Set<String>> map = INSTANCE.zoneIdCountriesMap;
        return (map.containsKey(zoneId)) ? map.get(zoneId) : Collections.emptySet();
    }

    public static Map<String, Set<ZoneId>> getCountryZoneIdMap() {
        return INSTANCE.countryZoneIdsMap;
    }

    public static Map<String, Set<String>> getZoneIdCountriesMap() {
        return INSTANCE.zoneIdCountriesMap;
    }

    private final Row first;
    private final Row last;
    private final int count;
    private final CountryMap countryZoneIdsMap = new CountryMap();
    private final ZoneIdMap zoneIdCountriesMap = new ZoneIdMap();

    // Singleton
    private RegionTable() {
        first = new Row(null);
        Row current = first;
        int nextIndex = 0;
        try (InputStream stream = RegionTable.class.getClassLoader().getResourceAsStream(TAB_FILE_PATH)) {
            if (stream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", TAB_FILE_PATH));
            } else {
                try (InputStreamReader isr = new InputStreamReader(stream)) {
                    try (BufferedReader reader = new BufferedReader(isr)) {
                        String line;
                        HashMap<String, ZoneId> zMap = new HashMap<>();
                        while (null != (line = reader.readLine())) {
                            if (line.isEmpty() || line.charAt(0) != '#') {
                                continue;
                            }
                            ArrayList<String> cols = Values.splitByChar(line, '\t');
                            if (cols.size() < 3) {
                                continue;
                            }
                            String id = cols.get(2);
                            ZoneId zoneId;
                            ZoneIdEntry zoneIdEntry;
                            if (zMap.containsKey(id)) {
                                zoneId = zMap.get(id);
                                zoneIdEntry = (ZoneIdEntry) zoneIdCountriesMap.get(id);
                            } else {
                                try {
                                    zoneId = ZoneId.of(id);
                                } catch (DateTimeException ex) {
                                    LOG.log(Level.WARNING, ex, () -> String.format("Zone id %s not found", id));
                                    zoneId = null;
                                }
                                zMap.put(id, zoneId);
                                zoneIdEntry = new ZoneIdEntry(id);
                                zoneIdCountriesMap.backingSet.add(zoneIdEntry);
                            }
                            if (null != zoneId) {
                                Iterator<String> iterator = Values.splitByChar(cols.get(0), ',').iterator();
                                while (iterator.hasNext()) {
                                    String cc = iterator.next();
                                    current.countryCode = cc;
                                    zoneIdEntry.backingSet.add(cc);
                                    current.zoneId = zoneId;
                                    Iterator<Map.Entry<String, Set<ZoneId>>> it2 = countryZoneIdsMap.backingSet.iterator();
                                    while (it2.hasNext()) {
                                        CountryEntry ce = (CountryEntry) it2.next();
                                        if (ce.key.equals(cc)) {
                                            ce.backingSet.add(zoneId);
                                            it2 = null;
                                            break;
                                        }
                                    }
                                    if (null != it2) {
                                        countryZoneIdsMap.backingSet.add(new CountryEntry(cc, zoneId));
                                    }
                                    current = current.next = new Row(current);
                                    nextIndex++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", TAB_FILE_PATH), ex);
        }
        (last = current).next = null;
        count = nextIndex;
    }

    public Row getFirst() {
        return first;
    }

    public Row getLast() {
        return last;
    }

    @Override
    public Iterator<Row> iterator() {
        return new Iterator<Row>() {
            private Row next = first;

            @Override
            public boolean hasNext() {
                return null != next;
            }

            @Override
            public Row next() {
                if (null == next) {
                    throw new NoSuchElementException();
                }
                Row result = next;
                next = next.next;
                return result;
            }

        };
    }

    @Override
    public int size() {
        return count;
    }

    public class Row {

        private final Row previous;
        private Row next;
        private String countryCode;
        private ZoneId zoneId;

        private Row(Row previous) {
            this.previous = previous;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public Row getNext() {
            return next;
        }

        public Row getPrevious() {
            return previous;
        }

        public ZoneId getZoneId() {
            return zoneId;
        }

    }

    private class CountryEntry implements Map.Entry<String, Set<ZoneId>> {

        private final HashSet<ZoneId> backingSet;
        private final Set<ZoneId> value;
        private final String key;

        private CountryEntry(String countryCode, ZoneId zoneId) {
            key = countryCode;
            backingSet = new HashSet<>();
            backingSet.add(zoneId);
            value = Collections.unmodifiableSet(backingSet);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Set<ZoneId> getValue() {
            return value;
        }

        @Override
        public Set<ZoneId> setValue(Set<ZoneId> value) {
            throw new UnsupportedOperationException();
        }

    }

    private class ZoneIdEntry implements Map.Entry<String, Set<String>> {

        private final HashSet<String> backingSet;
        private final Set<String> value;
        private final String key;

        private ZoneIdEntry(String zoneId) {
            key = zoneId;
            backingSet = new HashSet<>();
            value = Collections.unmodifiableSet(backingSet);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Set<String> getValue() {
            return value;
        }

        @Override
        public Set<String> setValue(Set<String> value) {
            throw new UnsupportedOperationException();
        }

    }

    private class CountryMap extends AbstractMap<String, Set<ZoneId>> {

        private final HashSet<Map.Entry<String, Set<ZoneId>>> backingSet;
        private final Set<Map.Entry<String, Set<ZoneId>>> entrySet;

        private CountryMap() {
            backingSet = new HashSet<>();
            entrySet = Collections.unmodifiableSet(backingSet);
        }

        @Override
        public Set<Map.Entry<String, Set<ZoneId>>> entrySet() {
            return entrySet;
        }

    }

    private class ZoneIdMap extends AbstractMap<String, Set<String>> {

        private final HashSet<Map.Entry<String, Set<String>>> backingSet;
        private final Set<Map.Entry<String, Set<String>>> entrySet;

        private ZoneIdMap() {
            backingSet = new HashSet<>();
            entrySet = Collections.unmodifiableSet(backingSet);
        }

        @Override
        public Set<Map.Entry<String, Set<String>>> entrySet() {
            return entrySet;
        }

    }

}
