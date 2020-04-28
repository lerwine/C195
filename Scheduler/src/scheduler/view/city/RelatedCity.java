package scheduler.view.city;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.DataRowState;
import scheduler.model.ModelHelper;
import scheduler.model.db.CityRowData;
import scheduler.model.db.CountryRowData;
import scheduler.model.predefined.PredefinedCity;
import scheduler.model.ui.CityDbItem;
import scheduler.model.ui.CountryItem;
import scheduler.observables.NestedStringBindingProperty;
import scheduler.observables.RowStateProperty;
import scheduler.view.country.RelatedCountry;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedCity implements CityDbItem<CityRowData> {

    private final ReadOnlyIntegerWrapper primaryKey;
    private final ReadOnlyStringWrapper name;
    private final NestedStringBindingProperty<CountryItem> countryName;
    private final ReadOnlyObjectWrapper<CountryItem> country;
    private final ReadOnlyObjectWrapper<CityRowData> dataObject;
    private final RowStateProperty rowState;
    private final PredefinedCity predefinedData;

    public RelatedCity(CityRowData rowData) {
        predefinedData = rowData.asPredefinedData();
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", rowData.getPrimaryKey());
        name = new ReadOnlyStringWrapper(this, "name", rowData.getName());
        CountryRowData c = rowData.getCountry();
        country = new ReadOnlyObjectWrapper<>(this, "country", (null == c) ? null : new RelatedCountry(c));
        countryName = new NestedStringBindingProperty<>(this, "countryName", country, (t) -> t.nameProperty());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", rowData);
        rowState = new RowStateProperty(this, "rowState", ModelHelper.getRowState(rowData));
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public String getName() {
        return name.get();
    }

    @Override
    public ReadOnlyStringProperty nameProperty() {
        return name.getReadOnlyProperty();
    }

    @Override
    public CountryItem getCountry() {
        return country.get();
    }

    @Override
    public ReadOnlyObjectProperty<CountryItem> countryProperty() {
        return country.getReadOnlyProperty();
    }

    @Override
    public NestedStringBindingProperty<CountryItem> countryNameProperty() {
        return countryName;
    }

    @Override
    public CityRowData getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<CityRowData> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyProperty<DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

    @Override
    public PredefinedCity asPredefinedData() {
        return predefinedData;
    }

}
