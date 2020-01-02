/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view.appointment;

import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

/**
 *
 * @author Leonard T. Erwine
 */
public class TimeZoneChoice implements Comparable<TimeZoneChoice> {

    private final ReadOnlyObjectWrapper<ZoneId> zoneId;

    public ZoneId getZoneId() { return zoneId.get(); }

    public ReadOnlyObjectProperty<ZoneId> zoneIdProperty() { return zoneId.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<TimeZone> timeZone;

    public TimeZone getTimeZone() { return timeZone.get(); }

    public ReadOnlyObjectProperty timeZoneProperty() { return timeZone.getReadOnlyProperty(); }
    private final ReadOnlyStringWrapper fullName;

    public String getFullName() {
        return fullName.get();
    }

    public ReadOnlyStringProperty fullNameProperty() {
        return fullName.getReadOnlyProperty();
    }
    private final ReadOnlyStringWrapper shortName;

    public String getShortName() {
        return shortName.get();
    }

    public ReadOnlyStringProperty shortNameProperty() {
        return shortName.getReadOnlyProperty();
    }
    
    private final ReadOnlyStringWrapper displayName;

    public String getDisplayName() { return displayName.get(); }

    public ReadOnlyStringProperty displayNameProperty() { return displayName.getReadOnlyProperty(); }
    
    public static Stream<TimeZoneChoice> getAllChoices(Locale locale) {
        final HashMap<Integer, TimeZoneChoice> byOffset = new HashMap<>();
        final HashMap<String, TimeZoneChoice> byName = new HashMap<>();
        // Well save this to make sure the current system default is included in the result choice list.
        final String dflt = ZoneId.systemDefault().getId();
        ZoneId.getAvailableZoneIds().stream().map((String id) -> new TimeZoneChoice(ZoneId.of(id), locale)).forEach((TimeZoneChoice item) -> {
            String key = item.getFullName().toLowerCase();
            if (byName.containsKey(key)) {
                if (item.getZoneId().getId().equals(dflt)) {
                    byOffset.put(item.getTimeZone().getRawOffset(), item);
                    byName.put(key, item);
                }
            } else if (!dflt.equals(item.getZoneId().getId())) {
                int n = item.getTimeZone().getRawOffset();
                if (byOffset.containsKey(n)) {
                    TimeZoneChoice c = byOffset.get(n);
                    if (c.getShortName().equalsIgnoreCase(c.getZoneId().getId())) {
                        byOffset.put(n, item);
                        byName.remove(c.getFullName().toLowerCase());
                    }
                }
                byName.put(key, item);
            }
        });
        return byName.values().stream().sorted();
    }
    
    public TimeZoneChoice(ZoneId id, Locale locale) {
        zoneId = new ReadOnlyObjectWrapper<>(id);
        String n = id.getDisplayName(TextStyle.SHORT, locale);
        shortName = new ReadOnlyStringWrapper();
        TimeZone tz = TimeZone.getTimeZone(id);
        String f = tz.getDisplayName(locale);
        fullName = new ReadOnlyStringWrapper(f);
        timeZone = new ReadOnlyObjectWrapper<>(tz);
        int value = tz.getRawOffset();
        boolean isNegative = value < 0;
        if (isNegative)
            value *= -1;
        int ms = value % 1000;
        int i = (value = (value - ms) / 1000) % 60;
        int m = (value = (value - i) / 60) % 60;
        value = (value - m) / 60;
        String s = (isNegative) ? "-" : "+";
        if (f.equalsIgnoreCase(n)) {
            if (ms > 0)
                displayName = new ReadOnlyStringWrapper(String.format("%s (%s%2d:%2d:%2d.%4d)", f, s, value, m, i, ms));
            else if (i > 0)
                displayName = new ReadOnlyStringWrapper(String.format("%s (%s%2d:%2d:%2d)", f, s, value, m, i));
            else
                displayName = new ReadOnlyStringWrapper(String.format("%s (%s%2d:%2d)", f, s, value, m));
        } else if (ms > 0)
            displayName = new ReadOnlyStringWrapper(String.format("%s (%s %s%2d:%2d:%2d.%4d)", f, s, n, value, m, i, ms));
        else if (i > 0)
            displayName = new ReadOnlyStringWrapper(String.format("%s (%s %s%2d:%2d:%2d)", f, s, n, value, m, i));
        else
            displayName = new ReadOnlyStringWrapper(String.format("%s (%s %s%2d:%2d)", f, s, n, value, m));
    }

    @Override
    public int hashCode() { return getTimeZone().getRawOffset(); }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        return getZoneId().getId().equals(((TimeZoneChoice)obj).getZoneId().getId());
    }

    @Override
    public String toString() { return displayName.get(); }

    @Override
    public int compareTo(TimeZoneChoice o) {
        if (o == null)
            return 1;
        if (o == this)
            return 0;
        int r = getTimeZone().getRawOffset() - o.getTimeZone().getRawOffset();
        return (r == 0 && (r = getDisplayName().compareToIgnoreCase(o.getDisplayName())) == 0) ? getDisplayName().compareTo(o.getDisplayName()) : r;
    }
}
