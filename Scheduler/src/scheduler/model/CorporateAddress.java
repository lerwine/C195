package scheduler.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import scheduler.model.ModelHelper.AddressHelper;
import scheduler.util.Values;

/**
 * Represents a statically-defined address of a corporate office or satellite location. This object is instantiated by the {@link PredefinedData} utility class.
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
    private boolean satelliteOffice;
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

    /**
     * Gets the display name of the corporate office or satellite location.
     *
     * @return The display name of the corporate office or satellite location.
     */
    public String getName() {
        return name;
    }

    /**
     * Indicates whether the current {@code CorporateAddress} is for the address of a satellite office location.
     *
     * @return {@code true} if this is a satellite office location; otherwise {@code true} if this is for a corporate office location.
     */
    public boolean isSatelliteOffice() {
        return satelliteOffice;
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
                && (this == obj || AddressHelper.arePropertiesEqual(this, (AddressProperties) obj));
    }

    @Override
    public String toString() {
        return ModelHelper.AddressHelper.appendCorporateAddressProperties(this, new StringBuilder(CorporateAddress.class.getName()).append(" { "))
                .append(Values.LINEBREAK_STRING).append("}").toString();
    }

    public String toMultiLineAddress() {
        SupportedCityDefinition city = getCity();
        return AddressHelper.calculateMultiLineAddress(
                AddressHelper.calculateAddressLines(address1, address2),
                AddressHelper.calculateCityZipCountry(city.getName(), city.getCountry().getName(), postalCode),
                phone
        );
    }
}
