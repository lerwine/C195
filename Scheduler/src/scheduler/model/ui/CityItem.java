package scheduler.model.ui;

import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.db.CityRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface CityItem extends CityRowData, FxModel {

    ReadOnlyProperty<String> nameProperty();

    @Override
    public CountryItem getCountry();

    ReadOnlyProperty<? extends CountryItem> countryProperty();

    ReadOnlyProperty<String> countryNameProperty();
 }
