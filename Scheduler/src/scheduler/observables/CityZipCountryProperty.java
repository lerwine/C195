package scheduler.observables;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.ui.AddressItem;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CityZipCountryProperty extends StringBindingProperty {

    private final ReadOnlyProperty<String> postalCode;
    private final ReadOnlyProperty<String> cityName;
    private final ReadOnlyProperty<String> countryName;

    public CityZipCountryProperty(Object bean, String name, AddressItem address) {
        super(bean, name);
        cityName = address.cityNameProperty();
        countryName = address.countryNameProperty();
        postalCode = address.postalCodeProperty();
        super.addDependency(cityName, countryName, postalCode);
    }

    @Override
    protected String computeValue() {
        String n = Values.asNonNullAndWsNormalized(cityName.getValue());
        String c = Values.asNonNullAndWsNormalized(countryName.getValue());
        String p = Values.asNonNullAndWsNormalized(postalCode.getValue());
        if (c.isEmpty()) {
            if (p.isEmpty()) {
                return n;
            }
            return (n.isEmpty()) ? p : String.format("%s %s", n, p);
        }

        if (p.isEmpty()) {
            return (n.isEmpty()) ? c : String.format("%s, %s", n, c);
        }

        return (n.isEmpty()) ? String.format("%s, %s", p, c) : String.format("%s %s, %s", n, p, c);
    }

}
