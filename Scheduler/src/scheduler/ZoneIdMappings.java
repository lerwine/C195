package scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import scheduler.util.LogHelper;
import scheduler.util.Values;

/**
 * Utility class to map Zone IDs to country/region codes.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = ZoneIdMappings.ELEMENT_NAME, namespace = ZoneIdMappings.NAMESPACE_URI)
@XmlType(name = ZoneIdMappings.ELEMENT_NAME, namespace = ZoneIdMappings.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class ZoneIdMappings {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ZoneIdMappings.class.getName()), Level.FINER);

    public static final String ELEMENT_NAME = "mappings";
    public static final String NAMESPACE_URI = "urn:Erwine.Leonard.T:C195:ZoneIdMap.xsd";
    static final String MAPPINGS_XML_FILE = "scheduler/ZoneIdMap.xml";

    private static List<Entry> ENTRIES;
    private static Map<String, String> ZONE_ID_MAP;
    private static Map<String, String> CODE_MAP;

    public static List<Entry> getEntries() {
        checkLoadEntries();
        return ENTRIES;
    }

    public static Map<String, String> getZoneIdMap() {
        checkLoadEntries();
        return ZONE_ID_MAP;
    }

    public static Map<String, String> getCodeMap() {
        checkLoadEntries();
        return CODE_MAP;
    }

    public static String toZoneId(String source) {
        checkLoadEntries();
        ArrayList<String> elements = Values.splitByChar(Objects.requireNonNull(source), '/');
        String key = elements.get(0);
        if (CODE_MAP.containsKey(key)) {
            key = CODE_MAP.get(key);
        }
        String result;
        if (elements.size() == 1) {
            result = key;
        } else {
            StringBuilder sb = new StringBuilder(key);
            for (int i = 1; i < elements.size(); i++) {
                key = elements.get(i);
                sb.append("/");
                if (CODE_MAP.containsKey(key)) {
                    sb.append(CODE_MAP.get(key));
                } else {
                    sb.append(key);
                }
            }
            result = sb.toString();
        }
        LOG.fine(() -> (source.equals(result)) ? String.format("No mapping for %s", source)
                : String.format("Mapped %s to %s", source, result));
        return result;
    }

    public static String fromZoneId(String zoneId) {
        checkLoadEntries();
        String result;
        if (ZONE_ID_MAP.containsKey(zoneId)) {
            result = ZONE_ID_MAP.get(zoneId);
        } else {
            int i = zoneId.lastIndexOf('/');
            if (i > 0) {
                String s = zoneId.substring(i + 1);
                if (ZONE_ID_MAP.containsKey(s)) {
                    s = ZONE_ID_MAP.get(s);
                }
                result = String.format("%s/%s", fromZoneId(zoneId.substring(0, i)), s);
            } else {
                result = zoneId;
            }
        }
        LOG.fine(() -> (zoneId.equals(result)) ? String.format("No conversion for %s", zoneId)
                : String.format("Converted %s to %s", zoneId, result));
        return result;
    }

    private static void checkLoadEntries() {
        if (null != ENTRIES) {
            return;
        }
        HashMap<String, String> zoneIdMap = new HashMap<>();
        HashMap<String, String> codeMap = new HashMap<>();
        ZONE_ID_MAP = Collections.unmodifiableMap(zoneIdMap);
        CODE_MAP = Collections.unmodifiableMap(codeMap);
        try (InputStream stream = ZoneIdMappings.class.getClassLoader().getResourceAsStream(MAPPINGS_XML_FILE)) {
            if (stream == null) {
                LOG.log(Level.SEVERE, String.format("File \"%s\" not found.", MAPPINGS_XML_FILE));
            } else {
                JAXBContext jaxbContext = JAXBContext.newInstance(ZoneIdMappings.class);
                Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
                ZoneIdMappings result = (ZoneIdMappings) unmarshaller.unmarshal(stream);
                ENTRIES = result.entries;
                result.entries.forEach((t) -> {
                    zoneIdMap.put(t.value, t.key);
                    codeMap.put(t.key, t.value);
                });
            }
        } catch (IOException | JAXBException ex) {
            LOG.log(Level.SEVERE, String.format("Error loading resource \"%s\"", MAPPINGS_XML_FILE), ex);
        }
    }

    private static ZoneIdMappings createInstanceJAXB() {
        return new ZoneIdMappings();
    }

    @XmlElement(name = Entry.ELEMENT_NAME, namespace = NAMESPACE_URI)
    private List<Entry> entries;

    private ZoneIdMappings() {
        entries = new ArrayList<>();
    }

    @XmlRootElement(name = Entry.ELEMENT_NAME, namespace = NAMESPACE_URI)
    @XmlType(name = Entry.ELEMENT_NAME, namespace = NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Entry {

        public static final String ELEMENT_NAME = "entry";

        @XmlAttribute
        private String key;

        @XmlAttribute
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        private Entry() {
        }

        private static Entry createInstanceJAXB() {
            return new Entry();
        }

    }
}
