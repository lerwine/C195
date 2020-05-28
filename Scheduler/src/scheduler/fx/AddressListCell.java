package scheduler.fx;

import javafx.scene.control.ListCell;
import scheduler.dao.IAddressDAO;
import scheduler.model.ui.AddressItem;
import scheduler.model.CustomerCity;
import scheduler.model.CustomerCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public class AddressListCell<T extends AddressItem<? extends IAddressDAO>> extends ListCell<T> {

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (null == item) {
            setText("");
        } else {
            String cityZipCountry = item.getPostalCode();
            CustomerCity city;
            if (null == cityZipCountry || (cityZipCountry = cityZipCountry.trim()).isEmpty()) {
                if (null == (city = item.getCity())) {
                    cityZipCountry = "";
                } else if ((cityZipCountry = CustomerCity.toString(city).trim()).isEmpty()) {
                    cityZipCountry = CustomerCountry.toString(city.getCountry()).trim();
                } else {
                    String country = CustomerCountry.toString(city.getCountry()).trim();
                    if (!country.isEmpty()) {
                        cityZipCountry = String.format("%s, %s", cityZipCountry, country);
                    }
                }
            } else if (null != (city = item.getCity())) {
                String cityName = city.getName();
                String country = CustomerCountry.toString(city.getCountry()).trim();
                if (null == cityName || (cityName = cityName.trim()).isEmpty()) {
                    if (!country.isEmpty()) {
                        cityZipCountry = String.format("%s, %s", cityZipCountry, cityName);
                    }
                } else {
                    if (country.isEmpty()) {
                        cityZipCountry = String.format("%s %s", cityName, cityZipCountry);
                    } else {
                        cityZipCountry = String.format("%s %s, %s", cityName, cityZipCountry, country);
                    }
                }
            } else {
                cityZipCountry = "";
            }
            StringBuilder sb = new StringBuilder();
            String s = item.getAddress1();
            if (null != s && !(s = s.trim()).isEmpty()) {
                sb.append(s);
            }
            s = item.getAddress2();
            if (null != s && !(s = s.trim()).isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(", ").append(s);
                } else {
                    sb.append(s);
                }
            }
            if (!cityZipCountry.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(", ").append(cityZipCountry);
                } else {
                    sb.append(cityZipCountry);
                }
            }
            if (sb.length() > 0) {
                setText(sb.toString());
            } else {
                setText(item.getPhone());
            }
        }
    }
}
