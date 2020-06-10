package scheduler.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import scheduler.util.ToStringPropertyBuilder;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = CorporateCountry.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = CorporateCountry.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class CorporateCountry extends PredefinedData.PredefinedCountry {

    public static final String ELEMENT_NAME = "country";

    @SuppressWarnings("unused")
    private static CorporateCountry createInstanceJAXB() {
        return new CorporateCountry();
    }

    private CorporateCountry() {

    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof CountryProperties
                && (obj == this || CountryProperties.arePropertiesEqual((CountryProperties) obj, this));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(getName());
        hash = 67 * hash + Objects.hashCode(getLocale());
        return hash;
    }

    ToStringPropertyBuilder toStringBuilder() {
        return ToStringPropertyBuilder.create(this)
                .addString(PROP_NAME, getName())
                .addLocale(PROP_LOCALE, getLocale());
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

}
