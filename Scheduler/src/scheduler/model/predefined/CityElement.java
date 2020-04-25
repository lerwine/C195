package scheduler.model.predefined;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Internal: not intended to be instantiated directly.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = CityElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CityElement {
    static final String ELEMENT_NAME = "city";
    
    @XmlAttribute
    private String key;
    
    @XmlAttribute
    private String zoneId;
    
    @XmlElement(name = AddressElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<AddressElement> addresses;

    String getKey() {
        return key;
    }

    String getZoneId() {
        return zoneId;
    }

    List<AddressElement> getAddresses() {
        return addresses;
    }

    public CityElement() {
        addresses = new ArrayList<>();
    }
    
}
