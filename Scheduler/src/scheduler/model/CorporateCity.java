package scheduler.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = CorporateCity.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = CorporateCity.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class CorporateCity extends PredefinedData.PredefinedCity {

    public static final String ELEMENT_NAME = "city";

    private static CorporateCity createInstanceJAXB() {
        return new CorporateCity();
    }

    @XmlAttribute()
    private String name;

    private CorporateCity() {

    }

    @Override
    public String getName() {
        return name;
    }

}
