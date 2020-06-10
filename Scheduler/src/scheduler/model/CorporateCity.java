package scheduler.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import scheduler.util.ToStringPropertyBuilder;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = CorporateCity.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = CorporateCity.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class CorporateCity extends PredefinedData.PredefinedCity {

    public static final String ELEMENT_NAME = "city";

    @SuppressWarnings("unused")
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(getName());
        hash = 89 * hash + Objects.hashCode(getCountry());
        hash = 89 * hash + Objects.hashCode(getTimeZone());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof CityProperties && (this == obj || CityProperties.arePropertiesEqual(this, (CityProperties) obj));
    }

    ToStringPropertyBuilder toStringBuilder() {
        return ToStringPropertyBuilder.create(this)
                .addString(PROP_NAME, name)
                .addToStringPropertyBuilder(PROP_COUNTRY, getCountry().toStringBuilder());
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

}
