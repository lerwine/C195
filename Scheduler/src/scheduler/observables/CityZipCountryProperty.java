package scheduler.observables;

import javafx.beans.binding.StringBinding;
import javafx.beans.property.ReadOnlyProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.dao.AddressElement;
import scheduler.util.Values;
import scheduler.view.address.AddressModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class CityZipCountryProperty extends StringBinding implements ReadOnlyProperty<String> {

    private final Object bean;
    private final String name;
    private final ReadOnlyProperty<String> postalCode;
    private final ReadOnlyProperty<String> cityName;
    private final ReadOnlyProperty<String> countryName;

    public CityZipCountryProperty(Object bean, String name, AddressModel<? extends AddressElement> address) {
        this.bean = bean;
        this.name = (null == name) ? "" : name;
        cityName = address.cityNameProperty();
        countryName = address.countryNameProperty();
        postalCode = address.postalCodeProperty();
        super.bind(cityName, countryName, postalCode);
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

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.observableArrayList(cityName, countryName, postalCode);
    }

    @Override
    public void dispose() {
        super.unbind(cityName, countryName, postalCode);
        super.dispose();
    }

}
