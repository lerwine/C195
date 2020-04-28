package scheduler.model.predefined;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAttribute;

/**
 * Internal: not intended to be instantiated directly.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@XmlRootElement(name = AddressElement.ELEMENT_NAME, namespace = PredefinedData.NAMESPACE_URI)
@XmlAccessorType(XmlAccessType.FIELD)
public class AddressElement {
    static final String ELEMENT_NAME = "address";

    @XmlAttribute
    private boolean mainOffice;
    @XmlAttribute
    private String key;
    @XmlAttribute
    private String address1;
    @XmlAttribute
    private String address2;
    @XmlAttribute
    private String postalCode;
    @XmlAttribute
    private String phone;

    public String getKey() {
        return key;
    }

    boolean isMainOffice() {
        return mainOffice;
    }

    String getAddress1() {
        return address1;
    }

    String getAddress2() {
        return address2;
    }

    String getPostalCode() {
        return postalCode;
    }

    String getPhone() {
        return phone;
    }

}
