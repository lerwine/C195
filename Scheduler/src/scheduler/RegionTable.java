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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import scheduler.util.LogHelper;
import scheduler.util.Tuple;
import scheduler.util.Values;

/**
 * Parsed contents of the zone1970.tab file which maps ISO 3166 2-character country codes to time zones IDs included within that country.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RegionTable extends AbstractSet<RegionTable.Row> {

    private static final String TAB_FILE_PATH = "scheduler/zone1970.tab";
    private static final Logger LOG = Logger.getLogger(RegionTable.class.getName());
//    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(RegionTable.class.getName()), Level.FINER);

    /**
     * The singleton {@code RegionTable} instance.
     */
    public static RegionTable INSTANCE = new RegionTable();

    /**
     * Gets the {@link TimeZone}s for the specified country.
     *
     * @param countryId The ISO 3166 2-character country code to look up.
     * @return A {@link List} of {@link TimeZone}s for the specified {@code countryId}, which may be empty if no matching {@code countryId} is found.
     */
    public static List<TimeZone> getZonesForCountry(String countryId) {
        Map<String, List<TimeZone>> map = INSTANCE.countryZoneIdsMap;
        return (map.containsKey(countryId)) ? map.get(countryId) : Collections.emptyList();
    }

    /**
     * Gets the ISO 3166 2-character country code for countries in the specified time zone.
     *
     * @param zoneId The time zone id.
     * @return A {@link List} of ISO 3166 2-character country code for countries for the specified {@code zoneId}, which may be empty if no matching {@code zoneId} is found.
     */
    public static List<String> getCountriesForZoneId(String zoneId) {
        Map<String, List<String>> map = INSTANCE.zoneIdCountriesMap;
        return (map.containsKey(zoneId)) ? map.get(zoneId) : Collections.emptyList();
    }

    /**
     * Maps ISO 3166 2-character country codes to a {@link List} of {@link TimeZone}s.
     *
     * @return A {@link Map} where the key is an ISO 3166 2-character country code, and the value is a {@link List} of {@link TimeZone}s.
     */
    public static Map<String, List<TimeZone>> getCountryZoneIdMap() {
        return INSTANCE.countryZoneIdsMap;
    }

    /**
     * Maps time zone IDs to a set of ISO 3166 2-character country codes.
     *
     * @return A {@link Map} where the key is a time zone ID, and the value is a {@link List} of ISO 3166 2-character country codes.
     */
    public static Map<String, List<String>> getZoneIdCountriesMap() {
        return INSTANCE.zoneIdCountriesMap;
    }

    public static List<TimeZone> getAllTimeZones() {
        return INSTANCE.allTimeZones;
    }

    private final Row first;
    private final Row last;
    private final int count;
    private final CountryMap countryZoneIdsMap;
    private final ZoneIdMap zoneIdCountriesMap;
    private final List<TimeZone> allTimeZones;

    // Singleton
    private RegionTable() {
        countryZoneIdsMap = new CountryMap();
        zoneIdCountriesMap = new ZoneIdMap();
        ArrayList<TimeZone> backingTimeZoneList = new ArrayList<>();
        ArrayList<Integer> offsetsMapped = new ArrayList<>();

        first = new Row(null);
        Row currentRow = first;
        int nextIndex = 0;
        try (InputStream stream = RegionTable.class.getClassLoader().getResourceAsStream(TAB_FILE_PATH)) {
            if (stream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", TAB_FILE_PATH));
            } else {
                try (InputStreamReader isr = new InputStreamReader(stream)) {
                    try (BufferedReader reader = new BufferedReader(isr)) {
                        String line;
                        HashMap<String, Tuple<List<String>, ZoneId>> zoneIdToCountryCodes = new HashMap<>();
                        while (null != (line = reader.readLine())) {
                            if (line.isEmpty() || line.charAt(0) == '#') {
                                continue;
                            }
                            final ArrayList<String> cols = Values.splitByChar(line, '\t');
                            LOG.fine(() -> String.format("Parsed columns: %s", LogHelper.iterableToLogText(cols)));
                            if (cols.size() < 3) {
                                continue;
                            }
                            String id = cols.get(2);
                            ZoneId zoneId;
                            Tuple<List<String>, ZoneId> countryCodesTuple;
                            if (zoneIdToCountryCodes.containsKey(id)) {
                                zoneId = (countryCodesTuple = zoneIdToCountryCodes.get(id)).getValue2();
                            } else {
                                try {
                                    zoneId = ZoneId.of(id);
                                } catch (DateTimeException ex) {
                                    LOG.log(Level.WARNING, ex, () -> String.format("Zone id %s not found", id));
                                    zoneId = null;
                                }
                                TimeZoneEntry timeZoneEntry = new TimeZoneEntry(id);
                                countryCodesTuple = Tuple.of(timeZoneEntry.backingList, zoneId);
                                zoneIdToCountryCodes.put(id, countryCodesTuple);
                                zoneIdCountriesMap.backingSet.add(timeZoneEntry);
                            }

                            if (null != zoneId) {
                                TimeZone timeZone = TimeZone.getTimeZone(zoneId);
                                backingTimeZoneList.add(timeZone);
                                int ro = timeZone.getRawOffset();
                                if (!offsetsMapped.contains(ro)) {
                                    offsetsMapped.add(ro);
                                }
                                Iterator<String> countryCodeIterator = Values.splitByChar(cols.get(0), ',').iterator();
                                while (countryCodeIterator.hasNext()) {
                                    String countryCode = countryCodeIterator.next();
                                    currentRow.countryCode = countryCode;
                                    countryCodesTuple.getValue1().add(countryCode);
                                    currentRow.timeZone = timeZone;
                                    Iterator<Map.Entry<String, List<TimeZone>>> entryIterator = countryZoneIdsMap.backingSet.iterator();
                                    while (entryIterator.hasNext()) {
                                        CountryEntry ce = (CountryEntry) entryIterator.next();
                                        if (ce.key.equals(countryCode)) {
                                            ce.backingSet.add(currentRow.timeZone);
                                            entryIterator = null;
                                            break;
                                        }
                                    }
                                    if (null != entryIterator) {
                                        countryZoneIdsMap.backingSet.add(new CountryEntry(countryCode, currentRow.timeZone));
                                    }
                                    currentRow = currentRow.next = new Row(currentRow);
                                    nextIndex++;
                                }
                            }
                        }
                    }
                }
                Arrays.stream(TimeZone.getAvailableIDs()).map((t) -> TimeZone.getTimeZone(t)).forEach((t) -> {
                    int ro = t.getRawOffset();
                    if (!offsetsMapped.contains(ro)) {
                        offsetsMapped.add(ro);
                        backingTimeZoneList.add(t);
                    }
                });
                backingTimeZoneList.sort(Values::compareTimeZones);
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", TAB_FILE_PATH), ex);
        }
        (last = currentRow).next = null;
        count = nextIndex;
        allTimeZones = Collections.unmodifiableList(backingTimeZoneList);
    }

    /**
     * Gets the first {@link Row} of data from the {@code zone1970.tab} data file.
     *
     * @return The first {@link Row} of data from the {@code zone1970.tab} data file.
     */
    public Row getFirst() {
        return first;
    }

    /**
     * Gets the last {@link Row} of data from the {@code zone1970.tab} data file.
     *
     * @return The last {@link Row} of data from the {@code zone1970.tab} data file.
     */
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

    /**
     * Represents a country / time zone pair from the {@code zone1970.tab} data file.
     */
    public class Row {

        private final Row previous;
        private Row next;
        private String countryCode;
        private TimeZone timeZone;

        private Row(Row previous) {
            this.previous = previous;
        }

        /**
         * Gets the ISO 3166 2-character country code.
         *
         * @return The ISO 3166 2-character country code.
         */
        public String getCountryCode() {
            return countryCode;
        }

        /**
         * Gets the previous {@link Row} of data from the {@code zone1970.tab} data file.
         *
         * @return The previous {@link Row} of data from the {@code zone1970.tab} data file or {@code null} if this is the first row.
         */
        public Row getPrevious() {
            return previous;
        }

        /**
         * Gets the next {@link Row} of data from the {@code zone1970.tab} data file.
         *
         * @return The next {@link Row} of data from the {@code zone1970.tab} data file or {@code null} if this is the last row.
         */
        public Row getNext() {
            return next;
        }

        /**
         * Gets the {@link TimeZone} value.
         *
         * @return The {@link TimeZone} that was created from the time zone ID string in the {@code zone1970.tab} data file.
         */
        public TimeZone getTimeZone() {
            return timeZone;
        }

    }

    private class CountryEntry implements Map.Entry<String, List<TimeZone>> {

        private final ArrayList<TimeZone> backingSet;
        private final List<TimeZone> value;
        private final String key;

        private CountryEntry(String countryCode, TimeZone tz) {
            key = countryCode;
            backingSet = new ArrayList<>();
            backingSet.add(tz);
            value = Collections.unmodifiableList(backingSet);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public List<TimeZone> getValue() {
            return value;
        }

        @Override
        public List<TimeZone> setValue(List<TimeZone> value) {
            throw new UnsupportedOperationException();
        }

    }

    private class TimeZoneEntry implements Map.Entry<String, List<String>> {

        private final ArrayList<String> backingList;
        private final List<String> value;
        private final String key;

        private TimeZoneEntry(String zoneId) {
            key = zoneId;
            backingList = new ArrayList<>();
            value = Collections.unmodifiableList(backingList);
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public List<String> getValue() {
            return value;
        }

        @Override
        public List<String> setValue(List<String> value) {
            throw new UnsupportedOperationException();
        }

    }

    private class CountryMap extends AbstractMap<String, List<TimeZone>> {

        private final HashSet<Map.Entry<String, List<TimeZone>>> backingSet;
        private final Set<Map.Entry<String, List<TimeZone>>> entrySet;

        private CountryMap() {
            backingSet = new HashSet<>();
            entrySet = Collections.unmodifiableSet(backingSet);
        }

        @Override
        public Set<Map.Entry<String, List<TimeZone>>> entrySet() {
            return entrySet;
        }

    }

    private class ZoneIdMap extends AbstractMap<String, List<String>> {

        private final HashSet<Map.Entry<String, List<String>>> backingSet;
        private final Set<Map.Entry<String, List<String>>> entrySet;

        private ZoneIdMap() {
            backingSet = new HashSet<>();
            entrySet = Collections.unmodifiableSet(backingSet);
        }

        @Override
        public Set<Map.Entry<String, List<String>>> entrySet() {
            return entrySet;
        }

    }

}
