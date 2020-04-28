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
@XmlRootElement(name = CountryElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class CountryElement {
    static final String ELEMENT_NAME = "country";

    @XmlAttribute
    private String languageTag;
    
    @XmlAttribute
    private String defaultZoneId;
    
    @XmlElement(name = CityElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
    private List<CityElement> cities;

    public String getLanguageTag() {
        return languageTag;
    }

    public String getDefaultZoneId() {
        return defaultZoneId;
    }

    List<CityElement> getCities() {
        return cities;
    }

    public CountryElement() {
        cities = new ArrayList<>();
    }
    
}
