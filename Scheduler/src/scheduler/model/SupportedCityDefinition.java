package scheduler.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import scheduler.model.ModelHelper.CityHelper;
import scheduler.util.ToStringPropertyBuilder;

/**
 * Defines a application-supported city definition, specifying an associated {@link java.util.TimeZone} for that city. This object is instantiated by the {@link PredefinedData}
 * utility class.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = SupportedCityDefinition.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = SupportedCityDefinition.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class SupportedCityDefinition extends PredefinedData.PredefinedCity {

    public static final String ELEMENT_NAME = "city";

    @SuppressWarnings("unused")
    private static SupportedCityDefinition createInstanceJAXB() {
        return new SupportedCityDefinition();
    }

    @XmlAttribute()
    private String name;

    private SupportedCityDefinition() {

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
        return null != obj && obj instanceof CityProperties && (this == obj || CityHelper.arePropertiesEqual(this, (CityProperties) obj));
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
