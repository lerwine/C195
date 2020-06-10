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
@XmlRootElement(name = CorporateAddress.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlType(name = CorporateAddress.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI, factoryMethod = "createInstanceJAXB")
@XmlAccessorType(XmlAccessType.FIELD)
public class CorporateAddress extends PredefinedData.PredefinedAddress {

    public static final String ELEMENT_NAME = "address";

    @SuppressWarnings("unused")
    private static CorporateAddress createInstanceJAXB() {
        return new CorporateAddress();
    }

    @XmlAttribute
    private String name;
    @XmlAttribute
    private boolean mainOffice;
    @XmlAttribute
    private String address1;
    @XmlAttribute
    private String address2;
    @XmlAttribute
    private String postalCode;
    @XmlAttribute
    private String phone;

    private CorporateAddress() {

    }

    public String getName() {
        return name;
    }

    public boolean isMainOffice() {
        return mainOffice;
    }

    @Override
    public String getAddress1() {
        return address1;
    }

    @Override
    public String getAddress2() {
        return address2;
    }

    @Override
    public String getPostalCode() {
        return postalCode;
    }

    @Override
    public String getPhone() {
        return phone;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(address1);
        hash = 79 * hash + Objects.hashCode(address2);
        hash = 79 * hash + Objects.hashCode(getCity());
        hash = 79 * hash + Objects.hashCode(postalCode);
        hash = 79 * hash + Objects.hashCode(phone);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        return null != obj && obj instanceof AddressProperties
                && (this == obj || AddressProperties.arePropertiesEqual(this, (AddressProperties) obj));
    }

    @Override
    public String toString() {
        return ToStringPropertyBuilder.create(this).addString(PROP_ADDRESS1, address1)
                .addString(PROP_ADDRESS2, address2)
                .addToStringPropertyBuilder(PROP_CITY, getCity().toStringBuilder())
                .addString(PROP_POSTALCODE, postalCode)
                .addString(PROP_PHONE, phone).build();
    }

}
